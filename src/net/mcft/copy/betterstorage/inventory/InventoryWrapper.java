package net.mcft.copy.betterstorage.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

/** Wraps around an ItemStack array (and optionally base inventory). */
public class InventoryWrapper extends InventoryBetterStorage {

	private ItemStack[] contents;
	private IInventory base;
	
	public InventoryWrapper(String name, ItemStack[] contents, IInventory base) {
		super(name);
		this.contents = contents;
		this.base = base;
	}
	public InventoryWrapper(ItemStack[] contents, IInventory base) {
		this("", contents, base);
	}
	public InventoryWrapper(ItemStack[] contents) {
		this(contents, null);
	}

	@Override
	public String getInvName() {
		return ((base != null) ? base.getInvName() : super.getInvName());
	}
	
	@Override
	public int getSizeInventory() { return contents.length; }
	
	@Override
	public ItemStack getStackInSlot(int slot) { return contents[slot]; }
	@Override
	public void setInventorySlotContents(int slot, ItemStack stack) { contents[slot] = stack; }
	
	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		return ((base != null) ? base.isUseableByPlayer(player) : false);
	}
	
	@Override
	public void onInventoryChanged() { if (base != null) base.onInventoryChanged(); }
	@Override
	public void openChest() { if (base != null) base.openChest(); }
	@Override
	public void closeChest() { if (base != null) base.closeChest(); }

}
