package net.mcft.copy.betterstorage.addon.minetweaker;

import java.util.ArrayList;
import java.util.List;

import minetweaker.MineTweakerAPI;
import minetweaker.api.item.IItemStack;
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
	
	public static ItemStack[] toStacks(IItemStack[] mtStacks) {
		ItemStack[] stacks = new ItemStack[mtStacks.length];
		for (int i = 0; i < stacks.length; i++)
			stacks[i] = toStack(mtStacks[i]);
		return stacks;
	}
	
	public static List<ItemStack> toStacks(List<IItemStack> mtStacks) {
		List<ItemStack> stacks = new ArrayList<ItemStack>();
		ItemStack stack;
		for (IItemStack mtStack : mtStacks)
			if ((stack = toStack(mtStack)) != null)
				stacks.add(stack);
		return stacks;
	}

}
