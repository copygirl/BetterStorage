package net.mcft.copy.betterstorage.container;

import net.mcft.copy.betterstorage.inventory.InventoryCratePlayerView;
import net.mcft.copy.betterstorage.tile.crate.CratePileData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.S2FPacketSetSlot;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

//@InventoryContainer
public class ContainerCrate extends ContainerBetterStorage {
	
	private InventoryCratePlayerView playerView;
	
	public int fullness = 0;
	private int lastFullness = 0;
	
	public ContainerCrate(EntityPlayer player, InventoryCratePlayerView inventory) {
		super(player, inventory, 9, inventory.getSizeInventory() / 9);
		this.playerView = inventory;
		CratePileData data = inventory.data;
		fullness = data.getOccupiedSlots() * 255 / data.getCapacity();
	}
	public ContainerCrate(EntityPlayer player, int rows, String name, boolean localized) {
		super(player, new InventoryBasic(name, !localized, 9 * rows), 9, rows);
	}
	
	@Override
	public void detectAndSendChanges() {
		if (playerView == null) return;
		super.detectAndSendChanges();
		
		CratePileData data = playerView.data;
		// detectAndSendChanges gets called after a crate gets broken.
		// So to prevent a division by zero, just return.
		if (data.getNumCrates() <= 0) return;
		fullness = data.getOccupiedSlots() * 255 / data.getCapacity();
		sendUpdateIfChanged(0, fullness, lastFullness);
		lastFullness = fullness;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void updateProgressBar(int id, int val) {
		if (id == 0) fullness = val;
	}
	
	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int slotId) {
		// On the client, don't do anything fancy, just
		// use ContainerBetterStorage.transferStackInSlot.
		if (playerView == null)
			return super.transferStackInSlot(player, slotId);
		
		Slot slot = (Slot)inventorySlots.get(slotId);
		ItemStack slotStack = slot.getStack();
		if (slotStack == null) return null;
		ItemStack stackBefore = slotStack.copy();
		
		// If slot is in the container inventory, try to transfer the item to the player.
		if (slotId < getColumns() * getRows()) {
			int count = slotStack.stackSize;
			boolean success = mergeItemStack(slotStack, playerView.getSizeInventory(), inventorySlots.size(), true);
			int amount = count - slotStack.stackSize;
			slotStack.stackSize = count;
			playerView.decrStackSize(slotId, amount);
			if (!success) return null;
		
		// If slot is in the player inventory, try to transfer the item to the container.
		} else {
			boolean success = playerView.canFitSome(slotStack);
			ItemStack overflow = playerView.data.addItems(slotStack);
			slot.putStack(overflow);
			// Send slot contents to player if it doesn't match the calculated overflow.
			((EntityPlayerMP)player).playerNetServerHandler.sendPacket(
					new S2FPacketSetSlot(player.openContainer.windowId, slotId, overflow));
			if (!success) return null;
		}
		return stackBefore;
	}
	
}
