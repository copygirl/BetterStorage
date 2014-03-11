package net.mcft.copy.betterstorage.item.recipe;

import java.util.ArrayList;
import java.util.List;

import net.mcft.copy.betterstorage.api.BetterStorageUtils;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;

/** A mixture of ShapedRecipe and ShapelessRecipe. */
public abstract class ComboRecipe implements IRecipe {
	
	public int width, height;
	public ItemStack[] recipe;
	public ItemStack result;
	
	public ComboRecipe(int width, int height, ItemStack[] recipe, ItemStack result) {
		this.width = width;
		this.height = height;
		this.recipe = recipe;
		this.result = result;
	}
	
	@Override
	public int getRecipeSize() { return width * height; }
	@Override
	public ItemStack getRecipeOutput() { return result; }
	
	@Override
	public boolean matches(InventoryCrafting crafting, World world) {
		for (int x = 0; x <= 3 - width; ++x)
			for (int y = 0; y <= 3 - height; ++y) {
				if (checkMatch(crafting, x, y, false)) return true;
				if (canMirror() && checkMatch(crafting, x, y, true)) return true;
			}
		return false;
	}
	
	/** Returns if the recipe can be mirrored. */
	public boolean canMirror() { return false; }
	
	/** Returns if the unmatched items are valid. */
	public abstract boolean checkShapelessItems(InventoryCrafting crafting, List<ItemStack> shapelessItems);
	
	private boolean checkMatch(InventoryCrafting crafting, int startX, int startY, boolean mirror) {
		List<ItemStack> shapelessItems = new ArrayList<ItemStack>();
		for (int x = 0; x < 3; ++x)
			for (int y = 0; y < 3; ++y) {
				int recipeX = x - startX;
				int recipeY = y - startY;
				ItemStack recipeStack = null;
				if ((recipeX >= 0) && (recipeY >= 0) && (recipeX < width) && (recipeY < height)) {
					if (mirror) recipeStack = recipe[width - recipeX - 1 + recipeY * width];
					else recipeStack = recipe[recipeX + recipeY * width];
				}
				ItemStack craftingStack = crafting.getStackInRowAndColumn(x, y);
				if ((craftingStack != null) || (recipeStack != null)) {
					if ((recipeStack != null) && (craftingStack == null)) return false;
					if ((recipeStack == null) && (craftingStack != null))
						shapelessItems.add(craftingStack);
					else if (!BetterStorageUtils.wildcardMatch(recipeStack, craftingStack)) return false;
				}
			}
		if ((shapelessItems.size() > 0) &&
		    !checkShapelessItems(crafting, shapelessItems)) return false;
		return true;
	}
	
}
