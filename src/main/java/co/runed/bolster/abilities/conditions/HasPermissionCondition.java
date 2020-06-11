package co.runed.bolster.abilities.conditions;

import co.runed.bolster.abilities.Ability;
import org.bukkit.entity.LivingEntity;

public class HasPermissionCondition extends Condition {
    String permission;

    public HasPermissionCondition(String permission) {
        this.permission = permission;
    }

    @Override
    public boolean evaluate(Ability ability, LivingEntity caster) {
        return caster.hasPermission(this.permission);
    }
}
