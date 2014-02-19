package net.mcft.copy.betterstorage.api.crafting;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.mcft.copy.betterstorage.api.BetterStorageUtils;
import net.mcft.copy.betterstorage.utils.StackUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.oredict.OreDictionary;

public class RecipeInputItemStack implements IRecipeInput {
	
	public final ItemStack stack;
	public final boolean nbtSensitive;
	
	public RecipeInputItemStack(ItemStack stack, boolean nbtSensitive) {
		this.stack = stack;
		this.nbtSensitive = nbtSensitive;
		// If input is NBT sensitive, make sure it has an NBT compound.
		// Empty means it only matches items with no NBT data.
		if (nbtSensitive) {
			if (!stack.hasTagCompound())
				stack.setTagCompound(new NBTTagCompound());
		// Otherwise, always remove the NBT compound,
		// because then it will match any item.
		} else if (stack.hasTagCompound())
			stack.setTagCompound(null);
	}
	public RecipeInputItemStack(ItemStack stack) {
		this(stack, false);
	}
	
	@Override
	public int getAmount() { return stack.stackSize; }
	
	@Override
	public boolean matches(ItemStack stack) { return BetterStorageUtils.wildcardMatch(this.stack, stack); }
	
	@Override
	public ItemStack getSampleInput(Random rnd) {
		if ((stack.getItemDamage() == OreDictionary.WILDCARD_VALUE) && (stack.getHasSubtypes())) {
			List<ItemStack> list = new ArrayList<ItemStack>();
			stack.getItem().getSubItems(stack.itemID, null, list);
			return StackUtils.copyStack(list.get(rnd.nextInt(list.size())), getAmount());
		} else return stack;
	}
	
}
