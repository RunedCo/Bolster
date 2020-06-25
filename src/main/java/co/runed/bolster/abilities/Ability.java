package co.runed.bolster.abilities;

import co.runed.bolster.Bolster;
import co.runed.bolster.abilities.conditions.AbilityOffCooldownCondition;
import co.runed.bolster.abilities.conditions.Condition;
import co.runed.bolster.abilities.conditions.HasManaCondition;
import co.runed.bolster.abilities.properties.AbilityProperties;
import co.runed.bolster.properties.Properties;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import java.util.HashSet;
import java.util.UUID;

public abstract class Ability implements Listener {
    private final String id = UUID.randomUUID().toString();
    private String description;
    private double cooldown = 0;
    private float manaCost = 0;
    private LivingEntity caster;
    private IAbilitySource abilitySource;

    private final HashSet<ConditionData> conditions = new HashSet<>();

    public Ability() {
        Bolster.getInstance().getServer().getPluginManager().registerEvents(this, Bolster.getInstance());

        this.addCondition(new AbilityOffCooldownCondition());
        this.addCondition(new HasManaCondition());
    }

    public String getId() {
        return this.getAbilitySource().getId() + "." + this.id;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LivingEntity getCaster() {
        return this.caster;
    }

    public void setCaster(LivingEntity caster) {
        this.caster = caster;
    }

    public float getManaCost() {
        return this.manaCost;
    }

    public void setManaCost(float manaCost) {
        this.manaCost = manaCost;
    }

    public IAbilitySource getAbilitySource() {
        return this.abilitySource;
    }

    public void setAbilitySource(IAbilitySource abilitySource) {
        this.abilitySource = abilitySource;
    }

    public void addCondition(Condition condition) {
        this.addCondition(condition, true);
    }

    public void addCondition(Condition condition, boolean result) {
        this.conditions.add(new ConditionData(condition, result));
    }

    public double getCooldown() {
        return this.cooldown;
    }

    public void setCooldown(double cooldownSeconds) {
        this.cooldown = cooldownSeconds;
    }

    public double getRemainingCooldown() {
        return Bolster.getCooldownManager().getRemainingTime(this.getCaster(), this.id);
    }

    public boolean isOnCooldown() {
        return this.getRemainingCooldown() > 0;
    }

    public void clearCooldown() {
        Bolster.getCooldownManager().clearCooldown(this.getCaster(), this.id);
    }

    public boolean canActivate(Properties properties) {
        if(!properties.contains(AbilityProperties.CASTER)) return false;

        for (ConditionData data : this.conditions) {
            Condition condition = data.condition;

            boolean result = condition.evaluate(this, properties);

            if(result != data.result) {
                condition.onFail(this, properties);

                return false;
            }
        }

        return true;
    }

    public boolean activate(Properties properties) {
        if(this.canActivate(properties)) {
            this.onActivate(properties);

            Bolster.getCooldownManager().setCooldown(this.getCaster(), this.id, this.getCooldown());

            this.onPostActivate(properties);

            Bolster.getManaManager().addCurrentMana(this.getCaster(), -this.getManaCost());

            return true;
        }

        return false;
    }

    public abstract void onActivate(Properties properties);

    public void onPostActivate(Properties properties) {

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


