package net.mcft.copy.betterstorage.container;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.mcft.copy.betterstorage.client.gui.GuiBetterStorage;
import net.mcft.copy.betterstorage.inventory.InventoryTileEntity;
import net.mcft.copy.betterstorage.utils.InventoryUtils;
import net.mcft.copy.betterstorage.utils.StackUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerBetterStorage extends Container {
	
	private final int columns;
	private final int rows;
	
	public final EntityPlayer player;
	public final IInventory inventory;
	public final int separation;
	
	private int startHotbar = -1;
	
	@SideOnly(Side.CLIENT)
	public GuiBetterStorage updateGui;
	
	public int getColumns() { return columns; }
	public int getRows() { return rows; }
	
	public ContainerBetterStorage(EntityPlayer player, IInventory inventory, int columns, int rows, int seperation) {
		this.player = player;
		this.inventory = inventory;
		this.columns = columns;
		this.rows = rows;
		this.separation = seperation;
		
		setupInventoryContainer();
		setupInventoryPlayer();
		inventory.openChest();
	}
	public ContainerBetterStorage(EntityPlayer player, IInventory inventory, int columns, int rows) {
		this(player, inventory, columns, rows, 14);
	}
	public ContainerBetterStorage(EntityPlayer player, InventoryTileEntity inventory) {
		this(player, inventory, inventory.columns, inventory.rows);
	}
	
	@SideOnly(Side.CLIENT)
	public ContainerBetterStorage(EntityPlayer player, IInventory inventory, int columns, int rows, GuiBetterStorage gui) {
		this(player, inventory, columns, rows);
		setUpdateGui(gui);
	}
	
	protected void setupInventoryContainer() {
		for (int y = 0; y < rows; y++)
			for (int x = 0; x < columns; x++)
				addSlotToContainer(new SlotBetterStorage(inventory, x + y * columns,
				                                         8 + x * 18, 18 + y * 18));
	}
	
	protected void setupInventoryPlayer() {
		for (int y = 0; y < 3; y++)
			for (int x = 0; x < 9; x++)
				addSlotToContainer(new SlotBetterStorage(player.inventory, 9 + x + y * 9,
				                                         8 + x * 18 + (columns - 9) * 9,
				                                         rows * 18 + y * 18 + 18 + separation));
		setHotbarStart();
		for (int x = 0; x < 9; x++)
			addSlotToContainer(new SlotBetterStorage(player.inventory, x,
			                                         8 + x * 18 + (columns - 9) * 9,
			                                         rows * 18 + 76 + separation));
	}
	
	protected void setHotbarStart() {
		startHotbar = inventorySlots.size();
	}
	
	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int slotId) {
		ItemStack stack = null;
		Slot slot = (Slot)inventorySlots.get(slotId);
		
		// If slot isn't empty and item can be stacked.
		if ((slot != null) && slot.getHasStack()) {
			ItemStack slotStack = slot.getStack();
			stack = slotStack.copy();
			// If slot is in the container inventory, try to transfer the item to the player.
			if (slotId < inventory.getSizeInventory()) {
				if (!mergeItemStack(slotStack, inventory.getSizeInventory(), inventorySlots.size(), true))
					return null;
			// If slot is in the player inventory, try to transfer the item to the container.
			} else if (!mergeItemStack(slotStack, 0, inventory.getSizeInventory(), false))
				return null;
			if (slotStack.stackSize != 0)
				slot.onSlotChanged();
			else slot.putStack(null);
		}
		
		return stack;
	}
	
	@Override
	protected boolean mergeItemStack(ItemStack stack, int start, int end, boolean backwards) {
		
		boolean success = false;
		
		// Try to put the stack into existing stacks with the same type.
		if (stack.isStackable()) {
			for (int i = 0; i < (end - start); i++) {
				int index = (backwards ? (end - 1 - i) : i);
				
				Slot slot = (Slot)this.inventorySlots.get(index);
				ItemStack slotStack = slot.getStack();
				int maxStackSize = Math.min(stack.getMaxStackSize(),
                        slot.inventory.getInventoryStackLimit());
				
				if (StackUtils.matches(stack, slotStack) &&
				    (slotStack.stackSize < maxStackSize)) {
					ItemStack testStack = stack.copy();
					testStack.stackSize = Math.min(slotStack.stackSize + stack.stackSize, maxStackSize);
					if (slot.isItemValid(testStack) &&
					    slot.inventory.isStackValidForSlot(slot.slotNumber, testStack)) {
						stack.stackSize -= (testStack.stackSize - slotStack.stackSize);
						slot.putStack(testStack);
						success = true;
					}
				}
				
				if (stack.stackSize <= 0) return success;
			}
		}
		
		// Try to put the stack into empty slots.
		for (int i = 0; i < (end - start); i++) {
			int index = (backwards ? (end - 1 - i) : i);
			
			Slot slot = (Slot)this.inventorySlots.get(index);
			ItemStack slotStack = slot.getStack();
			
			if (slotStack == null) {
				ItemStack testStack = stack.copy();
				int maxStackSize = Math.min(stack.getMaxStackSize(),
				                            slot.inventory.getInventoryStackLimit());
				testStack.stackSize = Math.min(stack.stackSize, maxStackSize);
				if (slot.isItemValid(testStack) &&
				    slot.inventory.isStackValidForSlot(slot.slotNumber, testStack)) {
					stack.stackSize -= testStack.stackSize;
					slot.putStack(testStack);
					success = true;
				}
			}
			
			if (stack.stackSize <= 0) return success;
		}
		
		return success;
		
	}
	
	@Override
	public ItemStack slotClick(int slotId, int button, int special, EntityPlayer player) {
		Slot slot = null;
		if ((slotId >= 0) && (slotId < inventorySlots.size()))
			slot = (Slot)inventorySlots.get(slotId);
		if (slot != null) {
			if (special == 0) {
				if ((button == 0) || (button == 1)) {
					ItemStack slotStack = slot.getStack();
					ItemStack holding = player.inventory.getItemStack();
					if ((slotStack != null) && (holding != null) &&
					    slot.canTakeStack(player) && slot.isItemValid(holding) &&
					    (slotStack.itemID == holding.itemID) &&
					    (slotStack.getItemDamage() == holding.getItemDamage()) &&
					    ItemStack.areItemStackTagsEqual(slotStack, holding)) {
						int amount = ((button == 0) ? holding.stackSize : 1);
						amount = Math.min(amount, slot.getSlotStackLimit() - slotStack.stackSize);
						amount = Math.min(amount, slotStack.getMaxStackSize() - slotStack.stackSize);
						if (amount > 0) {
							if ((holding.stackSize -= amount) <= 0)
								player.inventory.setItemStack(null);
							slot.putStack(InventoryUtils.copyStack(slotStack, slotStack.stackSize + amount));
						}
						return slotStack;
					}
				}
			} else if ((special == 2) && (button >= 0) && (button < 9)) {
				if (startHotbar < 0) return null;
				Slot slot2 = (Slot)inventorySlots.get(startHotbar + button);
				ItemStack stack = slot.getStack();
				if (!slot2.canTakeStack(player) ||
				    ((stack != null) && !slot2.isItemValid(stack)))
					return null;
			}
		}
		return super.slotClick(slotId, button, special, player);
	}
	
	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return inventory.isUseableByPlayer(player);
	}
	
	@Override
	public void onCraftGuiClosed(EntityPlayer player) {
		super.onCraftGuiClosed(player);
		inventory.closeChest();
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public void updateProgressBar(int par1, int par2) {
		if (updateGui != null) updateGui.update(par1, par2);
	}
	
	@SideOnly(Side.CLIENT)
	public void setUpdateGui(GuiBetterStorage gui) {
		updateGui = gui;
	}
	
}
