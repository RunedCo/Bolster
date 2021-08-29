package co.runed.bolster.wip;

import co.runed.bolster.util.TimeUtil;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

public class PotionSystem implements Listener {
    Map<UUID, Map<PotionEffectType, Boolean>> blockNextRemoval = new HashMap<>();
    List<PotionEffectContainer> activeEffects = new ArrayList<>();
    List<PotionEffectContainer> pendingEffects = new ArrayList<>();

    private static PotionSystem _instance;

    public PotionSystem() {
        _instance = this;
    }

    public static PotionEffectContainer addPotionEffect(LivingEntity entity, PotionEffect effect) {
        var container = _instance.addEffectToQueue(entity, effect);
        // block next removal event for this type and entity

        _instance.setBlockNextRemoval(entity, effect.getType(), true);

        // then remove current effect of that type
        entity.removePotionEffect(effect.getType());

        _instance.update(entity, effect.getType());

        return container;
    }

    public static void removeExactPotionEffect(LivingEntity entity, PotionEffectContainer effect) {
        _instance.removeExactPotionEffectInternal(entity, effect.activeEffect);
    }

    public static void removeExactPotionEffect(LivingEntity entity, PotionEffect effect) {
        _instance.removeExactPotionEffectInternal(entity, effect);
    }

    private void removeExactPotionEffectInternal(LivingEntity entity, PotionEffect effect) {
        //PotionEffectContainer active = this.getActiveEffect(entity, effect.getType());

        this.activeEffects.removeIf(e -> e != null && e.owner == entity.getUniqueId() && e.is(effect));
        this.pendingEffects.removeIf(e -> e != null && e.owner == entity.getUniqueId() && e.is(effect));

        // block next removal event for this type and entity
        this.setBlockNextRemoval(entity, effect.getType(), true);

        // then remove current effect of that type
        entity.removePotionEffect(effect.getType());

        this.update(entity, effect.getType());
    }

    private void setBlockNextRemoval(LivingEntity entity, PotionEffectType type, boolean block) {
        var uuid = entity.getUniqueId();

        this.blockNextRemoval.putIfAbsent(uuid, new HashMap<>());
        this.blockNextRemoval.get(uuid).put(type, block);
    }

    private boolean shouldBlockNextRemoval(LivingEntity entity, PotionEffectType type) {
        var uuid = entity.getUniqueId();

        return this.blockNextRemoval.containsKey(uuid) && this.blockNextRemoval.get(uuid).containsKey(type) && this.blockNextRemoval.get(uuid).get(type);
    }

    private void setActiveEffect(LivingEntity entity, PotionEffectContainer effect) {
        this.clearActiveEffect(entity, effect.type);
        this.activeEffects.add(effect);
    }

    private PotionEffectContainer getActiveEffect(LivingEntity entity, PotionEffectType type) {
        return activeEffects.stream().filter(e -> e.owner == entity.getUniqueId() && e.type.equals(type)).findFirst().orElse(null);
    }

    private void clearActiveEffect(LivingEntity entity, PotionEffectType type) {
        this.activeEffects.remove(this.getActiveEffect(entity, type));
    }

    private PotionEffectContainer getNextEffect(LivingEntity entity, PotionEffectType type) {
        return this.getEffectQueue(entity, type).stream().findFirst().orElse(null);
    }

    private PotionEffectContainer addEffectToQueue(LivingEntity entity, PotionEffect newEffect) {
        var container = new PotionEffectContainer(entity.getUniqueId(), newEffect);

        this.pendingEffects.add(container);

        return container;
    }

    private List<PotionEffectContainer> getEffectQueue(LivingEntity entity, PotionEffectType type) {
        return pendingEffects.stream()
                .filter(e -> e != null && e.type.equals(type) && e.owner == entity.getUniqueId())
                .sorted((e1, e2) -> e2.duration - e1.duration)
                .sorted((e1, e2) -> e2.amplifier - e1.amplifier)
                .collect(Collectors.toList());
    }

    private void update(LivingEntity entity, PotionEffectType type) {
        var nextEffect = this.getNextEffect(entity, type);
        var activeEffect = this.getActiveEffect(entity, type);
        var effectToAdd = activeEffect == null || (nextEffect != null && nextEffect.amplifier > activeEffect.amplifier) ? nextEffect : activeEffect;

        if (effectToAdd != null) {
            if (effectToAdd != activeEffect) {
                this.pendingEffects.add(activeEffect);
                this.pendingEffects.remove(nextEffect);

                this.setActiveEffect(entity, effectToAdd);
            }

            effectToAdd.start(entity);
        }

        //entity.sendMessage("NEXT: " + nextEffect + " ACTIVE: " + activeEffect);
    }

    private void clear(LivingEntity entity, PotionEffectType type) {
        this.pendingEffects.removeIf(e -> e.type.equals(type) && e.owner == entity.getUniqueId());

        this.clearActiveEffect(entity, type);
    }

    // TODO MAKE SURE WORKS
    /*
        TRY THIS:
        WHEN ADD POTION EFFECT, CLEAR EFFECT TYPE AND ADD TO PENDING EFFECTS, THEN READD EXISTING (OR NEW)
        WHEN ADD EVENT IS TRIGGERED AGAIN FOR THE READD CHECK IF IS IN ACTIVE EFFECTS ALREADY IF SO RETURN
        THEN ON REMOVE ADD NEXT POTION EFFECT
     */
    @EventHandler
    private void onPotionAdded(EntityPotionEffectEvent event) {
        if (!(event.getEntity() instanceof LivingEntity)) return;

        var cause = event.getCause();
        var action = event.getAction();
        var entity = (LivingEntity) event.getEntity();

        var oldEffect = event.getOldEffect();
        var newEffect = event.getNewEffect();
        var type = oldEffect == null ? newEffect.getType() : oldEffect.getType();

        pendingEffects.removeIf(e -> e == null || e.getRemainingDurationTicks() <= 0);

        // WHEN EFFECT ADDED USE DEFAULT BEHAVIOUR (+ LOGGING)
        if (action == EntityPotionEffectEvent.Action.ADDED) {
            //event.getEntity().sendMessage(ChatColor.GREEN + "ADDED " + newEffect + " O: " + oldEffect);

            if (this.getEffectQueue(entity, type).size() <= 0 && this.getActiveEffect(entity, type) == null) {
                this.setActiveEffect(entity, new PotionEffectContainer(entity.getUniqueId(), newEffect));
            }
        }

        // WHEN EFFECT CHANGED (AN EFFECT IS ADDED WHILE AN EXISTING EFFECT IS IN PROGRESS), ADD NEW EFFECT TO QUEUE, REMOVE POTION EFFECTS OF THAT TYPE, AND UPDATE
        if (action == EntityPotionEffectEvent.Action.CHANGED) {
            //entity.sendMessage(ChatColor.YELLOW + "CHANGED " + oldEffect + " N: " + newEffect);

            // when changed add to pending
            this.addEffectToQueue(entity, newEffect);

            // block next removal event for this type and entity
            this.setBlockNextRemoval(entity, type, true);

            // then remove current effect of that type
            entity.removePotionEffect(type);

            // and update current effect
            this.update(entity, type);
        }

        // WHEN EFFECT REMOVED BY EXPIRATION, UPDATE
        // TODO check whether removePotionEffect triggers this or clear (pretty sure it is this)
        if (action == EntityPotionEffectEvent.Action.REMOVED) {
            //entity.sendMessage(ChatColor.RED + "REMOVED " + oldEffect + " N: " + newEffect);

            if (cause == EntityPotionEffectEvent.Cause.PLUGIN && this.shouldBlockNextRemoval(entity, type)) {
                this.setBlockNextRemoval(entity, type, false);
                return;
            }

            // clear active effect if it is effect that expired
            this.clearActiveEffect(entity, type);

            // and update current effect
            this.update(entity, type);
        }

        if (action == EntityPotionEffectEvent.Action.CLEARED) {
            //entity.sendMessage(ChatColor.AQUA + "CLEARED " + oldEffect + " N: " + newEffect);

            // when clear event triggered always clear both pending and active
            this.clear(entity, type);
        }
    }

    public static class PotionEffectContainer {
        UUID owner;

        public PotionEffectType type;
        public int duration;
        public int amplifier;
        public boolean ambient;
        public boolean particles;
        public boolean icon;

        Instant startTime;
        PotionEffect baseEffect;
        PotionEffect activeEffect;

        public PotionEffectContainer(UUID owner, PotionEffect effect) {
            this.owner = owner;
            this.type = effect.getType();
            this.duration = effect.getDuration();
            this.amplifier = effect.getAmplifier();
            this.ambient = effect.isAmbient();
            this.particles = effect.hasParticles();
            this.icon = effect.hasIcon();

            this.startTime = Instant.now();
            this.baseEffect = effect;
        }

        public Duration getRemainingDuration() {
            if (this.duration == Integer.MAX_VALUE) return Duration.ofSeconds(Long.MAX_VALUE);

            var sinceStart = Duration.between(startTime, Instant.now());
            var remaining = TimeUtil.fromSeconds((double) (this.duration) / 20d).minus(sinceStart);
            return remaining.isNegative() ? Duration.ZERO : remaining;
        }

        public long getRemainingDurationMs() {
            if (this.duration == Integer.MAX_VALUE) return this.duration;

            return this.getRemainingDuration().toMillis();
        }

        public long getRemainingDurationTicks() {
            if (this.duration == Integer.MAX_VALUE) return this.duration;

            return TimeUtil.toTicks(this.getRemainingDuration());
        }

        public void start(LivingEntity entity) {
            this.activeEffect = this.toEffect();

//            this.startTime = Instant.now();
//            this.duration = this.activeEffect.getDuration();

            entity.addPotionEffect(this.activeEffect);
        }

        public PotionEffect toEffect() {
            return new PotionEffect(type, (int) this.getRemainingDurationTicks(), amplifier, ambient, particles, icon);
        }

        public boolean is(PotionEffect effect) {
            return this.type.equals(effect.getType())
                    && (this.duration == effect.getDuration() || this.activeEffect.getDuration() == effect.getDuration())
                    && this.amplifier == effect.getAmplifier()
                    && this.ambient == effect.isAmbient()
                    && this.particles == effect.hasParticles()
                    && this.icon == effect.hasIcon();
        }

        @Override
        public String toString() {
            return baseEffect.toString() + "{rd: " + this.getRemainingDurationMs() + ", st: " + this.startTime + "}";
        }
    }

    public static long round20(long b) {
        return (long) (Math.ceil(b / 20d) * 20);
    }
}
