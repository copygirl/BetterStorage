package net.mcft.copy.betterstorage.api.crafting;

import java.util.ArrayList;
import java.util.List;

import net.mcft.copy.betterstorage.utils.StackUtils;
import net.mcft.copy.betterstorage.utils.WorldUtils;
import net.minecraft.block.Block;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public final class BetterStorageCrafting {
	
	public static final List<IStationRecipe> recipes = new ArrayList<IStationRecipe>();
	
	private BetterStorageCrafting() {  }
	
	/** Adds a station recipe to the recipe list. */
	public static void addStationRecipe(IStationRecipe recipe) { recipes.add(recipe); }
	
	/** Returns a recipe matching the input, or null if none was found. */
	public static IStationRecipe findMatchingStationRecipe(ItemStack[] input) {
		for (IStationRecipe recipe : recipes)
			if (recipe.matches(input)) return recipe;
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
		else throw new IllegalArgumentException("Argument is not a IRecipeInput, ItemStack, Item, Block or String.");
	}
	
	/** Decreases the stack size of every item stack in the crafting matrix,
	 *  or in case the item is a container item, replaces it with that. <br>
	 *  This does the same as in the vanilla crafting table. */
	public static void decreaseCraftingMatrix(ItemStack[] crafting, ICraftingSource source, IRecipeInput[] craftReq) {
		for (int i = 0; i < crafting.length; i++) {
			ItemStack stack = crafting[i];
			if (stack == null) continue;
			Item item = stack.getItem();
			ItemStack containerItem = ItemStack.copyItemStack(item.getContainerItemStack(stack));
			stack.stackSize -= ((craftReq != null) ? craftReq[i].getAmount() : 1);
			if (containerItem == null) continue;
			if (!item.doesContainerItemLeaveCraftingGrid(stack) ||
			    !tryAddItemToInventory(source, containerItem)) {
				if (stack.stackSize <= 0) crafting[i] = containerItem;
				else if (source.getWorld() != null)
					WorldUtils.spawnItem(source.getWorld(), source.getX(), source.getY(), source.getZ(), containerItem);
			}
		}
	}
	/** Decreases the stack size of every item stack in the crafting matrix,
	 *  or in case the item is a container item, replaces it with that. <br>
	 *  This does the same as in the vanilla crafting table. */
	public static void decreaseCraftingMatrix(ItemStack[] crafting, ICraftingSource source, IStationRecipe recipe) {
		IRecipeInput[] craftReq = new IRecipeInput[9];
		recipe.getCraftRequirements(crafting, craftReq);
		decreaseCraftingMatrix(crafting, source, craftReq);
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
