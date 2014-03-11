package net.mcft.copy.betterstorage.inventory;

import net.mcft.copy.betterstorage.api.lock.IKey;
import net.mcft.copy.betterstorage.misc.Constants;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class InventoryKeyring extends InventoryItem {
	
	public InventoryKeyring(EntityPlayer player, String title) {
		super(player, 9, (title.isEmpty() ? Constants.containerKeyring : title), !title.isEmpty());
	}
	
	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack) {
		return ((stack == null) || ((stack.getItem() instanceof IKey) &&
		                            ((IKey)stack.getItem()).isNormalKey()));
	}
	
	@Override
	public void markDirty() {
		updateStack();
	}
	
	@Override
	protected void updateStack() {
		int count = 0;
		for (ItemStack stack : allContents[0])
			if (stack != null) count++;
		stack.setItemDamage(count);
		super.updateStack();
	}
	
}
