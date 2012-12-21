package net.mcft.copy.betterstorage.blocks;

import net.mcft.copy.betterstorage.ItemIdentifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

/** An inventory interface built for machines accessing the crate pile. */
public class InventoryCrateBlockView implements IInventory {
	
	private CratePileData data;
	
	public InventoryCrateBlockView(CratePileData data) {
		this.data = data;
	}
	
	@Override
	public int getSizeInventory() {
		// Return lower inventory size so machines don't look at all the
		// empty slots, looking for specific item or stacks to merge with.
		return Math.min(data.getOccupiedSlots() + 10, data.getCapacity());
	}
	@Override
	public String getInvName() { return "container.crate"; }
	@Override
	public int getInventoryStackLimit() { return 64; }
	
	@Override
	public boolean isUseableByPlayer(EntityPlayer player) { return false; }
	
	@Override
	public ItemStack getStackInSlot(int slot) {
		if (slot < 0 || slot >= getSizeInventory()) return null;
		// If there's space in the inventory, make the first slot be empty.
		// That way machines will easily find a slot to put items in.
		if (data.getFreeSlots() > 0 && --slot == -1) return null;
		if (slot >= data.getOccupiedSlots()) return null;
		ItemStack stack;
		int stackSize;
		int numItems = data.getNumItems();
		if (slot < numItems) {
			stack = data.getItemStack(slot);
			int stackLimit = stack.getItem().getItemStackLimit();
			stackSize = (stack.stackSize - 1) % stackLimit + 1;
		} else {
			slot -= numItems;
			int index = 0;
			do {
				stack = data.getItemStack(index);
				int numStacks = ItemIdentifier.calcNumStacks(stack) - 1;
				if (slot < numStacks) break;
				slot -= numStacks;
			} while (++index < data.getNumItems());
			stackSize = stack.getItem().getItemStackLimit();
		}
		stack = stack.copy();
		stack.stackSize = stackSize;
		return stack;
	}
	
	@Override
	public ItemStack getStackInSlotOnClosing(int slot) { return null; }
	
	@Override
	public ItemStack decrStackSize(int slot, int amount) {
		ItemStack stack = getStackInSlot(slot);
		if (stack == null) return null;
		amount = Math.min(amount, stack.stackSize);
		return data.removeItems(stack, amount);
	}
	
	@Override
	public void setInventorySlotContents(int slot, ItemStack stack) {
		if (slot < 0 || slot >= getSizeInventory()) return;
		ItemStack oldStack = getStackInSlot(slot);
		if (oldStack != null) data.removeItems(oldStack);
		data.addItems(stack);
	}
	
	@Override
	public void onInventoryChanged() { }
	
	@Override
	public void openChest() { }
	@Override
	public void closeChest() { }
	
}
