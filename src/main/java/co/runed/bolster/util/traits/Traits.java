package co.runed.bolster.util.traits;

import co.runed.bolster.util.Operation;

public class Traits
{
    public static final Trait<Boolean> DEBUG_MODE = new Trait<>("debug_mode", false, Operation.SET);
    public static final Trait<Double> MAX_HEALTH = new Trait<>("max_health", 0.0d, Operation.ADD);
    public static final Trait<Double> ATTACK_DAMAGE = new Trait<>("attack_damage", 0.0d, Operation.ADD);
}
