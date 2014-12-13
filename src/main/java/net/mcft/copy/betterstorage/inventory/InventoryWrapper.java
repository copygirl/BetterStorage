package net.mcft.copy.betterstorage.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IChatComponent;

public class InventoryWrapper implements IInventory {
	
	public final IInventory base;
	
	private boolean overwriteName = false;
	private String title;
	private boolean localized;
	
	public InventoryWrapper(IInventory base) {
		this.base = base;
	}
	public InventoryWrapper(IInventory base, String title, boolean localized) {
		this(base);
		overwriteName = true;
		this.title = title;
		this.localized = localized;
	}
	
	@Override
	public String getName() { return (overwriteName ? title : base.getName()); }
	@Override
	public boolean hasCustomName() { return (overwriteName ? localized : base.hasCustomName()); }
	
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
	public boolean isItemValidForSlot(int slot, ItemStack stack) { return base.isItemValidForSlot(slot, stack); }
	
	@Override
	public void markDirty() { base.markDirty(); }
	@Override
	public void openInventory(EntityPlayer player) { base.openInventory(player); }
	@Override
	public void closeInventory(EntityPlayer player) { base.closeInventory(player); }
	
	@Override
	public IChatComponent getDisplayName() { return base.getDisplayName(); }
	@Override
	public int getField(int id) { return base.getField(id); }
	@Override
	public void setField(int id, int value) { base.setField(id, value); }
	@Override
	public int getFieldCount() { return base.getFieldCount(); }
	@Override
	public void clear() { base.clear(); }
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof InventoryWrapper)) return false;
		InventoryWrapper inv = (InventoryWrapper)obj;
		return (base.equals(inv.base));
	}
}
