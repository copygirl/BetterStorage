package net.mcft.copy.betterstorage.api;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public interface ILock {
	
	/** Returns a string describing the lock's type or how it functions. <br>
	 *  Currently there is only "normal" locks, which work with damage. */
	public String getLockType();
	
	/** Gets called after a player tries to open a lock, successfully or not. */
	public void onUnlock(ItemStack lock, ItemStack key, ILockable lockable,
	                     EntityPlayer player, boolean success);
	
	/** Applies any effects from the lock. <br>
	 *  Power is 1 when a player tries to open it unsuccessfully, 2 when
	 *  the player failed trying to unlock it using a key and 3 when
	 *  the player is trying to break the container. */
	public void applyEffects(ItemStack lock, ILockable lockable,
	                         EntityPlayer player, int power);
	
}
