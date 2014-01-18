package net.mcft.copy.betterstorage.container;

import net.mcft.copy.betterstorage.inventory.InventoryCratePlayerView;
import net.mcft.copy.betterstorage.tile.crate.CratePileData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.Packet103SetSlot;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;

public class ContainerCrate extends ContainerBetterStorage {
	
	private EntityPlayer player;
	private InventoryCratePlayerView playerView;
	
	private int lastFullness = 0;
	private int fullness = 0;
	
	@Override
	public boolean invtweaks$validChest() { return false; }
	
	public ContainerCrate(EntityPlayer player, InventoryCratePlayerView inventory) {
		super(player, inventory, 9, inventory.getSizeInventory() / 9);
		
		this.player = player;
		this.playerView = inventory;
		
		CratePileData data = inventory.data;
		fullness = data.getOccupiedSlots() * 255 / data.getCapacity();
	}
	
	@Override
	public void detectAndSendChanges() {
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
	public ItemStack transferStackInSlot(EntityPlayer player, int slotId) {
		Slot slot = (Slot)inventorySlots.get(slotId);
		ItemStack slotStack = slot.getStack();
		if (slotStack == null) return null;
		ItemStack stackBefore = slotStack.copy();
		// If slot is in the container inventory, try to transfer the item to the player.
		if (slotId < playerView.getSizeInventory()) {
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
			PacketDispatcher.sendPacketToPlayer(new Packet103SetSlot(player.openContainer.windowId, slotId, overflow), (Player)player);
			if (!success) return null;
		}
		return stackBefore;
	}
	
}
