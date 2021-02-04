package co.runed.bolster.game;

import co.runed.bolster.util.Operation;
import co.runed.bolster.util.traits.Trait;

public class Traits
{
    public static final Trait<Double> COOLDOWN_REDUCTION_PERCENT = new Trait<>("cdr", 0.0D, Operation.ADD);
    public static final Trait<Float> MANA_PER_SECOND = new Trait<>("mana_per_second", 10f, Operation.ADD);
}
