package co.runed.bolster;

import co.runed.bolster.classes.BolsterClass;
import co.runed.bolster.managers.ClassManager;
import co.runed.bolster.managers.EntityManager;
import co.runed.bolster.managers.StatusEffectManager;
import co.runed.bolster.status.StatusEffect;
import co.runed.bolster.util.PlayerUtil;
import co.runed.bolster.wip.TraitProvider;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BolsterEntity extends TraitProvider
{
    private LivingEntity _entity;
    List<TraitProvider> traitProviders = new ArrayList<>();

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

    /* public Properties getTraits()
    {
        Properties traits = new Properties();

        for (TraitProvider traitProvider : this.traitProviders)
        {
            Properties existing = traitProvider.getTraits();

            for (Property exProp : existing.getAll().keySet())
            {
                if (traits.contains(exProp))
                {
                    Object exValue = traits.get(exProp);

                    if (exValue instanceof Number)
                    {
                        //traits.set(exProp, );
                    }
                }
            }
        }

        return traits;
    }

    @Override
    public <T> void setTrait(Property<T> key, T value)
    {
        super.setTrait(key, value);

        if (!this.traitProviders.contains(this)) this.traitProviders.add(this);
    }

    public <T> void setTrait(TraitProvider provider, Property<T> key, T value)
    {
        provider.setTrait(key, value);

        if (!this.traitProviders.contains(provider)) this.traitProviders.add(provider);
    }

    @Override
    public <T> T getTrait(Property<T> key)
    {
        return this.getTraits().get(key);
    }

    public <T> T getTrait(TraitProvider provider, Property<T> key)
    {
        return this.getTraits().get(key);
    } */

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
        double maxHealth = this.getMaxHealth();

        if (amount < 0) amount = maxHealth - this.getHealth();

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

        PlayerUtil.sendActionBar((Player) this._entity, message);
    }

    public EntityType getType()
    {
        return this._entity.getType();
    }

    public UUID getUniqueId()
    {
        return this._entity.getUniqueId();
    }

    public void destroy()
    {
        if (this.getBolsterClass() != null)
        {
            this.getBolsterClass().destroy();
        }

        this._entity.setHealth(0);
    }

    public static BolsterEntity from(LivingEntity entity)
    {
        return EntityManager.getInstance().from(entity);
    }
}
