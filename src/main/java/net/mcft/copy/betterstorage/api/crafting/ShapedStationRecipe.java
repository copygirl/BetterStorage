package net.mcft.copy.betterstorage.api.crafting;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import net.minecraft.item.ItemStack;

public class ShapedStationRecipe implements IStationRecipe {
	
	public final IRecipeInput[] recipeInput;
	public final ItemStack[] recipeOutput;
	public final int recipeWidth;
	public final int recipeHeight;
	
	public int requiredExperience = 0;
	public int craftingTime = 0;
	
	public ShapedStationRecipe(IRecipeInput[] input, int width, int height, ItemStack[] output) {
		recipeInput = input;
		recipeOutput = output;
		recipeWidth = width;
		recipeHeight = height;
	}
	
	public ShapedStationRecipe(ItemStack[] output, Object... input) {
		int width = 0;
		int height = 0;
		
		int inputIndex = 0;
		Map<Character, IRecipeInput> inputMap = new HashMap<Character, IRecipeInput>();
		
		while ((inputIndex < input.length) && (input[inputIndex] instanceof String)) {
			String line = (String)input[inputIndex++];
			if (line.isEmpty())
				throw new IllegalArgumentException("Empty string isn't valid.");
			if (width <= 0) width = line.length();
			else if (width != line.length())
				throw new IllegalArgumentException("All strings must have the same length.");
			for (char chr : line.toCharArray())
				inputMap.put(chr, null);
			height++;
		}
		if (height <= 0)
			throw new IllegalArgumentException("At least one string must be supplied.");
		
		if (inputIndex >= input.length)
			throw new IllegalArgumentException("At least one mapping must be supplied.");
		if ((input.length - inputIndex) % 2 > 0)
			throw new IllegalArgumentException("Mappings have to be in pairs of two.");
		
		for (; inputIndex < input.length; inputIndex += 2) {
			if (!(input[inputIndex] instanceof Character))
				throw new IllegalArgumentException("First argument of a mapping needs to be a character.");
			char chr = (Character)input[inputIndex];
			if (!inputMap.containsKey(chr))
				throw new IllegalArgumentException("Mapping for unused character '" + chr + "'.");
			if (inputMap.get(chr) != null)
				throw new IllegalArgumentException("Duplicate mapping for character '" + chr + "'.");
			IRecipeInput mapping = BetterStorageCrafting.makeInput(input[inputIndex + 1]);
			inputMap.put(chr, mapping);
		}
		
		for (Entry<Character, IRecipeInput> entry : inputMap.entrySet())
			if ((entry.getKey() != ' ') && (entry.getValue() == null))
				throw new IllegalArgumentException("No mapping for character '" + entry.getKey() + "'.");
		
		recipeInput = new IRecipeInput[width * height];
		recipeOutput = output;

		for (int x = 0; x < width; x++)
			for (int y = 0; y < height; y++)
				recipeInput[x + y * width] = inputMap.get(((String)input[y]).charAt(x));
		
		recipeWidth = width;
		recipeHeight = height;
	}
	
	public ShapedStationRecipe setRequiredExperience(int experience) {
		requiredExperience = experience;
		return this;
	}
	public ShapedStationRecipe setCraftingTime(int time) {
		craftingTime = time;
		return this;
	}
	
	// IStationRecipe implementation
	
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
	
	public static int getIndex(int x, int y) { return (x + y * 3); }
	
	/** Finds the position of the recipe in the input and
	 *  checks and returns if it is the correct size. */
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
