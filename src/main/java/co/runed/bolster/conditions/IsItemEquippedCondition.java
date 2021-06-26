package co.runed.bolster.conditions;

import co.runed.bolster.abilities.AbilityProperties;
import co.runed.bolster.entity.BolsterEntity;
import co.runed.bolster.items.Item;
import co.runed.bolster.managers.ItemManager;
import co.runed.bolster.util.Definition;
import co.runed.bolster.util.properties.Properties;
import co.runed.bolster.util.target.Target;
import org.bukkit.inventory.EquipmentSlot;

import java.util.Collection;
import java.util.EnumSet;

public class IsItemEquippedCondition extends TargetedCondition<BolsterEntity>
{
    Collection<EquipmentSlot> slots;
    Definition<Item> item;

    public IsItemEquippedCondition(Target<BolsterEntity> target, EquipmentSlot slot, Definition<Item> item)
    {
        this(target, EnumSet.of(slot), item);
    }

    public IsItemEquippedCondition(Target<BolsterEntity> target, Collection<EquipmentSlot> slots, Definition<Item> item)
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
                BolsterEntity entity = properties.get(AbilityProperties.CASTER);

                boolean isEquipped = ItemManager.getInstance().isItemEquipped(entity.getBukkit(), this.item, slot);

                if (isEquipped) return true;
            }
        }

        return false;
    }

    @Override
    public void onFail(IConditional conditional, Properties properties, boolean inverted)
    {

    }

    @Override
    public String getErrorMessage(IConditional conditional, Properties properties, boolean inverted)
    {
        return null;
    }
}
