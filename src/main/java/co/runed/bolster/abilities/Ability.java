package co.runed.bolster.abilities;

import co.runed.bolster.Bolster;
import co.runed.bolster.abilities.conditions.AbilityOffCooldownCondition;
import co.runed.bolster.abilities.conditions.Condition;
import co.runed.bolster.abilities.conditions.HasManaCondition;
import co.runed.bolster.properties.Properties;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import java.util.HashSet;
import java.util.UUID;

public abstract class Ability implements Listener {
    private String cooldownSource = UUID.randomUUID().toString();

    private long cooldown = 0;
    private float manaCost = 0;
    private LivingEntity caster;

    private final HashSet<ConditionData> conditions = new HashSet<>();

    public Ability() {
        Bolster.getInstance().getServer().getPluginManager().registerEvents(this, Bolster.getInstance());

        this.addCondition(new AbilityOffCooldownCondition());
        this.addCondition(new HasManaCondition());
    }

    public void loadConfig(ConfigurationSection config) {
        this.setTotalCooldown(config.getLong("cooldown", 0));
    }

    public boolean canActivate() {
        if(this.getCaster() == null) return false;

        for (ConditionData data : this.conditions) {
            Condition condition = data.condition;

            boolean result = condition.evaluate(this, this.getCaster());

            if(result != data.result) {
                condition.onFail(this, this.getCaster());

                return false;
            }
        }

        return true;
    }

    public boolean activate(Properties properties) {
        if(this.canActivate()) {
            this.onActivate(properties);

            Bolster.getCooldownManager().setCooldown(this.getCaster(), this.cooldownSource, this.getTotalCooldown());

            this.onPostActivate(properties);

            Bolster.getManaManager().addCurrentMana(this.getCaster(), -this.getManaCost());

            return true;
        }

        return false;
    }

    public abstract void onActivate(Properties properties);

    public void onPostActivate(Properties properties) {

    }

    public String getCooldownSource() {
        return this.cooldownSource;
    }

    public void setCooldownSource(String source) {
        this.cooldownSource = source;
    }

    public long getTotalCooldown() {
        return this.cooldown;
    }

    public void setTotalCooldown(long cooldown) {
        this.cooldown = cooldown;
    }

    public long getRemainingCooldown() {
        return Bolster.getCooldownManager().getRemainingTime(this.getCaster(), this.cooldownSource);
    }

    public boolean isOnCooldown() {
        return this.getRemainingCooldown() > 0;
    }

    public void clearCooldown() {
        Bolster.getCooldownManager().clearCooldown(this.getCaster(), this.cooldownSource);
    }

    public LivingEntity getCaster() {
        return this.caster;
    }

    public void setCaster(LivingEntity caster) {
        this.caster = caster;
    }

    public float getManaCost() {
        return manaCost;
    }

    public void setManaCost(float manaCost) {
        this.manaCost = manaCost;
    }

    public void addCondition(Condition condition) {
        this.addCondition(condition, true);
    }

    public void addCondition(Condition condition, boolean result) {
        this.conditions.add(new ConditionData(condition, result));
    }

    public void destroy() {
        HandlerList.unregisterAll(this);
    }

    private static class ConditionData {
        Condition condition;
        boolean result;

        public ConditionData(Condition condition, boolean result) {
            this.condition = condition;
            this.result = result;
        }
    }
}


