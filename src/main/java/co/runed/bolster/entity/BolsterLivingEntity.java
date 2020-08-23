package co.runed.bolster.entity;

import co.runed.bolster.classes.BolsterClass;
import co.runed.bolster.status.StatusEffect;
import co.runed.bolster.util.PlayerUtil;
import co.runed.bolster.util.properties.Properties;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BolsterLivingEntity<T extends LivingEntity> extends BolsterEntity<T>
{
    BolsterClass bolsterClass;
    Properties properties = new Properties();

    List<StatusEffect> statusEffects = new ArrayList<>();

    public BolsterLivingEntity(T entity)
    {
        super(entity);
    }

    public Properties getProperties()
    {
        return this.properties;
    }

    /* STATUS EFFECTS */
    public void addStatusEffect(StatusEffect effect)
    {
        this.clearStatusEffect(effect.getClass());

        List<StatusEffect> entityEffects = this.statusEffects;

        entityEffects.add(effect);
        effect.start(entity);
    }

    public void clearStatusEffect(Class<? extends StatusEffect> statusEffect)
    {
        for (StatusEffect effect : this.statusEffects)
        {
            if (effect.getClass() == statusEffect)
            {
                effect.clear();
                this.statusEffects.remove(effect);
                return;
            }
        }
    }

    public boolean hasStatusEffect(Class<? extends StatusEffect> statusEffect)
    {
        for (StatusEffect effect : this.statusEffects)
        {
            if (effect.getClass() == statusEffect)
            {
                return true;
            }
        }

        return false;
    }

    /* CLASSES */
    public void setBolsterClass(BolsterClass bolsterClass)
    {
        if(this.bolsterClass != null) this.bolsterClass.destroy();

        if(bolsterClass != null) this.bolsterClass = bolsterClass;
    }

    public BolsterClass getBolsterClass()
    {
        return this.bolsterClass;
    }

    public void clearBolsterClass()
    {
        this.setBolsterClass(null);
    }
}
