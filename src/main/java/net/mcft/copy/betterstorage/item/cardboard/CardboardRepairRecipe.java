package net.mcft.copy.betterstorage.item.cardboard;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import net.mcft.copy.betterstorage.api.crafting.BetterStorageCrafting;
import net.mcft.copy.betterstorage.api.crafting.IRecipeInput;
import net.mcft.copy.betterstorage.api.crafting.IStationRecipe;
import net.mcft.copy.betterstorage.api.crafting.RecipeBounds;
import net.mcft.copy.betterstorage.api.crafting.RecipeInputItemStack;
import net.mcft.copy.betterstorage.api.crafting.RecipeInputOreDict;
import net.mcft.copy.betterstorage.api.crafting.StationCrafting;
import net.mcft.copy.betterstorage.content.BetterStorageItems;
import net.mcft.copy.betterstorage.utils.StackUtils;
import net.mcft.copy.betterstorage.utils.StackUtils.StackEnchantment;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class CardboardRepairRecipe implements IStationRecipe {
	
	private static final IRecipeInput sheetUsed = new RecipeInputOreDict("sheetCardboard", 1);
	private static final IRecipeInput sheetUnused = new RecipeInputOreDict("sheetCardboard", 0);
	
	@Override
	public StationCrafting checkMatch(ItemStack[] input, RecipeBounds bounds) {
		
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
		
		if (!hasCardboardItems || (numSheets <= 0)) return null;
		
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
	
	@Override
	@SideOnly(Side.CLIENT)
	public List<IRecipeInput[]> getSampleInputs() {
		List<IRecipeInput[]> sampleInputs = new ArrayList<IRecipeInput[]>();
		if (BetterStorageItems.cardboardPickaxe != null) {
			ItemStack stack = new ItemStack(BetterStorageItems.cardboardPickaxe, 1, 32);
			stack.addEnchantment(Enchantment.efficiency, 5);
			stack.addEnchantment(Enchantment.unbreaking, 3);
			makeInput(sampleInputs, stack, BetterStorageItems.cardboardSheet);
		}
		makeInput(sampleInputs, new ItemStack(BetterStorageItems.cardboardPickaxe, 1, 32), BetterStorageItems.cardboardSheet);
		makeInput(sampleInputs, new ItemStack(BetterStorageItems.cardboardHelmet, 1, 11),
		                        new ItemStack(BetterStorageItems.cardboardChestplate, 1, 16), BetterStorageItems.cardboardSheet,
		                        new ItemStack(BetterStorageItems.cardboardLeggings, 1, 15),
		                        new ItemStack(BetterStorageItems.cardboardBoots, 1, 13));
		makeInput(sampleInputs, new ItemStack(BetterStorageItems.cardboardHelmet, 1, 11 * 2),
		                        new ItemStack(BetterStorageItems.cardboardChestplate, 1, 16 * 2), BetterStorageItems.cardboardSheet,
		                        new ItemStack(BetterStorageItems.cardboardLeggings, 1, 15 * 2),
		                        new ItemStack(BetterStorageItems.cardboardBoots, 1, 13 * 2), BetterStorageItems.cardboardSheet);
		makeInput(sampleInputs, new ItemStack(BetterStorageItems.cardboardHelmet, 1, 11 * 3),
		                        new ItemStack(BetterStorageItems.cardboardChestplate, 1, 16 * 3), BetterStorageItems.cardboardSheet,
		                        new ItemStack(BetterStorageItems.cardboardLeggings, 1, 15 * 3),
		                        new ItemStack(BetterStorageItems.cardboardBoots, 1, 13 * 3), BetterStorageItems.cardboardSheet,
		                        new ItemStack(BetterStorageItems.cardboardShovel, 1, 32),
		                        new ItemStack(BetterStorageItems.cardboardHoe, 1, 32), BetterStorageItems.cardboardSheet);
		return sampleInputs;
	}
	private void makeInput(List<IRecipeInput[]> sampleInputs, Object... obj) {
		IRecipeInput[] input = new IRecipeInput[9];
		boolean hasCardboardItem = false;
		for (int i = 0; i < obj.length; i++)
			if (obj[i] != null) {
				if (obj[i] instanceof ItemStack) {
					Item item = ((ItemStack)obj[i]).getItem();
					if (item == null) continue;
					if (item instanceof ICardboardItem)
						hasCardboardItem = true;
				}
				input[i] = BetterStorageCrafting.makeInput(obj[i]);
			}
		if (hasCardboardItem)
			sampleInputs.add(input);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public List<IRecipeInput> getPossibleInputs() {
		return Arrays.<IRecipeInput>asList(RecipeInputCardboard.instance,
				new RecipeInputItemStack(new ItemStack(BetterStorageItems.cardboardSheet)));
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public List<ItemStack> getPossibleOutputs() { return Collections.emptyList(); }
	
	// Utility functions
	
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
