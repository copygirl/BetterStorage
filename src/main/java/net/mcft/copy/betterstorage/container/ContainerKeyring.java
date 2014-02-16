package net.mcft.copy.betterstorage.container;

import net.mcft.copy.betterstorage.inventory.InventoryKeyring;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;

public class ContainerKeyring extends ContainerBetterStorage {
	
	private static int protectedIndex = -1;
	
	public ContainerKeyring(EntityPlayer player, String title) {
		super(player, new InventoryKeyring(player, title), 9, 1);
	}
	
	public static void setProtectedIndex(int index) {
		protectedIndex = index;
	}
	private int getProtectedIndex() {
		int index = protectedIndex;
		protectedIndex = -1;
		return ((index >= 0) ? index : player.inventory.currentItem);
	}
	
	@Override
	protected void setupInventoryPlayer() {
		for (int y = 0; y < 3; y++)
			for (int x = 0; x < 9; x++)
				addSlot(player.inventory, 9 + x + y * 9,
				        8 + x * 18 + (getColumns() - 9) * 9,
				        getRows() * 18 + y * 18 + 32);
		setHotbarStart();
		for (int x = 0; x < 9; x++)
			addSlot(player.inventory, x,
			        8 + x * 18 + (getColumns() - 9) * 9,
			        getRows() * 18 + 90);
	}
	
	private void addSlot(IInventory inventory, int slotId, int x, int y) {
		SlotBetterStorage slot = new SlotBetterStorage(this, inventory, slotId, x, y);
		if (slotId == getProtectedIndex()) slot.setProtected();
		addSlotToContainer(slot);
	}
	
}
