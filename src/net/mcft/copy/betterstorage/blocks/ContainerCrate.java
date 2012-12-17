package net.mcft.copy.betterstorage.blocks;

import net.minecraft.src.Container;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.EntityPlayerMP;
import net.minecraft.src.ItemStack;
import net.minecraft.src.Packet;
import net.minecraft.src.Packet103SetSlot;
import net.minecraft.src.Slot;

public class ContainerCrate extends Container {

	private EntityPlayer player;
	private InventoryCratePlayerView inventory;
	
	private int width = 9;
	private int height;
	
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
	}
	
	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return inventory.isUseableByPlayer(player);
	}
	
	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int slotId) {
		Slot slot = (Slot)inventorySlots.get(slotId);
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
				slot.putStack(inventory.data.addItems(slotStack));
				// Send slot contents to player.
				ItemStack stack = ((Slot)inventorySlots.get(slotId)).getStack();
				Packet packet = new Packet103SetSlot(player.openContainer.windowId, slotId, stack);
				((EntityPlayerMP)player).playerNetServerHandler.sendPacketToPlayer(packet);
			}
		}
		return null;
	}
	
	// We need to override this function because Minecraft's code is stupid.
	// ItemStacks are manipulated directly, instead how it should be done.
	// Which is by calling decrStackSize and setInventorySlotContents.
	@Override
	public ItemStack slotClick(int slot, int button, int special, EntityPlayer player) {
		// See if our container is being clicked ..
		if (slot >= 0 && slot < inventory.getSizeInventory()) {
			ItemStack stack = inventory.getStackInSlot(slot);
			ItemStack holding = player.inventory.getItemStack();
			
			// If clicking the slot normally.
			if (special == 0) {
				// If the player has an item picked up (on his mouse cursor).
				if (holding != null) {
					// Leftclick: Put in or swap items.
					// Also swap items on rightclick when they don't match
					if (button == 0 || (stack != null && !holding.isItemEqual(stack))) {
						ItemStack putStack = holding.copy();
						// If the items are equal, put some items in.
						if (stack != null && holding.isItemEqual(stack)) {
							if (stack.stackSize < stack.getMaxStackSize()) {
								putStack.stackSize = Math.min(holding.stackSize + stack.stackSize,
								                              holding.getMaxStackSize());
								holding.stackSize -= (putStack.stackSize - stack.stackSize);
								if (holding.stackSize == 0) holding = null;
								inventory.setInventorySlotContents(slot, putStack);
							}
						// Otherwise swap the stacks.
						} else {
							holding = ItemStack.copyItemStack(stack);
							inventory.setInventorySlotContents(slot, putStack);
						}
					// Rightclick: Move 1 item to the slot.
					} else {
						int stackSize = -1;
						if (stack == null) stackSize = 0;
						else if (holding.isItemEqual(stack) &&
						         stack.stackSize < stack.getMaxStackSize())
							stackSize = stack.stackSize;
						if (stackSize != -1) {
							stack = holding.copy();
							stack.stackSize = stackSize + 1;
							inventory.setInventorySlotContents(slot, stack);
							holding.stackSize -= 1;
							if (holding.stackSize == 0) holding = null;
						}
					}
				// Otherwise just pick up some items from the slot.
				} else if (stack != null) {
					int amount = ((button == 0) ? stack.stackSize : (stack.stackSize + 1) / 2);
					holding = inventory.decrStackSize(slot, amount);
				}
			// If holding shift, just transfer the item in the slot.
			} else if (special == 1 && stack != null)
				transferStackInSlot(player, slot);
			// If pressing the pick block button, not holding any items
			// and in creative mode, copy the item with max stack size.
			else if (special == 3 && stack != null && holding == null &&
			         player.capabilities.isCreativeMode) {
				holding = stack.copy();
				holding.stackSize = holding.getMaxStackSize();
			}
			
			// If the holding item changed, update it in the player inventory.
			if (holding != player.inventory.getItemStack())
				player.inventory.setItemStack(holding);
			
			return stack;
		
		// .. otherwise use the stupid Minecraft function.
		} else return super.slotClick(slot, button, special, player);
	}
	// </rant>
	
	@Override
	public void onCraftGuiClosed(EntityPlayer player) {
		super.onCraftGuiClosed(player);
		inventory.closeChest();
	}
	
}
