package co.runed.bolster.abilities.core;

import co.runed.bolster.Bolster;
import co.runed.bolster.abilities.TargetedAbility;
import co.runed.bolster.game.BolsterEntity;
import co.runed.bolster.util.properties.Properties;
import co.runed.bolster.util.target.Target;
import org.bukkit.entity.LivingEntity;

public class ManaRegenAbility extends TargetedAbility<BolsterEntity>
{
    float manaPerSecond = 0;

    public ManaRegenAbility(float manaPerSecond)
    {
        super(Target.CASTER);

        this.setCooldown(0.5d);

        this.manaPerSecond = manaPerSecond;
    }

    @Override
    public void onActivate(Properties properties)
    {
        Bolster.getManaManager().addCurrentMana(this.getTarget().get(properties).getBukkit(), this.manaPerSecond / 2);
    }

    public float getManaPerSecond()
    {
        return this.manaPerSecond;
    }

    public void setManaPerSecond(float manaPerSecond)
    {
        this.manaPerSecond = manaPerSecond;
    }
}
