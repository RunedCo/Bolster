package co.runed.bolster.abilities.base;

import co.runed.bolster.entity.BolsterEntity;
import co.runed.bolster.util.BukkitUtil;
import co.runed.bolster.util.target.Target;

public class InFrontOfAbility extends MultiTargetAbility
{
    Target<BolsterEntity> target;
    float distance;

    public InFrontOfAbility(Target<BolsterEntity> target, float distance)
    {
        super(null);

        this.target = target;
        this.distance = distance;

        this.setEntityFunction((properties) -> BukkitUtil.getEntitiesInFrontOf(this.target.get(properties).getBukkit(), this.distance));
    }
}
