package co.runed.bolster.abilities;

public interface ICooldownSource
{
    double getCooldown();

    void setCooldown(double cooldown);

    double getRemainingCooldown();

    void setOnCooldown(boolean onCooldown);

    boolean isOnCooldown();

    void clearCooldown();
}
