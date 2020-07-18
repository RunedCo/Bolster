package co.runed.bolster.abilities.conditions;

import co.runed.bolster.Bolster;
import co.runed.bolster.abilities.Ability;
import co.runed.bolster.abilities.AbilityProperties;
import co.runed.bolster.items.Item;
import co.runed.bolster.properties.Properties;
import org.bukkit.entity.LivingEntity;

public class HoldingItemCondition extends Condition
{
    Item item;

    public HoldingItemCondition(Item item)
    {
        this.item = item;
    }

    @Override
    public boolean evaluate(Ability ability, Properties properties)
    {
        if (this.item == null) return false;

        LivingEntity entity = properties.get(AbilityProperties.CASTER);

        return Bolster.getItemManager().isEntityHolding(entity, this.item);
    }
}
