package net.mcft.copy.betterstorage.container;

import net.mcft.copy.betterstorage.inventory.InventoryKeyring;
import net.mcft.copy.betterstorage.item.ItemKey;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

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
	protected void setupInventoryContainer() {
		for (int y = 0; y < getRows(); y++)
			for (int x = 0; x < getColumns(); x++)
				addSlotToContainer(new SlotKey(inventory, x + y * getColumns(),
						8 + x * 18, 18 + y * 18));
	}
	
	@Override
	protected void setupInventoryPlayer() {
		for (int y = 0; y < 3; y++)
			for (int x = 0; x < 9; x++)
				addSlot(player.inventory, 9 + x + y * 9,
				        8 + x * 18 + (getColumns() - 9) * 9, getRows() * 18 + y * 18 + 32);
		setHotbarStart();
		for (int x = 0; x < 9; x++)
			addSlot(player.inventory, x, 8 + x * 18 + (getColumns() - 9) * 9, getRows() * 18 + 90);
	}
	
	private void addSlot(IInventory inventory, int slotId, int x, int y) {
		Slot slot;
		if (slotId == getProtectedIndex())
			slot = new SlotProtected(inventory, slotId, x, y);
		else slot = new Slot(inventory, slotId, x, y);
		addSlotToContainer(slot);
	}
	
	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int slotId) {
		ItemStack stack = null;
		Slot slot = (Slot)inventorySlots.get(slotId);
		if ((slot != null) && slot.getHasStack()) {
			stack = slot.getStack();
			if (!(stack.getItem() instanceof ItemKey))
				return null;
		}
		return super.transferStackInSlot(player, slotId);
	}
	
}
