package co.runed.bolster.entity;

import co.runed.bolster.damage.DamageSource;
import co.runed.bolster.events.entity.EntityDestroyEvent;
import co.runed.bolster.game.PlayerData;
import co.runed.bolster.game.Settings;
import co.runed.bolster.game.traits.Trait;
import co.runed.bolster.game.traits.TraitProvider;
import co.runed.bolster.game.traits.Traits;
import co.runed.bolster.managers.EntityManager;
import co.runed.bolster.managers.PlayerManager;
import co.runed.bolster.managers.StatusEffectManager;
import co.runed.bolster.status.StatusEffect;
import co.runed.bolster.util.BukkitUtil;
import co.runed.bolster.util.TimeUtil;
import co.runed.bolster.util.task.TaskUtil;
import co.runed.bolster.wip.BowTracker;
import co.runed.dayroom.math.NumberUtil;
import co.runed.dayroom.math.Operation;
import co.runed.dayroom.math.easing.Ease;
import co.runed.dayroom.properties.Properties;
import co.runed.dayroom.util.Enableable;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.Inventory;
import org.bukkit.util.Vector;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class BolsterEntity extends TraitProvider implements Enableable, DamageSource {
    private LivingEntity _entity;
    private List<TraitProvider> traitProviders = new ArrayList<>();
    private Map<String, Inventory> inventories = new TreeMap<>();
    private boolean enabled = true;

    private static final String PLAYER_INVENTORY_KEY = "_player_inventory";

    public BolsterEntity(LivingEntity entity) {
        this._entity = entity;

        this.setEnabled(true);
    }

    public LivingEntity getEntity() {
        return this._entity;
    }

    public void setEntity(LivingEntity entity) {
        var inventory = Bukkit.createInventory(null, InventoryType.PLAYER);

        if (entity instanceof Player) {
            inventory = ((Player) entity).getInventory();
        }

        this.setInventory(PLAYER_INVENTORY_KEY, inventory);

        this._entity = entity;
    }

    @Override
    public Properties getTraits() {
        var traits = new Properties();

        for (var traitProvider : this.traitProviders) {
            if (traitProvider instanceof Enableable enableable && !enableable.isEnabled()) continue;

            var next = traitProvider == this ? super.getTraits() : traitProvider.getTraits();

            for (var exProp : next.getAll().keySet()) {
                if (!(exProp instanceof Trait)) continue;

                var trait = (Trait) exProp;
                var exValue = traits.get(trait);
                var nextValue = next.get(trait);

                if (traits.contains(exProp)) {
                    if (exValue instanceof Number && trait.getOperation() != Operation.SET) {
                        var exNumber = (Number) exValue;

                        switch (trait.getOperation()) {
                            case ADD: {
                                exValue = NumberUtil.addNumbers(exNumber, (Number) nextValue);
                                break;
                            }
                            case SUBTRACT: {
                                exValue = NumberUtil.subtractNumbers(exNumber, (Number) nextValue);
                                break;
                            }
                            case MULTIPLY: {
                                exValue = NumberUtil.multiplyNumbers(exNumber, (Number) nextValue);
                                break;
                            }
                            case DIVIDE: {
                                exValue = NumberUtil.divideNumbers(exNumber, (Number) nextValue);
                                break;
                            }
                        }
                    }

                    if (trait.getOperation() == Operation.SET && nextValue != trait.getDefault()) {
                        exValue = nextValue;
                    }
                }
                else {
                    exValue = nextValue;
                }

                traits.set(trait, exValue);
            }
        }

        return traits;
    }

    public Properties getBackingTraits() {
        return super.getTraits();
    }

    @Override
    public <T> void setTrait(Trait<T> key, T value) {
        super.setTrait(key, value);

        addTraitProvider(this);
    }

    public <T> void setTrait(TraitProvider provider, Trait<T> key, T value) {
        provider.setTrait(key, value);

        addTraitProvider(provider);
    }

    @Override
    public <T> T getTrait(Trait<T> key) {
        return this.getTraits().get(key);
    }

    public <T> T getTrait(TraitProvider provider, Trait<T> key) {
        return this.traitProviders.stream().filter(p -> p == provider).findFirst().get().getTrait(key);
    }

    public void addTraitProvider(TraitProvider provider) {
        if (this.traitProviders.contains(provider)) return;

        this.traitProviders.add(provider);
    }

    public void removeTraitProvider(TraitProvider provider) {
        this.traitProviders.remove(provider);
    }

    public List<TraitProvider> getTraitProviders() {
        return this.traitProviders;
    }

    public void setAbsorption(double health) {
        this._entity.setAbsorptionAmount(health);
    }

    public double getAbsorption() {
        return this._entity.getAbsorptionAmount();
    }

    public double getMaxHealth() {
        return this._entity.getMaxHealth();
    }

    public double getHealth() {
        return this._entity.getHealth();
    }

    public void addHealth(double amount) {
        this.addHealth(amount, false);
    }

    public void addHealth(double amount, boolean overheal) {
        var maxHealth = this.getMaxHealth();

        if (amount < 0) amount = maxHealth - this.getHealth();

        var overhealAmount = Math.max(0, amount - maxHealth);

        if (overheal) this._entity.setAbsorptionAmount(this._entity.getAbsorptionAmount() + overhealAmount);

        this.setHealth(Math.min(this.getHealth() + amount, maxHealth));
    }

    public World getWorld() {
        return this._entity.getWorld();
    }

    public String getName() {
        return this._entity.getName();
    }

    public void sendMessage(Component component) {
        this._entity.sendMessage(component);
    }

    public Location getLocation() {
        return this._entity.getLocation();
    }

    public Location getEyeLocation() {
        return this._entity.getEyeLocation();
    }

    public boolean teleport(Location location) {
        return this._entity.teleport(location);
    }

    public boolean isOnline() {
        var online = !(this._entity instanceof Player player) || player.isOnline();

        return online && this._entity.isValid();
    }

    public void setFireTicks(int ticks) {
        this._entity.setFireTicks(ticks);
    }

    public void setHealth(double health) {
        this._entity.setHealth(health);
    }

    public void setMaxHealth(double health) {
        this._entity.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(health);
    }

    public void damage(double damage, Entity source, EntityDamageEvent.DamageCause damageCause) {
        this.getEntity().damage(damage, source);
    }

    public void setVelocity(Vector vector) {
        this._entity.setVelocity(vector);
    }

    public void addStatusEffect(StatusEffect statusEffect) {
        StatusEffectManager.getInstance().addStatusEffect(this._entity, statusEffect);
    }

    public void clearStatusEffect(Class<? extends StatusEffect> statusEffect) {
        this.clearStatusEffect(statusEffect, false);
    }

    public void clearStatusEffect(Class<? extends StatusEffect> statusEffect, boolean force) {
        StatusEffectManager.getInstance().clearStatusEffect(this._entity, statusEffect, force);
    }

    public boolean hasStatusEffect(Class<? extends StatusEffect> statusEffect) {
        return StatusEffectManager.getInstance().hasStatusEffect(this._entity, statusEffect);
    }

    public StatusEffect getStatusEffect(Class<? extends StatusEffect> statusEffect) {
        if (!this.hasStatusEffect(statusEffect)) return null;

        return StatusEffectManager.getInstance().getStatusEffects(this._entity).stream()
                .filter(e -> e.getClass() == statusEffect)
                .findFirst()
                .get();
    }

    public EntityEquipment getEquipment() {
        return this._entity.getEquipment();
    }

    /* PLAYER EXCLUSIVE METHODS */
    public PlayerData getPlayerData() {
        if (!(_entity instanceof Player player)) return new PlayerData();

        return PlayerManager.getInstance().getPlayerData(player);
    }

    public void sendDebugMessage(String message) {
        if (!getPlayerData().getSetting(Settings.DEBUG_MODE)) return;

        getEntity().sendMessage(message);
    }

    public boolean isDrawingBow() {
        if (this._entity.getType() != EntityType.PLAYER) return false;

        return BowTracker.getInstance().isDrawingBow((Player) this.getEntity());
    }

    public void setFoodLevel(int foodLevel) {
        if (!(this._entity instanceof Player player)) return;

        player.setFoodLevel(foodLevel);
    }

    public void playSound(Sound sound, SoundCategory soundCategory, float f, float g) {
        if (!(this._entity instanceof Player player)) return;

        player.playSound(this._entity.getLocation(), sound, soundCategory, f, g);
    }

    public void playSound(Location location, Sound sound, SoundCategory soundCategory, float f, float g) {
        if (!(this._entity instanceof Player player)) return;

        player.playSound(location, sound, soundCategory, f, g);
    }

    public void sendTitle(Component title, Component subtitle, Duration fadeIn, Duration stay, Duration fadeOut) {
        if (!(this._entity instanceof Player player)) return;

        var times = Title.Times.of(fadeIn, stay, fadeOut);
        var titleInstance = Title.title(title, subtitle, times);

        player.showTitle(titleInstance);
    }

    public void sendActionBar(String message) {
        if (!(this._entity instanceof Player player)) return;

        BukkitUtil.sendActionBar((Player) this._entity, message);
    }

    public EntityType getType() {
        return this._entity.getType();
    }

    public UUID getUniqueId() {
        return this._entity.getUniqueId();
    }

    public void updateHealth() {
        this.setMaxHealth(this.getTrait(Traits.MAX_HEALTH));
    }

    public Inventory getPlayerInventory() {
        return this.getInventory(PLAYER_INVENTORY_KEY);
    }

    public void setInventory(String id, Inventory inventory) {
        this.inventories.put(id, inventory);
    }

    public Inventory getInventory(String id) {
        if (!this.inventories.containsKey(id)) return null;

        return this.inventories.get(id);
    }

    public Inventory removeInventory(String id) {
        if (!this.inventories.containsKey(id)) return null;

        return this.inventories.remove(id);
    }

    public boolean hasInventory(String id) {
        return this.inventories.containsKey(id);
    }

    public Collection<Inventory> getInventories() {
        return this.inventories.values();
    }

    public Map<String, Inventory> getInventoryMap() {
        return this.inventories;
    }

    public CompletableFuture<BolsterEntity> moveTo(Location position, Ease ease, Duration duration, double speed) {
        var completableFuture = new CompletableFuture<BolsterEntity>();
        var entity = this.getEntity();
        var startTime = Instant.now();

        var task = new TaskUtil.TaskSeries();

        task.addRepeating(() -> {
            var sinceStart = TimeUtil.toSeconds(Duration.between(startTime, Instant.now()));
            var durationSeconds = TimeUtil.toSeconds(duration);
            var time = sinceStart / durationSeconds;

            if (completableFuture.isCancelled()) {
                if (!task.isCancelled()) task.cancel();
                return;
            }

            entity.teleport(BukkitUtil.lerp(entity.getLocation(), position, ease.getEaseFunction().apply(time, durationSeconds) * speed));
        }, TimeUtil.toTicks(duration), 1);

        task.add(() -> completableFuture.complete(this));

        return completableFuture;
    }

    public void remove() {
        this._entity.remove();
    }

    public void destroy() {
        BukkitUtil.triggerEvent(new EntityDestroyEvent(this));

        this._entity.setHealth(0);
    }

    public static BolsterEntity from(LivingEntity entity) {
        if (entity == null) return null;

        return EntityManager.getInstance().from(entity);
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
