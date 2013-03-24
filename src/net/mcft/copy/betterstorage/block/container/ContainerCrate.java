package net.mcft.copy.betterstorage.block.container;

import net.mcft.copy.betterstorage.block.crate.CratePileData;
import net.mcft.copy.betterstorage.inventory.InventoryCratePlayerView;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet103SetSlot;

public class ContainerCrate extends Container {
	
	private EntityPlayer player;
	private InventoryCratePlayerView inventory;
	
	private int width = 9;
	private int height;
	
	private int lastFullness = 0;
	private int fullness = 0;
	
	public ContainerCrate(EntityPlayer player, InventoryCratePlayerView inventory) {
		this.player = player;
		this.inventory = inventory;
		
		int height = inventory.getSizeInventory() / width;
		
		for (int y = 0; y < height; y++)
			for (int x = 0; x < width; x++)
				addSlotToContainer(new Slot(inventory, x + y * width,
				                            8 + x * 18, 18 + y * 18));
		
		for (int y = 0; y < 3; y++)
			for (int x = 0; x < 9; x++)
				addSlotToContainer(new Slot(player.inventory, 9 + x + y * 9,
				                            8 + x * 18, height * 18 + y * 18 + 32));
		for (int x = 0; x < 9; x++)
			addSlotToContainer(new Slot(player.inventory, x,
			                            8 + x * 18, height * 18 + 90));
		
		CratePileData data = inventory.data;
		fullness = data.getOccupiedSlots() * 255 / data.getCapacity();
	}
	
	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		
		CratePileData data = inventory.data;
		fullness = data.getOccupiedSlots() * 255 / data.getCapacity();
		
		if (lastFullness != fullness)
			for (Object c : crafters)
				((ICrafting)c).sendProgressBarUpdate(this, 0, fullness);
		
		lastFullness = fullness;
	}
	
	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return inventory.isUseableByPlayer(player);
	}
	
	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int slotId) {
		Slot slot = (Slot)inventorySlots.get(slotId);
		ItemStack stackBefore = slot.getStack();
		if (stackBefore != null) stackBefore.copy();
		// If slot isn't empty and item can be stacked.
		if (slot != null && slot.getHasStack()) {
			ItemStack slotStack = slot.getStack();
			// If slot is in the container inventory, try to transfer the item to the player.
			if (slotId < inventory.getSizeInventory()) {
				int count = slotStack.stackSize;
				mergeItemStack(slotStack, inventory.getSizeInventory(), inventorySlots.size(), true);
				int amount = count - slotStack.stackSize;
				slotStack.stackSize = count;
				inventory.decrStackSize(slotId, amount);
			// If slot is in the player inventory, try to transfer the item to the container.
			} else {
				ItemStack stack = inventory.data.addItems(slotStack);
				slot.putStack(stack);
				// Send slot contents to player.
				Packet packet = new Packet103SetSlot(player.openContainer.windowId, slotId, stack);
				((EntityPlayerMP)player).playerNetServerHandler.sendPacketToPlayer(packet);
			}
		}
		return stackBefore;
	}
	
	@Override
	public void onCraftGuiClosed(EntityPlayer player) {
		super.onCraftGuiClosed(player);
		inventory.closeChest();
	}
	
}
