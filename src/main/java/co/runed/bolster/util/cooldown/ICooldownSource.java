package co.runed.bolster.util.cooldown;

public interface ICooldownSource
{
    String getCooldownId();

    double getCooldown();

    void setCooldown(double cooldown);

    void setRemainingCooldown(double cooldown);

    double getRemainingCooldown();

    void setOnCooldown(boolean onCooldown);

    boolean isOnCooldown();

    void clearCooldown();

    void onToggleCooldown();
}
