package net.mcft.copy.betterstorage.item.cardboard;

import java.util.Collection;
import net.mcft.copy.betterstorage.api.crafting.IRecipeInput;
import net.mcft.copy.betterstorage.api.crafting.StationCrafting;
import net.mcft.copy.betterstorage.api.crafting.IStationRecipe;
import net.mcft.copy.betterstorage.api.crafting.RecipeInputItemStack;
import net.mcft.copy.betterstorage.api.crafting.RecipeInputOreDict;
import net.mcft.copy.betterstorage.utils.StackUtils;
import net.mcft.copy.betterstorage.utils.StackUtils.StackEnchantment;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class CardboardRepairRecipe implements IStationRecipe {
	
	private static final IRecipeInput sheetUsed = new RecipeInputOreDict("cardboardSheet", 1);
	private static final IRecipeInput sheetUnused = new RecipeInputOreDict("cardboardSheet", 0);
	
	@Override
	public StationCrafting checkMatch(ItemStack[] input) {
		
		// Quick check if input matches the recipe.
		
		boolean hasCardboardItems = false;
		int numSheets = 0;
		int totalDamage = 0;
		
		for (int i = 0; i < input.length; i++) {
			ItemStack stack = input[i];
			if (stack == null) continue;
			if (stack.getItem() instanceof ICardboardItem) {
				hasCardboardItems = true;
				totalDamage += stack.getItemDamage();
			} else if (sheetUsed.matches(stack))
				numSheets++;
			else return null;
		}
		
		if (!hasCardboardItems) return null;
		
		// If there's not enough sheets to repair all items, return null.
		int numSheetsNeeded = (totalDamage + 79) / 80;
		if (numSheetsNeeded > numSheets) return null;
		
		// Basic items match the recipe,
		// do more expensive stuff now.
		
		ItemStack[] output = new ItemStack[9];
		int experienceCost = 0;
		IRecipeInput[] requiredInput = new IRecipeInput[9];
		
		for (int i = 0; i < input.length; i++) {
			ItemStack stack = input[i];
			if (stack == null) continue;
			ItemStack outputStack = null;
			
			if (stack.getItem() instanceof ICardboardItem) {
				
				Collection<StackEnchantment> enchantments = StackUtils.getEnchantments(stack).values();
				experienceCost += Math.max(enchantments.size() - 1, 0);
				for (StackEnchantment ench : enchantments)
					experienceCost += calculateCost(ench);
				
				outputStack = StackUtils.copyStack(stack, 1);
				outputStack.setItemDamage(0);
				
				ItemStack requiredStack = outputStack.copy();
				requiredStack.setItemDamage(OreDictionary.WILDCARD_VALUE);
				requiredStack.setTagCompound(null);
				requiredInput[i] = new RecipeInputItemStack(requiredStack);
				
			} else requiredInput[i] = ((numSheetsNeeded-- > 0) ? sheetUsed : sheetUnused);
			
			output[i] = outputStack;
		}
		
		return new StationCrafting(output, requiredInput, experienceCost);
		
	}
	
	private int calculateCost(StackEnchantment ench) {
		int cost = 0;
		int weight = ench.ench.getWeight();
		int level = ench.getLevel();
		if (weight > 8) cost += Math.max(level - 2, 0);
		else if (weight > 4) cost += level - 1;
		else if (weight > 2) cost += level;
		else cost = level * 2;
		cost += CardboardRecipeHelper.getAdditionalEnchantmentCost(ench);
		return cost;
	}
	
}
