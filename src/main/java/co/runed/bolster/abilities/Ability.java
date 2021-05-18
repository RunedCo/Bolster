package co.runed.bolster.abilities;

import co.runed.bolster.Bolster;
import co.runed.bolster.abilities.base.DynamicParameterAbility;
import co.runed.bolster.abilities.base.FunctionAbility;
import co.runed.bolster.conditions.*;
import co.runed.bolster.managers.CooldownManager;
import co.runed.bolster.managers.ManaManager;
import co.runed.bolster.util.TaskUtil;
import co.runed.bolster.util.cooldown.ICooldownSource;
import co.runed.bolster.util.properties.Properties;
import co.runed.bolster.util.target.Target;
import co.runed.bolster.game.cost.Cost;
import co.runed.bolster.game.cost.ManaCost;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BiConsumer;
import java.util.function.Function;

public abstract class Ability implements Listener, IConditional<Ability>, ICooldownSource<Ability>
{
    private static final long CAST_BAR_UPDATE_TICKS = 5L;

    private final String parentId = UUID.randomUUID().toString();
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
    private boolean evaluateConditions = true;
    private boolean useCosts = true;
    private int priority = 0;
    private boolean inProgress = false;
    private boolean skipIfCancelled = false;

    private int charges = 1;
    private boolean cancelledByMovement = false;
    private boolean cancelledByTakingDamage = false;
    private boolean cancelledByCast = false;
    private boolean cancelledByDealingDamage = false;
    private long lastErrorTime = 0;

    TaskUtil.TaskSeries castingTask;

    private Duration duration = Duration.ofSeconds(0);

    private final List<Condition.Data> conditions = new ArrayList<>();
    private final List<Cost> costs = new ArrayList<>();
    private final List<Ability> children = new ArrayList<>();

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

    public Ability setName(String name)
    {
        this.name = name;

        return this;
    }

    public Ability setId(String id)
    {
        this.id = id;

        return this;
    }

    public String getId()
    {
        return (this.getAbilityProvider() == null ? this.parentId : this.getAbilityProvider().getId()) + "." + this.id;
    }

    public String getDescription()
    {
        return this.description;
    }

    public Ability setDescription(String description)
    {
        this.description = description;

        return this;
    }

    public LivingEntity getCaster()
    {
        return this.caster;
    }

    public Ability setCaster(LivingEntity caster)
    {
        this.caster = caster;

        for (Ability ability : this.getChildren())
        {
            ability.setCaster(caster);
        }

        return this;
    }

    public float getManaCost()
    {
        return this.manaCost;
    }

    public Ability setManaCost(float manaCost)
    {
        this.manaCost = manaCost;

        return this;
    }

    public Ability addCost(Cost cost)
    {
        this.costs.add(cost);

        return this;
    }

    public Ability addAbility(BiConsumer<LivingEntity, Properties> func)
    {
        return this.addAbility(new FunctionAbility(func));
    }

    public Ability addAbility(Function<Properties, Ability> func)
    {
        return this.addAbility(new DynamicParameterAbility(func));
    }

    public Ability addAbility(Ability ability)
    {
        ability.setShouldShowErrorMessages(false);

        if (this.getCaster() != null) ability.setCaster(this.getCaster());

        this.children.add(ability);

        return this;
    }

    public Ability removeAbility(Ability ability)
    {
        ability.destroy();

        this.children.remove(ability);

        return this;
    }

    public AbilityTrigger getTrigger()
    {
        return this.trigger;
    }

    public Ability setTrigger(AbilityTrigger trigger)
    {
        this.trigger = trigger;

        for (Ability ability : this.children)
        {
            ability.setTrigger(trigger);
        }

        return this;
    }

    @Override
    public Ability addCondition(Condition condition, ConditionPriority priority)
    {
        this.conditions.add(new Condition.Data(condition, priority));

        return this;
    }

    public Ability setSkipIfCancelled(boolean skipIfCancelled)
    {
        this.skipIfCancelled = skipIfCancelled;

        return this;
    }

    public boolean shouldSkipIfCancelled()
    {
        return skipIfCancelled;
    }

    public Duration getDuration()
    {
        // TODO find highest duration from all children and return
        // TODO if sequence add all together
        return this.duration;
    }

    public Ability setDuration(Duration duration)
    {
        if (duration == null) return this;

        this.duration = duration;

        return this;
    }

    public boolean shouldCancelEvent()
    {
        return cancelEventOnCast;
    }

    public Ability setShouldCancelEvent(boolean cancelEventOnCast)
    {
        this.cancelEventOnCast = cancelEventOnCast;

        return this;
    }

    @Override
    public Ability setShouldShowErrorMessages(boolean showErrors)
    {
        this.showErrors = showErrors;

        return this;
    }

    @Override
    public boolean shouldShowErrorMessages()
    {
        return this.showErrors && !this.getTrigger().isPassive();
    }

    public boolean isEnabled()
    {
        return this.enabled;
    }

    public Ability setEnabled(boolean enabled)
    {
        this.enabled = enabled;

        return this;
    }

    public boolean isInProgress()
    {
        if (this.inProgress) return true;

        for (Ability ability : this.getChildren())
        {
            if (ability.isInProgress())
            {
                return true;
            }
        }

        return false;
    }

    public void setInProgress(boolean inProgress)
    {
        this.inProgress = inProgress;
    }

    public Ability setCastTime(double castTime)
    {
        this.castTime = castTime;

        return this;
    }

    public double getCastTime()
    {
        return castTime;
    }

    public Ability setPriority(int priority)
    {
        this.priority = priority;

        return this;
    }

    public int getPriority()
    {
        return priority;
    }

    public Ability setCharges(int charges)
    {
        this.charges = charges;

        for (Ability ability : this.children)
        {
            ability.setCharges(charges);
        }

        return this;
    }

    public int getCharges()
    {
        return charges;
    }

    public int getLowestCooldownCharge()
    {
        int charge = 0;
        double lowest = this.getCooldown();

        for (int i = 0; i < this.getCharges(); i++)
        {
            double remaining = CooldownManager.getInstance().getRemainingTime(this.getCaster(), this, i);

            if (remaining < lowest)
            {
                lowest = remaining;
                charge = i;

                if (lowest <= 0) break;
            }
        }

        return charge;
    }

    public Ability setCancelledByMovement(boolean cancelledByMovement)
    {
        this.cancelledByMovement = cancelledByMovement;

        return this;
    }

    public boolean isCancelledByMovement()
    {
        return cancelledByMovement;
    }

    public Ability setCancelledByTakingDamage(boolean cancelledByDamage)
    {
        this.cancelledByTakingDamage = cancelledByDamage;

        return this;
    }

    public boolean isCancelledByTakingDamage()
    {
        return cancelledByTakingDamage;
    }

    public Ability setCancelledByDealingDamage(boolean cancelledByDealingDamage)
    {
        this.cancelledByDealingDamage = cancelledByDealingDamage;

        return this;
    }

    public boolean isCancelledByDealingDamage()
    {
        return cancelledByDealingDamage;
    }

    public Ability setCancelledByCast(boolean cancelledByCast)
    {
        this.cancelledByCast = cancelledByCast;

        return this;
    }

    public boolean isCancelledByCast()
    {
        return cancelledByCast;
    }

    public Ability setEvaluateConditions(boolean evaluateConditions)
    {
        this.evaluateConditions = evaluateConditions;

        return this;
    }

    public boolean shouldEvaluateConditions()
    {
        return evaluateConditions;
    }

    public Ability setUseCosts(boolean useCosts)
    {
        this.useCosts = useCosts;

        return this;
    }

    public boolean shouldUseCosts()
    {
        return useCosts;
    }

    public List<Ability> getChildren()
    {
        return new ArrayList<>(children);
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
    public Ability setCooldown(double cooldown)
    {
        this.cooldown = cooldown;

        return this;
    }

    @Override
    public double getRemainingCooldown()
    {
        return CooldownManager.getInstance().getRemainingTime(this.getCaster(), this, this.getLowestCooldownCharge());
    }

    @Override
    public void setRemainingCooldown(double cooldown)
    {
        CooldownManager.getInstance().setCooldown(this.getCaster(), this, this.getLowestCooldownCharge(), cooldown);
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
        CooldownManager.getInstance().clearCooldown(this.getCaster(), this, this.getLowestCooldownCharge());
    }

    public void clearAllCooldowns()
    {
        for (int i = 0; i < this.getCharges(); i++)
        {
            CooldownManager.getInstance().clearCooldown(this.getCaster(), this, i);
        }
    }

    @Override
    public void onToggleCooldown()
    {
        // todo set all slots on 0.2s cooldown after one is used

        if (this.getAbilityProvider() != null) this.getAbilityProvider().onToggleCooldown(this);
    }

    public AbilityProvider getAbilityProvider()
    {
        return this.abilityProvider;
    }

    public void setAbilityProvider(AbilityProvider abilityProvider)
    {
        this.abilityProvider = abilityProvider;
    }

    public void cancel()
    {
        this.cancelled = true;
        this.setInProgress(false);
        if (this.castingTask != null) this.castingTask.cancel();
        this.setInProgress(false);
    }

    public boolean evaluateConditions(Properties properties)
    {
        Collections.sort(this.conditions);

        for (Condition.Data data : this.conditions)
        {
            Condition condition = data.condition;

            boolean result = condition.evaluate(this, properties);

            if (!result)
            {
                condition.onFail(this, properties, false);

                if (this.shouldShowErrorMessages())
                {
                    String error = condition.getErrorMessage(this, properties, false);

                    if (error != null && System.currentTimeMillis() - this.lastErrorTime >= 750)
                    {
                        this.getCaster().sendMessage(error);
                        //BolsterEntity.from(this.getCaster()).sendActionBar(error);
                        this.lastErrorTime = System.currentTimeMillis();
                    }
                }

                return false;
            }
        }

        return true;
    }

    public boolean evaluateCosts(Properties properties)
    {
        List<Cost> costs = new ArrayList<>(this.costs);
        costs.add(new ManaCost(this.getManaCost()));

        // loop through every cost and remove
        for (Cost cost : costs)
        {
            boolean result = cost.evaluate(properties);

            if (!result)
            {
                return false;
            }
        }

        return true;
    }

    public boolean canActivate(Properties properties)
    {
        if (!properties.contains(AbilityProperties.CASTER)) return false;
        if (this.getAbilityProvider() != null && !this.getAbilityProvider().isEnabled()) return false;
        if (!this.isEnabled()) return false;

        if (this.casting) return false;

        if (this.shouldEvaluateConditions() && !this.evaluateConditions(properties)) return false;
        return !this.shouldUseCosts() || this.evaluateCosts(properties);
    }

    public boolean applyCosts(Properties properties)
    {
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
            if (this.shouldUseCosts() && !this.applyCosts(properties)) return false;

            // TODO change system for cast time
            if (this.getCastTime() > 0)
            {
                this.casting = true;
                this.setInProgress(true);

                long castTimeTicks = (long) (this.getCastTime() * 20L);
                AtomicLong repeats = new AtomicLong();

                if (this.getCaster() instanceof Player)
                {
                    Player player = (Player) this.getCaster();

                    this.castingTask = new TaskUtil.TaskSeries()
                            .addRepeating(
                                    () -> {
                                        repeats.addAndGet(CAST_BAR_UPDATE_TICKS);

                                        float xpPercent = (repeats.floatValue() / (float) castTimeTicks);

                                        player.setExp(Math.min(xpPercent, 0.999f));
                                        player.setLevel(0);
                                    }, castTimeTicks, CAST_BAR_UPDATE_TICKS)
                            .add(() -> this.preActivate(properties))
                            .onCancel(() -> this.preActivate(properties));
                }

                return true;
            }

            this.preActivate(properties);
            return true;
        }

        return false;
    }

    public void activateAbilityAndChildren(Properties properties)
    {
        this.onActivate(properties);

        for (Ability ability : this.getChildren())
        {
            ability.activate(properties);
        }
    }

    private void preActivate(Properties properties)
    {
        if (this.getCastTime() > 0 && this.getCaster() instanceof Player)
        {
            ManaManager.getInstance().updateManaDisplay((Player) this.getCaster());
        }

        if (!this.cancelled)
        {
            this.setInProgress(true);

            this.activateAbilityAndChildren(properties);

            this.onPostActivate(properties);
        }

        this.casting = false;
        this.cancelled = false;
        this.castingTask = null;
        this.setInProgress(false);
    }

    public abstract void onActivate(Properties properties);

    public void onPostActivate(Properties properties)
    {
        this.setOnCooldown(true);

        if (properties.get(AbilityProperties.EVENT) != null)
        {
            Event event = properties.get(AbilityProperties.EVENT);

            if (event instanceof Cancellable && this.shouldCancelEvent())
            {
                ((Cancellable) event).setCancelled(true);
            }
        }
    }

    @Override
    public String toString()
    {
        return this.getClass() + " (id: " + this.getId() + ", cooldown id: " + this.getCooldownId() + ", " + this.getName() + ")";
    }

    public void destroy()
    {
        HandlerList.unregisterAll(this);

        for (Ability child : this.getChildren())
        {
            child.destroy();
        }

        this.children.clear();
    }
}


