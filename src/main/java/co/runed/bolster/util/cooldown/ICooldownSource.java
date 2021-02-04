package co.runed.bolster.util.cooldown;

public interface ICooldownSource<T>
{
    String getCooldownId();

    double getCooldown();

    T setCooldown(double cooldown);

    void setRemainingCooldown(double cooldown);

    double getRemainingCooldown();

    void setOnCooldown(boolean onCooldown);

    boolean isOnCooldown();

    void clearCooldown();

    void onToggleCooldown();
}
