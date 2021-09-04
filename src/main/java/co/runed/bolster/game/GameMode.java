package co.runed.bolster.game;

import co.runed.bolster.Bolster;
import co.runed.bolster.common.properties.Properties;
import co.runed.bolster.common.properties.Property;
import co.runed.bolster.common.util.Identifiable;
import co.runed.bolster.entity.BolsterEntity;
import co.runed.bolster.events.game.GameModePauseEvent;
import co.runed.bolster.events.player.LoadPlayerDataEvent;
import co.runed.bolster.events.player.SavePlayerDataEvent;
import co.runed.bolster.game.state.State;
import co.runed.bolster.game.state.StateSeries;
import co.runed.bolster.managers.Manager;
import co.runed.bolster.managers.PlayerManager;
import co.runed.bolster.match.MatchHistory;
import co.runed.bolster.util.BukkitUtil;
import co.runed.bolster.util.TimeUtil;
import co.runed.bolster.util.config.Configurable;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import java.time.Clock;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.UUID;

public abstract class GameMode extends Manager implements Identifiable, Configurable {
    public static final Property<Double> XP_MULTIPLER = new Property<>("xp_multiplier", 1.0);
    public static final Property<Double> GOLD_MULTIPLER = new Property<>("gold_multiplier", 1.0);
    public static final Property<Double> DAMAGE_MULTIPLIER = new Property<>("damage_multiplier", 1.0);
    public static final Property<Double> HEALTH_MULTIPLIER = new Property<>("health_multiplier", 1.0);

    StateSeries mainState;
    String id;
    GameProperties properties;
    HashMap<UUID, Properties> statistics = new HashMap<>();
    Properties globalStatistics = new Properties();
    MatchHistory matchHistory;

    String status;
    boolean hasStarted = false;
    boolean paused = false;
    boolean requiresResourcePack = false;
    boolean serializeInventories = false;

    BukkitTask tabMenuTask = null;

    public GameMode(String id, Class<? extends GameModeData> gameModeData, Plugin plugin) {
        super(plugin);

        this.id = id;
        this.properties = new GameProperties();

        this.matchHistory = new MatchHistory(this);

        if (gameModeData != null) PlayerManager.getInstance().addGameModeDataClass(this.getId(), gameModeData);
    }

    public String getName() {
        return Bolster.getBolsterConfig().gameName;
    }

    public StateSeries getMainState() {
        return this.mainState;
    }

    public void setState(StateSeries mainState) {
        this.mainState = mainState;
    }

    public State getCurrentState() {
        return this.mainState.getCurrent();
    }

    public boolean isCurrentState(Class<? extends State> state) {
        return this.getCurrentState().getClass() == state;
    }

    public void start() {
        if (this.hasStarted) return;

        this.matchHistory.start();

        this.hasStarted = true;
        if (this.mainState != null) this.mainState.start();

        this.tabMenuTask = Bukkit.getScheduler().runTaskTimer(plugin, this::buildAllTabMenu, 0L, 20L);
    }

    public void stop() {
        if (this.tabMenuTask != null) this.tabMenuTask.cancel();

        this.matchHistory.end();
    }

    public boolean isPaused() {
        return paused;
    }

    public void setPaused(boolean paused) {
        this.paused = paused;

        BukkitUtil.triggerEvent(new GameModePauseEvent(this, paused));
    }

    public void setSerializeInventories(boolean serializeInventories) {
        this.serializeInventories = serializeInventories;
    }

    public boolean shouldSerializeInventories() {
        return serializeInventories;
    }

    public void setRequiresResourcePack(boolean requiresResourcePack) {
        this.requiresResourcePack = requiresResourcePack;
    }

    public boolean getRequiresResourcePack() {
        return requiresResourcePack;
    }

    public boolean hasStarted() {
        return hasStarted;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;

        Bolster.updateServer();
    }

    public GameProperties getProperties() {
        return this.properties;
    }

    public MatchHistory getMatchHistory() {
        return matchHistory;
    }

    public <T> T getStatistic(Player player, Property<T> statistic) {
        this.statistics.putIfAbsent(player.getUniqueId(), new Properties());

        return this.statistics.get(player.getUniqueId()).get(statistic);
    }

    // TODO better statistics?
    public <T> void setStatistic(Player player, Property<Integer> statistic, int value) {
        this.statistics.putIfAbsent(player.getUniqueId(), new Properties());

        this.statistics.get(player.getUniqueId()).set(statistic, value);
    }

    // TODO better statistics?
    public <T> void incrementStatistic(Player player, Property<Integer> statistic, int increment) {
        this.statistics.putIfAbsent(player.getUniqueId(), new Properties());

        int value = this.statistics.get(player.getUniqueId()).get(statistic);

        this.statistics.get(player.getUniqueId()).set(statistic, value + increment);
    }

    public <T> T getGlobalStatistic(Property<T> statistic) {
        return this.globalStatistics.get(statistic);
    }

    public <T> void setGlobalStatistic(Property<T> statistic, T value) {
        this.globalStatistics.set(statistic, value);
    }

    public <T> void incrementGlobalStatistic(Property<Integer> statistic, int increment) {
        int value = this.globalStatistics.get(statistic);

        this.globalStatistics.set(statistic, value + increment);
    }

    public void buildAllTabMenu() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            this.buildTabMenu(player);
        }
    }

    public void buildTabMenu(Player player) {
        var bolsterConfig = Bolster.getBolsterConfig();

        Component headerComponent = Component.text("  Welcome to ", NamedTextColor.YELLOW)
                .append(bolsterConfig.longGameName)
                .append(Component.text("  "))
                .append(Component.newline());

        player.sendPlayerListHeader(headerComponent);

        var playerData = PlayerManager.getInstance().getPlayerData(player);

        Component footerComponent = Component.empty().append(Component.newline());

        if (playerData.isPremium()) {
            var expiryTime = playerData.getPremiumExpiryTime();
            footerComponent = footerComponent.append(Component.text("  Thank you for supporting the server!  ", NamedTextColor.AQUA));

            if (expiryTime.isAfter(ZonedDateTime.now(Clock.systemUTC()))) {
                footerComponent = footerComponent
                        .append(Component.text("Your " + bolsterConfig.premiumMembershipName + " expires in"))
                        .append(Component.newline())
                        .append(Component.text(TimeUtil.formatDatePrettyRounded(expiryTime)));
            }
        }
        else {
            footerComponent = footerComponent
                    .append(Component.text("Support the server at ", NamedTextColor.AQUA))
                    .append(Component.text(bolsterConfig.storeUrl + "!", NamedTextColor.GOLD));
        }

        player.sendPlayerListFooter(footerComponent);
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public void loadConfig(ConfigurationSection config) {

    }

    @EventHandler
    public void onResourcePackFailed(PlayerResourcePackStatusEvent event) {
        if (event.getStatus() == PlayerResourcePackStatusEvent.Status.DECLINED || event.getStatus() == PlayerResourcePackStatusEvent.Status.FAILED_DOWNLOAD) {
            event.getPlayer().kickPlayer("You need to enable resource packs.");
        }
    }

    @EventHandler
    public void onLoadPlayer(LoadPlayerDataEvent event) {
        if (!this.shouldSerializeInventories()) return;
        if (event.getPlayer() == null) return;

        var playerData = event.getPlayerData();
        var gameModeData = playerData.getGameModeData(this.getId());
        var entity = BolsterEntity.from(event.getPlayer());

        for (var entry : gameModeData.inventories.entrySet()) {
            if (entity.hasInventory(entry.getKey())) {
                var inventory = entity.getInventory(entry.getKey());
                var contents = entry.getValue().getContents();

                inventory.clear();

                for (var i = 0; i < inventory.getContents().length; i++) {
                    var itemStack = contents[i];
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
    public void onSavePlayer(SavePlayerDataEvent event) {
        if (!this.shouldSerializeInventories()) return;
        if (event.getPlayer() == null) return;

        var playerData = event.getPlayerData();
        var gameModeData = playerData.getGameModeData(this.getId());

        for (var entry : BolsterEntity.from(event.getPlayer()).getInventoryMap().entrySet()) {
            gameModeData.setInventory(entry.getKey(), entry.getValue());
        }
    }
}
