package net.mcft.copy.betterstorage.addon.minetweaker;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import minetweaker.MineTweakerAPI;
import minetweaker.api.item.IIngredient;
import minetweaker.api.item.IItemStack;
import net.mcft.copy.betterstorage.api.crafting.IRecipeInput;
import net.mcft.copy.betterstorage.api.crafting.RecipeInputList;
import net.minecraft.item.ItemStack;

public final class MTHelper {
	
	private MTHelper() {  }
	
	public static ItemStack toStack(IItemStack stack) {
		if (stack == null) return null;
		else if (!(stack.getInternal() instanceof ItemStack)) {
			MineTweakerAPI.getLogger().logError("Not a valid item stack: " + stack);
			return null;
		} else return (ItemStack)stack.getInternal();
	}
	public static List<ItemStack> toStacks(List<IItemStack> stacks) {
		List<ItemStack> list = new ArrayList<ItemStack>(stacks.size());
		for (IItemStack mtStack : stacks)
			list.add(toStack(mtStack));
		return list;
	}
	
	public static ItemStack toStack(IIngredient ingredient) {
		return toStacks(ingredient.getItems()).get(0);
	}
	public static ItemStack[] toStacks(IIngredient[] ingredients) {
		ItemStack[] outputs = new ItemStack[ingredients.length];
		for (int i = 0; i < ingredients.length; i++)
			outputs[i] = toStack(ingredients[i]);
		return outputs;
	}
	
	public static IRecipeInput toRecipeInput(IIngredient ingredient) {
		if (ingredient == null) return null;
		List<ItemStack> items = toStacks(ingredient.getItems());
		return (!items.isEmpty() ? new RecipeInputList(items) : null);
	}
	public static IRecipeInput[] toRecipeInputs(IIngredient[] ingredients) {
		IRecipeInput[] inputs = new IRecipeInput[ingredients.length];
		for (int i = 0; i < ingredients.length; i++)
			inputs[i] = toRecipeInput(ingredients[i]);
		return inputs;
	}
	public static IRecipeInput[] toRecipeInputs(IIngredient[][] ingredients, AtomicInteger width, AtomicInteger height) {
		width.set(ingredients[0].length);
		height.set(ingredients.length);
		IRecipeInput[] inputs = new IRecipeInput[width.get() * height.get()];
		for (int x = 0; x < width.get(); x++)
			for (int y = 0; y < height.get(); y++)
				inputs[x + y * width.get()] = toRecipeInput(ingredients[y][x]);
		return inputs;
	}
	
}
