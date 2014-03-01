package net.mcft.copy.betterstorage.api.crafting;

import net.minecraft.item.ItemStack;

public interface IRecipeInput {
	
	/** Returns the amount needed to craft. */
	int getAmount();
	
	/** Returns if the stack matches the input, ignores stack size. */
	boolean matches(ItemStack stack);
	
	/** Called when a recipe is crafted, allows modification (decrease stack size, damage item)
	 *  of the input stack. Empty stacks and damageable items with no durability left are removed
	 *  automatically. */
	void craft(ItemStack input, ContainerInfo containerInfo);
	
}
