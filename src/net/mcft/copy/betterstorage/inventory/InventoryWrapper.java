package net.mcft.copy.betterstorage.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public class InventoryWrapper implements IInventory {
	
	public final IInventory base;
	
	public InventoryWrapper(IInventory base) {
		this.base = base;
	}
	
	@Override
	public String getInvName() { return base.getInvName(); }
	@Override
	public boolean isInvNameLocalized() { return base.isInvNameLocalized(); }
	
	@Override
	public int getSizeInventory() { return base.getSizeInventory(); }
	@Override
	public ItemStack getStackInSlot(int slot) { return base.getStackInSlot(slot); }
	@Override
	public ItemStack decrStackSize(int slot, int amount) { return base.decrStackSize(slot, amount); }
	@Override
	public void setInventorySlotContents(int slot, ItemStack stack) { base.setInventorySlotContents(slot, stack); }
	@Override
	public ItemStack getStackInSlotOnClosing(int slot) { return base.getStackInSlotOnClosing(slot); }
	@Override
	public int getInventoryStackLimit() { return base.getInventoryStackLimit(); }

	@Override
	public boolean isUseableByPlayer(EntityPlayer player) { return base.isUseableByPlayer(player); }
	@Override
	public boolean isStackValidForSlot(int slot, ItemStack stack) { return base.isStackValidForSlot(slot, stack); }
	
	@Override
	public void onInventoryChanged() { base.onInventoryChanged(); }
	@Override
	public void openChest() { base.openChest(); }
	@Override
	public void closeChest() { base.closeChest(); }
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof InventoryWrapper)) return false;
		InventoryWrapper inv = (InventoryWrapper)obj;
		return (base.equals(inv.base));
	}
}
