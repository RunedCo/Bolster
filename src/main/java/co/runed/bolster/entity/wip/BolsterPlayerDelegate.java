package co.runed.bolster.entity.wip;

import co.runed.bolster.entity.BolsterLivingEntity;
import org.bukkit.*;
import org.bukkit.advancement.Advancement;
import org.bukkit.advancement.AdvancementProgress;
import org.bukkit.block.data.BlockData;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationAbandonedEvent;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.*;
import org.bukkit.map.MapView;
import org.bukkit.plugin.Plugin;
import org.bukkit.scoreboard.Scoreboard;

import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

public abstract class BolsterPlayerDelegate extends BolsterLivingEntity implements Player
{
    Player player;

    public BolsterPlayerDelegate(Player player)
    {
        super(player);

        this.player = player;
    }

    @Override
    public Player bukkit()
    {
        return this.player;
    }

    @Override
    public String getDisplayName()
    {
        return player.getDisplayName();
    }

    @Override
    public void setDisplayName(String s)
    {
        player.setDisplayName(s);
    }

    @Override
    public String getPlayerListName()
    {
        return player.getPlayerListName();
    }

    @Override
    public void setPlayerListName(String s)
    {
        player.setPlayerListName(s);
    }

    @Override
    public String getPlayerListHeader()
    {
        return player.getPlayerListHeader();
    }

    @Override
    public String getPlayerListFooter()
    {
        return player.getPlayerListFooter();
    }

    @Override
    public void setPlayerListHeader(String s)
    {
        player.setPlayerListHeader(s);
    }

    @Override
    public void setPlayerListFooter(String s)
    {
        player.setPlayerListFooter(s);
    }

    @Override
    public void setPlayerListHeaderFooter(String s, String s1)
    {
        player.setPlayerListHeaderFooter(s, s1);
    }

    @Override
    public void setCompassTarget(Location location)
    {
        player.setCompassTarget(location);
    }

    @Override
    public Location getCompassTarget()
    {
        return player.getCompassTarget();
    }

    @Override
    public InetSocketAddress getAddress()
    {
        return player.getAddress();
    }

    @Override
    public void sendRawMessage(String s)
    {
        player.sendRawMessage(s);
    }

    @Override
    public void kickPlayer(String s)
    {
        player.kickPlayer(s);
    }

    @Override
    public void chat(String s)
    {
        player.chat(s);
    }

    @Override
    public boolean performCommand(String s)
    {
        return player.performCommand(s);
    }

    @Override
    @Deprecated
    public boolean isOnGround()
    {
        return player.isOnGround();
    }

    @Override
    public boolean isSneaking()
    {
        return player.isSneaking();
    }

    @Override
    public void setSneaking(boolean b)
    {
        player.setSneaking(b);
    }

    @Override
    public boolean isSprinting()
    {
        return player.isSprinting();
    }

    @Override
    public void setSprinting(boolean b)
    {
        player.setSprinting(b);
    }

    @Override
    public void saveData()
    {
        player.saveData();
    }

    @Override
    public void loadData()
    {
        player.loadData();
    }

    @Override
    public void setSleepingIgnored(boolean b)
    {
        player.setSleepingIgnored(b);
    }

    @Override
    public boolean isSleepingIgnored()
    {
        return player.isSleepingIgnored();
    }

    @Override
    public Location getBedSpawnLocation()
    {
        return player.getBedSpawnLocation();
    }

    @Override
    public void setBedSpawnLocation(Location location)
    {
        player.setBedSpawnLocation(location);
    }

    @Override
    public void setBedSpawnLocation(Location location, boolean b)
    {
        player.setBedSpawnLocation(location, b);
    }

    @Override
    @Deprecated
    public void playNote(Location location, byte b, byte b1)
    {
        player.playNote(location, b, b1);
    }

    @Override
    public void playNote(Location location, Instrument instrument, Note note)
    {
        player.playNote(location, instrument, note);
    }

    @Override
    public void playSound(Location location, Sound sound, float v, float v1)
    {
        player.playSound(location, sound, v, v1);
    }

    @Override
    public void playSound(Location location, String s, float v, float v1)
    {
        player.playSound(location, s, v, v1);
    }

    @Override
    public void playSound(Location location, Sound sound, SoundCategory soundCategory, float v, float v1)
    {
        player.playSound(location, sound, soundCategory, v, v1);
    }

    @Override
    public void playSound(Location location, String s, SoundCategory soundCategory, float v, float v1)
    {
        player.playSound(location, s, soundCategory, v, v1);
    }

    @Override
    public void stopSound(Sound sound)
    {
        player.stopSound(sound);
    }

    @Override
    public void stopSound(String s)
    {
        player.stopSound(s);
    }

    @Override
    public void stopSound(Sound sound, SoundCategory soundCategory)
    {
        player.stopSound(sound, soundCategory);
    }

    @Override
    public void stopSound(String s, SoundCategory soundCategory)
    {
        player.stopSound(s, soundCategory);
    }

    @Override
    @Deprecated
    public void playEffect(Location location, Effect effect, int i)
    {
        player.playEffect(location, effect, i);
    }

    @Override
    public <T> void playEffect(Location location, Effect effect, T t)
    {
        player.playEffect(location, effect, t);
    }

    @Override
    @Deprecated
    public void sendBlockChange(Location location, Material material, byte b)
    {
        player.sendBlockChange(location, material, b);
    }

    @Override
    public void sendBlockChange(Location location, BlockData blockData)
    {
        player.sendBlockChange(location, blockData);
    }

    @Override
    @Deprecated
    public boolean sendChunkChange(Location location, int i, int i1, int i2, byte[] bytes)
    {
        return player.sendChunkChange(location, i, i1, i2, bytes);
    }

    @Override
    public void sendSignChange(Location location, String[] strings) throws IllegalArgumentException
    {
        player.sendSignChange(location, strings);
    }

    @Override
    public void sendSignChange(Location location, String[] strings, DyeColor dyeColor) throws IllegalArgumentException
    {
        player.sendSignChange(location, strings, dyeColor);
    }

    @Override
    public void sendMap(MapView mapView)
    {
        player.sendMap(mapView);
    }

    @Override
    public void updateInventory()
    {
        player.updateInventory();
    }

    @Override
    public void setPlayerTime(long l, boolean b)
    {
        player.setPlayerTime(l, b);
    }

    @Override
    public long getPlayerTime()
    {
        return player.getPlayerTime();
    }

    @Override
    public long getPlayerTimeOffset()
    {
        return player.getPlayerTimeOffset();
    }

    @Override
    public boolean isPlayerTimeRelative()
    {
        return player.isPlayerTimeRelative();
    }

    @Override
    public void resetPlayerTime()
    {
        player.resetPlayerTime();
    }

    @Override
    public void setPlayerWeather(WeatherType weatherType)
    {
        player.setPlayerWeather(weatherType);
    }

    @Override
    public WeatherType getPlayerWeather()
    {
        return player.getPlayerWeather();
    }

    @Override
    public void resetPlayerWeather()
    {
        player.resetPlayerWeather();
    }

    @Override
    public void giveExp(int i)
    {
        player.giveExp(i);
    }

    @Override
    public void giveExpLevels(int i)
    {
        player.giveExpLevels(i);
    }

    @Override
    public float getExp()
    {
        return player.getExp();
    }

    @Override
    public void setExp(float v)
    {
        player.setExp(v);
    }

    @Override
    public int getLevel()
    {
        return player.getLevel();
    }

    @Override
    public void setLevel(int i)
    {
        player.setLevel(i);
    }

    @Override
    public int getTotalExperience()
    {
        return player.getTotalExperience();
    }

    @Override
    public void setTotalExperience(int i)
    {
        player.setTotalExperience(i);
    }

    @Override
    public void sendExperienceChange(float v)
    {
        player.sendExperienceChange(v);
    }

    @Override
    public void sendExperienceChange(float v, int i)
    {
        player.sendExperienceChange(v, i);
    }

    @Override
    public float getExhaustion()
    {
        return player.getExhaustion();
    }

    @Override
    public void setExhaustion(float v)
    {
        player.setExhaustion(v);
    }

    @Override
    public float getSaturation()
    {
        return player.getSaturation();
    }

    @Override
    public void setSaturation(float v)
    {
        player.setSaturation(v);
    }

    @Override
    public int getFoodLevel()
    {
        return player.getFoodLevel();
    }

    @Override
    public void setFoodLevel(int i)
    {
        player.setFoodLevel(i);
    }

    @Override
    public boolean getAllowFlight()
    {
        return player.getAllowFlight();
    }

    @Override
    public void setAllowFlight(boolean b)
    {
        player.setAllowFlight(b);
    }

    @Override
    @Deprecated
    public void hidePlayer(Player player)
    {
        this.player.hidePlayer(player);
    }

    @Override
    public void hidePlayer(Plugin plugin, Player player)
    {
        this.player.hidePlayer(plugin, player);
    }

    @Override
    @Deprecated
    public void showPlayer(Player player)
    {
        this.player.showPlayer(player);
    }

    @Override
    public void showPlayer(Plugin plugin, Player player)
    {
        this.player.showPlayer(plugin, player);
    }

    @Override
    public boolean canSee(Player player)
    {
        return this.player.canSee(player);
    }

    @Override
    public boolean isFlying()
    {
        return player.isFlying();
    }

    @Override
    public void setFlying(boolean b)
    {
        player.setFlying(b);
    }

    @Override
    public void setFlySpeed(float v) throws IllegalArgumentException
    {
        player.setFlySpeed(v);
    }

    @Override
    public void setWalkSpeed(float v) throws IllegalArgumentException
    {
        player.setWalkSpeed(v);
    }

    @Override
    public float getFlySpeed()
    {
        return player.getFlySpeed();
    }

    @Override
    public float getWalkSpeed()
    {
        return player.getWalkSpeed();
    }

    @Override
    @Deprecated
    public void setTexturePack(String s)
    {
        player.setTexturePack(s);
    }

    @Override
    public void setResourcePack(String s)
    {
        player.setResourcePack(s);
    }

    @Override
    public void setResourcePack(String s, byte[] bytes)
    {
        player.setResourcePack(s, bytes);
    }

    @Override
    public Scoreboard getScoreboard()
    {
        return player.getScoreboard();
    }

    @Override
    public void setScoreboard(Scoreboard scoreboard) throws IllegalArgumentException, IllegalStateException
    {
        player.setScoreboard(scoreboard);
    }

    @Override
    public boolean isHealthScaled()
    {
        return player.isHealthScaled();
    }

    @Override
    public void setHealthScaled(boolean b)
    {
        player.setHealthScaled(b);
    }

    @Override
    public void setHealthScale(double v) throws IllegalArgumentException
    {
        player.setHealthScale(v);
    }

    @Override
    public double getHealthScale()
    {
        return player.getHealthScale();
    }

    @Override
    public Entity getSpectatorTarget()
    {
        return player.getSpectatorTarget();
    }

    @Override
    public void setSpectatorTarget(Entity entity)
    {
        player.setSpectatorTarget(entity);
    }

    @Override
    @Deprecated
    public void sendTitle(String s, String s1)
    {
        player.sendTitle(s, s1);
    }

    @Override
    public void sendTitle(String s, String s1, int i, int i1, int i2)
    {
        player.sendTitle(s, s1, i, i1, i2);
    }

    @Override
    public void resetTitle()
    {
        player.resetTitle();
    }

    @Override
    public void spawnParticle(Particle particle, Location location, int i)
    {
        player.spawnParticle(particle, location, i);
    }

    @Override
    public void spawnParticle(Particle particle, double v, double v1, double v2, int i)
    {
        player.spawnParticle(particle, v, v1, v2, i);
    }

    @Override
    public <T> void spawnParticle(Particle particle, Location location, int i, T t)
    {
        player.spawnParticle(particle, location, i, t);
    }

    @Override
    public <T> void spawnParticle(Particle particle, double v, double v1, double v2, int i, T t)
    {
        player.spawnParticle(particle, v, v1, v2, i, t);
    }

    @Override
    public void spawnParticle(Particle particle, Location location, int i, double v, double v1, double v2)
    {
        player.spawnParticle(particle, location, i, v, v1, v2);
    }

    @Override
    public void spawnParticle(Particle particle, double v, double v1, double v2, int i, double v3, double v4, double v5)
    {
        player.spawnParticle(particle, v, v1, v2, i, v3, v4, v5);
    }

    @Override
    public <T> void spawnParticle(Particle particle, Location location, int i, double v, double v1, double v2, T t)
    {
        player.spawnParticle(particle, location, i, v, v1, v2, t);
    }

    @Override
    public <T> void spawnParticle(Particle particle, double v, double v1, double v2, int i, double v3, double v4, double v5, T t)
    {
        player.spawnParticle(particle, v, v1, v2, i, v3, v4, v5, t);
    }

    @Override
    public void spawnParticle(Particle particle, Location location, int i, double v, double v1, double v2, double v3)
    {
        player.spawnParticle(particle, location, i, v, v1, v2, v3);
    }

    @Override
    public void spawnParticle(Particle particle, double v, double v1, double v2, int i, double v3, double v4, double v5, double v6)
    {
        player.spawnParticle(particle, v, v1, v2, i, v3, v4, v5, v6);
    }

    @Override
    public <T> void spawnParticle(Particle particle, Location location, int i, double v, double v1, double v2, double v3, T t)
    {
        player.spawnParticle(particle, location, i, v, v1, v2, v3, t);
    }

    @Override
    public <T> void spawnParticle(Particle particle, double v, double v1, double v2, int i, double v3, double v4, double v5, double v6, T t)
    {
        player.spawnParticle(particle, v, v1, v2, i, v3, v4, v5, v6, t);
    }

    @Override
    public AdvancementProgress getAdvancementProgress(Advancement advancement)
    {
        return player.getAdvancementProgress(advancement);
    }

    @Override
    public int getClientViewDistance()
    {
        return player.getClientViewDistance();
    }

    @Override
    public String getLocale()
    {
        return player.getLocale();
    }

    @Override
    public void updateCommands()
    {
        player.updateCommands();
    }

    @Override
    public void openBook(ItemStack itemStack)
    {
        player.openBook(itemStack);
    }

    @Override
    public Player.Spigot spigot()
    {
        return player.spigot();
    }

    @Override
    public String getName()
    {
        return player.getName();
    }

    @Override
    public PlayerInventory getInventory()
    {
        return player.getInventory();
    }

    @Override
    public Inventory getEnderChest()
    {
        return player.getEnderChest();
    }

    @Override
    public MainHand getMainHand()
    {
        return player.getMainHand();
    }

    @Override
    public boolean setWindowProperty(InventoryView.Property property, int i)
    {
        return player.setWindowProperty(property, i);
    }

    @Override
    public InventoryView getOpenInventory()
    {
        return player.getOpenInventory();
    }

    @Override
    public InventoryView openInventory(Inventory inventory)
    {
        return player.openInventory(inventory);
    }

    @Override
    public InventoryView openWorkbench(Location location, boolean b)
    {
        return player.openWorkbench(location, b);
    }

    @Override
    public InventoryView openEnchanting(Location location, boolean b)
    {
        return player.openEnchanting(location, b);
    }

    @Override
    public void openInventory(InventoryView inventoryView)
    {
        player.openInventory(inventoryView);
    }

    @Override
    public InventoryView openMerchant(Villager villager, boolean b)
    {
        return player.openMerchant(villager, b);
    }

    @Override
    public InventoryView openMerchant(Merchant merchant, boolean b)
    {
        return player.openMerchant(merchant, b);
    }

    @Override
    public void closeInventory()
    {
        player.closeInventory();
    }

    @Override
    @Deprecated
    public ItemStack getItemInHand()
    {
        return player.getItemInHand();
    }

    @Override
    @Deprecated
    public void setItemInHand(ItemStack itemStack)
    {
        player.setItemInHand(itemStack);
    }

    @Override
    public ItemStack getItemOnCursor()
    {
        return player.getItemOnCursor();
    }

    @Override
    public void setItemOnCursor(ItemStack itemStack)
    {
        player.setItemOnCursor(itemStack);
    }

    @Override
    public boolean hasCooldown(Material material)
    {
        return player.hasCooldown(material);
    }

    @Override
    public int getCooldown(Material material)
    {
        return player.getCooldown(material);
    }

    @Override
    public void setCooldown(Material material, int i)
    {
        player.setCooldown(material, i);
    }

    @Override
    public int getSleepTicks()
    {
        return player.getSleepTicks();
    }

    @Override
    public boolean sleep(Location location, boolean b)
    {
        return player.sleep(location, b);
    }

    @Override
    public void wakeup(boolean b)
    {
        player.wakeup(b);
    }

    @Override
    public Location getBedLocation()
    {
        return player.getBedLocation();
    }

    @Override
    public GameMode getGameMode()
    {
        return player.getGameMode();
    }

    @Override
    public void setGameMode(GameMode gameMode)
    {
        player.setGameMode(gameMode);
    }

    @Override
    public boolean isBlocking()
    {
        return player.isBlocking();
    }

    @Override
    public boolean isHandRaised()
    {
        return player.isHandRaised();
    }

    @Override
    public int getExpToLevel()
    {
        return player.getExpToLevel();
    }

    @Override
    public float getAttackCooldown()
    {
        return player.getAttackCooldown();
    }

    @Override
    public boolean discoverRecipe(NamespacedKey namespacedKey)
    {
        return player.discoverRecipe(namespacedKey);
    }

    @Override
    public int discoverRecipes(Collection<NamespacedKey> collection)
    {
        return player.discoverRecipes(collection);
    }

    @Override
    public boolean undiscoverRecipe(NamespacedKey namespacedKey)
    {
        return player.undiscoverRecipe(namespacedKey);
    }

    @Override
    public int undiscoverRecipes(Collection<NamespacedKey> collection)
    {
        return player.undiscoverRecipes(collection);
    }

    @Override
    public boolean hasDiscoveredRecipe(NamespacedKey namespacedKey)
    {
        return player.hasDiscoveredRecipe(namespacedKey);
    }

    @Override
    public Set<NamespacedKey> getDiscoveredRecipes()
    {
        return player.getDiscoveredRecipes();
    }

    @Override
    @Deprecated
    public Entity getShoulderEntityLeft()
    {
        return player.getShoulderEntityLeft();
    }

    @Override
    @Deprecated
    public void setShoulderEntityLeft(Entity entity)
    {
        player.setShoulderEntityLeft(entity);
    }

    @Override
    @Deprecated
    public Entity getShoulderEntityRight()
    {
        return player.getShoulderEntityRight();
    }

    @Override
    @Deprecated
    public void setShoulderEntityRight(Entity entity)
    {
        player.setShoulderEntityRight(entity);
    }

    @Override
    public boolean dropItem(boolean b)
    {
        return player.dropItem(b);
    }

    @Override
    public boolean isConversing()
    {
        return player.isConversing();
    }

    @Override
    public void acceptConversationInput(String s)
    {
        player.acceptConversationInput(s);
    }

    @Override
    public boolean beginConversation(Conversation conversation)
    {
        return player.beginConversation(conversation);
    }

    @Override
    public void abandonConversation(Conversation conversation)
    {
        player.abandonConversation(conversation);
    }

    @Override
    public void abandonConversation(Conversation conversation, ConversationAbandonedEvent conversationAbandonedEvent)
    {
        player.abandonConversation(conversation, conversationAbandonedEvent);
    }

    @Override
    public boolean isOnline()
    {
        return player.isOnline();
    }

    @Override
    public boolean isBanned()
    {
        return player.isBanned();
    }

    @Override
    public boolean isWhitelisted()
    {
        return player.isWhitelisted();
    }

    @Override
    public void setWhitelisted(boolean b)
    {
        player.setWhitelisted(b);
    }

    @Override
    public Player getPlayer()
    {
        return player.getPlayer();
    }

    @Override
    public long getFirstPlayed()
    {
        return player.getFirstPlayed();
    }

    @Override
    public long getLastPlayed()
    {
        return player.getLastPlayed();
    }

    @Override
    public boolean hasPlayedBefore()
    {
        return player.hasPlayedBefore();
    }

    @Override
    public void incrementStatistic(Statistic statistic) throws IllegalArgumentException
    {
        player.incrementStatistic(statistic);
    }

    @Override
    public void decrementStatistic(Statistic statistic) throws IllegalArgumentException
    {
        player.decrementStatistic(statistic);
    }

    @Override
    public void incrementStatistic(Statistic statistic, int i) throws IllegalArgumentException
    {
        player.incrementStatistic(statistic, i);
    }

    @Override
    public void decrementStatistic(Statistic statistic, int i) throws IllegalArgumentException
    {
        player.decrementStatistic(statistic, i);
    }

    @Override
    public void setStatistic(Statistic statistic, int i) throws IllegalArgumentException
    {
        player.setStatistic(statistic, i);
    }

    @Override
    public int getStatistic(Statistic statistic) throws IllegalArgumentException
    {
        return player.getStatistic(statistic);
    }

    @Override
    public void incrementStatistic(Statistic statistic, Material material) throws IllegalArgumentException
    {
        player.incrementStatistic(statistic, material);
    }

    @Override
    public void decrementStatistic(Statistic statistic, Material material) throws IllegalArgumentException
    {
        player.decrementStatistic(statistic, material);
    }

    @Override
    public int getStatistic(Statistic statistic, Material material) throws IllegalArgumentException
    {
        return player.getStatistic(statistic, material);
    }

    @Override
    public void incrementStatistic(Statistic statistic, Material material, int i) throws IllegalArgumentException
    {
        player.incrementStatistic(statistic, material, i);
    }

    @Override
    public void decrementStatistic(Statistic statistic, Material material, int i) throws IllegalArgumentException
    {
        player.decrementStatistic(statistic, material, i);
    }

    @Override
    public void setStatistic(Statistic statistic, Material material, int i) throws IllegalArgumentException
    {
        player.setStatistic(statistic, material, i);
    }

    @Override
    public void incrementStatistic(Statistic statistic, EntityType entityType) throws IllegalArgumentException
    {
        player.incrementStatistic(statistic, entityType);
    }

    @Override
    public void decrementStatistic(Statistic statistic, EntityType entityType) throws IllegalArgumentException
    {
        player.decrementStatistic(statistic, entityType);
    }

    @Override
    public int getStatistic(Statistic statistic, EntityType entityType) throws IllegalArgumentException
    {
        return player.getStatistic(statistic, entityType);
    }

    @Override
    public void incrementStatistic(Statistic statistic, EntityType entityType, int i) throws IllegalArgumentException
    {
        player.incrementStatistic(statistic, entityType, i);
    }

    @Override
    public void decrementStatistic(Statistic statistic, EntityType entityType, int i)
    {
        player.decrementStatistic(statistic, entityType, i);
    }

    @Override
    public void setStatistic(Statistic statistic, EntityType entityType, int i)
    {
        player.setStatistic(statistic, entityType, i);
    }

    @Override
    public Map<String, Object> serialize()
    {
        return player.serialize();
    }

    @Override
    public void sendPluginMessage(Plugin plugin, String s, byte[] bytes)
    {
        player.sendPluginMessage(plugin, s, bytes);
    }

    @Override
    public Set<String> getListeningPluginChannels()
    {
        return player.getListeningPluginChannels();
    }
}
