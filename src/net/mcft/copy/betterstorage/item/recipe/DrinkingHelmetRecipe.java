package net.mcft.copy.betterstorage.item.recipe;

import net.mcft.copy.betterstorage.item.ItemDrinkingHelmet;
import net.mcft.copy.betterstorage.misc.Constants;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ShapedRecipes;

public class DrinkingHelmetRecipe extends ShapedRecipes {
	
	private static final ItemStack potion = new ItemStack(Item.potion, 1, Constants.anyDamage);
	
	public DrinkingHelmetRecipe(Item drinkingHelmet) {
		super(3, 1, new ItemStack[]{ potion, new ItemStack(drinkingHelmet, 1, Constants.anyDamage), potion }, new ItemStack(drinkingHelmet));
	}
	
	@Override
	public ItemStack getCraftingResult(InventoryCrafting crafting) {
		ItemStack drinkingHelmet = null;
		ItemStack[] potions = null;
		for (int i = 0; i < crafting.getSizeInventory(); i++) {
			drinkingHelmet = crafting.getStackInSlot(i);
			if ((drinkingHelmet != null) && (drinkingHelmet.getItem() instanceof ItemDrinkingHelmet)) {
				drinkingHelmet = drinkingHelmet.copy();
				potions = new ItemStack[]{ crafting.getStackInSlot(i + 1), crafting.getStackInSlot(i - 1) };
				break;
			}
		}
		ItemDrinkingHelmet.setPotions(drinkingHelmet, potions);
		return drinkingHelmet;
	}
	
}
