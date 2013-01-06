package net.mcft.copy.betterstorage.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public interface ILockable {
	
	/** Returns the lock of this container. */
	public ItemStack getLock();
	/** Sets the lock of this container. */
	public void setLock(ItemStack lock);
	
	/** Returns if this container is locked. */
	public boolean isLocked();
	/** Returns if this container can be locked with this lock. */
	public boolean canLock(ItemStack lock);
	/** Returns if this container can be used by this player. */
	public boolean canUse(EntityPlayer player);
	
}
