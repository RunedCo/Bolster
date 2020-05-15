package co.runed.bolster.abilities.conditions;

import co.runed.bolster.abilities.Ability;
import co.runed.bolster.abilities.Condition;
import co.runed.bolster.items.Item;
import co.runed.bolster.items.ItemAbilitySlot;
import org.bukkit.entity.Player;

public class ItemOffCooldownCondition extends Condition {
    Item item;
    ItemAbilitySlot slot;

    public ItemOffCooldownCondition(Item item, ItemAbilitySlot slot) {
        this.item = item;
        this.slot = slot;
    }

    @Override
    public boolean evaluate(Ability ability, Player player) {
        return false;
    }
}
