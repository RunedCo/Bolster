package co.runed.bolster.abilities.conditions;

import co.runed.bolster.abilities.Ability;
import co.runed.bolster.items.Item;
import co.runed.bolster.items.ItemAction;
import co.runed.bolster.properties.Properties;

public class ItemOffCooldownCondition extends Condition {
    Item item;
    ItemAction slot;

    public ItemOffCooldownCondition(Item item, ItemAction slot) {
        this.item = item;
        this.slot = slot;
    }

    @Override
    public boolean evaluate(Ability ability, Properties properties) {
        return false;
    }
}
