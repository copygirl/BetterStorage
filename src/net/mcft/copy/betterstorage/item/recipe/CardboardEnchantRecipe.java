package net.mcft.copy.betterstorage.item.recipe;

import java.util.Map;

import net.mcft.copy.betterstorage.item.cardboard.ICardboardItem;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;

public class CardboardEnchantRecipe implements IRecipe {
	
	@Override
	public int getRecipeSize() { return 2; }
	@Override
	public ItemStack getRecipeOutput() { return null; }
	
	@Override
	public boolean matches(InventoryCrafting crafting, World world) {
		ItemStack book = null;
		ItemStack item = null;
		for (int i = 0; i < crafting.getSizeInventory(); i++) {
			ItemStack stack = crafting.getStackInSlot(i);
			if (stack == null) continue;
			else if (stack.getItem() == Item.enchantedBook) {
				if (book != null) return false;
				book = stack;
			} else if (stack.getItem() instanceof ICardboardItem) {
				if (item != null) return false;
				item = stack;
			} else return false;
		}
		if ((book != null) && (item != null)) {
			Map<Integer, Integer> enchantments = EnchantmentHelper.getEnchantments(book);
			for (Integer enchId : enchantments.keySet())
				if (Enchantment.enchantmentsList[enchId].canApply(item) &&
				    (enchantments.get(enchId) > EnchantmentHelper.getEnchantmentLevel(enchId, item)))
					return true;
		}
		return false;
	}
	
	@Override
	public ItemStack getCraftingResult(InventoryCrafting crafting) {
		ItemStack book = null;
		ItemStack item = null;
		for (int i = 0; i < crafting.getSizeInventory(); i++) {
			ItemStack stack = crafting.getStackInSlot(i);
			if (stack == null) continue;
			else if (stack.getItem() == Item.enchantedBook) {
				if (book != null) return null;
				book = stack;
			} else if (stack.getItem() instanceof ICardboardItem) {
				if (item != null) return null;
				item = stack;
			} else return null;
		}
		ItemStack result = null;
		if ((book != null) && (item != null)) {
			Map<Integer, Integer> enchantments = EnchantmentHelper.getEnchantments(book);
			for (Integer enchId : enchantments.keySet()) {
				Enchantment ench = Enchantment.enchantmentsList[enchId];
				int enchLvl = enchantments.get(enchId);
				if (ench.canApply(item) && (enchLvl > EnchantmentHelper.getEnchantmentLevel(enchId, item)))
					((result == null) ? (result = item.copy()) : result).addEnchantment(ench, enchLvl);
			}
		}
		return result;
	}
	
}
