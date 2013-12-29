package net.mcft.copy.betterstorage.api.crafting;

import java.util.List;
import java.util.Random;

import net.mcft.copy.betterstorage.api.BetterStorageUtils;
import net.mcft.copy.betterstorage.utils.StackUtils;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class RecipeInputOreDict implements IRecipeInput {
	
	public final String name;
	public final int amount;
	
	public RecipeInputOreDict(String name, int amount) {
		this.name = name;
		this.amount = amount;
	}
	
	@Override
	public int getAmount() { return amount; }
	
	@Override
	public boolean matches(ItemStack stack) {
		for (ItemStack oreStack : OreDictionary.getOres(name))
			if (BetterStorageUtils.wildcardMatch(oreStack, stack)) return true;
		return false;
	}
	
	@Override
	public ItemStack getSampleInput(Random rnd) {
		List<ItemStack> ores = OreDictionary.getOres(name);
		return (!ores.isEmpty() ? StackUtils.copyStack(ores.get(rnd.nextInt(ores.size())), getAmount()) : null);
	}
	
}
