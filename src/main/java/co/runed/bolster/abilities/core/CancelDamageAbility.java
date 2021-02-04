package co.runed.bolster.abilities.core;

import co.runed.bolster.abilities.targeted.SetDamageAbility;
import co.runed.bolster.util.Operation;

public class CancelDamageAbility extends SetDamageAbility
{
    public CancelDamageAbility()
    {
        super(0, Operation.SET);
    }
}
