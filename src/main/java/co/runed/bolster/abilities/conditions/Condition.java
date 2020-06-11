package co.runed.bolster.abilities.conditions;

import co.runed.bolster.abilities.Ability;
import org.bukkit.entity.LivingEntity;

public abstract class Condition {
    public abstract boolean evaluate(Ability ability, LivingEntity caster);

    public void onFail(Ability ability, LivingEntity entity) {

    }
}

