package co.runed.bolster.properties;

import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.plugin.Plugin;

import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

public class GameProperties extends Properties implements Listener {
    public static final Property<Boolean> ENABLE_FALL_DAMAGE = new Property<>("enable_fall_damage", true);
    public static final Property<Boolean> ENABLE_EXPLOSION_DAMAGE = new Property<>("enable_explosion_damage", true);
    public static final Property<Boolean> ENABLE_POISON_DAMAGE = new Property<>("enable_poison_damage", true);
    public static final Property<Boolean> ENABLE_WITHER_DAMAGE = new Property<>("enable_wither_damage", true);
    public static final Property<Boolean> ENABLE_FIRE_DAMAGE = new Property<>("enable_fire_damage",true);

    public static final Property<Boolean> ENABLE_PVP = new Property<>("enable_pvp", true);
    public static final Property<Boolean> ENABLE_PVE = new Property<>("enable_pve", true);

    public static final Property<Boolean> DISABLE_ALL_DAMAGE = new Property<>("disable_all_damage", false);

    public static final Property<Boolean> ENABLE_HUNGER = new Property<>("enable_hunger", true);

    public GameProperties(Plugin plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onEntityTakeDamage(EntityDamageEvent event) {
        DamageCause cause = event.getCause();

        switch (cause) {
            case FALL:
                if (!this.get(GameProperties.ENABLE_FALL_DAMAGE)) event.setCancelled(true);
                break;
            case ENTITY_EXPLOSION:
            case BLOCK_EXPLOSION:
                if (!this.get(GameProperties.ENABLE_EXPLOSION_DAMAGE)) event.setCancelled(true);
                break;
            case WITHER:
                if (!this.get(GameProperties.ENABLE_WITHER_DAMAGE)) event.setCancelled(true);
                break;
            case POISON:
                if (!this.get(GameProperties.ENABLE_POISON_DAMAGE)) event.setCancelled(true);
                break;
            case FIRE:
            case LAVA:
            case FIRE_TICK:
            case HOT_FLOOR:
                if (!this.get(GameProperties.ENABLE_FIRE_DAMAGE)) event.setCancelled(true);
                break;
        }

        if (this.get(GameProperties.DISABLE_ALL_DAMAGE)) event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onEntityDamageEntity(EntityDamageByEntityEvent event) {
        if (event.getDamager().getType() == EntityType.PLAYER) {
            if (event.getEntityType() == EntityType.PLAYER) {
                if (!this.get(GameProperties.ENABLE_PVP)) event.setCancelled(true);
            } else {
                if (!this.get(GameProperties.ENABLE_PVE)) event.setCancelled(true);
            }
        }
    }

    @EventHandler
    private void onPlayerHunger(FoodLevelChangeEvent event) {
        if (!this.get(GameProperties.ENABLE_HUNGER)) {
            event.setCancelled(true);
        }
    }
}
