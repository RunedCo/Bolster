package co.runed.bolster.abilities.conditions;

import co.runed.bolster.abilities.Ability;
import co.runed.bolster.abilities.Condition;
import org.bukkit.entity.Player;

public class HasPermissionCondition extends Condition {
    String permission;

    public HasPermissionCondition(String permission) {
        this.permission = permission;
    }

    @Override
    public boolean evaluate(Ability ability, Player player) {
        return player.hasPermission(this.permission);
    }
}
