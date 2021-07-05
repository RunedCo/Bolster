package co.runed.bolster.game.traits;

import co.runed.bolster.common.math.Operation;

public class Traits
{
    public static final Trait<Boolean> DEBUG_MODE = new Trait<>("debug_mode", false, Operation.SET);

    /* Merlin Traits */
    public static final Trait<Double> COOLDOWN_REDUCTION_PERCENT = new Trait<>("cdr", 0.0d, Operation.ADD);
    public static final Trait<Float> MANA_PER_SECOND = new Trait<>("mana_per_second", 10f, Operation.ADD);
    public static final Trait<Double> SPELL_DAMAGE = new Trait<>("spell_damage", 0.0d, Operation.ADD);

    /* Vanilla Attribute Traits */
    public static final Trait<Double> ATTACK_DAMAGE = new Trait<>("attack_damage", 0.0d, Operation.ADD);
    public static final Trait<Double> ATTACK_SPEED = new Trait<>("attack_speed", 0.0d, Operation.ADD);
    public static final Trait<Double> KNOCKBACK_RESISTANCE = new Trait<>("knockback_resistance", 0.0D, Operation.ADD);
    public static final Trait<Double> KNOCKBACK = new Trait<>("knockback", 0.0d, Operation.ADD);
    public static final Trait<Double> MAX_HEALTH = new Trait<>("max_health", 0.0d, Operation.ADD);
}
