package co.runed.bolster.v1_16_R3;

import net.minecraft.server.v1_16_R3.*;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_16_R3.event.CraftEventFactory;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;

import java.lang.reflect.Field;

public class CraftUtil
{
    /**
     * Spawns entity at specified Location
     *
     * @param entityTypes Type of entity to spawn
     * @param loc         Location to spawn at
     * @return Reference to the spawned bukkit Entity
     */
    public static Entity spawnEntity(EntityTypes entityTypes, Location loc)
    {
        net.minecraft.server.v1_16_R3.Entity nmsEntity = entityTypes.spawnCreature( // NMS method to spawn an entity from an EntityTypes
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

    public static void setMaxStack(Material material, int max)
    {
        try
        {
            NamespacedKey key = material.getKey();
            Item item = IRegistry.ITEM.get(new MinecraftKey(key.getNamespace(), key.getKey()));
            Field field = Item.class.getDeclaredField("maxStackSize");
            field.setAccessible(true);
            field.setInt(item, max);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void damageEntity(LivingEntity entity, double damage)
    {
        damageEntity(entity, damage, EntityDamageEvent.DamageCause.CUSTOM);
    }

    public static void damageEntity(LivingEntity entity, double damage, LivingEntity damager)
    {
        damageEntity(entity, damage, damager, EntityDamageEvent.DamageCause.CUSTOM);
    }

    public static void damageEntity(LivingEntity entity, double damage, EntityDamageEvent.DamageCause cause)
    {
        damageEntity(entity, damage, null, cause);
    }

    public static void damageEntity(LivingEntity entity, double damage, LivingEntity damager, EntityDamageEvent.DamageCause cause)
    {
        net.minecraft.server.v1_16_R3.Entity nmsEntity = ((CraftEntity) entity).getHandle();
        net.minecraft.server.v1_16_R3.Entity nmsDamager = ((CraftEntity) damager).getHandle();
        DamageSource source = DamageSource.GENERIC;

        switch (cause)
        {
            case CONTACT:
            {
                source = DamageSource.CACTUS;
                break;
            }
            case ENTITY_ATTACK:
            {
                if (damager instanceof Player)
                {
                    source = DamageSource.playerAttack((EntityHuman) nmsDamager);
                }
                else
                {
                    source = DamageSource.mobAttack((EntityLiving) nmsDamager);
                }

                break;
            }
            case ENTITY_SWEEP_ATTACK:
            {
                if (damager instanceof Player)
                {
                    source = DamageSource.playerAttack((EntityHuman) nmsDamager).sweep();
                }
                else
                {
                    source = DamageSource.mobAttack((EntityLiving) nmsDamager).sweep();
                }
                break;
            }
//            case PROJECTILE:
//            {
//                source = DamageSource.PROJECTILE;
//                break;
//            }
            case SUFFOCATION:
            {
                source = DamageSource.STUCK;
                break;
            }
            case FALL:
            {
                source = DamageSource.FALL;
                break;
            }
            case FIRE:
            {
                source = DamageSource.FIRE;
                break;
            }
            case FIRE_TICK:
            {
                source = DamageSource.BURN;
                break;
            }
            case MELTING:
            {
                source = CraftEventFactory.MELTING;
                break;
            }
            case LAVA:
            {
                source = DamageSource.LAVA;
                break;
            }
            case DROWNING:
            {
                source = DamageSource.DROWN;
                break;
            }
            case BLOCK_EXPLOSION:
            case ENTITY_EXPLOSION:
            {
                source = DamageSource.d((EntityLiving) nmsDamager);
                break;
            }
            case VOID:
            {
                source = DamageSource.OUT_OF_WORLD;
                break;
            }
            case LIGHTNING:
            {
                source = DamageSource.LIGHTNING;
                break;
            }
//            case SUICIDE:
//            {
//                source = DamageSource.SUICIDE;
//                break;
//            }
            case STARVATION:
            {
                source = DamageSource.STARVE;
                break;
            }
            case POISON:
            {
                source = CraftEventFactory.POISON;
                break;
            }
            case MAGIC:
            {
                source = DamageSource.MAGIC;
                break;
            }
            case WITHER:
            {
                source = DamageSource.WITHER;
                break;
            }
            case FALLING_BLOCK:
            {
                source = DamageSource.FALLING_BLOCK;
                break;
            }
            case THORNS:
            {
                source = DamageSource.a(nmsDamager);
                break;

            }
            case DRAGON_BREATH:
            {
                source = DamageSource.DRAGON_BREATH;
                break;
            }
            case FLY_INTO_WALL:
            {
                source = DamageSource.FLY_INTO_WALL;
                break;
            }
            case HOT_FLOOR:
            {
                source = DamageSource.HOT_FLOOR;
                break;
            }
            case CRAMMING:
            {
                source = DamageSource.CRAMMING;
                break;
            }
            case DRYOUT:
            {
                source = DamageSource.DRYOUT;
                break;
            }
            default:
            {
                source = DamageSource.GENERIC;
                break;
            }
        }

        nmsEntity.damageEntity(source, (float) damage);
    }
}
