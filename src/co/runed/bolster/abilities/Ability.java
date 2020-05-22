package co.runed.bolster.abilities;

import co.runed.bolster.Bolster;
import co.runed.bolster.abilities.conditions.AbilityOffCooldownCondition;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import java.util.HashSet;
import java.util.UUID;

public abstract class Ability implements Listener {
    private String cooldownSource = UUID.randomUUID().toString();

    private long cooldown = 0;
    private double manaCost = 0;
    private Player caster;

    private final HashSet<ConditionData> conditions = new HashSet<>();

    public Ability() {
        Bolster.getInstance().getServer().getPluginManager().registerEvents(this, Bolster.getInstance());

        this.addCondition(new AbilityOffCooldownCondition());
    }

    public void loadConfig(ConfigurationSection config) {
        this.setCooldown(config.getLong("cooldown", 0));
    }

    public boolean canActivate() {
        for (ConditionData data : this.conditions) {
            Condition condition = data.condition;

            boolean result = condition.evaluate(this, this.getCaster());

            if(result != data.result) return false;
        }

        return true;
    }

    public boolean activate() {
        if(this.canActivate()) {
            this.onActivate();

            Bolster.getCooldownManager().setCooldown(this.getCaster(), this.cooldownSource, this.getCooldown());

            this.onPostActivate();
            return true;
        }

        return false;
    }

    public abstract void onActivate();

    public void onPostActivate() {

    }

    public String getCooldownSource() {
        return this.cooldownSource;
    }

    public void setCooldownSource(String source) {
        this.cooldownSource = source;
    }

    public long getCooldown() {
        return this.cooldown;
    }

    public void setCooldown(long cooldown) {
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

    public Player getCaster() {
        return this.caster;
    }

    public void setCaster(Player caster) {
        this.caster = caster;
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


