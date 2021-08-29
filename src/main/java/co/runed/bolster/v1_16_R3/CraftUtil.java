package co.runed.bolster.v1_16_R3;

import net.minecraft.server.v1_16_R3.*;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_16_R3.event.CraftEventFactory;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftItemStack;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.Random;

public class CraftUtil {
    /**
     * Spawns entity at specified Location
     *
     * @param entityTypes Type of entity to spawn
     * @param loc         Location to spawn at
     * @return Reference to the spawned bukkit Entity
     */
    public static Entity spawnEntity(EntityTypes entityTypes, Location loc) {
        var nmsEntity = entityTypes.spawnCreature( // NMS method to spawn an entity from an EntityTypes
                ((CraftWorld) loc.getWorld()).getHandle(), // reference to the NMS world
                null, // itemstack
                null, // player reference. used to know if player is OP to apply EntityTag NBT compound
                new BlockPosition(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()), // the BlockPosition to spawn at
                EnumMobSpawn.SPAWNER, // enum method of spawning
                true, // center entity on BlockPosition and correct Y position for Entity's height
                false); // not sure. alters the Y position. this is only ever true when using spawn egg and clicked face is UP

        // feel free to further modify your entity here if wanted
        // it's already been added to the world at this point

        return nmsEntity == null ? null : nmsEntity.getBukkitEntity(); // convert to a Bukkit entity
    }

    /**
     * Shake the players screen
     *
     * @param player the player
     */
    public static void showScreenShake(Player player) {
        var packet = new PacketPlayOutAnimation(((CraftPlayer) player).getHandle(), 3);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
    }

    public static void playDeath(Player player) {
        var packet = new net.minecraft.server.v1_16_R3.PacketPlayOutEntityStatus(((CraftPlayer) player).getHandle(), (byte) 3);

        for (Player p : org.bukkit.Bukkit.getOnlinePlayers()) {
            if (!p.equals(player) && p.getLocation().distanceSquared(player.getLocation()) < 900.0D) {
                ((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet);
            }
        }
    }

    public static void sendFakeSlotUpdate(Player player, int slot, ItemStack item) {
        net.minecraft.server.v1_16_R3.ItemStack nmsItem;
        if (item != null) {
            nmsItem = CraftItemStack.asNMSCopy(item);
        }
        else {
            nmsItem = null;
        }
        var packet = new PacketPlayOutSetSlot(0, (short) slot + 36, nmsItem);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
    }

    /**
     * Drop an item from a player in a realistic manner
     *
     * @param player    the player
     * @param itemStack the item stack
     */
    public static void dropItem(Player player, ItemStack itemStack) {
        var random = new Random();
        var world = player.getWorld();

        var spawnLoc = player.getLocation().clone();
        spawnLoc.setY(player.getEyeLocation().getY() - 0.30000001192092896D);

        var item = (org.bukkit.entity.Item) world.spawnEntity(spawnLoc, EntityType.DROPPED_ITEM);
        item.setItemStack(itemStack);
        item.setPickupDelay(40);

        var pitch = player.getEyeLocation().getPitch();
        var yaw = player.getEyeLocation().getYaw();

        var f1 = MathHelper.sin(pitch * 0.017453292F);
        var f2 = MathHelper.cos(pitch * 0.017453292F);
        var f3 = MathHelper.sin(yaw * 0.017453292F);
        var f4 = MathHelper.cos(yaw * 0.017453292F);
        var f5 = random.nextFloat() * 6.2831855F;
        var f6 = 0.02F * random.nextFloat();

        item.setVelocity(new Vector((double) (-f3 * f2 * 0.3F) + Math.cos(f5) * (double) f6,
                (-f1 * 0.3F + 0.1F + (random.nextFloat() - random.nextFloat()) * 0.1F),
                (double) (f4 * f2 * 0.3F) + Math.sin(f5) * (double) f6));
    }

    public static void setMaxStack(Material material, int max) {
        try {
            var key = material.getKey();
            var item = IRegistry.ITEM.get(new MinecraftKey(key.getNamespace(), key.getKey()));
            var field = Item.class.getDeclaredField("maxStackSize");
            field.setAccessible(true);
            field.setInt(item, max);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void killEntity(LivingEntity entity) {
        CraftEntity entity1 = (CraftLivingEntity) entity;
        var living = (EntityLiving) entity1.getHandle();
        living.die(DamageSource.GENERIC);
    }

    public static void damageEntity(LivingEntity entity, double damage) {
        damageEntity(entity, damage, EntityDamageEvent.DamageCause.CUSTOM);
    }

    public static void damageEntity(LivingEntity entity, double damage, LivingEntity damager) {
        damageEntity(entity, damage, damager, EntityDamageEvent.DamageCause.CUSTOM);
    }

    public static void damageEntity(LivingEntity entity, double damage, EntityDamageEvent.DamageCause cause) {
        damageEntity(entity, damage, null, cause);
    }

    public static void damageEntity(LivingEntity entity, double damage, LivingEntity damager, EntityDamageEvent.DamageCause cause) {
        var nmsEntity = ((CraftEntity) entity).getHandle();
        var nmsDamager = ((CraftEntity) damager).getHandle();
        var source = DamageSource.GENERIC;

        switch (cause) {
            case CONTACT: {
                source = DamageSource.CACTUS;
                break;
            }
            case ENTITY_ATTACK: {
                if (damager instanceof Player) {
                    source = DamageSource.playerAttack((EntityHuman) nmsDamager);
                }
                else {
                    source = DamageSource.mobAttack((EntityLiving) nmsDamager);
                }

                break;
            }
            case ENTITY_SWEEP_ATTACK: {
                if (damager instanceof Player) {
                    source = DamageSource.playerAttack((EntityHuman) nmsDamager).sweep();
                }
                else {
                    source = DamageSource.mobAttack((EntityLiving) nmsDamager).sweep();
                }
                break;
            }
//            case PROJECTILE:
//            {
//                source = DamageSource.PROJECTILE;
//                break;
//            }
            case SUFFOCATION: {
                source = DamageSource.STUCK;
                break;
            }
            case FALL: {
                source = DamageSource.FALL;
                break;
            }
            case FIRE: {
                source = DamageSource.FIRE;
                break;
            }
            case FIRE_TICK: {
                source = DamageSource.BURN;
                break;
            }
            case MELTING: {
                source = CraftEventFactory.MELTING;
                break;
            }
            case LAVA: {
                source = DamageSource.LAVA;
                break;
            }
            case DROWNING: {
                source = DamageSource.DROWN;
                break;
            }
            case BLOCK_EXPLOSION:
            case ENTITY_EXPLOSION: {
                source = DamageSource.d((EntityLiving) nmsDamager);
                break;
            }
            case VOID: {
                source = DamageSource.OUT_OF_WORLD;
                break;
            }
            case LIGHTNING: {
                source = DamageSource.LIGHTNING;
                break;
            }
//            case SUICIDE:
//            {
//                source = DamageSource.SUICIDE;
//                break;
//            }
            case STARVATION: {
                source = DamageSource.STARVE;
                break;
            }
            case POISON: {
                source = CraftEventFactory.POISON;
                break;
            }
            case MAGIC: {
                source = DamageSource.MAGIC;
                break;
            }
            case WITHER: {
                source = DamageSource.WITHER;
                break;
            }
            case FALLING_BLOCK: {
                source = DamageSource.FALLING_BLOCK;
                break;
            }
            case THORNS: {
                source = DamageSource.a(nmsDamager);
                break;

            }
            case DRAGON_BREATH: {
                source = DamageSource.DRAGON_BREATH;
                break;
            }
            case FLY_INTO_WALL: {
                source = DamageSource.FLY_INTO_WALL;
                break;
            }
            case HOT_FLOOR: {
                source = DamageSource.HOT_FLOOR;
                break;
            }
            case CRAMMING: {
                source = DamageSource.CRAMMING;
                break;
            }
            case DRYOUT: {
                source = DamageSource.DRYOUT;
                break;
            }
            default: {
                source = DamageSource.GENERIC;
                break;
            }
        }

        nmsEntity.damageEntity(source, (float) damage);
    }
}
