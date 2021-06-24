package co.runed.bolster.abilities.targeted;

import co.runed.bolster.abilities.TargetedAbility;
import co.runed.bolster.entity.BolsterEntity;
import co.runed.bolster.util.properties.Properties;
import co.runed.bolster.util.target.Target;
import co.runed.bolster.v1_16_R3.CraftUtil;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageEvent;

public class DamageEntityAbility extends TargetedAbility<BolsterEntity>
{
    double damage;
    EntityDamageEvent.DamageCause cause;

    public DamageEntityAbility(Target<BolsterEntity> target, double damage, EntityDamageEvent.DamageCause cause)
    {
        super(target);

        this.damage = damage;
        this.cause = cause;
    }

    @Override
    public void onActivate(Properties properties)
    {
        LivingEntity target = this.getTarget().get(properties).getBukkit();

        CraftUtil.damageEntity(target, damage, this.getCaster(), this.cause);
    }
}
