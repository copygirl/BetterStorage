package net.mcft.copy.betterstorage.inventory;

import net.mcft.copy.betterstorage.utils.InventoryUtils;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

/** Basic IInventory with name and default
 *  decrStackSize and getStackInSlotOnClosing, */
public abstract class InventoryBetterStorage implements IInventory {
	
	private String name;
	
	public InventoryBetterStorage(String name) {
		this.name = name;
	}
	public InventoryBetterStorage() {
		this("");
	}
	
	@Override
	public String getInventoryName() { return name; }
	
	@Override
	public int getInventoryStackLimit() { return 64; }
	
	@Override
	public ItemStack decrStackSize(int slot, int amount) {
		return InventoryUtils.unsafeDecreaseStackSize(this, slot, amount);
	}
	
	@Override
	public ItemStack getStackInSlotOnClosing(int slot) {
		ItemStack stack = getStackInSlot(slot);
		if (stack == null) return null;
		setInventorySlotContents(slot, null);
		return stack;
	}
	
	@Override
	public boolean hasCustomInventoryName() { return false; }
	
	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack) { return true; }
	
}
