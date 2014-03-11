package net.mcft.copy.betterstorage.item.recipe;

import net.mcft.copy.betterstorage.content.BetterStorageItems;
import net.mcft.copy.betterstorage.item.ItemBetterStorage;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraftforge.oredict.OreDictionary;

public class LockRecipe extends ShapedRecipes {
	
	private LockRecipe(ItemStack output, ItemStack[] items) {
		super(3, 3, items, output);
	}
	
	@Override
	public ItemStack getCraftingResult(InventoryCrafting crafting) {
		int id = ItemBetterStorage.getID(crafting.getStackInSlot(4));
		ItemStack result = getRecipeOutput().copy();
		ItemBetterStorage.setID(result, id);
		return result;
	}
	
	public static LockRecipe createLockRecipe() {
		ItemStack gold = new ItemStack(Items.gold_ingot);
		ItemStack iron = new ItemStack(Items.iron_ingot);
		ItemStack key = new ItemStack(BetterStorageItems.key, 1, OreDictionary.WILDCARD_VALUE);
		ItemStack lock = new ItemStack(BetterStorageItems.lock);
		ItemStack[] items = new ItemStack[]{
			null, gold, null,
			gold, key,  gold,
			gold, iron, gold
		};
		return new LockRecipe(lock, items);
	}
	
}
