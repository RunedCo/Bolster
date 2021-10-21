package co.runed.bolster.managers;

import co.runed.bolster.events.server.ReloadConfigEvent;
import co.runed.bolster.util.NPC;
import co.runed.dayroom.gson.GsonUtil;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import me.libraryaddict.disguise.utilities.DisguiseUtilities;
import org.apache.commons.io.FileUtils;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class NPCManager extends Manager {
    Map<UUID, NPC> npcMap = new HashMap<>();
    Map<String, WrappedGameProfile> gameProfiles = new HashMap<>();

    private static NPCManager _instance;

    public NPCManager(Plugin plugin) {
        super(plugin);

        _instance = this;

        this.loadFiles();
    }

    private void loadFiles() {
        var gson = GsonUtil.create();
        var folder = new File(plugin.getDataFolder(), "gameprofiles");

        for (var file : FileUtils.listFiles(folder, new String[]{"json"}, true)) {
            try {
                var reader = new BufferedReader(new FileReader(file));
                var cached = reader.lines().collect(Collectors.joining());
                reader.close();

                var data = gson.fromJson(cached, WrappedGameProfile.class);

                gameProfiles.put(data.getName(), data);

                plugin.getLogger().info("Loaded game profile " + data.getName());
            }
            catch (Exception e) {
                plugin.getLogger().severe("Error loading game profile " + file.getName());
                e.printStackTrace();
            }
        }
    }

    public void add(@NotNull NPC npc) {
        var entity = npc.getEntity();

        if (entity == null) return;

        npcMap.put(entity.getUniqueId(), npc);
    }

    public void remove(@NotNull NPC npc) {
        var entity = npc.getEntity();

        if (entity == null) return;

        this.remove(entity.getUniqueId());
    }

    public void remove(UUID uuid) {
        if (!npcMap.containsKey(uuid)) return;

        var npc = npcMap.get(uuid);

        npc.remove();

        npcMap.remove(uuid);
    }

    @EventHandler
    private void onReload(ReloadConfigEvent event) {
        this.gameProfiles.clear();

        this.loadFiles();
    }

    @EventHandler
    private void onInteract(PlayerInteractAtEntityEvent event) {
        var uuid = event.getRightClicked().getUniqueId();

        if (!npcMap.containsKey(uuid)) return;

        var npc = npcMap.get(uuid);
        npc.onRightClick.accept(event.getPlayer(), event.getClickedPosition());
        npc.onAnyClick.accept(event.getPlayer(), event.getClickedPosition());
    }

    @EventHandler
    private void onInteract(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player player)) return;
        if (!(event.getEntity() instanceof LivingEntity livingEntity)) return;

        var uuid = event.getEntity().getUniqueId();

        if (!npcMap.containsKey(uuid)) return;

        var npc = npcMap.get(uuid);
        npc.onLeftClick.accept(player, livingEntity.getEyeLocation().toVector());
        npc.onAnyClick.accept(player, livingEntity.getEyeLocation().toVector());
    }

    public static WrappedGameProfile getProfile(String name) {
        var manager = NPCManager.getInstance();

        if (!manager.gameProfiles.containsKey(name)) return DisguiseUtilities.getGameProfile(name);

        return manager.gameProfiles.get(name);
    }

    public static NPCManager getInstance() {
        return _instance;
    }
}
