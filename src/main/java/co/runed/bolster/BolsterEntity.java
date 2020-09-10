package co.runed.bolster;

import co.runed.bolster.classes.BolsterClass;
import co.runed.bolster.managers.ClassManager;
import co.runed.bolster.managers.EntityManager;
import co.runed.bolster.managers.StatusEffectManager;
import co.runed.bolster.status.StatusEffect;
import co.runed.bolster.util.PlayerUtil;
import co.runed.bolster.util.properties.Properties;
import co.runed.bolster.util.properties.Property;
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

import java.util.UUID;

public class BolsterEntity
{
    private LivingEntity _entity;

    private final Properties traits;

    public BolsterEntity(LivingEntity entity)
    {
        this._entity = entity;
        this.traits = new Properties();
    }

    public BolsterClass getBolsterClass()
    {
        return ClassManager.getInstance().getClass(this._entity);
    }

    public void setBolsterClass(BolsterClass bolsterClass)
    {
        ClassManager.getInstance().setClass(this._entity, bolsterClass);
    }

    public Properties getTraits()
    {
        return traits;
    }

    public <T> void setTrait(Property<T> key, T value)
    {
        this.getTraits().set(key, value);
    }

    public LivingEntity getBukkit()
    {
        return this._entity;
    }

    public void setBukkit(LivingEntity entity)
    {
        this._entity = entity;
    }

    public void setAbsorption(double health)
    {
        this._entity.setAbsorptionAmount(health);
    }

    public double getAbsorption()
    {
        return this._entity.getAbsorptionAmount();
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

    public <T> T getTrait(Property<T> key)
    {
        return this.getTraits().get(key);
    }

}
