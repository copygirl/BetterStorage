package net.mcft.copy.betterstorage.item.recipe;

import net.mcft.copy.betterstorage.content.Items;
import net.mcft.copy.betterstorage.misc.Constants;
import net.mcft.copy.betterstorage.utils.StackUtils;
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
		int id = StackUtils.get(crafting.getStackInSlot(4), 0, "id");
		ItemStack result = getRecipeOutput().copy();
		StackUtils.set(result, id, "id");
		return result;
	}
	
	public static LockRecipe createLockRecipe() {
		ItemStack gold = new ItemStack(Item.ingotGold);
		ItemStack iron = new ItemStack(Item.ingotIron);
		ItemStack key = new ItemStack(Items.key, 1, Constants.anyDamage);
		ItemStack lock = new ItemStack(Items.lock);
		ItemStack[] items = new ItemStack[]{
			null, gold, null,
			gold, key,  gold,
			gold, iron, gold
		};
		return new LockRecipe(lock, items);
	}
	
}
