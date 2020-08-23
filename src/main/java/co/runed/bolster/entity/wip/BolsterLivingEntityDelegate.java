package co.runed.bolster.entity.wip;

import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.PistonMoveReaction;
import org.bukkit.entity.*;
import org.bukkit.entity.memory.MemoryKey;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public abstract class BolsterLivingEntityDelegate implements LivingEntity
{
    LivingEntity entity;

    public BolsterLivingEntityDelegate(LivingEntity entity)
    {
        this.entity = entity;
    }

    public LivingEntity getBukkit()
    {
        return this.entity;
    }

    @Override
    public double getEyeHeight()
    {
        return entity.getEyeHeight();
    }

    @Override
    public double getEyeHeight(boolean b)
    {
        return entity.getEyeHeight(b);
    }

    @Override
    public Location getEyeLocation()
    {
        return entity.getEyeLocation();
    }

    @Override
    public List<Block> getLineOfSight(Set<Material> set, int i)
    {
        return entity.getLineOfSight(set, i);
    }

    @Override
    public Block getTargetBlock(Set<Material> set, int i)
    {
        return entity.getTargetBlock(set, i);
    }

    @Override
    public List<Block> getLastTwoTargetBlocks(Set<Material> set, int i)
    {
        return entity.getLastTwoTargetBlocks(set, i);
    }

    @Override
    public Block getTargetBlockExact(int i)
    {
        return entity.getTargetBlockExact(i);
    }

    @Override
    public Block getTargetBlockExact(int i, FluidCollisionMode fluidCollisionMode)
    {
        return entity.getTargetBlockExact(i, fluidCollisionMode);
    }

    @Override
    public RayTraceResult rayTraceBlocks(double v)
    {
        return entity.rayTraceBlocks(v);
    }

    @Override
    public RayTraceResult rayTraceBlocks(double v, FluidCollisionMode fluidCollisionMode)
    {
        return entity.rayTraceBlocks(v, fluidCollisionMode);
    }

    @Override
    public int getRemainingAir()
    {
        return entity.getRemainingAir();
    }

    @Override
    public void setRemainingAir(int i)
    {
        entity.setRemainingAir(i);
    }

    @Override
    public int getMaximumAir()
    {
        return entity.getMaximumAir();
    }

    @Override
    public void setMaximumAir(int i)
    {
        entity.setMaximumAir(i);
    }

    @Override
    public int getMaximumNoDamageTicks()
    {
        return entity.getMaximumNoDamageTicks();
    }

    @Override
    public void setMaximumNoDamageTicks(int i)
    {
        entity.setMaximumNoDamageTicks(i);
    }

    @Override
    public double getLastDamage()
    {
        return entity.getLastDamage();
    }

    @Override
    public void setLastDamage(double v)
    {
        entity.setLastDamage(v);
    }

    @Override
    public int getNoDamageTicks()
    {
        return entity.getNoDamageTicks();
    }

    @Override
    public void setNoDamageTicks(int i)
    {
        entity.setNoDamageTicks(i);
    }

    @Override
    public Player getKiller()
    {
        return entity.getKiller();
    }

    @Override
    public boolean addPotionEffect(PotionEffect potionEffect)
    {
        return entity.addPotionEffect(potionEffect);
    }

    @Override
    @Deprecated
    public boolean addPotionEffect(PotionEffect potionEffect, boolean b)
    {
        return entity.addPotionEffect(potionEffect, b);
    }

    @Override
    public boolean addPotionEffects(Collection<PotionEffect> collection)
    {
        return entity.addPotionEffects(collection);
    }

    @Override
    public boolean hasPotionEffect(PotionEffectType potionEffectType)
    {
        return entity.hasPotionEffect(potionEffectType);
    }

    @Override
    public PotionEffect getPotionEffect(PotionEffectType potionEffectType)
    {
        return entity.getPotionEffect(potionEffectType);
    }

    @Override
    public void removePotionEffect(PotionEffectType potionEffectType)
    {
        entity.removePotionEffect(potionEffectType);
    }

    @Override
    public Collection<PotionEffect> getActivePotionEffects()
    {
        return entity.getActivePotionEffects();
    }

    @Override
    public boolean hasLineOfSight(Entity entity)
    {
        return this.entity.hasLineOfSight(entity);
    }

    @Override
    public boolean getRemoveWhenFarAway()
    {
        return entity.getRemoveWhenFarAway();
    }

    @Override
    public void setRemoveWhenFarAway(boolean b)
    {
        entity.setRemoveWhenFarAway(b);
    }

    @Override
    public EntityEquipment getEquipment()
    {
        return entity.getEquipment();
    }

    @Override
    public void setCanPickupItems(boolean b)
    {
        entity.setCanPickupItems(b);
    }

    @Override
    public boolean getCanPickupItems()
    {
        return entity.getCanPickupItems();
    }

    @Override
    public boolean isLeashed()
    {
        return entity.isLeashed();
    }

    @Override
    public Entity getLeashHolder() throws IllegalStateException
    {
        return entity.getLeashHolder();
    }

    @Override
    public boolean setLeashHolder(Entity entity)
    {
        return this.entity.setLeashHolder(entity);
    }

    @Override
    public boolean isGliding()
    {
        return entity.isGliding();
    }

    @Override
    public void setGliding(boolean b)
    {
        entity.setGliding(b);
    }

    @Override
    public boolean isSwimming()
    {
        return entity.isSwimming();
    }

    @Override
    public void setSwimming(boolean b)
    {
        entity.setSwimming(b);
    }

    @Override
    public boolean isRiptiding()
    {
        return entity.isRiptiding();
    }

    @Override
    public boolean isSleeping()
    {
        return entity.isSleeping();
    }

    @Override
    public void setAI(boolean b)
    {
        entity.setAI(b);
    }

    @Override
    public boolean hasAI()
    {
        return entity.hasAI();
    }

    @Override
    public void attack(Entity entity)
    {
        this.entity.attack(entity);
    }

    @Override
    public void swingMainHand()
    {
        entity.swingMainHand();
    }

    @Override
    public void swingOffHand()
    {
        entity.swingOffHand();
    }

    @Override
    public void setCollidable(boolean b)
    {
        entity.setCollidable(b);
    }

    @Override
    public boolean isCollidable()
    {
        return entity.isCollidable();
    }

    @Override
    public Set<UUID> getCollidableExemptions()
    {
        return entity.getCollidableExemptions();
    }

    @Override
    public <T> T getMemory(MemoryKey<T> memoryKey)
    {
        return entity.getMemory(memoryKey);
    }

    @Override
    public <T> void setMemory(MemoryKey<T> memoryKey, T t)
    {
        entity.setMemory(memoryKey, t);
    }

    @Override
    public AttributeInstance getAttribute(Attribute attribute)
    {
        return entity.getAttribute(attribute);
    }

    @Override
    public void damage(double v)
    {
        entity.damage(v);
    }

    @Override
    public void damage(double v, Entity entity)
    {
        this.entity.damage(v, entity);
    }

    @Override
    public double getHealth()
    {
        return entity.getHealth();
    }

    @Override
    public void setHealth(double v)
    {
        entity.setHealth(v);
    }

    @Override
    public double getAbsorptionAmount()
    {
        return entity.getAbsorptionAmount();
    }

    @Override
    public void setAbsorptionAmount(double v)
    {
        entity.setAbsorptionAmount(v);
    }

    @Override
    @Deprecated
    public double getMaxHealth()
    {
        return entity.getMaxHealth();
    }

    @Override
    @Deprecated
    public void setMaxHealth(double v)
    {
        entity.setMaxHealth(v);
    }

    @Override
    @Deprecated
    public void resetMaxHealth()
    {
        entity.resetMaxHealth();
    }

    @Override
    public Location getLocation()
    {
        return entity.getLocation();
    }

    @Override
    public Location getLocation(Location location)
    {
        return entity.getLocation(location);
    }

    @Override
    public void setVelocity(Vector vector)
    {
        entity.setVelocity(vector);
    }

    @Override
    public Vector getVelocity()
    {
        return entity.getVelocity();
    }

    @Override
    public double getHeight()
    {
        return entity.getHeight();
    }

    @Override
    public double getWidth()
    {
        return entity.getWidth();
    }

    @Override
    public BoundingBox getBoundingBox()
    {
        return entity.getBoundingBox();
    }

    @Override
    public boolean isOnGround()
    {
        return entity.isOnGround();
    }

    @Override
    public World getWorld()
    {
        return entity.getWorld();
    }

    @Override
    public void setRotation(float v, float v1)
    {
        entity.setRotation(v, v1);
    }

    @Override
    public boolean teleport(Location location)
    {
        return entity.teleport(location);
    }

    @Override
    public boolean teleport(Location location, PlayerTeleportEvent.TeleportCause teleportCause)
    {
        return entity.teleport(location, teleportCause);
    }

    @Override
    public boolean teleport(Entity entity)
    {
        return this.entity.teleport(entity);
    }

    @Override
    public boolean teleport(Entity entity, PlayerTeleportEvent.TeleportCause teleportCause)
    {
        return this.entity.teleport(entity, teleportCause);
    }

    @Override
    public List<Entity> getNearbyEntities(double v, double v1, double v2)
    {
        return entity.getNearbyEntities(v, v1, v2);
    }

    @Override
    public int getEntityId()
    {
        return entity.getEntityId();
    }

    @Override
    public int getFireTicks()
    {
        return entity.getFireTicks();
    }

    @Override
    public int getMaxFireTicks()
    {
        return entity.getMaxFireTicks();
    }

    @Override
    public void setFireTicks(int i)
    {
        entity.setFireTicks(i);
    }

    @Override
    public void remove()
    {
        entity.remove();
    }

    @Override
    public boolean isDead()
    {
        return entity.isDead();
    }

    @Override
    public boolean isValid()
    {
        return entity.isValid();
    }

    @Override
    public Server getServer()
    {
        return entity.getServer();
    }

    @Override
    @Deprecated
    public boolean isPersistent()
    {
        return entity.isPersistent();
    }

    @Override
    @Deprecated
    public void setPersistent(boolean b)
    {
        entity.setPersistent(b);
    }

    @Override
    @Deprecated
    public Entity getPassenger()
    {
        return entity.getPassenger();
    }

    @Override
    @Deprecated
    public boolean setPassenger(Entity entity)
    {
        return this.entity.setPassenger(entity);
    }

    @Override
    public List<Entity> getPassengers()
    {
        return entity.getPassengers();
    }

    @Override
    public boolean addPassenger(Entity entity)
    {
        return this.entity.addPassenger(entity);
    }

    @Override
    public boolean removePassenger(Entity entity)
    {
        return this.entity.removePassenger(entity);
    }

    @Override
    public boolean isEmpty()
    {
        return entity.isEmpty();
    }

    @Override
    public boolean eject()
    {
        return entity.eject();
    }

    @Override
    public float getFallDistance()
    {
        return entity.getFallDistance();
    }

    @Override
    public void setFallDistance(float v)
    {
        entity.setFallDistance(v);
    }

    @Override
    public void setLastDamageCause(EntityDamageEvent entityDamageEvent)
    {
        entity.setLastDamageCause(entityDamageEvent);
    }

    @Override
    public EntityDamageEvent getLastDamageCause()
    {
        return entity.getLastDamageCause();
    }

    @Override
    public UUID getUniqueId()
    {
        return entity.getUniqueId();
    }

    @Override
    public int getTicksLived()
    {
        return entity.getTicksLived();
    }

    @Override
    public void setTicksLived(int i)
    {
        entity.setTicksLived(i);
    }

    @Override
    public void playEffect(EntityEffect entityEffect)
    {
        entity.playEffect(entityEffect);
    }

    @Override
    public EntityType getType()
    {
        return entity.getType();
    }

    @Override
    public boolean isInsideVehicle()
    {
        return entity.isInsideVehicle();
    }

    @Override
    public boolean leaveVehicle()
    {
        return entity.leaveVehicle();
    }

    @Override
    public Entity getVehicle()
    {
        return entity.getVehicle();
    }

    @Override
    public void setCustomNameVisible(boolean b)
    {
        entity.setCustomNameVisible(b);
    }

    @Override
    public boolean isCustomNameVisible()
    {
        return entity.isCustomNameVisible();
    }

    @Override
    public void setGlowing(boolean b)
    {
        entity.setGlowing(b);
    }

    @Override
    public boolean isGlowing()
    {
        return entity.isGlowing();
    }

    @Override
    public void setInvulnerable(boolean b)
    {
        entity.setInvulnerable(b);
    }

    @Override
    public boolean isInvulnerable()
    {
        return entity.isInvulnerable();
    }

    @Override
    public boolean isSilent()
    {
        return entity.isSilent();
    }

    @Override
    public void setSilent(boolean b)
    {
        entity.setSilent(b);
    }

    @Override
    public boolean hasGravity()
    {
        return entity.hasGravity();
    }

    @Override
    public void setGravity(boolean b)
    {
        entity.setGravity(b);
    }

    @Override
    public int getPortalCooldown()
    {
        return entity.getPortalCooldown();
    }

    @Override
    public void setPortalCooldown(int i)
    {
        entity.setPortalCooldown(i);
    }

    @Override
    public Set<String> getScoreboardTags()
    {
        return entity.getScoreboardTags();
    }

    @Override
    public boolean addScoreboardTag(String s)
    {
        return entity.addScoreboardTag(s);
    }

    @Override
    public boolean removeScoreboardTag(String s)
    {
        return entity.removeScoreboardTag(s);
    }

    @Override
    public PistonMoveReaction getPistonMoveReaction()
    {
        return entity.getPistonMoveReaction();
    }

    @Override
    public BlockFace getFacing()
    {
        return entity.getFacing();
    }

    @Override
    public Pose getPose()
    {
        return entity.getPose();
    }

    @Override
    public Spigot spigot()
    {
        return entity.spigot();
    }

    @Override
    public void setMetadata(String s, MetadataValue metadataValue)
    {
        entity.setMetadata(s, metadataValue);
    }

    @Override
    public List<MetadataValue> getMetadata(String s)
    {
        return entity.getMetadata(s);
    }

    @Override
    public boolean hasMetadata(String s)
    {
        return entity.hasMetadata(s);
    }

    @Override
    public void removeMetadata(String s, Plugin plugin)
    {
        entity.removeMetadata(s, plugin);
    }

    @Override
    public void sendMessage(String s)
    {
        entity.sendMessage(s);
    }

    @Override
    public void sendMessage(String[] strings)
    {
        entity.sendMessage(strings);
    }

    @Override
    public String getName()
    {
        return entity.getName();
    }

    @Override
    public boolean isPermissionSet(String s)
    {
        return entity.isPermissionSet(s);
    }

    @Override
    public boolean isPermissionSet(Permission permission)
    {
        return entity.isPermissionSet(permission);
    }

    @Override
    public boolean hasPermission(String s)
    {
        return entity.hasPermission(s);
    }

    @Override
    public boolean hasPermission(Permission permission)
    {
        return entity.hasPermission(permission);
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin, String s, boolean b)
    {
        return entity.addAttachment(plugin, s, b);
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin)
    {
        return entity.addAttachment(plugin);
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin, String s, boolean b, int i)
    {
        return entity.addAttachment(plugin, s, b, i);
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin, int i)
    {
        return entity.addAttachment(plugin, i);
    }

    @Override
    public void removeAttachment(PermissionAttachment permissionAttachment)
    {
        entity.removeAttachment(permissionAttachment);
    }

    @Override
    public void recalculatePermissions()
    {
        entity.recalculatePermissions();
    }

    @Override
    public Set<PermissionAttachmentInfo> getEffectivePermissions()
    {
        return entity.getEffectivePermissions();
    }

    @Override
    public boolean isOp()
    {
        return entity.isOp();
    }

    @Override
    public void setOp(boolean b)
    {
        entity.setOp(b);
    }

    @Override
    public String getCustomName()
    {
        return entity.getCustomName();
    }

    @Override
    public void setCustomName(String s)
    {
        entity.setCustomName(s);
    }

    @Override
    public PersistentDataContainer getPersistentDataContainer()
    {
        return entity.getPersistentDataContainer();
    }

    @Override
    public <T extends Projectile> T launchProjectile(Class<? extends T> aClass)
    {
        return entity.launchProjectile(aClass);
    }

    @Override
    public <T extends Projectile> T launchProjectile(Class<? extends T> aClass, Vector vector)
    {
        return entity.launchProjectile(aClass, vector);
    }
}
