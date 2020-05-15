package co.runed.bolster.abilities;

import co.runed.bolster.Bolster;
import co.runed.bolster.abilities.conditions.OffCooldownCondition;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import java.util.HashSet;
import java.util.UUID;

public abstract class Ability implements Listener {
    private final UUID uuid = UUID.randomUUID();

    private final String id;
    private long cooldown = 0;
    private double manaCost = 0;
    private Player caster;

    private final HashSet<ConditionData> conditions = new HashSet<>();

    public Ability(String id) {
        this.id = id;

        Bolster.getInstance().getServer().getPluginManager().registerEvents(this, Bolster.getInstance());

        this.addCondition(new OffCooldownCondition());
    }

    public String getId() {
        return this.id;
    }

    public long getCooldown() {
        return this.cooldown;
    }

    public void setCooldown(long cooldown) {
        this.cooldown = cooldown;
    }

    public long getRemainingCooldown() {
        return Bolster.getCooldownManager().getRemainingTime(this.getCaster(), this.uuid.toString());
    }

    public boolean isOnCooldown() {
        return this.getRemainingCooldown() > 0;
    }

    public void clearCooldown() {
        Bolster.getCooldownManager().clearCooldown(this.getCaster(), this.uuid.toString());
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

            Bolster.getCooldownManager().setCooldown(this.getCaster(), this.uuid.toString(), this.getCooldown());
            return true;
        }

        return false;
    }

    public abstract void onActivate();

    public void remove() {
        HandlerList.unregisterAll(this);
    }

    private static class ConditionData {
        Condition condition;
        boolean result = true;

        public ConditionData(Condition condition, boolean result) {
            this.condition = condition;
            this.result = result;
        }
    }
}


