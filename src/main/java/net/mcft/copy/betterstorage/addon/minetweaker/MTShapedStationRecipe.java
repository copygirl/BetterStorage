package net.mcft.copy.betterstorage.addon.minetweaker;

import minetweaker.api.item.IIngredient;
import net.mcft.copy.betterstorage.api.crafting.IRecipeInput;
import net.mcft.copy.betterstorage.api.crafting.RecipeBounds;
import net.mcft.copy.betterstorage.api.crafting.ShapedStationRecipe;
import net.mcft.copy.betterstorage.api.crafting.StationCrafting;
import net.minecraft.item.ItemStack;

public class MTShapedStationRecipe extends ShapedStationRecipe {
	
	private static int width, height;
	
	public MTShapedStationRecipe(IIngredient[][] ingredients, ItemStack[] output, int experience, int craftingTime, boolean mirrored) {
		super(toRecipeInputs(ingredients), width, height, mirrored, output);
		setRequiredExperience(experience);
		setCraftingTime(craftingTime);
	}
	private static IRecipeInput[] toRecipeInputs(IIngredient[][] ingredients) {
		width = ingredients[0].length;
		height = ingredients.length;
		IRecipeInput[] inputs = new IRecipeInput[width * height];
		for (int x = 0; x < width; x++)
			for (int y = 0; y < height; y++)
				if (ingredients[y][x] != null)
					inputs[x + y * width] = new RecipeInputIngredient(ingredients[y][x]);
		return inputs;
	}
	
	@Override
	public StationCrafting checkMatch(ItemStack[] input, RecipeBounds bounds) {
		StationCrafting crafting = super.checkMatch(input, bounds);
		return ((crafting != null) ? new MTStationCrafting(crafting) : null);
	}
	
}
