package co.runed.bolster.abilities.conditions;

import co.runed.bolster.Bolster;
import co.runed.bolster.abilities.Ability;
import co.runed.bolster.items.Item;
import org.bukkit.entity.LivingEntity;

public class HoldingItemCondition extends Condition {
    Item item;

    public HoldingItemCondition(Item item) {
        this.item = item;
    }

    @Override
    public boolean evaluate(Ability ability, LivingEntity caster) {
        return Bolster.getItemManager().isEntityHolding(caster, this.item);
    }
}
