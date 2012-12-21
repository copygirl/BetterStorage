package buildcraft.api.inventory;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.ForgeDirection;

public interface ISpecialInventory extends IInventory {
	
	int addItem(ItemStack stack, boolean doAdd, ForgeDirection from);
	
	ItemStack[] extractItem(boolean doRemove, ForgeDirection from, int maxItemCount);
	
}
