package net.mcft.copy.betterstorage.api.crafting;

import java.util.ArrayList;
import java.util.List;

import net.mcft.copy.betterstorage.utils.StackUtils;
import net.minecraft.block.Block;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public final class BetterStorageCrafting {
	
	public static final List<IStationRecipe> recipes = new ArrayList<IStationRecipe>();
	
	private BetterStorageCrafting() {  }
	
	/** Adds a station recipe to the recipe list. */
	public static void addStationRecipe(IStationRecipe recipe) { recipes.add(recipe); }
	
	/** Creates and returns a crafting matching the input, or null if none was found. */
	public static StationCrafting findMatchingStationCrafting(ItemStack[] input) {
		for (IStationRecipe recipe : recipes) {
			StationCrafting crafting = recipe.checkMatch(input);
			if (crafting != null) return crafting;
		}
		return null;
	}
	
	// Utility functions
	
	/** Returns an IRecipeInput from the object. <br>
	 *  Can be an IRecipeInput, ItemStack, Item, Block or ore dictionary String. */
	public static IRecipeInput makeInput(Object obj) {
		if (obj instanceof IRecipeInput) return (IRecipeInput)obj;
		else if (obj instanceof ItemStack) return new RecipeInputItemStack((ItemStack)obj);
		else if (obj instanceof Item) return new RecipeInputItemStack(new ItemStack((Item)obj));
		else if (obj instanceof Block) return new RecipeInputItemStack(new ItemStack((Block)obj));
		else if (obj instanceof String) return new RecipeInputOreDict((String)obj, 1);
		else throw new IllegalArgumentException(
				String.format("Argument is %s, not an IRecipeInput, ItemStack, Item, Block or String.",
				              ((obj != null) ? obj.getClass().getSimpleName() : "null")));
	}
	
	public static boolean tryAddItemToInventory(ICraftingSource source, ItemStack stack) {
		IInventory inventory = source.getInventory();
		if (inventory == null) return false;
		// Try to put the stack into existing stacks with the same type.
		if (stack.isStackable()) {
			for (int i = 0; i < inventory.getSizeInventory(); i++) {
				ItemStack invStack = inventory.getStackInSlot(i);
				int maxStackSize = Math.min(stack.getMaxStackSize(), inventory.getInventoryStackLimit());
				if (StackUtils.matches(stack, invStack) && (invStack.stackSize < maxStackSize)) {
					int amount = Math.min(invStack.stackSize + stack.stackSize, maxStackSize);
					ItemStack testStack = StackUtils.copyStack(stack, amount);
					if (inventory.isItemValidForSlot(i, testStack)) {
						stack.stackSize -= (testStack.stackSize - invStack.stackSize);
						inventory.setInventorySlotContents(i, testStack);
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
				int maxStackSize = Math.min(stack.getMaxStackSize(), inventory.getInventoryStackLimit());
				ItemStack testStack = StackUtils.copyStack(stack, Math.min(stack.stackSize, maxStackSize));
				if (inventory.isItemValidForSlot(i, testStack)) {
					stack.stackSize -= testStack.stackSize;
					inventory.setInventorySlotContents(i, testStack);
				}
			}
			if (stack.stackSize <= 0)
				return true;
		}
		return false;
	}
	
}
