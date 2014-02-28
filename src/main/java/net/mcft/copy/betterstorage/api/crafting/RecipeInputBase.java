package net.mcft.copy.betterstorage.api.crafting;

import net.mcft.copy.betterstorage.utils.WorldUtils;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public abstract class RecipeInputBase implements IRecipeInput {
	
	@Override
	public abstract int getAmount();
	
	@Override
	public abstract boolean matches(ItemStack stack);
	
	@Override
	public ItemStack craft(ItemStack input, ICraftingSource source) {
		ItemStack ret = input;
		if (ret != null) {
			Item item = ret.getItem();
			ItemStack containerItem = ItemStack.copyItemStack(item.getContainerItemStack(ret));
			ret.stackSize -= getAmount();
			if ((containerItem != null) &&
			    (item.doesContainerItemLeaveCraftingGrid(ret) || ((ret = containerItem).stackSize > 0)) &&
			    !BetterStorageCrafting.tryAddItemToInventory(source, containerItem) &&
			    (source.getWorld() != null))
					WorldUtils.spawnItem(source.getWorld(), source.getX(), source.getY(), source.getZ(), containerItem);
		}
		return ret;
	}
	
}
