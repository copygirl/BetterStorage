package net.mcft.copy.betterstorage.item;

import net.mcft.copy.betterstorage.BetterStorage;
import net.mcft.copy.betterstorage.misc.Constants;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ShapedRecipes;

public class LockRecipe extends ShapedRecipes {
	
	private LockRecipe(ItemStack output, ItemStack[] items) {
		super(3, 3, items, output);
	}
	
	@Override
	public ItemStack getCraftingResult(InventoryCrafting crafting) {
		ItemStack result = getRecipeOutput().copy();
		result.setItemDamage(crafting.getStackInSlot(4).getItemDamage());
		return result;
	}
	
	public static LockRecipe createLockRecipe() {
		ItemStack gold = new ItemStack(Item.ingotGold);
		ItemStack iron = new ItemStack(Item.ingotIron);
		ItemStack key = new ItemStack(BetterStorage.key, 1, Constants.anyDamage);
		ItemStack lock = new ItemStack(BetterStorage.lock);
		ItemStack[] items = new ItemStack[]{
			null, gold, null,
			gold, key,  gold,
			gold, iron, gold
		};
		return new LockRecipe(lock, items);
	}
	
}
