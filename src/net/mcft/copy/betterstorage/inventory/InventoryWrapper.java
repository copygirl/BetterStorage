package net.mcft.copy.betterstorage.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

/** An inventory that wraps around one or more ItemStack arrays. */
public class InventoryWrapper extends InventoryBetterStorage {
	
	protected final ItemStack[][] allContents;
	
	private int totalSize = 0;
	
	public InventoryWrapper(String name, ItemStack[]... allContents) {
		super(name);
		this.allContents = allContents;
		
		for (ItemStack[] contents : allContents)
			totalSize += contents.length;
	}
	public InventoryWrapper(ItemStack[]... allContents) {
		this("", allContents);
	}
	
	@Override
	public int getSizeInventory() { return totalSize; }
	
	// Really hacky way to make good looking (?) code.
	private int tempSlot;
	private ItemStack[] getContentsAndSlot(int slot) {
		if (slot < 0 || slot >= totalSize)
			throw new IndexOutOfBoundsException("slot");
		tempSlot = slot;
		for (int i = 0; ; i++) {
			ItemStack[] contents = allContents[i];
			if (tempSlot < contents.length) return contents;
			tempSlot -= contents.length;
		}
	}
	
	@Override
	public ItemStack getStackInSlot(int slot) {
		return getContentsAndSlot(slot)[tempSlot];
	}
	@Override
	public void setInventorySlotContents(int slot, ItemStack stack) {
		getContentsAndSlot(slot)[tempSlot] = stack;
	}
	
	@Override
	public boolean isUseableByPlayer(EntityPlayer player) { return false; }
	@Override
	public void onInventoryChanged() {  }
	@Override
	public void openChest() {  }
	@Override
	public void closeChest() {  }
	
}
