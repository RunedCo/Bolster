package co.runed.bolster.events.world;

import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.block.BlockEvent;
import org.bukkit.inventory.ItemStack;

public final class CustomCanPlaceBlockEvent extends BlockEvent implements Cancellable
{
    private static final HandlerList handlers = new HandlerList();
    protected boolean cancel;
    protected boolean canBuild;
    protected Block placedAgainst;
    protected BlockState replacedBlockState;
    protected ItemStack itemInHand;
    protected LivingEntity entity;

    public CustomCanPlaceBlockEvent(Block placedBlock, ItemStack itemInHand, LivingEntity entity, boolean canBuild)
    {
        super(placedBlock);

        this.itemInHand = itemInHand;
        this.entity = entity;
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

    public HandlerList getHandlers()
    {
        return handlers;
    }

    public static HandlerList getHandlerList()
    {
        return handlers;
    }
}
