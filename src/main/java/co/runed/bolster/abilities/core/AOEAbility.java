package co.runed.bolster.abilities.core;

import co.runed.bolster.abilities.Ability;
import co.runed.bolster.abilities.MultiTargetAbility;
import co.runed.bolster.util.BukkitUtil;

public class AOEAbility extends MultiTargetAbility
{
    double radius;

    public AOEAbility(Ability ability, double radius)
    {
        super(null, ability);

        this.radius = radius;

        this.setEntitySupplier(() -> BukkitUtil.getEntitiesRadius(this.getCaster().getLocation(), radius));
    }
}
