package co.runed.bolster;

import co.runed.bolster.classes.BolsterClass;
import co.runed.bolster.managers.ClassManager;
import co.runed.bolster.managers.EntityManager;
import co.runed.bolster.managers.StatusEffectManager;
import co.runed.bolster.status.StatusEffect;
import co.runed.bolster.util.NumberUtil;
import co.runed.bolster.util.Operation;
import co.runed.bolster.util.properties.Properties;
import co.runed.bolster.util.properties.Property;
import co.runed.bolster.util.traits.Trait;
import co.runed.bolster.util.traits.TraitProvider;
import co.runed.bolster.util.traits.Traits;
import co.runed.bolster.v1_16_R3.CraftUtil;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.Inventory;
import org.bukkit.util.Vector;

import java.util.*;

public class BolsterEntity extends TraitProvider
{
    private LivingEntity _entity;
    List<TraitProvider> traitProviders = new ArrayList<>();
    HashMap<String, Inventory> additionalInventories = new HashMap<>();

    public BolsterEntity(LivingEntity entity)
    {
        this._entity = entity;
    }

    public BolsterClass getBolsterClass()
    {
        return ClassManager.getInstance().getClass(this._entity);
    }

    public void setBolsterClass(BolsterClass bolsterClass)
    {
        ClassManager.getInstance().setClass(this._entity, bolsterClass);
    }

    public LivingEntity getBukkit()
    {
        return this._entity;
    }

    public void setBukkit(LivingEntity entity)
    {
        this._entity = entity;
    }

    public Properties getTraits()
    {
        Properties traits = new Properties();

        for (TraitProvider traitProvider : this.traitProviders)
        {
            if (!traitProvider.isEnabled()) continue;

            Properties next = traitProvider == this ? super.getTraits() : traitProvider.getTraits();

            for (Property exProp : next.getAll().keySet())
            {
                if (!(exProp instanceof Trait)) continue;

                Trait trait = (Trait) exProp;
                Object exValue = traits.get(trait);
                Object nextValue = next.get(trait);

                if (traits.contains(exProp))
                {
                    if (exValue instanceof Number && trait.getOperation() != Operation.SET)
                    {
                        Number exNumber = (Number) exValue;

                        switch (trait.getOperation())
                        {
                            case ADD:
                            {
                                exValue = NumberUtil.addNumbers(exNumber, (Number) nextValue);
                                break;
                            }
                            case SUBTRACT:
                            {
                                exValue = NumberUtil.subtractNumbers(exNumber, (Number) nextValue);
                                break;
                            }
                            case MULTIPLY:
                            {
                                exValue = NumberUtil.multiplyNumbers(exNumber, (Number) nextValue);
                                break;
                            }
                            case DIVIDE:
                            {
                                exValue = NumberUtil.divideNumbers(exNumber, (Number) nextValue);
                                break;
                            }
                        }
                    }

                    if (trait.getOperation() == Operation.SET && nextValue != trait.getDefault())
                    {
                        exValue = nextValue;
                    }
                }
                else
                {
                    exValue = nextValue;
                }

                traits.set(trait, exValue);
            }
        }

        return traits;
    }

    @Override
    public <T> void setTrait(Trait<T> key, T value)
    {
        super.setTrait(key, value);

        if (!this.traitProviders.contains(this)) this.traitProviders.add(this);
    }

    public <T> void setTrait(TraitProvider provider, Trait<T> key, T value)
    {
        provider.setTrait(key, value);

        if (!this.traitProviders.contains(provider)) this.traitProviders.add(provider);
    }

    @Override
    public <T> T getTrait(Trait<T> key)
    {
        return this.getTraits().get(key);
    }

    public <T> T getTrait(TraitProvider provider, Trait<T> key)
    {
        return this.traitProviders.stream().filter(p -> p == provider).findFirst().get().getTrait(key);
    }

    public void addTraitProvider(TraitProvider provider)
    {
        if (this.traitProviders.contains(provider)) return;

        this.traitProviders.add(provider);
    }

    public void removeTraitProvider(TraitProvider provider)
    {
        this.traitProviders.remove(provider);
    }

    public List<TraitProvider> getTraitProviders()
    {
        return this.traitProviders;
    }

    public void setAbsorption(double health)
    {
        this._entity.setAbsorptionAmount(health);
    }

    public double getAbsorption()
    {
        return this._entity.getAbsorptionAmount();
    }

    public double getMaxHealth()
    {
        return this._entity.getMaxHealth();
    }

    public double getHealth()
    {
        return this._entity.getHealth();
    }

    public void addHealth(double amount)
    {
        this.addHealth(amount, false);
    }

    public void addHealth(double amount, boolean overheal)
    {
        double maxHealth = this.getMaxHealth();

        if (amount < 0) amount = maxHealth - this.getHealth();

        double overhealAmount = Math.max(0, amount - maxHealth);

        if (overheal) this._entity.setAbsorptionAmount(this._entity.getAbsorptionAmount() + overhealAmount);

        this.setHealth(Math.min(this.getHealth() + amount, maxHealth));
    }

    public World getWorld()
    {
        return this._entity.getWorld();
    }

    public String getName()
    {
        return this._entity.getName();
    }

    public void sendMessage(String string)
    {
        this._entity.sendMessage(string);
    }

    public Location getLocation()
    {
        return this._entity.getLocation();
    }

    public Location getEyeLocation()
    {
        return this._entity.getEyeLocation();
    }

    public boolean teleport(Location location)
    {
        return this._entity.teleport(location);
    }

    public boolean isOnline()
    {
        boolean online = this._entity.getType() != EntityType.PLAYER || ((Player) this._entity).isOnline();

        return online && this._entity.isValid();
    }

    public void setFireTicks(int ticks)
    {
        this._entity.setFireTicks(ticks);
    }

    public void setHealth(double health)
    {
        this._entity.setHealth(health);
    }

    public void setMaxHealth(double health)
    {
        this._entity.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(health);
    }

    public void damage(double damage, Entity source, EntityDamageEvent.DamageCause damageCause)
    {
        this.getBukkit().damage(damage, source);
    }

    public void setVelocity(Vector vector)
    {
        this._entity.setVelocity(vector);
    }

    public void addStatusEffect(StatusEffect statusEffect)
    {
        StatusEffectManager.getInstance().addStatusEffect(this._entity, statusEffect);
    }

    public void clearStatusEffect(Class<? extends StatusEffect> statusEffect)
    {
        StatusEffectManager.getInstance().clearStatusEffect(this._entity, statusEffect);
    }

    public boolean hasStatusEffect(Class<? extends StatusEffect> statusEffect)
    {
        return StatusEffectManager.getInstance().hasStatusEffect(this._entity, statusEffect);
    }

    public StatusEffect getStatusEffect(Class<? extends StatusEffect> statusEffect)
    {
        if (!this.hasStatusEffect(statusEffect)) return null;

        return StatusEffectManager.getInstance().getStatusEffects(this._entity).stream()
                .filter(e -> e.getClass() == statusEffect)
                .findFirst()
                .get();
    }

    public EntityEquipment getEquipment()
    {
        return this._entity.getEquipment();
    }

    /* PLAYER EXCLUSIVE METHODS */

    public void setFoodLevel(int foodLevel)
    {
        if (this._entity.getType() != EntityType.PLAYER) return;

        ((Player) this._entity).setFoodLevel(foodLevel);
    }

    public void playSound(Sound sound, SoundCategory soundCategory, float f, float g)
    {
        if (this._entity.getType() != EntityType.PLAYER) return;

        ((Player) this._entity).playSound(this._entity.getLocation(), sound, soundCategory, f, g);
    }

    public void playSound(Location location, Sound sound, SoundCategory soundCategory, float f, float g)
    {
        if (this._entity.getType() != EntityType.PLAYER) return;

        ((Player) this._entity).playSound(location, sound, soundCategory, f, g);
    }

    public void sendTitle(String title, String subtitle, int fadeIn, int stay, int fadeOut)
    {
        if (this._entity.getType() != EntityType.PLAYER) return;

        ((Player) this._entity).sendTitle(title, subtitle, fadeIn, stay, fadeOut);
    }

    public void sendActionBar(String message)
    {
        if (this._entity.getType() != EntityType.PLAYER) return;

        CraftUtil.sendActionBar((Player) this._entity, message);
    }

    public EntityType getType()
    {
        return this._entity.getType();
    }

    public UUID getUniqueId()
    {
        return this._entity.getUniqueId();
    }

    public void updateHealth()
    {
        this.setMaxHealth(this.getTrait(Traits.MAX_HEALTH));
    }

    public void addAdditionalInventory(String id, Inventory inventory)
    {
        this.additionalInventories.put(id, inventory);
    }

    public Inventory getAdditionalInventory(String id)
    {
        if (!this.additionalInventories.containsKey(id)) return null;

        return this.additionalInventories.get(id);
    }

    public Collection<Inventory> getAdditionalInventories()
    {
        return this.additionalInventories.values();
    }

    public void destroy()
    {
        if (this.getBolsterClass() != null)
        {
            this.getBolsterClass().destroy();
        }

        this._entity.setHealth(0);
    }

    @Override
    public boolean equals(Object obj)
    {
        //TODO OVERRIDE EQUALS FUNCTION
        return super.equals(obj);
    }

    public static BolsterEntity from(LivingEntity entity)
    {
        return EntityManager.getInstance().from(entity);
    }
}
