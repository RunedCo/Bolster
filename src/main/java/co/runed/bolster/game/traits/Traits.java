package co.runed.bolster.game.traits;

import co.runed.bolster.common.math.Operation;

public class Traits {
    public static final Trait<Boolean> DEBUG_MODE = new Trait<>("debug_mode", false, Operation.SET);
    public static final Trait<Double> COOLDOWN_REDUCTION_PERCENT = new Trait<>("cdr", 0.0d, Operation.ADD);
    public static final Trait<Double> MAX_HEALTH = new Trait<>("max_health", 0.0d, Operation.ADD);
}
