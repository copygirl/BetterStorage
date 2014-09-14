package net.mcft.copy.betterstorage.utils;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public final class InventoryUtils {
	
	private InventoryUtils() {  }
	
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
	
	/** Tries to add an item to the target inventory and returns if it was successful. <br>
	 *  Even if not successful, some of the items may have been put into the inventory,
	 *  and the stack size of the input stack will have been decreased. <br>
	 *  If doAdd is false, no modifications will be done to input stack or target inventory.*/
	public static boolean tryAddItemToInventory(ItemStack stack, IInventory inventory, boolean doAdd) {
		int maxStackSize = Math.min(stack.getMaxStackSize(), inventory.getInventoryStackLimit());
		if (!doAdd) stack = stack.copy();
		// Try to put the stack into existing stacks with the same type.
		if (stack.isStackable()) {
			for (int i = 0; i < inventory.getSizeInventory(); i++) {
				ItemStack invStack = inventory.getStackInSlot(i);
				if (StackUtils.matches(stack, invStack) && (invStack.stackSize < maxStackSize)) {
					int amount = Math.min(invStack.stackSize + stack.stackSize, maxStackSize);
					ItemStack testStack = StackUtils.copyStack(stack, amount);
					if (inventory.isItemValidForSlot(i, testStack)) {
						stack.stackSize -= (testStack.stackSize - invStack.stackSize);
						if (doAdd) inventory.setInventorySlotContents(i, testStack);
					}
				}
				if (stack.stackSize <= 0)
					return true;
			}
		}
		// Try to put the stack into empty slots.
		for (int i = 0; i < inventory.getSizeInventory(); i++) {
			ItemStack invStack = inventory.getStackInSlot(i);
			if (invStack == null) {
				ItemStack testStack = StackUtils.copyStack(stack, Math.min(stack.stackSize, maxStackSize));
				if (inventory.isItemValidForSlot(i, testStack)) {
					stack.stackSize -= testStack.stackSize;
					if (doAdd) inventory.setInventorySlotContents(i, testStack);
				}
			}
			if (stack.stackSize <= 0)
				return true;
		}
		return false;
	}
	
}
