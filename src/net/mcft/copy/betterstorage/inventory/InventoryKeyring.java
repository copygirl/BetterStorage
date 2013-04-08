package net.mcft.copy.betterstorage.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class InventoryKeyring extends InventoryItem {
	
	public InventoryKeyring(EntityPlayer player) {
		super("container.keyring", player, 9);
	}
	
	@Override
	public void onInventoryChanged() {
		updateStack();
	}
	
	@Override
	protected void updateStack() {
		int count = 0;
		for (ItemStack stack : contents)
			if (stack != null) count++;
		stack.setItemDamage(count);
		super.updateStack();
	}
	
}
