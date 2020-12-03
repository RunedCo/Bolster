package co.runed.bolster.abilities;

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
