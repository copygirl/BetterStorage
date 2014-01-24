package net.mcft.copy.betterstorage.api.crafting;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

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
	public boolean matches(ItemStack[] input) {
		return findRecipeInputs(input, new IRecipeInput[9]);
	}
	
	@Override
	public void getSampleInput(ItemStack[] input, Random rnd) {
		for (int i = 0; i < recipeInput.length; i++)
			input[i] = recipeInput[i].getSampleInput(rnd);
	}
	
	@Override
	public void getCraftRequirements(ItemStack[] currentInput, IRecipeInput[] requiredInput) {
		findRecipeInputs(currentInput, requiredInput);
	}
	
	@Override
	public int getExperienceDisplay(ItemStack[] input) { return requiredExperience; }
	
	@Override
	public int getCraftingTime(ItemStack[] input) { return craftingTime; }
	
	@Override
	public ItemStack[] getOutput(ItemStack[] input) { return recipeOutput; }
	
	@Override
	public boolean canCraft(ItemStack[] input, ICraftingSource source) {
		return ((requiredExperience <= 0) || ((source.getPlayer() != null) &&
		                                      (source.getPlayer().experienceLevel >= requiredExperience)));
	}
	
	@Override
	public void craft(ItemStack[] input, ICraftingSource source) {
		BetterStorageCrafting.decreaseCraftingMatrix(input, source, this);
		if (requiredExperience != 0)
			source.getPlayer().addExperienceLevel(-requiredExperience);
	}
	
	// Utility functions
	
	public boolean findRecipeInputs(ItemStack[] input, IRecipeInput[] foundInput) {
		List<IRecipeInput> inputRequired = new LinkedList<IRecipeInput>(Arrays.asList(recipeInput));
		inputLoop:
		for (ItemStack item : input) {
			if (item == null) continue;
			Iterator<IRecipeInput> iter = inputRequired.iterator();
			while (iter.hasNext())
				if (iter.next().matches(item)) {
					iter.remove();
					continue inputLoop;
				}
			return false;
		}
		return inputRequired.isEmpty();
	}
	
}
