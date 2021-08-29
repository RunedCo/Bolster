package co.runed.bolster.damage;

import org.bukkit.entity.LivingEntity;

import java.time.Duration;

public class DamageInfo
{
    double damage;
    DamageType damageType = DamageType.PRIMARY;
    LivingEntity attacker;
    DamageSource damageSource;
    Duration duration = null;

    public DamageInfo(double damage)
    {
        this.damage = damage;
    }

    public DamageInfo withType(DamageType type)
    {
        this.damageType = type;

        return this;
    }

    public DamageInfo withAttacker(LivingEntity attacker)
    {
        this.attacker = attacker;

        return this;
    }

    public DamageInfo withSource(DamageSource damageSource)
    {
        this.damageSource = damageSource;

        return this;
    }

    public DamageInfo withDuration(Duration duration)
    {
        this.duration = duration;

        return this;
    }

    public DamageInfo withFrequency(long frequency)
    {

        return this;
    }

    public DamageInfo apply(LivingEntity target)
    {
        target.damage(damage);

        return this;
    }
}
