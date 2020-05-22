package co.runed.bolster.abilities.conditions;

import co.runed.bolster.Bolster;
import co.runed.bolster.abilities.Ability;
import co.runed.bolster.abilities.Condition;
import co.runed.bolster.items.Item;
import org.bukkit.entity.Player;

public class HoldingItemCondition extends Condition {
    Item item;

    public HoldingItemCondition(Item item) {
        this.item = item;
    }

    @Override
    public boolean evaluate(Ability ability, Player player) {
        return Bolster.getItemManager().isPlayerHolding(player, this.item);
    }
}
