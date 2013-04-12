package net.mcft.copy.betterstorage.api;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public interface ILockable {
	
	/** Returns the lock of this container, or null if there is none. */
	public ItemStack getLock();
	
	/** Returns if this container can be locked with this lock. */
	public boolean isLockValid(ItemStack lock);
	
	/** Sets the lock of this container. <br>
	 *  Has no effect of canSetLock() returns false. */
	public void setLock(ItemStack lock);
	
	/** Returns if this container can be used by the player without using a key,
	 *  for example, while the container is being held open by another player. */
	public boolean canUse(EntityPlayer player);
	
	/** Makes the container emit a redstone signal for 10 ticks. */
	public void applyTrigger();
	
}
