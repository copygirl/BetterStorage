package buildcraft.api.inventory;

import net.minecraft.src.IInventory;
import net.minecraft.src.ItemStack;
import net.minecraftforge.common.ForgeDirection;

public interface ISpecialInventory extends IInventory {
	
	int addItem(ItemStack stack, boolean doAdd, ForgeDirection from);
	
	ItemStack[] extractItem(boolean doRemove, ForgeDirection from, int maxItemCount);
	
}
