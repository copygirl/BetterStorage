package net.mcft.copy.betterstorage.utils;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public class InventoryUtils {
	
	/** Standard decrStackSize which modifies stackSize directly. */
	public static ItemStack unsafeDecreaseStackSize(IInventory inventory, int slot, int amount) {
		ItemStack stack = inventory.getStackInSlot(slot);
		if (stack == null) return null;
		amount = Math.min(amount, stack.stackSize);
		if (amount < stack.stackSize) {
			stack.stackSize -= amount;
			stack = stack.copy();
			stack.stackSize = amount;
		} else inventory.setInventorySlotContents(slot, null);
		return stack;
	}
	
}
