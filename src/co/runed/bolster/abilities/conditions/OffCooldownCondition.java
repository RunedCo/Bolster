package co.runed.bolster.abilities.conditions;

import co.runed.bolster.abilities.Ability;
import co.runed.bolster.abilities.Condition;
import org.bukkit.entity.Player;

public class OffCooldownCondition extends Condition {
    @Override
    public boolean evaluate(Ability ability, Player player) {
        return !ability.isOnCooldown();
    }
}
