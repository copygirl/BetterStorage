package net.mcft.copy.betterstorage.inventory;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

/** An IInventory that combines multiple inventories into one. <br>
 *  Only supports fixed sized inventories. */
public class InventoryCombined<T extends IInventory> extends InventoryBetterStorage implements Iterable<T> {
	
	private List<T> inventories;
	
	private int totalSize;
	private int[] individualSizes;
	
	// Used by getInventoryAndSlot to make clean
	// and at the same time really dirty code.
	private int tempSlot;
	
	public InventoryCombined(String name, T... inventories) {
		super(name);
		this.inventories = Arrays.asList(inventories);
		
		totalSize = 0;
		individualSizes = new int[inventories.length];
		for (int i = 0; i < inventories.length; i++) {
			int size = inventories[i].getSizeInventory();
			totalSize += size;
			individualSizes[i] += size;
		}
	}
	
	private IInventory getInventoryAndSlot(int slot) {
		if (slot < 0 || slot >= totalSize)
			throw new IndexOutOfBoundsException("slot");
		tempSlot = slot;
		for (int i = 0;; i++) {
			if (tempSlot < individualSizes[i])
				return inventories.get(i);
			tempSlot -= individualSizes[i];
		}
	}
	
	/** Returns if the inventory is part of this InventoryCombined. */
	public boolean contains(T inventory) {
		for (IInventory i : this)
			if (inventory == this) return true;
		return false;
	}
	
	// IInventory implementation
	
	@Override
	public int getSizeInventory() { return totalSize; }
	
	@Override
	public ItemStack getStackInSlot(int slot) {
		return getInventoryAndSlot(slot).getStackInSlot(tempSlot);
	}
	@Override
	public void setInventorySlotContents(int slot, ItemStack stack) {
		getInventoryAndSlot(slot).setInventorySlotContents(tempSlot, stack);
	}
	@Override
	public ItemStack decrStackSize(int slot, int amount) {
		return getInventoryAndSlot(slot).decrStackSize(tempSlot, amount);
	}
	@Override
	public ItemStack getStackInSlotOnClosing(int slot) {
		return getInventoryAndSlot(slot).getStackInSlotOnClosing(tempSlot);
	}
	
	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		for (IInventory i : this)
			if (!i.isUseableByPlayer(player))
				return false;
		return true;
	}
	
	@Override
	public void onInventoryChanged() { for (IInventory i : this) i.onInventoryChanged(); }
	@Override
	public void openChest() { for (IInventory i : this) i.openChest(); }
	@Override
	public void closeChest() { for (IInventory i : this) i.closeChest(); }
	
	// Iterator implementation
	
	@Override
	public Iterator<T> iterator() {
		return inventories.iterator();
	}

}
