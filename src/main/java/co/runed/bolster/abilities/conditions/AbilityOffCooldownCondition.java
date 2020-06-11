package co.runed.bolster.abilities.conditions;

import co.runed.bolster.abilities.Ability;
import co.runed.bolster.abilities.PassiveAbility;
import co.runed.bolster.util.PlayerUtil;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class AbilityOffCooldownCondition extends Condition {
    @Override
    public boolean evaluate(Ability ability, LivingEntity caster) {
        return !ability.isOnCooldown();
    }

    //TODO: REMOVE HARDCODED MESSAGE
    @Override
    public void onFail(Ability ability, LivingEntity entity) {
        if(ability instanceof PassiveAbility) return;

        if(entity.getType() == EntityType.PLAYER) {
            PlayerUtil.sendActionBar((Player)entity, "Ability on cooldown (" + ability.getRemainingCooldown() +" seconds remaining)");
        }
        //player.sendMessage("Ability on cooldown (" + ability.getRemainingCooldown() +" seconds remaining)");
    }
}
