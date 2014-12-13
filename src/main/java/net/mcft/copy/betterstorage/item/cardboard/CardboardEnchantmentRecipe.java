package net.mcft.copy.betterstorage.item.cardboard;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import net.mcft.copy.betterstorage.api.crafting.BetterStorageCrafting;
import net.mcft.copy.betterstorage.api.crafting.IRecipeInput;
import net.mcft.copy.betterstorage.api.crafting.IStationRecipe;
import net.mcft.copy.betterstorage.api.crafting.RecipeBounds;
import net.mcft.copy.betterstorage.api.crafting.RecipeInputItemStack;
import net.mcft.copy.betterstorage.api.crafting.StationCrafting;
import net.mcft.copy.betterstorage.content.BetterStorageItems;
import net.mcft.copy.betterstorage.utils.StackUtils;
import net.mcft.copy.betterstorage.utils.StackUtils.StackEnchantment;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.init.Items;
import net.minecraft.item.ItemEnchantedBook;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class CardboardEnchantmentRecipe implements IStationRecipe {
	
	@Override
	public StationCrafting checkMatch(ItemStack[] input, RecipeBounds bounds) {
		
		// Quick check if input matches the recipe.
		
		boolean hasCardboardItems = false;
		int bookIndex = -1;
		ItemStack book = null;
		
		for (int i = 0; i < input.length; i++) {
			ItemStack stack = input[i];
			if (stack == null) continue;
			if (stack.getItem() instanceof ICardboardItem)
				hasCardboardItems = true;
			else if ((book == null) && (stack.getItem() == Items.enchanted_book)) {
				bookIndex = i;
				book = stack;
			} else return null;
		}
		
		if ((book == null) || !hasCardboardItems)
			return null;
		
		// Basic items match the recipe,
		// do more expensive stuff now.
		
		ItemStack[] output = new ItemStack[9];
		int experienceCost = 0;
		IRecipeInput[] requiredInput = new IRecipeInput[9];
		
		Collection<StackEnchantment> bookEnchantments = StackUtils.getEnchantments(book).values();
		
		for (int i = 0; i < input.length; i++) {
			ItemStack stack = input[i];
			if ((stack == null) || !(stack.getItem() instanceof ICardboardItem))
				continue;
			
			ItemStack outputStack = stack.copy();
			
			boolean canApply = false;
			Map<Integer, StackEnchantment> stackEnchants = StackUtils.getEnchantments(outputStack);
			int numEnchants = stackEnchants.size();
			
			for (StackEnchantment bookEnch : bookEnchantments) {
				if (!StackUtils.isEnchantmentCompatible(outputStack, stackEnchants.values(), bookEnch))
					continue;
				
				StackEnchantment stackEnch = stackEnchants.get(bookEnch.ench.effectId);

				// Calculate enchantment cost.
				int level = (bookEnch.getLevel() - ((stackEnch != null) ? stackEnch.getLevel() : 0));
				experienceCost += calculateCost(bookEnch, (stackEnch == null), numEnchants);
				
				// Set enchantment level of output item.
				if (stackEnch != null) stackEnch.setLevel(bookEnch.getLevel());
				else outputStack.addEnchantment(bookEnch.ench, bookEnch.getLevel());
				
				canApply = true;
			}
			
			// If none of the enchantments on the book can
			// be applied on the item, the recipe is invalid.
			if (!canApply) return null;
			
			output[i] = outputStack;
			requiredInput[i] = new RecipeInputItemStack(StackUtils.copyStack(stack, 1), true);
		}
		
		requiredInput[bookIndex] = new RecipeInputItemStack(StackUtils.copyStack(book, 0, false));
		
		return new StationCrafting(output, requiredInput, experienceCost);
		
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public List<IRecipeInput[]> getSampleInputs() {
		List<IRecipeInput[]> sampleInputs = new ArrayList<IRecipeInput[]>();
		makeInput(sampleInputs, BetterStorageItems.cardboardHelmet, BetterStorageItems.cardboardChestplate, null,
		                        BetterStorageItems.cardboardLeggings, BetterStorageItems.cardboardBoots,
		                        makeEnchantedBook(Enchantment.protection, 4));
		makeInput(sampleInputs, null, BetterStorageItems.cardboardPickaxe, null,
		                        null, BetterStorageItems.cardboardShovel, makeEnchantedBook(Enchantment.efficiency, 5),
		                        null, BetterStorageItems.cardboardAxe);
		makeInput(sampleInputs, null, null, null,
	                            BetterStorageItems.cardboardSword,
	                            BetterStorageItems.cardboardAxe,
	                            makeEnchantedBook(Enchantment.sharpness, 5));
		makeInput(sampleInputs, BetterStorageItems.cardboardChestplate, BetterStorageItems.cardboardLeggings, null,
		                        BetterStorageItems.cardboardSword, BetterStorageItems.cardboardPickaxe,
		                        makeEnchantedBook(Enchantment.unbreaking, 3),
		                        BetterStorageItems.cardboardShovel, BetterStorageItems.cardboardHoe);
		return sampleInputs;
	}
	private void makeInput(List<IRecipeInput[]> sampleInputs, Object... obj) {
		IRecipeInput[] input = new IRecipeInput[9];
		boolean hasCardboardItem = false;
		for (int i = 0; i < obj.length; i++)
			if (obj[i] != null) {
				input[i] = BetterStorageCrafting.makeInput(obj[i]);
				if (obj[i] instanceof ICardboardItem)
					hasCardboardItem = true;
			}
		if (hasCardboardItem)
			sampleInputs.add(input);
	}
	private ItemStack makeEnchantedBook(Enchantment ench, int level) {
		ItemStack book = new ItemStack(Items.enchanted_book);
		((ItemEnchantedBook)book.getItem()).addEnchantment(book, new EnchantmentData(ench, level));
		return book;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public List<IRecipeInput> getPossibleInputs() {
		return Arrays.<IRecipeInput>asList(
				RecipeInputCardboard.instance,
				RecipeInputEnchantedBook.instance);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public List<ItemStack> getPossibleOutputs() { return Collections.emptyList(); }
	
	// Utility functions
	
	private int calculateCost(StackEnchantment ench, boolean hasEnchantment, int numEnchants) {
		int weight = ench.ench.getWeight();
		int costPerLevel;
		if (weight > 8) costPerLevel = 1;
		else if (weight > 6) costPerLevel = 2;
		else if (weight > 3) costPerLevel = 3;
		else if (weight > 1) costPerLevel = 4;
		else costPerLevel = 6;
		
		int cost = (costPerLevel * ench.getLevel()) - 1;
		cost += CardboardRecipeHelper.getAdditionalEnchantmentCost(ench);
		if (hasEnchantment)
			cost += (numEnchants + 1) / 2;
		return cost;
	}
	
}
