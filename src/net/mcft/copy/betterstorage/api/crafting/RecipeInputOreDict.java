package net.mcft.copy.betterstorage.api.crafting;

import net.mcft.copy.betterstorage.api.BetterStorageUtils;
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
	
}
