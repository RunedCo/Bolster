package co.runed.bolster.abilities;

public interface ICooldownSource
{
    double getCooldown();

    void setCooldown(double cooldown);

    double getRemainingCooldown();

    boolean isOnCooldown();

    void clearCooldown();
}
