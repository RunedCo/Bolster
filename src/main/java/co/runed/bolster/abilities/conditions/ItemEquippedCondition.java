package co.runed.bolster.abilities.conditions;

import co.runed.bolster.Bolster;
import co.runed.bolster.abilities.Ability;
import co.runed.bolster.abilities.AbilityProperties;
import co.runed.bolster.items.Item;
import co.runed.bolster.properties.Properties;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EquipmentSlot;

import java.util.Collection;
import java.util.EnumSet;

public class ItemEquippedCondition extends Condition
{
    Collection<EquipmentSlot> slots;
    Item item;

    public ItemEquippedCondition(EquipmentSlot slot, Item item)
    {
        this(EnumSet.of(slot), item);
    }

    public ItemEquippedCondition(Collection<EquipmentSlot> slots, Item item)
    {
        this.slots = slots;
        this.item = item;
    }

    @Override
    public boolean evaluate(Ability ability, Properties properties)
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
    public void onFail(Ability ability, Properties properties)
    {

    }
}
