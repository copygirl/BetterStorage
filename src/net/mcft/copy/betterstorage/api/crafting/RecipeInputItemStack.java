package net.mcft.copy.betterstorage.api.crafting;

import net.mcft.copy.betterstorage.api.BetterStorageUtils;
import net.minecraft.item.ItemStack;

public class RecipeInputItemStack implements IRecipeInput {
	
	public final ItemStack stack;
	
	public RecipeInputItemStack(ItemStack stack) {
		this.stack = stack;
	}
	
	@Override
	public int getAmount() { return stack.stackSize; }
	
	@Override
	public boolean matches(ItemStack stack) { return BetterStorageUtils.wildcardMatch(this.stack, stack); }
	
}
