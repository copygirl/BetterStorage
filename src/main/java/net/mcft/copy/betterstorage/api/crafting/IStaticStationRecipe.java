package net.mcft.copy.betterstorage.api.crafting;

import net.minecraft.item.ItemStack;

/** A static Crafting Station recipe. These recipes can be displayed from NEI, the parameters don't vary.*/
public interface IStaticStationRecipe extends IStationRecipe {
	
	public IRecipeInput[] getRecipeInput();
	
	public ItemStack[] getRecipeOutput();
	
	public int getRequiredExperience();
	
	public int getCraftingTime();
}
