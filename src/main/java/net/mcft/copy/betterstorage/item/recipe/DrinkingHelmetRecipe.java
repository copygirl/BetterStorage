package net.mcft.copy.betterstorage.item.recipe;

import net.mcft.copy.betterstorage.item.ItemDrinkingHelmet;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraftforge.oredict.OreDictionary;

public class DrinkingHelmetRecipe extends ShapedRecipes {
	
	private static final ItemStack potion = new ItemStack(Items.potionitem, 1, OreDictionary.WILDCARD_VALUE);
	
	public DrinkingHelmetRecipe(Item drinkingHelmet) {
		super(3, 1, new ItemStack[]{ potion, new ItemStack(drinkingHelmet, 1, OreDictionary.WILDCARD_VALUE), potion }, new ItemStack(drinkingHelmet));
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
