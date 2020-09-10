package co.runed.bolster.game;

import co.runed.bolster.util.properties.Property;

public class Traits
{
    public static final Property<Double> COOLDOWN_REDUCTION_MULTIPLIER = new Property<>("cdr", 1.0D);
    public static final Property<Float> MANA_PER_SECOND = new Property<>("mana_per_second", 10f);
    public static final Property<Long> SHIELD_DURATION_TICKS = new Property<>("shield_duration", 20L);
    public static final Property<Boolean> SHRINE_IMMUNE = new Property<>("shrine_immune", false);
}
