package co.runed.bolster.managers;

import co.runed.bolster.Bolster;
import co.runed.bolster.status.StatusEffect;
import co.runed.bolster.util.Manager;
import co.runed.bolster.util.NetworkUtil;
import co.runed.bolster.util.TimeUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.bukkit.ChatColor;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

public class StatusEffectManager extends Manager
{
    Map<UUID, List<StatusEffect>> currentStatusEffects = new HashMap<>();
    Map<UUID, HashMap<PotionEffectType, List<PotionEffectData>>> potionEffects = new HashMap<>();

    private static StatusEffectManager _instance;

    public StatusEffectManager(Plugin plugin)
    {
        super(plugin);

        _instance = this;
    }

    // TODO:
    // CHECK IF HARD CC, IF NOT JUST APPLY CC
    // IF HARD CC CHECK IF HARD CC ALREADY ACTIVE AND CHECK PRIORITY
    // IF PRIORITY HIGHER APPLY INSTEAD OF OTHER CC
    // IF NEW CC EXPIRES FIRST THEN REAPPLY REMAINING CC FOR REST OF DURATION
    public void addStatusEffect(LivingEntity entity, StatusEffect statusEffect)
    {
        this.clearStatusEffect(entity, statusEffect.getClass());

        List<StatusEffect> entityEffects = this.getStatusEffects(entity);

        entityEffects.add(statusEffect);

        statusEffect.start(entity);

        if (entity instanceof Player && statusEffect.getId() != null)
        {
            Player player = (Player) entity;
            ByteBuf byteBuf = Unpooled.buffer();
            NetworkUtil.writeString(byteBuf, statusEffect.getId());

            player.sendPluginMessage(Bolster.getInstance(), "bolster:add_status_effect", byteBuf.array());

            this.updateTitleDisplay(player);
        }
    }

    public void clearStatusEffects(LivingEntity entity)
    {
        List<StatusEffect> effects = new ArrayList<>(this.getStatusEffects(entity));

        for (StatusEffect effect : effects)
        {
            this.clearStatusEffect(entity, effect.getClass());
        }
    }

    public void clearStatusEffect(LivingEntity entity, Class<? extends StatusEffect> statusEffect)
    {
        List<StatusEffect> entityEffects = this.getStatusEffects(entity);

        StatusEffect effectToRemove = null;
        for (StatusEffect effect : entityEffects)
        {
            if (effect.getClass() == statusEffect)
            {
                effectToRemove = effect;
                break;
            }
        }

        if (effectToRemove != null)
        {
            effectToRemove.clear();
            entityEffects.remove(effectToRemove);
        }
    }

    public void removeStatusEffect(LivingEntity entity, StatusEffect statusEffect)
    {
        UUID uuid = entity.getUniqueId();

        if (!this.currentStatusEffects.containsKey(uuid)) return;

        statusEffect.clear();

        if (entity instanceof Player && statusEffect.getId() != null)
        {
            Player player = (Player) entity;
            ByteBuf byteBuf = Unpooled.buffer();
            NetworkUtil.writeString(byteBuf, statusEffect.getId());
            byteBuf.writeDouble(statusEffect.getDuration());

            player.sendPluginMessage(Bolster.getInstance(), "bolster:remove_status_effect", byteBuf.array());
        }

        if (this.currentStatusEffects.containsKey(uuid)) this.currentStatusEffects.get(uuid).remove(statusEffect);
    }

    public List<StatusEffect> getStatusEffects(LivingEntity entity)
    {
        UUID uuid = entity.getUniqueId();

        if (!this.currentStatusEffects.containsKey(uuid))
        {
            this.currentStatusEffects.put(uuid, new ArrayList<>());
        }

        return this.currentStatusEffects.get(uuid);
    }

    public boolean hasStatusEffect(LivingEntity entity, Class<? extends StatusEffect> statusEffect)
    {
        List<StatusEffect> entityEffects = this.getStatusEffects(entity);

        for (StatusEffect effect : entityEffects)
        {
            if (effect.getClass() == statusEffect)
            {
                return true;
            }
        }

        return false;
    }

    public void updateTitleDisplay(Player player)
    {
        StringBuilder display = new StringBuilder();

        for (StatusEffect effect : this.getStatusEffects(player))
        {
            if (effect.getName() == null) continue;

            display.append(effect.getColor()).append(effect.getName().toUpperCase()).append(" (").append(effect.getDuration()).append(")").append(", ");
        }

        display = new StringBuilder(display.substring(0, display.length() - 2));

        //PlayerUtil.sendActionBar(player, ChatColor.BOLD + display.toString());

        //player.sendTitle("", display.toString(), 0, 10, 0);
    }

    @EventHandler
    private void onConnect(PlayerJoinEvent event)
    {
        UUID uuid = event.getPlayer().getUniqueId();

        if (!this.currentStatusEffects.containsKey(uuid)) return;

        for (StatusEffect effect : this.currentStatusEffects.get(uuid))
        {
            effect.setEntity(event.getPlayer());
        }
    }

    @EventHandler
    private void onEntityDeath(EntityDeathEvent event)
    {
        this.clearStatusEffects(event.getEntity());
    }

    // TODO MAKE SURE WORKS
    // TODO DOES NOT WORK IF YOU ALREADY HAVE STATUS EFFECT SET (try setting speed 1 for 100 seconds and then speed 9 for 10 seconds)
    @EventHandler
    private void onPotionAdded(EntityPotionEffectEvent event)
    {
        if (!(event.getEntity() instanceof LivingEntity)) return;

        EntityPotionEffectEvent.Cause cause = event.getCause();
        EntityPotionEffectEvent.Action action = event.getAction();
        LivingEntity entity = (LivingEntity) event.getEntity();

        PotionEffect oldEffect = event.getOldEffect();
        PotionEffect newEffect = event.getNewEffect();
        PotionEffectType type = oldEffect == null ? newEffect.getType() : oldEffect.getType();

        this.potionEffects.putIfAbsent(entity.getUniqueId(), new HashMap<>());
        this.potionEffects.get(entity.getUniqueId()).putIfAbsent(type, new ArrayList<>());

        List<PotionEffectData> effects = this.potionEffects.get(entity.getUniqueId()).get(type);

        if (action == EntityPotionEffectEvent.Action.ADDED)
        {
            PotionEffectData wrappedNewEffect = effects.stream().filter(e -> e.getEffect() == newEffect).findFirst().orElse(null);
            effects.remove(wrappedNewEffect);

            List<PotionEffectData> toRemove = effects.stream().filter(e -> e.isFinished() || (e.getEffect().getDuration() <= newEffect.getDuration() && e.getEffect().getAmplifier() <= newEffect.getAmplifier())).collect(Collectors.toList());
            effects.removeAll(toRemove);
        }
        else if (action == EntityPotionEffectEvent.Action.CHANGED)
        {
            if (oldEffect == null) return;
            if (effects.stream().anyMatch(e -> e.getEffect() == newEffect)) return;

            if (newEffect.getDuration() > oldEffect.getDuration() && newEffect.getAmplifier() <= oldEffect.getAmplifier())
            {
                PotionEffectData data = new PotionEffectData(newEffect);
                effects.add(data);

                effects.sort((e1, e2) -> e2.getEffect().getDuration() - e1.getEffect().getDuration());
                effects.sort((e1, e2) -> e2.getEffect().getAmplifier() - e1.getEffect().getAmplifier());

                int index = effects.indexOf(data);
                PotionEffectData oldData = index == 0 ? new PotionEffectData(oldEffect) : effects.get(index - 1);

                data.duration = newEffect.getDuration() - oldData.getEffect().getDuration();
            }

//            if (newEffect.getDuration() < oldEffect.getDuration() && newEffect.getAmplifier() >= oldEffect.getAmplifier())
//            {
//                PotionEffectData data = new PotionEffectData(newEffect);
//                effects.add(data);
//
//                PotionEffectData oldData = new PotionEffectData(oldEffect);
//                oldData.duration = oldData.duration - data.duration;
//                effects.add(oldData);
//
//                //entity.removePotionEffect(oldEffect.getType());
//
//                data.getEffect().apply(entity);
//            }
        }
        else
        {
            if (effects.size() <= 0) return;

            PotionEffectData wrappedOldEffect = effects.stream().filter(e -> e.getEffect() == oldEffect).findFirst().orElse(null);
            effects.remove(wrappedOldEffect);

            PotionEffectData effectData = effects.stream().min((e1, e2) -> e2.getEffect().getAmplifier() - e1.getEffect().getAmplifier()).orElse(null);

            if (effectData == null) return;

            PotionEffect effect = effectData.getEffect();
            effectData.effect = new PotionEffect(effect.getType(), effectData.duration, effect.getAmplifier(), effect.isAmbient(), effect.hasParticles(), effect.hasIcon());
            effectData.effect.apply(entity);
        }

        ChatColor color = ChatColor.values()[9 + action.ordinal()];
        entity.sendMessage(color + "Potion effect " + action + ". New effect is " + newEffect + ". Old effect is " + oldEffect);
    }

    private static class PotionEffectData
    {
        PotionEffect effect;
        Instant startTime = Instant.now();
        int duration;

        private PotionEffectData(PotionEffect effect)
        {
            this.effect = effect;
        }

        public PotionEffect getEffect()
        {
            return effect;
        }

        private boolean isFinished()
        {
            return Instant.now().isAfter(TimeUtil.addSeconds(this.startTime, effect.getDuration() / 20f));
        }

        @Override
        public String toString()
        {
            return this.effect.toString() + " (st: " + this.startTime + ")";
        }
    }

    public static StatusEffectManager getInstance()
    {
        return _instance;
    }
}
