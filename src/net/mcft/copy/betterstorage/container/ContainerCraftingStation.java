package net.mcft.copy.betterstorage.container;

import net.mcft.copy.betterstorage.inventory.InventoryCraftingStation;
import net.minecraft.entity.player.EntityPlayer;

public class ContainerCraftingStation extends ContainerBetterStorage {
	
	public ContainerCraftingStation(EntityPlayer player, InventoryCraftingStation inventory) {
		super(player, inventory, 9, 2);
	}
	
	@Override
	public int getHeight() { return 209; }
	
	@Override
	protected void setupInventoryContainer() {
		for (int i = 0; i < 2; i++)
			for (int y = 0; y < 3; y++)
				for (int x = 0; x < 3; x++)
					addSlotToContainer(new SlotBetterStorage(
							inventory, getRows() * getColumns() + i * 9 + x + y * 3,
							17 + i * 90 + x * 18, 17 + y * 18));
		
		for (int y = 0; y < getRows(); y++)
			for (int x = 0; x < getColumns(); x++)
				addSlotToContainer(new SlotBetterStorage(
						inventory, x + y * getColumns(),
						8 + x * 18, 76 + y * 18));
	}
	
	@Override
	protected boolean inInventory(int slot) { return (super.inInventory(slot) && (slot >= 18)); }
	@Override
	protected int transferStart(int slot) {
		return (!inInventory(slot) ? 18 : super.transferStart(slot));
	}
	
}
