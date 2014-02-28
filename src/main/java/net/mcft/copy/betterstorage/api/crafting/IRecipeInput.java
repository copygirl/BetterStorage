package net.mcft.copy.betterstorage.api.crafting;

import net.minecraft.item.ItemStack;

public interface IRecipeInput {
	
	/** Returns the amount needed to craft. */
	int getAmount();
	
	/** Returns if the stack matches the input, ignores stack size. */
	boolean matches(ItemStack stack);
	
	/** Called when a recipe is crafted, returns what is left after the crafting. <br>
	 *  Empty stacks and damageable items with no durability left are removed automatically. */
	ItemStack craft(ItemStack input, ICraftingSource source);
	
}
