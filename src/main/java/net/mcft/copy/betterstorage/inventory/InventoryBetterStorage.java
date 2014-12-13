package net.mcft.copy.betterstorage.inventory;

import net.mcft.copy.betterstorage.utils.StackUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;

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
	public String getName() { return name; }
	
	@Override
	public IChatComponent getDisplayName() {
		return new ChatComponentText(getName());
	}
	
	@Override
	public int getInventoryStackLimit() { return 64; }
	
	@Override
	public ItemStack decrStackSize(int slot, int amount) {
		ItemStack stack = getStackInSlot(slot);
		if (stack == null) return null;
		amount = Math.min(amount, stack.stackSize);
		if (amount < stack.stackSize) {
			stack.stackSize -= amount;
			stack = StackUtils.copyStack(stack, amount);
			markDirty();
		} else setInventorySlotContents(slot, null);
		return stack;
	}
	
	@Override
	public ItemStack getStackInSlotOnClosing(int slot) {
		ItemStack stack = getStackInSlot(slot);
		if (stack == null) return null;
		setInventorySlotContents(slot, null);
		return stack;
	}
	
	@Override
	public boolean hasCustomName() { return false; }
	
	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack) { return true; }
	
	@Override
	public void openInventory(EntityPlayer playerIn) { }
	
	@Override
	public void closeInventory(EntityPlayer playerIn) { }
	
	@Override
	public int getField(int id) {
		return 0;
	}
	
	@Override
	public void setField(int id, int value) {}
	
	@Override
	public int getFieldCount() {
		return 0;
	}
}
