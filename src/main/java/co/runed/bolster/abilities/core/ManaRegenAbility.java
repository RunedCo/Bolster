package co.runed.bolster.abilities.core;

import co.runed.bolster.BolsterEntity;
import co.runed.bolster.abilities.TargetedAbility;
import co.runed.bolster.game.Traits;
import co.runed.bolster.managers.ManaManager;
import co.runed.bolster.util.properties.Properties;
import co.runed.bolster.wip.target.Target;

public class ManaRegenAbility extends TargetedAbility<BolsterEntity>
{
    public ManaRegenAbility()
    {
        super(Target.CASTER);

        this.setCooldown(0.5d);
    }

    @Override
    public void onActivate(Properties properties)
    {
        BolsterEntity target = this.getTarget().get(properties);

        ManaManager.getInstance().addCurrentMana(target.getBukkit(), target.getTrait(Traits.MANA_PER_SECOND) / 2);
    }
}
