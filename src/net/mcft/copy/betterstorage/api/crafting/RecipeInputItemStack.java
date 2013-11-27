package net.mcft.copy.betterstorage.api.crafting;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.mcft.copy.betterstorage.api.BetterStorageUtils;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class RecipeInputItemStack implements IRecipeInput {
	
	public final ItemStack stack;
	
	public RecipeInputItemStack(ItemStack stack) {
		this.stack = stack;
	}
	
	@Override
	public int getAmount() { return stack.stackSize; }
	
	@Override
	public boolean matches(ItemStack stack) { return BetterStorageUtils.wildcardMatch(this.stack, stack); }
	
	@Override
	public ItemStack getSampleInput(Random rnd) {
		ItemStack stack = this.stack;
		if ((stack.getItemDamage() == OreDictionary.WILDCARD_VALUE) && (stack.getHasSubtypes())) {
			List<ItemStack> list = new ArrayList<ItemStack>();
			stack.getItem().getSubItems(stack.itemID, null, list);
			stack = list.get(rnd.nextInt(list.size())).copy();
			stack.stackSize = getAmount();
		}
		return stack;
	}
	
}
