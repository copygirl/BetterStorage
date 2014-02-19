package net.mcft.copy.betterstorage.item.cardboard;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import net.mcft.copy.betterstorage.api.crafting.ICraftingSource;
import net.mcft.copy.betterstorage.api.crafting.IRecipeInput;
import net.mcft.copy.betterstorage.api.crafting.IStationRecipe;
import net.mcft.copy.betterstorage.api.crafting.RecipeInputItemStack;
import net.mcft.copy.betterstorage.utils.StackUtils;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class CardboardEnchantmentRecipe implements IStationRecipe {
	
	@Override
	public boolean matches(ItemStack[] input) { return (getRecipeInfo(input, true) != null); }
	
	@Override
	public void getSampleInput(ItemStack[] input, Random rnd) {
		// TODO
	}
	
	@Override
	public void getCraftRequirements(ItemStack[] currentInput, IRecipeInput[] requiredInput) {
		for (int i = 0; i < currentInput.length; i++) {
			ItemStack stack = currentInput[i];
			if (stack == null) continue;
			boolean isCardboard = (stack.getItem() instanceof ICardboardItem);
			requiredInput[i] = new RecipeInputItemStack(
					StackUtils.copyStack(stack, (isCardboard ? 1 : 0), false), true);
		}
	}
	
	@Override
	public int getExperienceDisplay(ItemStack[] input) {
		return getRecipeInfo(input, false).getEnchantmentCost();
	}
	
	@Override
	public int getCraftingTime(ItemStack[] input) { return 0; }
	
	@Override
	public ItemStack[] getOutput(ItemStack[] input) {
		return getRecipeInfo(input, false).applyEnchantments();
	}
	
	@Override
	public boolean canCraft(ItemStack[] input, ICraftingSource source) {
		int requiredExperience = getRecipeInfo(input, false).getEnchantmentCost();
		return ((requiredExperience <= 0) ||
		        ((source.getPlayer() != null) &&
		         ((source.getPlayer().experienceLevel >= requiredExperience) ||
		          source.getPlayer().capabilities.isCreativeMode)));
	}
	
	@Override
	public void craft(ItemStack[] input, ICraftingSource source) {
		for (int i = 0; i < input.length; i++) {
			ItemStack stack = input[i];
			if ((stack != null) && (stack.getItem() instanceof ICardboardItem))
				stack.stackSize--;
		}
		int requiredExperience = getRecipeInfo(input, false).getEnchantmentCost();
		if ((requiredExperience != 0) && !source.getPlayer().capabilities.isCreativeMode)
			source.getPlayer().addExperienceLevel(-requiredExperience);
	}
	
	// Helper functions
	
	private static Map<Integer, StackEnchantment> getEnchantmentMap(ItemStack stack) {
		Map<Integer, StackEnchantment> enchantments = new HashMap<Integer, StackEnchantment>();
		NBTTagList list = ((stack.getItem() == Item.enchantedBook) ? Item.enchantedBook.func_92110_g(stack)
		                                                           : stack.getEnchantmentTagList());
		if (list != null)
			for (int i = 0; i < list.tagCount(); i++) {
				StackEnchantment ench = new StackEnchantment(stack, (NBTTagCompound)list.tagAt(i));
				enchantments.put(ench.ench.effectId, ench);
			}
		return enchantments;
	}
	
	private static boolean enchantmentCompatible(ItemStack stack,
	                                             Collection<StackEnchantment> stackEnchants,
	                                             StackEnchantment bookEnch) {
		if (!bookEnch.ench.canApply(stack)) return false;
		for (StackEnchantment stackEnch : stackEnchants)
			if ((bookEnch.ench == stackEnch.ench)
					? (bookEnch.getLevel() <= stackEnch.getLevel())
					: (!bookEnch.ench.canApplyTogether(stackEnch.ench) ||
					   !stackEnch.ench.canApplyTogether(bookEnch.ench))) return false;
		return true;
	}
	
	private static RecipeInfo getRecipeInfo(ItemStack[] input, boolean checkCanEnchant) {
		
		ItemStack book = null;
		List<ItemStackTuple> cardboardItems = new ArrayList<ItemStackTuple>();
		
		for (int i = 0; i < input.length; i++) {
			ItemStack stack = input[i];
			if (stack == null) continue;
			if (stack.getItem() instanceof ICardboardItem)
				cardboardItems.add(new ItemStackTuple(stack, i));
			else if ((book == null) && (stack.getItem() == Item.enchantedBook))
				book = stack;
			else return null;
		}
		
		if ((book == null) || cardboardItems.isEmpty())
			return null;
		
		Map<Integer, StackEnchantment> bookEnchantments = getEnchantmentMap(book);
		
		if (checkCanEnchant)
			for (ItemStackTuple cardboardTuple : cardboardItems) {
				boolean canApply = false;
				Map<Integer, StackEnchantment> stackEnchants = getEnchantmentMap(cardboardTuple.stack);
				for (StackEnchantment bookEnch : bookEnchantments.values())
					if (enchantmentCompatible(cardboardTuple.stack, stackEnchants.values(), bookEnch)) {
						canApply = true;
						break;
					}
				if (!canApply) return null;
			}
		
		return new RecipeInfo(cardboardItems, bookEnchantments.values());
		
	}
	
	// Helper classes
	
	private static class RecipeInfo {
		
		public final List<ItemStackTuple> cardboardItems;
		public final Collection<StackEnchantment> bookEnchantments;
		
		public RecipeInfo(List<ItemStackTuple> cardboardItems, Collection<StackEnchantment> bookEnchantments) {
			this.cardboardItems = cardboardItems;
			this.bookEnchantments = bookEnchantments;
		}
		
		public ItemStack[] applyEnchantments() {
			ItemStack[] output = new ItemStack[9];
			for (ItemStackTuple stackTuple : cardboardItems) {
				ItemStack stack = stackTuple.stack.copy();
				Map<Integer, StackEnchantment> stackEnchants = getEnchantmentMap(stack);
				for (StackEnchantment bookEnch : bookEnchantments) {
					if (!enchantmentCompatible(stack, stackEnchants.values(), bookEnch))
						continue;
					StackEnchantment stackEnch = stackEnchants.get(bookEnch.ench.effectId);
					if (stackEnch != null) stackEnch.setLevel(bookEnch.getLevel());
					else stack.addEnchantment(bookEnch.ench, bookEnch.getLevel());
				}
				output[stackTuple.index] = stack;
			}
			return output;
		}
		
		public int getEnchantmentCost() {
			int cost = 0;
			for (ItemStackTuple stackTuple : cardboardItems) {
				ItemStack stack = stackTuple.stack;
				Map<Integer, StackEnchantment> stackEnchants = getEnchantmentMap(stackTuple.stack);
				int numEnchants = stackEnchants.size();
				for (StackEnchantment bookEnch : bookEnchantments) {
					if (!enchantmentCompatible(stack, stackEnchants.values(), bookEnch))
						continue;
					StackEnchantment stackEnch = stackEnchants.get(bookEnch.ench.effectId);
					int levels = (bookEnch.getLevel() - ((stackEnch != null) ? stackEnch.getLevel() : 0));
					if (stackEnch == null) {
						if (numEnchants > 0) cost++;
						if (numEnchants > 2) cost++;
					}
					cost += calculateCost(bookEnch.ench, levels);
				}
			}
			return cost;
		}
		
		private int calculateCost(Enchantment ench, int levels) {
			int enchWeight = ench.getWeight();
			int costPerLevel;
			if (enchWeight > 8) costPerLevel = 1;
			else if (enchWeight > 6) costPerLevel = 2;
			else if (enchWeight > 3) costPerLevel = 3;
			else if (enchWeight > 1) costPerLevel = 4;
			else costPerLevel = 6;
			return (costPerLevel * levels - 1);
		}
		
	}
	
	private static class StackEnchantment {
		
		public final ItemStack stack;
		public final Enchantment ench;
		
		private final NBTTagCompound entry;
		
		public int getLevel() { return entry.getShort("lvl"); }
		public void setLevel(int level) { entry.setShort("lvl", (short)level); }
		
		public StackEnchantment(ItemStack stack, NBTTagCompound entry) {
			this.stack = stack;
			this.entry = entry;
			
			this.ench = Enchantment.enchantmentsList[entry.getShort("id")];
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
