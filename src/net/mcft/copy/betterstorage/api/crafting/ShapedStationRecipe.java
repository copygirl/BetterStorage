package net.mcft.copy.betterstorage.api.crafting;

import java.util.Random;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class ShapedStationRecipe implements IStationRecipe {
	
	public final IRecipeInput[] recipeInput;
	public final ItemStack[] recipeOutput;
	public final int recipeWidth;
	public final int recipeHeight;
	
	public int requiredExperience = 0;
	
	public ShapedStationRecipe(IRecipeInput[] input, int width, int height, ItemStack[] output) {
		recipeInput = input;
		recipeOutput = output;
		recipeWidth = width;
		recipeHeight = height;
	}
	
	public ShapedStationRecipe setRequiredExperience(int experience) {
		requiredExperience = experience;
		return this;
	}
	
	@Override
	public boolean matches(ItemStack[] input) {
		int[] pos = new int[2];
		if (!findShapedPosition(input, recipeWidth, recipeHeight, pos)) return false;
		int startX = pos[0], startY = pos[1];
		
		for (int x = 0; x < recipeWidth; x++)
			for (int y = 0; y < recipeHeight; y++) {
				IRecipeInput recipeStack = recipeInput[getIndex(x, y)];
				ItemStack inputStack = input[getIndex(startX + x, startY + y)];
				if (!recipeStack.matches(inputStack) ||
				    (inputStack.stackSize < recipeStack.getAmount())) return false;
			}
		
		return true;
	}
	
	@Override
	public void getSampleInput(ItemStack[] input, Random rnd) {
		for (int x = 0; x < recipeWidth; x++)
			for (int y = 0; y < recipeHeight; y++)
				input[getIndex(x, y)] = recipeInput[getIndex(x, y)].getSampleInput(rnd);
	}
	
	@Override
	public void getCraftRequirements(ItemStack[] currentInput, IRecipeInput[] requiredInput) {
		int[] pos = new int[2];
		findShapedPosition(currentInput, recipeWidth, recipeHeight, pos);
		int startX = pos[0], startY = pos[1];
		for (int x = 0; x < recipeWidth; x++)
			for (int y = 0; y < recipeHeight; y++)
				requiredInput[getIndex(startX + x, startY + y)] = recipeInput[getIndex(x, y)];
	}
	
	@Override
	public int getExperienceDisplay(ItemStack[] input) { return requiredExperience; }
	
	@Override
	public ItemStack[] getOutput(ItemStack[] input) { return recipeOutput; }
	
	@Override
	public boolean canCraft(ItemStack[] input, EntityPlayer player) {
		if ((requiredExperience > 0) &&
		    ((player == null) || (player.experienceLevel < requiredExperience))) return false;
		return true;
	}
	
	@Override
	public void craft(ItemStack[] input, ItemStack[] output, EntityPlayer player) {
		int[] pos = new int[2];
		findShapedPosition(input, recipeWidth, recipeHeight, pos);
		int startX = pos[0], startY = pos[1];

		for (int x = 0; x < recipeWidth; x++)
			for (int y = 0; y < recipeHeight; y++) {
				int inputIndex = getIndex(startX + x, startY + y);
				ItemStack inputStack = input[inputIndex];
				ItemStack containerItem = inputStack.getItem().getContainerItemStack(inputStack);
				if (containerItem == null) inputStack.stackSize--;
				else input[inputIndex] = containerItem.copy();
			}
		
		if (requiredExperience != 0)
			player.addExperienceLevel(-requiredExperience);
	}
	
	// Utility functions
	
	public static int getIndex(int x, int y) { return (x + y * 3); }
	
	public static boolean findShapedPosition(ItemStack[] input, int width, int height, int[] position) {
		int minX = 0, maxX = 2, minY = 0, maxY = 2;
		for (int x = minX; x <= maxX; x++)
			for (int y = minY; y <= maxY; y++)
				if (input[x + y * 3] != null) { minX = x; break; }
		for (int x = maxX; x > minX; x++)
			for (int y = minY; y <= maxY; y++)
				if (input[x + y * 3] != null) { maxX = x; break; }
		for (int y = minY; y <= maxY; y++)
			for (int x = minX; x <= maxX; x++)
				if (input[x + y * 3] != null) { minY = y; break; }
		for (int y = maxY; y > minY; y++)
			for (int x = minX; x <= maxX; x++)
				if (input[x + y * 3] != null) { maxY = y; break; }
		if ((width  != (maxX - minX + 1)) ||
		    (height != (maxY - minY + 1))) return false;
		position[0] = minX;
		position[1] = minY;
		return true;
	}
	
}
