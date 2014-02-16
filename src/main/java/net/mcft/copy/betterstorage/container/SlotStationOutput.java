package net.mcft.copy.betterstorage.container;

import net.mcft.copy.betterstorage.inventory.InventoryCraftingStation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public class SlotStationOutput extends SlotBetterStorage {
	
	public final InventoryCraftingStation statingInventory;
	
	public SlotStationOutput(ContainerCraftingStation container,
	                         IInventory inventory, int index, int x, int y) {
		super(container, inventory, index, x, y);
		statingInventory = container.inv;
	}
	
	@Override
	public boolean isItemValid(ItemStack stack) { return false; }
	
	@Override
	public boolean canTakeStack(EntityPlayer player) {
		return (super.canTakeStack(player) && statingInventory.canTake(player));
	}
	
}
