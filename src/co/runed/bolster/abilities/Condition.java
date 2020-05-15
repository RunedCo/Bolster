package co.runed.bolster.abilities;

import org.bukkit.entity.Player;

public abstract class Condition {
    public abstract boolean evaluate(Ability ability, Player player);
}

