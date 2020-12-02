package co.runed.bolster.events;

import co.runed.bolster.abilities.Ability;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.block.BlockEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public final class AbilityPlaceBlockEvent extends BlockEvent implements Cancellable
{
    Ability ability;

    private static final HandlerList handlers = new HandlerList();
    protected boolean cancel;
    protected boolean canBuild;
    protected Block placedAgainst;
    protected BlockState replacedBlockState;
    protected ItemStack itemInHand;
    protected LivingEntity entity;

    public AbilityPlaceBlockEvent(Ability ability, Block placedBlock, BlockState replacedBlockState, Block placedAgainst, ItemStack itemInHand, LivingEntity entity, boolean canBuild)
    {
        super(placedBlock);
        this.ability = ability;
        this.placedAgainst = placedAgainst;
        this.itemInHand = itemInHand;
        this.entity = entity;
        this.replacedBlockState = replacedBlockState;
        this.canBuild = canBuild;
        this.cancel = false;
    }

    public boolean isCancelled()
    {
        return this.cancel;
    }

    public void setCancelled(boolean cancel)
    {
        this.cancel = cancel;
    }

    public LivingEntity getEntity()
    {
        return this.entity;
    }

    public Block getBlockPlaced()
    {
        return this.getBlock();
    }

    public BlockState getBlockReplacedState()
    {
        return this.replacedBlockState;
    }

    public Block getBlockAgainst()
    {
        return this.placedAgainst;
    }

    public ItemStack getItemInHand()
    {
        return this.itemInHand;
    }

    public boolean canBuild()
    {
        return this.canBuild;
    }

    public void setBuild(boolean canBuild)
    {
        this.canBuild = canBuild;
    }

    public Ability getAbility()
    {
        return ability;
    }

    public HandlerList getHandlers()
    {
        return handlers;
    }

    public static HandlerList getHandlerList()
    {
        return handlers;
    }
}
