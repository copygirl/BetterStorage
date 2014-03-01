package net.mcft.copy.betterstorage.api.crafting;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public abstract class RecipeInputBase implements IRecipeInput {
	
	@Override
	public abstract int getAmount();
	
	@Override
	public abstract boolean matches(ItemStack stack);
	
	@Override
	public void craft(ItemStack input, ContainerInfo containerInfo) {
		if (input == null) return;
		input.stackSize -= getAmount();

		Item item = input.getItem();
		ItemStack containerItem = item.getContainerItemStack(input);
		boolean doesLeaveCrafting = item.doesContainerItemLeaveCraftingGrid(input);
		containerInfo.set(containerItem, doesLeaveCrafting);
	}
	
}
