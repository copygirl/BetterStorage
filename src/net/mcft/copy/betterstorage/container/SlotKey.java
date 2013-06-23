package net.mcft.copy.betterstorage.container;

import net.mcft.copy.betterstorage.api.IKey;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public class SlotKey extends SlotBetterStorage {
	
	public SlotKey(IInventory inventory, int slot, int x, int y) {
		super(inventory, slot, x, y);
	}
	
	@Override
	public boolean isItemValid(ItemStack stack) {
		return ((stack != null) && (stack.getItem() instanceof IKey) &&
		        ((IKey)stack.getItem()).isNormalKey());
	}
	
}
