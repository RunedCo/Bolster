package co.runed.bolster.game;

import co.runed.bolster.Bolster;
import co.runed.bolster.BolsterEntity;
import co.runed.bolster.Config;
import co.runed.bolster.events.GameModePauseChangeEvent;
import co.runed.bolster.events.LoadPlayerDataEvent;
import co.runed.bolster.events.SavePlayerDataEvent;
import co.runed.bolster.game.state.State;
import co.runed.bolster.game.state.StateSeries;
import co.runed.bolster.managers.Manager;
import co.runed.bolster.managers.PlayerManager;
import co.runed.bolster.util.IConfigurable;
import co.runed.bolster.util.TimeUtil;
import co.runed.bolster.util.properties.Properties;
import co.runed.bolster.util.properties.Property;
import co.runed.bolster.util.registries.IRegisterable;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import java.time.Clock;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public abstract class GameMode extends Manager implements IRegisterable, IConfigurable
{
    public static final Property<Double> XP_MULTIPLER = new Property<>("xp_multiplier", 1.0);
    public static final Property<Double> GOLD_MULTIPLER = new Property<>("gold_multiplier", 1.0);
    public static final Property<Double> DAMAGE_MULTIPLIER = new Property<>("damage_multiplier", 1.0);
    public static final Property<Double> HEALTH_MULTIPLIER = new Property<>("health_multiplier", 1.0);

    StateSeries mainState;
    String id;
    GameProperties properties;
    HashMap<UUID, Properties> statistics = new HashMap<>();
    Properties globalStatistics = new Properties();

    boolean hasStarted = false;
    boolean paused = false;
    boolean requiresResourcePack = false;
    boolean serializeInventories = false;

    BukkitTask tabMenuTask = null;

    public GameMode(String id, Class<? extends GameModeData> gameModeData, Plugin plugin)
    {
        super(plugin);

        this.id = id;
        this.properties = new GameProperties();

        if (gameModeData != null) PlayerManager.getInstance().addGameModeDataClass(this.getId(), gameModeData);
    }

    public abstract String getName();

    public StateSeries getMainState()
    {
        return this.mainState;
    }

    public void setState(StateSeries mainState)
    {
        this.mainState = mainState;
    }

    public State getCurrentState()
    {
        return this.mainState.getCurrent();
    }

    public boolean isCurrentState(Class<? extends State> state)
    {
        return this.getCurrentState().getClass() == state;
    }

    public void start()
    {
        if (this.hasStarted) return;

        this.hasStarted = true;
        this.mainState.start();

        this.tabMenuTask = Bukkit.getScheduler().runTaskTimer(plugin, this::buildAllTabMenu, 0L, 20L);
    }

    public void stop()
    {
        if (this.tabMenuTask != null) this.tabMenuTask.cancel();
    }

    public boolean isPaused()
    {
        return paused;
    }

    public void setPaused(boolean paused)
    {
        this.paused = paused;

        GameModePauseChangeEvent event = new GameModePauseChangeEvent(paused);
        Bukkit.getServer().getPluginManager().callEvent(event);
    }

    public void setSerializeInventories(boolean serializeInventories)
    {
        this.serializeInventories = serializeInventories;
    }

    public boolean shouldSerializeInventories()
    {
        return serializeInventories;
    }

    public void setRequiresResourcePack(boolean requiresResourcePack)
    {
        this.requiresResourcePack = requiresResourcePack;
    }

    public boolean getRequiresResourcePack()
    {
        return requiresResourcePack;
    }

    public boolean hasStarted()
    {
        return hasStarted;
    }

    public GameProperties getProperties()
    {
        return this.properties;
    }

    public <T> T getStatistic(Player player, Property<T> statistic)
    {
        this.statistics.putIfAbsent(player.getUniqueId(), new Properties());

        return this.statistics.get(player.getUniqueId()).get(statistic);
    }

    // TODO better statistics?
    public <T> void setStatistic(Player player, Property<Integer> statistic, int value)
    {
        this.statistics.putIfAbsent(player.getUniqueId(), new Properties());

        this.statistics.get(player.getUniqueId()).set(statistic, value);
    }

    // TODO better statistics?
    public <T> void incrementStatistic(Player player, Property<Integer> statistic, int increment)
    {
        this.statistics.putIfAbsent(player.getUniqueId(), new Properties());

        int value = this.statistics.get(player.getUniqueId()).get(statistic);

        this.statistics.get(player.getUniqueId()).set(statistic, value + increment);
    }

    public <T> T getGlobalStatistic(Property<T> statistic)
    {
        return this.globalStatistics.get(statistic);
    }

    public <T> void setGlobalStatistic(Property<T> statistic, T value)
    {
        this.globalStatistics.set(statistic, value);
    }

    public <T> void incrementGlobalStatistic(Property<Integer> statistic, int increment)
    {
        int value = this.globalStatistics.get(statistic);

        this.globalStatistics.set(statistic, value + increment);
    }

    public void buildAllTabMenu()
    {
        for (Player player : Bukkit.getOnlinePlayers())
        {
            this.buildTabMenu(player);
        }
    }

    public void buildTabMenu(Player player)
    {
        Config bolsterConfig = Bolster.getBolsterConfig();

        player.setPlayerListHeader(ChatColor.YELLOW + "  Welcome to " + bolsterConfig.longGameName + "  \n");

        PlayerData playerData = PlayerManager.getInstance().getPlayerData(player);
        if (playerData.isPremium())
        {
            ZonedDateTime expiryTime = playerData.getPremiumExpiryTime();
            String footer = ChatColor.AQUA + "  Thank you for supporting the server!  ";

            if (expiryTime.isAfter(ZonedDateTime.now(Clock.systemUTC())))
            {
                footer += "\n\nYour " + bolsterConfig.premiumMembershipName + " expires in\n";
                footer += TimeUtil.formatDatePrettyRounded(expiryTime);
            }

            player.setPlayerListFooter("\n" + footer);
        }
        else
        {
            player.setPlayerListFooter("\n" + ChatColor.AQUA + "Support the server at " + ChatColor.GOLD + ChatColor.BOLD + bolsterConfig.storeUrl + "!");
        }
    }

    @Override
    public String getId()
    {
        return this.id;
    }

    @Override
    public String getDescription()
    {
        return null;
    }

    @Override
    public void create(ConfigurationSection config)
    {

    }

    @EventHandler
    public void onResourcePackFailed(PlayerResourcePackStatusEvent event)
    {
        if (event.getStatus() == PlayerResourcePackStatusEvent.Status.DECLINED || event.getStatus() == PlayerResourcePackStatusEvent.Status.FAILED_DOWNLOAD)
        {
            event.getPlayer().kickPlayer("You need to enable resource packs.");
        }
    }

    @EventHandler
    public void onLoadPlayer(LoadPlayerDataEvent event)
    {
        if (!this.shouldSerializeInventories()) return;
        if (event.getPlayer() == null) return;

        PlayerData playerData = event.getPlayerData();
        GameModeData gameModeData = playerData.getGameModeData(this.getId());
        BolsterEntity entity = BolsterEntity.from(event.getPlayer());

        for (Map.Entry<String, Inventory> entry : gameModeData.inventories.entrySet())
        {
            if (entity.hasInventory(entry.getKey()))
            {
                Inventory inventory = entity.getInventory(entry.getKey());
                ItemStack[] contents = entry.getValue().getContents();

                inventory.clear();

                for (int i = 0; i < inventory.getContents().length; i++)
                {
                    ItemStack itemStack = contents[i];
                    if (itemStack == null || itemStack.getType() == Material.AIR) continue;

                    inventory.setItem(i, itemStack);
                }

                entity.setInventory(entry.getKey(), inventory);

                continue;
            }

            entity.setInventory(entry.getKey(), entry.getValue());
        }
    }

    @EventHandler
    public void onSavePlayer(SavePlayerDataEvent event)
    {
        if (!this.shouldSerializeInventories()) return;
        if (event.getPlayer() == null) return;

        PlayerData playerData = event.getPlayerData();
        GameModeData gameModeData = playerData.getGameModeData(this.getId());

        for (Map.Entry<String, Inventory> entry : BolsterEntity.from(event.getPlayer()).getInventoryMap().entrySet())
        {
            gameModeData.setInventory(entry.getKey(), entry.getValue());
        }
    }
}
