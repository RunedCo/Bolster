package co.runed.bolster.abilities.conditions;

import co.runed.bolster.abilities.Ability;
import co.runed.bolster.abilities.PassiveAbility;
import co.runed.bolster.abilities.properties.AbilityProperties;
import co.runed.bolster.properties.Properties;
import co.runed.bolster.util.PlayerUtil;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class AbilityOffCooldownCondition extends Condition {
    @Override
    public boolean evaluate(Ability ability, Properties properties) {
        return !ability.isOnCooldown();
    }

    //TODO: REMOVE HARDCODED MESSAGE
    @Override
    public void onFail(Ability ability, Properties properties) {
        if(ability instanceof PassiveAbility) return;

        LivingEntity entity = properties.get(AbilityProperties.CASTER);

        if(entity.getType() == EntityType.PLAYER) {
            PlayerUtil.sendActionBar((Player) entity, "Ability on cooldown (" + (int) ability.getRemainingCooldown() +" seconds remaining)");
        }
        //player.sendMessage("Ability on cooldown (" + ability.getRemainingCooldown() +" seconds remaining)");
    }
}
