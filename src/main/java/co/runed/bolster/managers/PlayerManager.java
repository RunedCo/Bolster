package co.runed.bolster.managers;

import co.runed.bolster.PlayerData;
import co.runed.bolster.util.Manager;
import com.google.gson.Gson;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.UUID;

public class PlayerManager extends Manager
{
    private static PlayerManager _instance;
    HashMap<UUID, PlayerData> playerData = new HashMap<>();
    private Class<? extends PlayerData> dataClass = PlayerData.class;
    private final Gson gson = new Gson();

    public PlayerManager(Plugin plugin)
    {
        super(plugin);

        _instance = this;
    }

    public void setDataClass(Class<? extends PlayerData> dataClass)
    {
        this.dataClass = dataClass;
    }

    public PlayerData deserialize(String json)
    {
        return this.gson.fromJson(json, dataClass);
    }

    public PlayerData getPlayerData(Player player)
    {
        return new PlayerData();
    }

    @EventHandler
    private void onPlayerJoin(PlayerJoinEvent event)
    {

    }

    public static PlayerManager getInstance()
    {
        return _instance;
    }
}
