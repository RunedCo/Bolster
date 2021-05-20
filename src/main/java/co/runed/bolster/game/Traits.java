package co.runed.bolster.game;

import co.runed.bolster.math.Operation;
import co.runed.bolster.game.traits.Trait;

public class Traits
{
    public static final Trait<Boolean> DEBUG_MODE = new Trait<>("debug_mode", false, Operation.SET);
    public static final Trait<Double> MAX_HEALTH = new Trait<>("max_health", 0.0d, Operation.ADD);
    public static final Trait<Double> ATTACK_DAMAGE = new Trait<>("attack_damage", 0.0d, Operation.ADD);
    public static final Trait<Double> COOLDOWN_REDUCTION_PERCENT = new Trait<>("cdr", 0.0D, Operation.ADD);
    public static final Trait<Float> MANA_PER_SECOND = new Trait<>("mana_per_second", 10f, Operation.ADD);
}
