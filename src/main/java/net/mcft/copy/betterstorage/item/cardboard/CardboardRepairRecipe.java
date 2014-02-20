package net.mcft.copy.betterstorage.item.cardboard;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import net.mcft.copy.betterstorage.api.crafting.ICraftingSource;
import net.mcft.copy.betterstorage.api.crafting.IRecipeInput;
import net.mcft.copy.betterstorage.api.crafting.IStationRecipe;
import net.mcft.copy.betterstorage.api.crafting.RecipeInputItemStack;
import net.mcft.copy.betterstorage.content.Items;
import net.mcft.copy.betterstorage.utils.StackUtils;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;

public class CardboardRepairRecipe implements IStationRecipe {
	
	@Override
	public boolean matches(ItemStack[] input) {
		return (getRecipeInfo(input) != null);
	}
	
	@Override
	public void getSampleInput(ItemStack[] input, Random rnd) {
		// TODO
	}
	
	@Override
	public void getCraftRequirements(ItemStack[] currentInput, IRecipeInput[] requiredInput) {
		List<ItemStackTuple> cardboardItems = new ArrayList<ItemStackTuple>();
		List<Integer> cardboardSheets = new ArrayList<Integer>();
		for (int i = 0; i < currentInput.length; i++) {
			ItemStack stack = currentInput[i];
			if (stack == null) continue;
			if (stack.getItem() instanceof ICardboardItem) {
				cardboardItems.add(new ItemStackTuple(stack, i));
				requiredInput[i] = new RecipeInputItemStack(StackUtils.copyStack(stack, 1, false));
			} else if (stack.getItem() == Items.cardboardSheet)
				cardboardSheets.add(i);
		}
		int numSheetsNeeded = caluculateSheetsNeeded(cardboardItems);
		for (int i = 0; i < cardboardSheets.size(); i++) {
			int sheetRequired = ((i < numSheetsNeeded) ? 1 : 0);
			int index = cardboardSheets.get(i);
			requiredInput[index] = new RecipeInputItemStack(StackUtils.copyStack(currentInput[index], sheetRequired, false));
		}
	}
	
	@Override
	public int getExperienceDisplay(ItemStack[] input) {
		return getRecipeInfo(input).getEnchantmentCost();
	}
	
	@Override
	public int getCraftingTime(ItemStack[] input) { return 0; }
	
	@Override
	public ItemStack[] getOutput(ItemStack[] input) {
		return getRecipeInfo(input).getOutput();
	}
	
	@Override
	public boolean canCraft(ItemStack[] input, ICraftingSource source) {
		int requiredExperience = getRecipeInfo(input).getEnchantmentCost();
		return ((requiredExperience <= 0) ||
		        ((source.getPlayer() != null) &&
		         ((source.getPlayer().experienceLevel >= requiredExperience) ||
		          source.getPlayer().capabilities.isCreativeMode)));
	}
	
	@Override
	public void craft(ItemStack[] input, ICraftingSource source) {
		getRecipeInfo(input).craft(source);
	}
	
	// Helper functions
	
	private static RecipeInfo getRecipeInfo(ItemStack[] input) {
		
		List<ItemStackTuple> cardboardItems = new ArrayList<ItemStackTuple>();
		List<ItemStackTuple> cardboardSheets = new ArrayList<ItemStackTuple>();
		
		for (int i = 0; i < input.length; i++) {
			ItemStack stack = input[i];
			if (stack == null) continue;
			if (stack.getItem() instanceof ICardboardItem)
				cardboardItems.add(new ItemStackTuple(stack, i));
			else if (stack.getItem() == Items.cardboardSheet)
				cardboardSheets.add(new ItemStackTuple(stack, i));
			else return null;
		}
		
		if (cardboardItems.isEmpty()) return null;
		
		// If there's not enough sheets to repair all items, return null.
		int numSheetsNeeded = caluculateSheetsNeeded(cardboardItems);
		if (numSheetsNeeded > cardboardSheets.size()) return null;
		
		// If there's more sheets than needed, cut down
		// the list to the amount that is needed.
		if (numSheetsNeeded < cardboardSheets.size())
			cardboardSheets = cardboardSheets.subList(0, numSheetsNeeded);
		
		return new RecipeInfo(cardboardItems, cardboardSheets);
		
	}
	
	private static int caluculateSheetsNeeded(List<ItemStackTuple> cardboardItems) {
		int totalDurabilityToRepair = 0;
		for (ItemStackTuple stackTuple : cardboardItems)
			totalDurabilityToRepair += stackTuple.stack.getItemDamage();
		return (totalDurabilityToRepair + 79) / 80;
	}
	
	// Helper classes
	
	private static class RecipeInfo {
		
		public final List<ItemStackTuple> cardboardItems;
		public final List<ItemStackTuple> cardboardSheets;
		
		public RecipeInfo(List<ItemStackTuple> cardboardItems, List<ItemStackTuple> cardboardSheets) {
			this.cardboardItems = cardboardItems;
			this.cardboardSheets = cardboardSheets;
		}
		
		public ItemStack[] getOutput() {
			ItemStack[] output = new ItemStack[9];
			for (ItemStackTuple stackTuple : cardboardItems) {
				ItemStack stack = stackTuple.stack.copy();
				stack.setItemDamage(0);
				output[stackTuple.index] = stack;
			}
			return output;
		}
		
		public void craft(ICraftingSource source) {
			
			for (ItemStackTuple stackTuple : cardboardItems)
				stackTuple.stack.stackSize--;
			for (ItemStackTuple sheetTuple : cardboardSheets)
				sheetTuple.stack.stackSize--;
			
			int requiredExperience = getEnchantmentCost();
			if ((requiredExperience != 0) && !source.getPlayer().capabilities.isCreativeMode)
				source.getPlayer().addExperienceLevel(-requiredExperience);
			
		}
		
		public int getEnchantmentCost() {
			int cost = 0;
			for (ItemStackTuple stackTuple : cardboardItems) {
				Map<Integer, Integer> enchants = EnchantmentHelper.getEnchantments(stackTuple.stack);
				// More than 1 enchantment = 1 cost per extra enchantment.
				cost += Math.max(enchants.size() - 1, 0);
				// Add cost depending on the enchantments and their level.
				for (Entry<Integer, Integer> entry : enchants.entrySet()) {
					Enchantment ench = Enchantment.enchantmentsList[entry.getKey()];
					int level = entry.getValue();
					cost += calculateCost(ench, level);
				}
			}
			return cost;
		}
		
		private int calculateCost(Enchantment ench, int level) {
			int cost = 0;
			int enchWeight = ench.getWeight();
			if (enchWeight > 8) cost += Math.max(level - 2, 0);
			else if (enchWeight > 4) cost += level - 1;
			else if (enchWeight > 2) cost += level;
			else cost = level * 2;
			cost += CardboardEnchantmentRecipe.getAdditionalEnchantmentCost(ench, level);
			return cost;
		}
		
	}
	
	private static class ItemStackTuple {
		public final ItemStack stack;
		public final int index;
		public ItemStackTuple(ItemStack stack, int index) {
			this.stack = stack;
			this.index = index;
		}
	}
	
}
