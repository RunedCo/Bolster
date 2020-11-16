package co.runed.bolster.game;

import co.runed.bolster.util.properties.Properties;
import co.runed.bolster.util.properties.Property;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;

import java.util.EnumSet;
import java.util.Set;

public class GameProperties extends Properties implements Listener
{
    public static final Property<Boolean> ENABLE_FALL_DAMAGE = new Property<>("enable_fall_damage", true);
    public static final Property<Boolean> ENABLE_EXPLOSION_DAMAGE = new Property<>("enable_explosion_damage", true);
    public static final Property<Boolean> ENABLE_POISON_DAMAGE = new Property<>("enable_poison_damage", true);
    public static final Property<Boolean> ENABLE_WITHER_DAMAGE = new Property<>("enable_wither_damage", true);
    public static final Property<Boolean> ENABLE_FIRE_DAMAGE = new Property<>("enable_fire_damage", true);

    public static final Property<Boolean> ENABLE_PVP = new Property<>("enable_pvp", true);
    public static final Property<Boolean> ENABLE_PVE = new Property<>("enable_pve", true);

    public static final Property<Boolean> DISABLE_ALL_DAMAGE = new Property<>("disable_all_damage", false);

    public static final Property<Boolean> ENABLE_HUNGER = new Property<>("enable_hunger", true);
    // TODO INVENTORY CLICK EVENT FOR OFFHAND SLOT
    public static final Property<Boolean> ENABLE_OFFHAND = new Property<>("enable_offhand", true);
    public static final Property<Boolean> ENABLE_ITEM_DROPS = new Property<>("enable_item_drops", true);

    public static final Property<Boolean> ENABLE_XP = new Property<>("enable_xp", true);

    public static final Property<Boolean> ENABLE_LOG_STRIP = new Property<>("enable_log_strip", true);
    public static final Property<Boolean> ENABLE_GRASS_PATH = new Property<>("enable_grass_path", true);

    private static final Set<Material> SHOVELS = EnumSet.of(Material.STONE_SHOVEL, Material.DIAMOND_SHOVEL, Material.GOLDEN_SHOVEL, Material.IRON_SHOVEL, Material.NETHERITE_SHOVEL, Material.WOODEN_SHOVEL);
    private static final Set<Material> AXES = EnumSet.of(Material.STONE_AXE, Material.DIAMOND_AXE, Material.GOLDEN_AXE, Material.IRON_AXE, Material.NETHERITE_AXE, Material.WOODEN_AXE);

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onEntityTakeDamage(EntityDamageEvent event)
    {
        DamageCause cause = event.getCause();

        switch (cause)
        {
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

        if (cause != DamageCause.VOID && this.get(GameProperties.DISABLE_ALL_DAMAGE)) event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onEntityDamageEntity(EntityDamageByEntityEvent event)
    {
        Entity damager = event.getDamager();

        if (event.getEntityType() == EntityType.PLAYER)
        {
            if (damager.getType() == EntityType.PLAYER || (damager instanceof Projectile && ((Projectile) damager).getShooter() instanceof Player))
            {
                if (!this.get(GameProperties.ENABLE_PVP)) event.setCancelled(true);
            }
            else
            {
                if (!this.get(GameProperties.ENABLE_PVE)) event.setCancelled(true);
            }
        }
    }

    @EventHandler
    private void onPlayerOffhand(PlayerSwapHandItemsEvent event)
    {
        if (!this.get(GameProperties.ENABLE_OFFHAND)) event.setCancelled(true);
    }

    @EventHandler
    private void onInventoryClick(InventoryClickEvent event)
    {
        if (!this.get(GameProperties.ENABLE_OFFHAND))
        {
            if (event.getRawSlot() == 45 || event.getClick() == ClickType.SWAP_OFFHAND)
            {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    private void onPlayerHunger(FoodLevelChangeEvent event)
    {
        if (!this.get(GameProperties.ENABLE_HUNGER))
            event.setCancelled(true);
    }

    @EventHandler
    private void onPlayerDeath(PlayerDeathEvent e)
    {
        if (!this.get(GameProperties.ENABLE_XP)) e.setDroppedExp(0);

        if (!this.get(GameProperties.ENABLE_ITEM_DROPS)) e.getDrops().clear();
    }

    @EventHandler
    private void onBreakBlock(BlockBreakEvent e)
    {
        if (!this.get(GameProperties.ENABLE_XP)) e.setExpToDrop(0);
    }

    @EventHandler
    private void onExpChange(PlayerExpChangeEvent event)
    {
        if (!this.get(GameProperties.ENABLE_XP)) event.setAmount(0);
    }

    @EventHandler
    private void onInteractStrip(PlayerInteractEvent e)
    {
        if (e.getAction() == Action.RIGHT_CLICK_BLOCK)
        {
            Block block = e.getClickedBlock();
            ItemStack item = e.getItem();

            if (item == null || item.getType().isBlock()) return;

            if (!this.get(GameProperties.ENABLE_LOG_STRIP))
            {
                if (!AXES.contains(item.getType()))
                {
                    if (Tag.LOGS.isTagged(block.getType()))
                    {
                        e.setUseInteractedBlock(Event.Result.ALLOW);
                        e.setUseItemInHand(Event.Result.DENY);
                    }
                }
            }

            if (!this.get(GameProperties.ENABLE_GRASS_PATH))
            {
                if (!SHOVELS.contains(item.getType()))
                {
                    if (block.getType() == Material.GRASS_BLOCK)
                    {
                        e.setUseInteractedBlock(Event.Result.ALLOW);
                        e.setUseItemInHand(Event.Result.DENY);
                    }
                }
            }
        }
    }
}
