package co.runed.bolster.abilities.conditions;

import co.runed.bolster.Bolster;
import co.runed.bolster.abilities.Ability;
import co.runed.bolster.items.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class HoldingItemCondition extends Condition {
    Item item;

    public HoldingItemCondition(Item item) {
        this.item = item;
    }

    @Override
    public boolean evaluate(Ability ability, LivingEntity caster) {
        if(this.item == null) return false;

        return Bolster.getItemManager().isEntityHolding(caster, this.item);
    }
}
