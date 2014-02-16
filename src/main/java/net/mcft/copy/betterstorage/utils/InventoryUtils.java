package net.mcft.copy.betterstorage.utils;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public final class InventoryUtils {
	
	private InventoryUtils() {  }
	
	/** Standard decrStackSize which modifies stackSize directly. */
	public static ItemStack unsafeDecreaseStackSize(IInventory inventory, int slot, int amount) {
		ItemStack stack = inventory.getStackInSlot(slot);
		if (stack == null) return null;
		amount = Math.min(amount, stack.stackSize);
		if (amount < stack.stackSize) {
			stack.stackSize -= amount;
			stack = StackUtils.copyStack(stack, amount);
		} else inventory.setInventorySlotContents(slot, null);
		return stack;
	}
	
	/** Returns if the inventory has a specific item. */
	public static boolean hasItem(IInventory inventory, Item item) {
		for (int i = 0; i < inventory.getSizeInventory(); i++) {
			ItemStack stack = inventory.getStackInSlot(i);
			if (stack != null && stack.getItem() == item) return true;
		}
		return false;
	}
	/** Finds and returns a specific item in the inventory, null if none found. */
	public static ItemStack findItem(IInventory inventory, Item item) {
		for (int i = 0; i < inventory.getSizeInventory(); i++) {
			ItemStack stack = inventory.getStackInSlot(i);
			if (stack != null && stack.getItem() == item) return stack;
		}
		return null;
	}
	/** Finds and returns the slot a specific item is in, -1 if none found. */
	public static int findItemSlot(IInventory inventory, Item item) {
		for (int i = 0; i < inventory.getSizeInventory(); i++) {
			ItemStack stack = inventory.getStackInSlot(i);
			if (stack != null && stack.getItem() == item) return i;
		}
		return -1;
	}

	/** Returns a list of items of the specific type in the inventory. */
	public static int countItems(IInventory inventory, Item item) {
		int count = 0;
		for (int i = 0; i < inventory.getSizeInventory(); i++) {
			ItemStack stack = inventory.getStackInSlot(i);
			if (stack != null && stack.getItem() == item) count++;
		}
		return count;
	}
	/** Returns a list of items of the specific type in the inventory. */
	public static List<ItemStack> findItems(IInventory inventory, Item item) {
		List<ItemStack> list = new ArrayList<ItemStack>();
		for (int i = 0; i < inventory.getSizeInventory(); i++) {
			ItemStack stack = inventory.getStackInSlot(i);
			if (stack != null && stack.getItem() == item) list.add(stack);
		}
		return list;
	}
	/** Returns a list of slot indices which hold a specific item type in the inventory. */
	public static List<Integer> findItemSlots(IInventory inventory, Item item) {
		List<Integer> list = new ArrayList<Integer>();
		for (int i = 0; i < inventory.getSizeInventory(); i++) {
			ItemStack stack = inventory.getStackInSlot(i);
			if (stack != null && stack.getItem() == item) list.add(i);
		}
		return list;
	}
	
	/** Returns a list of dyes in the inventory. */
	public static List<ItemStack> findDyes(IInventory inventory) {
		List<ItemStack> list = new ArrayList<ItemStack>();
		for (int i = 0; i < inventory.getSizeInventory(); i++) {
			ItemStack stack = inventory.getStackInSlot(i);
			if (DyeUtils.isDye(stack)) list.add(stack);
		}
		return list;
	}
	
}
