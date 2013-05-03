package net.mcft.copy.betterstorage.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class InventoryKeyring extends InventoryItem {
	
	public InventoryKeyring(EntityPlayer player, String title) {
		super(player, 9, (title.isEmpty() ? "container.keyring" : title), !title.isEmpty());
	}
	
	@Override
	public void onInventoryChanged() {
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
