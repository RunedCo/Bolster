package co.runed.bolster.util.cooldown;

import co.runed.bolster.managers.CooldownManager;
import co.runed.bolster.util.Owned;

public interface CooldownSource<T> extends Owned {
    String getCooldownId();

    double getCooldown();

    T cooldown(double cooldown);

    void onToggleCooldown();

    boolean isGlobalCooldown();

    default void setRemainingCooldown(double cooldown) {
        CooldownManager.getInstance().setCooldown(getOwner(), this, 0, cooldown, true, isGlobalCooldown());
    }

    default double getRemainingCooldown() {
        return CooldownManager.getInstance().getRemainingTime(getOwner(), this, 0);
    }

    default void setOnCooldown(boolean onCooldown) {
        CooldownManager.getInstance().setCooldown(getOwner(), this, 0, getCooldown(), true, isGlobalCooldown());
    }

    default boolean isOnCooldown() {
        return this.getRemainingCooldown() > 0;
    }

    default void clearCooldown() {
        CooldownManager.getInstance().clearCooldown(getOwner(), this, 0);
    }
}
