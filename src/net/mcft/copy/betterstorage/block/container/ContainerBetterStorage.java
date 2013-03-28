package net.mcft.copy.betterstorage.block.container;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.mcft.copy.betterstorage.client.GuiBetterStorage;
import net.mcft.copy.betterstorage.utils.InventoryUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerBetterStorage extends Container {
	
	public IInventory inventory;
	
	@SideOnly(Side.CLIENT)
	public GuiBetterStorage updateGui;
	
	public ContainerBetterStorage(EntityPlayer player, IInventory inventory, int columns, int rows) {
		this.inventory = inventory;
		
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
		
		inventory.openChest();
	}
	public ContainerBetterStorage(EntityPlayer player, IInventory inventory, int columns, int rows, GuiBetterStorage gui) {
		this(player, inventory, columns, rows);
		setUpdateGui(gui);
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
	public ItemStack slotClick(int slotId, int button, int special, EntityPlayer player) {
		if ((button == 0 || button == 1) && special == 0 && slotId >= 0) {
			Slot slot = (Slot)inventorySlots.get(slotId);
			if (slot != null) {
				ItemStack slotStack = slot.getStack();
				ItemStack holding = player.inventory.getItemStack();
				if (slotStack != null && holding != null &&
				    slot.canTakeStack(player) && slot.isItemValid(holding) &&
				    slotStack.itemID == holding.itemID &&
				    slotStack.getItemDamage() == holding.getItemDamage() &&
				    ItemStack.areItemStackTagsEqual(slotStack, holding)) {
					int amount = ((button == 0) ? holding.stackSize : 1);
					amount = Math.min(amount, slot.getSlotStackLimit() - slotStack.stackSize);
					amount = Math.min(amount, slotStack.getMaxStackSize() - slotStack.stackSize);
					if (amount > 0) {
						if ((holding.stackSize -= amount) <= 0)
							player.inventory.setItemStack(null);
						slot.putStack(InventoryUtils.copyStack(slotStack, slotStack.stackSize + amount));
					}
					return slotStack;
				}
			}
		}
		return super.slotClick(slotId, button, special, player);
	}
	
	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return inventory.isUseableByPlayer(player);
	}
	
	@Override
	public void onCraftGuiClosed(EntityPlayer player) {
		super.onCraftGuiClosed(player);
		inventory.closeChest();
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public void updateProgressBar(int par1, int par2) {
		if (updateGui != null) updateGui.update(par1, par2);
	}
	
	@SideOnly(Side.CLIENT)
	public void setUpdateGui(GuiBetterStorage gui) {
		updateGui = gui;
	}
	
}
