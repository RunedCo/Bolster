package co.runed.bolster.abilities.conditions;

import co.runed.bolster.Bolster;
import co.runed.bolster.abilities.AbilityProperties;
import co.runed.bolster.conditions.Condition;
import co.runed.bolster.conditions.IConditional;
import co.runed.bolster.conditions.TargetedCondition;
import co.runed.bolster.items.Item;
import co.runed.bolster.util.properties.Properties;
import co.runed.bolster.util.target.Target;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EquipmentSlot;

import java.util.Collection;
import java.util.EnumSet;

public class ItemEquippedCondition extends TargetedCondition<LivingEntity>
{
    Collection<EquipmentSlot> slots;
    Class<? extends Item> item;

    public ItemEquippedCondition(Target<LivingEntity> target, EquipmentSlot slot, Class<? extends Item> item)
    {
        this(target, EnumSet.of(slot), item);
    }

    public ItemEquippedCondition(Target<LivingEntity> target, Collection<EquipmentSlot> slots, Class<? extends Item> item)
    {
        super(target);

        this.slots = slots;
        this.item = item;
    }

    @Override
    public boolean evaluate(IConditional conditional, Properties properties)
    {
        for (EquipmentSlot slot : this.slots)
        {
            if (this.item != null)
            {
                LivingEntity entity = properties.get(AbilityProperties.CASTER);

                boolean isEquipped = Bolster.getItemManager().isItemEquipped(entity, this.item, slot);

                if (isEquipped) return true;
            }
        }

        return false;
    }

    @Override
    public void onFail(IConditional conditional, Properties properties)
    {

    }
}
