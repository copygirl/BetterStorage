package net.mcft.copy.betterstorage.blocks;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerReinforcedChest extends Container {
	
	private IInventory inventory;
	
	public ContainerReinforcedChest(EntityPlayer player, int columns, int rows, IInventory inventory) {
		this.inventory = inventory;
		inventory.openChest();
		
		for (int y = 0; y < rows; y++)
			for (int x = 0; x < columns; x++)
				addSlotToContainer(new Slot(inventory, x + y * columns,
						8 + x * 18, 18 + y * 18));
		
		// Add player inventory
		for (int y = 0; y < 3; y++)
			for (int x = 0; x < 9; x++)
				addSlotToContainer(new Slot(player.inventory, 9 + x + y * 9,
						8 + x * 18 + (columns - 9) * 9, rows * 18 + y * 18 + 32));
		for (int x = 0; x < 9; x++)
			addSlotToContainer(new Slot(player.inventory, x,
					8 + x * 18 + (columns - 9) * 9, rows * 18 + 90));
	}
	
	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return inventory.isUseableByPlayer(player);
	}
	
	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int slotId) {
		ItemStack stack = null;
		Slot slot = (Slot)inventorySlots.get(slotId);
		
		// If slot isn't empty and item can be stacked.
		if (slot != null && slot.getHasStack()) {
			ItemStack slotStack = slot.getStack();
			stack = slotStack.copy();
			// If slot is in the container inventory, try to transfer the item to the player.
			if (slotId < inventory.getSizeInventory()) {
				if (!mergeItemStack(slotStack, inventory.getSizeInventory(), inventorySlots.size(), true))
					return null;
			// If slot is in the player inventory, try to transfer the item to the container.
			} else if (!mergeItemStack(slotStack, 0, inventory.getSizeInventory(), false))
				return null;
			if (slotStack.stackSize != 0)
				slot.onSlotChanged();
			else slot.putStack(null);
		}
		
		return stack;
	}
	
	@Override
	public void onCraftGuiClosed(EntityPlayer player) {
		super.onCraftGuiClosed(player);
		inventory.closeChest();
	}
	
}
