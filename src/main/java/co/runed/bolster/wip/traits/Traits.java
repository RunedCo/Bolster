package co.runed.bolster.wip.traits;

public class Traits
{
    public static final Trait<Boolean> DEBUG_MODE = new Trait<>("debug_mode", false, Trait.Operation.SET);
    public static final Trait<Double> MAX_HEALTH = new Trait<>("max_health", 0.0d, Trait.Operation.ADD);
}
