package net.mcft.copy.betterstorage.api.crafting;

import net.minecraft.item.ItemStack;

public interface IStationRecipe {
	
	/** If the input matches this recipe, returns a new station crafting
	 *  instance, specific for this input, or null if it didn't match. */
	StationCrafting checkMatch(ItemStack[] input);
	
}
