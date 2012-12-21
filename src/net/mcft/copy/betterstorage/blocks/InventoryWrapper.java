package net.mcft.copy.betterstorage.blocks;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public class InventoryWrapper implements IInventory {
	
	private TileEntityReinforcedChest chest;
	
	public InventoryWrapper(TileEntityReinforcedChest chest) {
		this.chest = chest;
	}
	
	@Override
	public int getSizeInventory() { return chest.contents.length; }
	@Override
	public ItemStack getStackInSlot(int slot) { return chest.contents[slot]; }
	@Override
	public ItemStack decrStackSize(int slot, int amount) {
		ItemStack stack = getStackInSlot(slot);
		if (stack == null) return null;
		amount = Math.min(amount, stack.stackSize);
		if (amount < stack.stackSize) {
			stack.stackSize -= amount;
			stack = stack.copy();
			stack.stackSize = amount;
		} else setInventorySlotContents(slot, null);
		return stack;
	}
	@Override
	public ItemStack getStackInSlotOnClosing(int var1) { return null; }
	@Override
	public void setInventorySlotContents(int slot, ItemStack stack) { chest.contents[slot] = stack; }
	@Override
	public String getInvName() { return ""; }
	@Override
	public int getInventoryStackLimit() { return 64; }
	@Override
	public void onInventoryChanged() { chest.onInventoryChanged(); }
	@Override
	public boolean isUseableByPlayer(EntityPlayer player) { return chest.isUseableByPlayer(player); }
	@Override
	public void openChest() { chest.openChest(); }
	@Override
	public void closeChest() { chest.closeChest(); }

}
