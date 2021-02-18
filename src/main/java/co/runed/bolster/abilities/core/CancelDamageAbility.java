package co.runed.bolster.abilities.core;

import co.runed.bolster.abilities.targeted.ModifyDamageAbility;
import co.runed.bolster.util.Operation;

public class CancelDamageAbility extends ModifyDamageAbility
{
    public CancelDamageAbility()
    {
        super(Operation.SET, 0);
    }
}
