package net.mcft.copy.betterstorage.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class SlotProtected extends Slot {
	
	public SlotProtected(IInventory inventory, int slot, int x, int y) {
		super(inventory, slot, x, y);
	}
	
	@Override
	public boolean canTakeStack(EntityPlayer player) { return false; }
	
	@Override
	public boolean isItemValid(ItemStack stack) { return false; }
	
}
