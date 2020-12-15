package co.runed.bolster.conditions;

import co.runed.bolster.BolsterEntity;
import co.runed.bolster.abilities.AbilityProperties;
import co.runed.bolster.items.Item;
import co.runed.bolster.managers.ItemManager;
import co.runed.bolster.util.properties.Properties;
import co.runed.bolster.util.target.Target;
import org.bukkit.inventory.EquipmentSlot;

import java.util.Collection;
import java.util.EnumSet;

public class ItemEquippedCondition extends TargetedCondition<BolsterEntity>
{
    Collection<EquipmentSlot> slots;
    Class<? extends Item> item;

    public ItemEquippedCondition(Target<BolsterEntity> target, EquipmentSlot slot, Class<? extends Item> item)
    {
        this(target, EnumSet.of(slot), item);
    }

    public ItemEquippedCondition(Target<BolsterEntity> target, Collection<EquipmentSlot> slots, Class<? extends Item> item)
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
    public void onFail(IConditional conditional, Properties properties)
    {

    }
}
