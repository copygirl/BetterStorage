package net.mcft.copy.betterstorage.api.crafting;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import net.minecraft.item.ItemStack;

public class ShapelessStationRecipe implements IStationRecipe {
	
	public final IRecipeInput[] recipeInput;
	public final ItemStack[] recipeOutput;
	
	public int requiredExperience = 0;
	public int craftingTime = 0;
	
	public ShapelessStationRecipe(IRecipeInput[] input, ItemStack[] output) {
		recipeInput = input;
		recipeOutput = output;
	}
	
	public ShapelessStationRecipe(ItemStack[] output, Object... input) {
		recipeInput = new IRecipeInput[input.length];
		recipeOutput = output;
		for (int i = 0; i < input.length; i++)
			recipeInput[i] = BetterStorageCrafting.makeInput(input[i]);
	}
	
	public ShapelessStationRecipe setRequiredExperience(int experience) {
		requiredExperience = experience;
		return this;
	}
	public ShapelessStationRecipe setCraftingTime(int time) {
		craftingTime = time;
		return this;
	}
	
	// IStationRecipe implementation
	
	@Override
	public StationCrafting checkMatch(ItemStack[] input) {
		IRecipeInput[] requiredInput = new IRecipeInput[9];
		List<IRecipeInput> checklist = new LinkedList<IRecipeInput>(Arrays.asList(recipeInput));
		inputLoop:
		for (int i = 0; i < input.length; i++) {
			ItemStack item = input[i];
			if (item == null) continue;
			Iterator<IRecipeInput> iter = checklist.iterator();
			while (iter.hasNext())
				if ((requiredInput[i] = iter.next()).matches(item)) {
					iter.remove();
					continue inputLoop;
				}
			return null;
		}
		if (!checklist.isEmpty()) return null;
		return new StationCrafting(recipeOutput, recipeInput, requiredExperience, craftingTime);
	}
	
}
