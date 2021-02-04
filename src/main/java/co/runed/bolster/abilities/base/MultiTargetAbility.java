package co.runed.bolster.abilities.base;

import co.runed.bolster.abilities.Ability;
import co.runed.bolster.abilities.AbilityProperties;
import co.runed.bolster.util.properties.Properties;
import org.bukkit.entity.Entity;

import java.util.Collection;
import java.util.function.Supplier;

public class MultiTargetAbility extends MultiAbility
{
    Supplier<Collection<Entity>> entitySupplier;

    public MultiTargetAbility(Supplier<Collection<Entity>> entitySupplier, Ability ability)
    {
        this.entitySupplier = entitySupplier;

        this.addAbility(ability);
    }

    public void setEntitySupplier(Supplier<Collection<Entity>> entitySupplier)
    {
        this.entitySupplier = entitySupplier;
    }

    @Override
    public String getDescription()
    {
        if (super.getDescription() != null) return super.getDescription();

        String desc = "";

        for (Ability ability : this.abilities)
        {
            if (ability.getDescription() == null) continue;

            desc += ability.getDescription() + "\n";
        }

        return desc;
    }

    @Override
    public void onActivate(Properties properties)
    {
        Collection<Entity> entities = entitySupplier.get();

        for (Entity entity : entities)
        {
            Properties newProperties = new Properties(properties);
            newProperties.set(AbilityProperties.TARGET, entity);

            super.onActivate(newProperties);
        }
    }
}
