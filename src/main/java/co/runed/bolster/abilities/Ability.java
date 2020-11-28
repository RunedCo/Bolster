package co.runed.bolster.abilities;

import co.runed.bolster.Bolster;
import co.runed.bolster.conditions.*;
import co.runed.bolster.managers.CooldownManager;
import co.runed.bolster.managers.ManaManager;
import co.runed.bolster.util.ICooldownSource;
import co.runed.bolster.util.TaskUtil;
import co.runed.bolster.util.TimeUtil;
import co.runed.bolster.util.properties.Properties;
import co.runed.bolster.wip.cost.Cost;
import co.runed.bolster.wip.cost.ManaCost;
import co.runed.bolster.wip.target.Target;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitTask;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public abstract class Ability implements Listener, IConditional, ICooldownSource
{
    private String parentId = UUID.randomUUID().toString();
    private String id = UUID.randomUUID().toString();
    private String name = null;
    private String description = null;
    private double cooldown = 0;
    private double castTime = 0;
    private float manaCost = 0;
    private Boolean cancelEventOnCast = false;
    private LivingEntity caster;
    private AbilityProvider abilityProvider;
    private AbilityTrigger trigger;
    private boolean showErrors = true;
    private boolean casting = false;
    private boolean cancelled = false;
    private boolean enabled = true;
    private int priority = 0;
    private boolean inProgress = false;

    private int availableCharges = 1;
    private int charges = 1;
    private boolean cancelledByMovement;
    private boolean cancelledByDamage;
    private boolean cancelledByCast;
    private boolean cancelledByDealingDamage;

    BukkitTask castingTask;

    private Duration duration = Duration.ofSeconds(0);

    private final List<Condition.Data> conditions = new ArrayList<>();
    private final List<Cost> costs = new ArrayList<>();

    public Ability()
    {
        Bukkit.getPluginManager().registerEvents(this, Bolster.getInstance());

        this.addCondition(new OffCooldownCondition(Target.CASTER), ConditionPriority.LOWEST);
        this.addCondition(new HasManaCondition(Target.CASTER), ConditionPriority.LOWEST);
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public String getId()
    {
        int chargeId = this.getCharges() - this.getAvailableCharges();

        return (this.getAbilityProvider() == null ? this.parentId : this.getAbilityProvider().getId()) + "." + this.id + (this.getCharges() > 1 ? "." + chargeId : "");
    }

    public String getDescription()
    {
        return this.description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public LivingEntity getCaster()
    {
        return this.caster;
    }

    public void setCaster(LivingEntity caster)
    {
        this.caster = caster;
    }

    public float getManaCost()
    {
        return this.manaCost;
    }

    public void setManaCost(float manaCost)
    {
        this.manaCost = manaCost;
    }

    public void addCost(Cost cost)
    {
        this.costs.add(cost);
    }

    public AbilityTrigger getTrigger()
    {
        return this.trigger;
    }

    public void setTrigger(AbilityTrigger trigger)
    {
        this.trigger = trigger;
    }

    @Override
    public void addCondition(Condition condition, ConditionPriority priority)
    {
        this.conditions.add(new Condition.Data(condition, priority));
    }

    public Duration getDuration()
    {
        return this.duration;
    }

    public void setDuration(Duration duration)
    {
        if (duration == null) return;

        this.duration = duration;
    }

    public boolean shouldCancelEvent()
    {
        return cancelEventOnCast;
    }

    public void setShouldCancelEvent(boolean cancelEventOnCast)
    {
        this.cancelEventOnCast = cancelEventOnCast;
    }

    public void setShouldShowErrorMessages(boolean showErrors)
    {
        this.showErrors = showErrors;
    }

    public boolean shouldShowErrorMessages()
    {
        return this.showErrors;
    }

    public boolean isEnabled()
    {
        return this.enabled;
    }

    public void setEnabled(boolean enabled)
    {
        this.enabled = enabled;
    }

    public boolean isInProgress()
    {
        return this.inProgress;
    }

    public void setInProgress(boolean inProgress)
    {
        this.inProgress = inProgress;
    }

    public void setCastTime(double castTime)
    {
        this.castTime = castTime;
    }

    public double getCastTime()
    {
        return castTime;
    }

    public void setPriority(int priority)
    {
        this.priority = priority;
    }

    public int getPriority()
    {
        return priority;
    }

    public void setCharges(int charges)
    {
        this.charges = charges;
    }

    public int getCharges()
    {
        return charges;
    }

    public void setAvailableCharges(int availableCharges)
    {
        this.availableCharges = availableCharges;
    }

    public int getAvailableCharges()
    {
        return availableCharges;
    }

    public void setCancelledByMovement(boolean cancelledByMovement)
    {
        this.cancelledByMovement = cancelledByMovement;
    }

    public boolean isCancelledByMovement()
    {
        return cancelledByMovement;
    }

    public void setCancelledByDamage(boolean cancelledByDamage)
    {
        this.cancelledByDamage = cancelledByDamage;
    }

    public boolean isCancelledByDamage()
    {
        return cancelledByDamage;
    }

    public void setCancelledByDealingDamage(boolean cancelledByDealingDamage)
    {
        this.cancelledByDealingDamage = cancelledByDealingDamage;
    }

    public boolean isCancelledByDealingDamage()
    {
        return cancelledByDealingDamage;
    }

    public void setCancelledByCast(boolean cancelledByCast)
    {
        this.cancelledByCast = cancelledByCast;
    }

    public boolean isCancelledByCast()
    {
        return cancelledByCast;
    }

    @Override
    public String getCooldownId()
    {
        return this.getId();
    }

    @Override
    public double getCooldown()
    {
        return this.cooldown;
    }

    @Override
    public void setCooldown(double cooldown)
    {
        this.cooldown = cooldown;
    }

    @Override
    public double getRemainingCooldown()
    {
        return CooldownManager.getInstance().getRemainingTime(this.getCaster(), this);
    }

    public void setRemainingCooldown(double cooldown)
    {
        CooldownManager.getInstance().setCooldown(this.getCaster(), this, cooldown);

        if (this.isOnCooldown() && this.getAbilityProvider() != null) this.getAbilityProvider().onToggleCooldown(this);
    }


    @Override
    public void setOnCooldown(boolean onCooldown)
    {
        this.setRemainingCooldown(onCooldown ? this.getCooldown() : 0);
    }

    @Override
    public boolean isOnCooldown()
    {
        return this.getRemainingCooldown() > 0;
    }

    @Override
    public void clearCooldown()
    {
        CooldownManager.getInstance().clearCooldown(this.getCaster(), this);
    }

    public AbilityProvider getAbilityProvider()
    {
        return this.abilityProvider;
    }

    public void setAbilityProvider(AbilityProvider abilityProvider)
    {
        this.abilityProvider = abilityProvider;
    }

    public boolean canActivate(Properties properties)
    {
        if (!properties.contains(AbilityProperties.CASTER)) return false;

        if (this.casting) return false;

        Collections.sort(this.conditions);

        for (Condition.Data data : this.conditions)
        {
            Condition condition = data.condition;

            boolean result = condition.evaluate(this, properties);

            if (!result)
            {
                condition.onFail(this, properties);

                return false;
            }
        }

        List<Cost> costs = new ArrayList<>(this.costs);
        costs.add(new ManaCost(this.getManaCost()));

        // loop through every cost and remove
        for (Cost cost : costs)
        {
            boolean result = cost.run(properties);

            if (!result)
            {
                return false;
            }
        }

        return true;
    }

    public boolean activate(Properties properties)
    {
        if (this.canActivate(properties))
        {
            if (this.getCastTime() > 0)
            {
                this.casting = true;

                long updatePeriod = 5L;
                long castTimeTicks = (long) (this.getCastTime() * 20L);
                AtomicLong repeats = new AtomicLong();

                if (this.getCaster() instanceof Player)
                {
                    Player player = (Player) this.getCaster();

                    this.castingTask = TaskUtil.runDurationTaskTimer(Bolster.getInstance(),
                            () -> {
                                repeats.addAndGet(updatePeriod);

                                float xpPercent = (repeats.floatValue() / (float) castTimeTicks);

                                player.setExp(Math.min(xpPercent, 0.999f));
                                player.setLevel(0);
                            },
                            TimeUtil.fromSeconds(this.getCastTime()), 0L, updatePeriod,
                            () -> {
                                if (!this.cancelled)
                                {
                                    this.setInProgress(true);

                                    this.onActivate(properties);
                                    this.onPostActivate(properties);
                                }
                                ManaManager.getInstance().updateManaDisplay(player);
                            });
                }
            }
            else
            {
                this.setInProgress(true);

                this.onActivate(properties);
                this.onPostActivate(properties);
            }

            return true;
        }

        return false;
    }

    public abstract void onActivate(Properties properties);

    public void onPostActivate(Properties properties)
    {
        this.setOnCooldown(true);

        this.casting = false;
        this.cancelled = false;
        this.castingTask = null;
        this.setInProgress(false);

        if (properties.get(AbilityProperties.EVENT) != null)
        {
            Event event = properties.get(AbilityProperties.EVENT);

            if (event instanceof Cancellable && this.shouldCancelEvent())
            {
                ((Cancellable) event).setCancelled(true);
            }
        }
    }

    public void destroy()
    {
        HandlerList.unregisterAll(this);

        //CooldownManager.getInstance().clearCooldown(this.getCaster(), this);
    }
}


