package co.runed.bolster.game;

import co.runed.bolster.Bolster;
import co.runed.bolster.entity.BolsterEntity;
import co.runed.bolster.events.game.GameModePauseEvent;
import co.runed.bolster.events.player.LoadPlayerDataEvent;
import co.runed.bolster.events.player.SavePlayerDataEvent;
import co.runed.bolster.events.server.RedisMessageEvent;
import co.runed.bolster.fx.Glyphs;
import co.runed.bolster.game.state.State;
import co.runed.bolster.game.state.StateSeries;
import co.runed.bolster.managers.Manager;
import co.runed.bolster.managers.PlayerManager;
import co.runed.bolster.match.MatchHistory;
import co.runed.bolster.util.BukkitUtil;
import co.runed.bolster.util.TimeUtil;
import co.runed.bolster.util.lang.Lang;
import co.runed.dayroom.properties.Properties;
import co.runed.dayroom.properties.Property;
import co.runed.dayroom.redis.RedisChannels;
import co.runed.dayroom.redis.RedisManager;
import co.runed.dayroom.redis.payload.Payload;
import co.runed.dayroom.redis.request.RequestMatchHistoryIdPayload;
import co.runed.dayroom.redis.response.RequestMatchHistoryIdResponsePayload;
import co.runed.dayroom.util.Identifiable;
import co.runed.dayroom.util.Nameable;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import java.time.Clock;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.UUID;

public abstract class GameMode extends Manager implements Identifiable, Nameable {
    private String id;
    private String status;

    private StateSeries mainState;
    private GameProperties properties;

    private HashMap<UUID, Properties> statistics = new HashMap<>();
    private Properties globalStatistics = new Properties();
    private MatchHistory matchHistory;

    private boolean hasStarted = false;
    private boolean paused = false;
    private boolean requiresResourcePack = false;
    private boolean serializeInventories = false;

    private BukkitTask tabMenuTask = null;
    private BukkitTask matchHistoryTask = null;

    public GameMode(String id, Class<? extends GameModeData> gameModeData, Plugin plugin) {
        super(plugin);

        this.id = id;
        this.properties = new GameProperties();

        this.matchHistory = new MatchHistory(this);

        if (gameModeData != null) PlayerManager.getInstance().addGameModeDataClass(this.getId(), gameModeData);
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

        RedisManager.getInstance().publish(RedisChannels.REQUEST_MATCH_HISTORY_ID, new RequestMatchHistoryIdPayload());
        this.matchHistory.start();

        this.hasStarted = true;
        if (this.mainState != null) this.mainState.start();

        this.tabMenuTask = Bukkit.getScheduler().runTaskTimer(plugin, this::buildAllTabMenu, 0L, 20L);
        this.matchHistoryTask = Bukkit.getScheduler().runTaskTimer(plugin, matchHistory::save, 0L, 20L * 60L);
    }

    public void stop() {
        if (this.tabMenuTask != null) this.tabMenuTask.cancel();
        if (this.matchHistoryTask != null) this.matchHistoryTask.cancel();

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
//        Component headerComponent = Component.newline()
//                .append(Component.text("  Welcome to ", NamedTextColor.YELLOW))
//                .append(Lang.simple("game.long-name"))
//                .append(Component.text("  "))
//                .append(Component.newline());

        Component headerComponent = Component.newline()
                .append(Component.text(Glyphs.LOGO))
                .append(Component.newline())
                .append(Component.newline())
                .append(Component.newline());

        player.sendPlayerListHeader(headerComponent);

        var playerData = PlayerManager.getInstance().getPlayerData(player);

        Component footerComponent = Component.empty().append(Component.newline());

        if (playerData.isPremium()) {
            var expiryTime = playerData.getPremiumExpiryTime();
            footerComponent = footerComponent.append(Component.text("  Thank you for supporting the server!  ", NamedTextColor.AQUA));

            if (expiryTime.isAfter(ZonedDateTime.now(Clock.systemUTC()))) {
                footerComponent = footerComponent
                        .append(Component.newline())
                        .append(Component.text("Your ").append(Lang.simple("rank.premium.name")).append(Component.text(" expires in")))
                        .append(Component.newline())
                        .append(Component.text(TimeUtil.formatDatePrettyRounded(expiryTime)));
            }
        }
        else {
            footerComponent = footerComponent
                    .append(Component.text("Support the server at ", NamedTextColor.AQUA))
                    .append(Lang.simple("game.store-url").append(Component.text("!", NamedTextColor.GOLD)));
        }


        footerComponent = footerComponent.append(Component.newline());

        player.sendPlayerListFooter(footerComponent);
    }

    @Override
    public String getId() {
        return this.id;
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

    @EventHandler
    public void onRedisMessage(RedisMessageEvent event) {
        if (event.getChannel().equals(RedisChannels.REQUEST_MATCH_HISTORY_ID_RESPONSE)) {
            var payload = Payload.fromJson(event.getMessage(), RequestMatchHistoryIdResponsePayload.class);
            Bolster.getInstance().getLogger().info("Set match id to " + payload.matchId);

            this.matchHistory.setMatchId(payload.matchId);
        }
    }
}
