package co.runed.bolster.game.traits;

import co.runed.bolster.commands.CommandTrait;
import co.runed.bolster.managers.CommandManager;
import co.runed.bolster.util.registries.Registries;
import co.runed.dayroom.math.Operation;

public class Traits {
    public static final Trait<Double> COOLDOWN_REDUCTION_PERCENT = new Trait<>("cdr", 0.0d, Operation.ADD);
    public static final Trait<Double> MAX_HEALTH = new Trait<>("max_health", 0.0d, Operation.ADD);

    public static void initialize() {
        Registries.TRAITS.onRegister(entry -> CommandManager.getInstance().add(new CommandTrait(entry.create())));

        COOLDOWN_REDUCTION_PERCENT.register();
        MAX_HEALTH.register();
    }
}
