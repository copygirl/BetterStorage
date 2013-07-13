package net.mcft.copy.betterstorage.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotBetterStorage extends Slot {
	
	private boolean isProtected = false;
	
	public SlotBetterStorage(IInventory inventory, int index, int x, int y) {
		super(inventory, index, x, y);
	}
	
	/** Prevents the slot from being interacted with. 
	 * @return */
	public SlotBetterStorage setProtected() {
		isProtected = true;
		return this;
	}
	
	@Override
	public boolean isItemValid(ItemStack stack) {
		return (!isProtected && inventory.isItemValidForSlot(slotNumber, stack));
	}
	
	@Override
	public boolean canTakeStack(EntityPlayer player) {
		// Not sure if isItemValidForSlot can / should be used to check
		// if items can be taken out, but I'll just use it anyway.
		return (!isProtected && inventory.isItemValidForSlot(slotNumber, null));
	}
	
}
