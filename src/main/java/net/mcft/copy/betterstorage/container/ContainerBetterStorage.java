package net.mcft.copy.betterstorage.container;

import net.mcft.copy.betterstorage.client.gui.GuiBetterStorage;
import net.mcft.copy.betterstorage.inventory.InventoryTileEntity;
import net.mcft.copy.betterstorage.utils.StackUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

//TODO (1.8): Inventory Tweaks API
//@ChestContainer(isLargeChest = true)
public class ContainerBetterStorage extends Container {
	
	private final int columns;
	private final int rows;
	
	public final EntityPlayer player;
	public final IInventory inventory;
	public final int separation;
	
	private int startHotbar = -1;
	
	@SideOnly(Side.CLIENT)
	public GuiBetterStorage updateGui;
	
//	@ChestContainer.RowSizeCallback
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
		inventory.openInventory(player);
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
	
	public int getHeight() { return ((getRows() + 4) * 18) + separation + 29; }
	
	protected void setupInventoryContainer() {
		for (int y = 0; y < getRows(); y++)
			for (int x = 0; x < getColumns(); x++)
				addSlotToContainer(new SlotBetterStorage(this, inventory, x + y * getColumns(),
				                                         8 + x * 18, 18 + y * 18));
	}
	
	protected void setupInventoryPlayer() {
		for (int y = 0; y < 3; y++)
			for (int x = 0; x < 9; x++)
				addSlotToContainer(new SlotBetterStorage(this, player.inventory, 9 + x + y * 9,
				                                         8 + x * 18 + (getColumns() - 9) * 9,
				                                         getHeight() - 83 + y * 18));
		setHotbarStart();
		for (int x = 0; x < 9; x++)
			addSlotToContainer(new SlotBetterStorage(this, player.inventory, x,
			                                         8 + x * 18 + (getColumns() - 9) * 9,
			                                         getHeight() - 25));
	}
	
	protected void setHotbarStart() {
		startHotbar = inventorySlots.size();
	}
	
	/** Returns if the slot is in the inventory. */
	protected boolean inInventory(int slot) { return (slot < inventory.getSizeInventory()); }
	/** Returns the start slot where items should be transfered to from this slot. */
	protected int transferStart(int slot) {
		return (inInventory(slot) ? inventory.getSizeInventory() : 0);
	}
	/** Returns the end slot where items should be transfered to from this slot. */
	protected int transferEnd(int slot) {
		return (inInventory(slot) ? inventorySlots.size() : inventory.getSizeInventory());
	}
	/** Returns the direction the stack should be transfered in. true = normal, false = backwards. */
	protected boolean transferDirection(int slot) { return inInventory(slot); }
	
	/** Called when a slot is changed. */
	public void onSlotChanged(int slot) {  }
	
	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int slotId) {
		ItemStack stack = null;
		Slot slot = (Slot)inventorySlots.get(slotId);
		
		// If slot isn't empty and item can be stacked.
		if ((slot != null) && slot.getHasStack()) {
			ItemStack slotStack = slot.getStack();
			stack = slotStack.copy();
			if (!mergeItemStack(slotStack, transferStart(slotId), transferEnd(slotId), transferDirection(slotId)))
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
				int index = start + (backwards ? ((end - start) - 1 - i) : i);
				
				Slot slot = (Slot)this.inventorySlots.get(index);
				ItemStack slotStack = slot.getStack();
				int maxStackSize = Math.min(stack.getMaxStackSize(),
                        slot.inventory.getInventoryStackLimit());
				
				if (StackUtils.matches(stack, slotStack) &&
				    (slotStack.stackSize < maxStackSize)) {
					int amount = Math.min(slotStack.stackSize + stack.stackSize, maxStackSize);
					ItemStack testStack = StackUtils.copyStack(stack, amount);
					if (slot.isItemValid(testStack) &&
					    slot.inventory.isItemValidForSlot(slot.slotNumber, testStack)) {
						stack.stackSize -= (testStack.stackSize - slotStack.stackSize);
						slot.putStack(testStack);
						success = true;
					}
				}
				
				if (stack.stackSize <= 0)
					return success;
			}
		}
		
		// Try to put the stack into empty slots.
		for (int i = 0; i < (end - start); i++) {
			int index = start + (backwards ? ((end - start) - 1 - i) : i);
			
			Slot slot = (Slot)this.inventorySlots.get(index);
			ItemStack slotStack = slot.getStack();
			
			if (slotStack == null) {
				int maxStackSize = Math.min(stack.getMaxStackSize(), slot.inventory.getInventoryStackLimit());
				ItemStack testStack = StackUtils.copyStack(stack, Math.min(stack.stackSize, maxStackSize));
				if (slot.isItemValid(testStack) &&
				    slot.inventory.isItemValidForSlot(slot.slotNumber, testStack)) {
					stack.stackSize -= testStack.stackSize;
					slot.putStack(testStack);
					success = true;
				}
			}
			
			if (stack.stackSize <= 0) break;
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
					// Override default behavior to use putStack
					// instead of manipulating stackSize directly.
					ItemStack slotStack = slot.getStack();
					ItemStack holding = player.inventory.getItemStack();
					if ((slotStack != null) && (holding != null) &&
					    ((holding == null) ? slot.canTakeStack(player) : slot.isItemValid(holding)) &&
					    StackUtils.matches(slotStack, holding)) {
						int amount = ((button == 0) ? holding.stackSize : 1);
						amount = Math.min(amount, slot.getSlotStackLimit() - slotStack.stackSize);
						amount = Math.min(amount, slotStack.getMaxStackSize() - slotStack.stackSize);
						if (amount > 0) {
							if ((holding.stackSize -= amount) <= 0)
								player.inventory.setItemStack(null);
							slot.putStack(StackUtils.copyStack(slotStack, slotStack.stackSize + amount));
						}
						return slotStack;
					}
				}
			} else if (special == 1) {
				// Override default shift-click so it doesn't call
				// retrySlotClick, whatever that was meant for.
				return (slot.canTakeStack(player) ? transferStackInSlot(player, slotId) : null);
			} else if ((special == 2) && (button >= 0) && (button < 9)) {
				// Override default hotbar switching to make sure
				// the items can be taken and put into those slots.
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
	public void onContainerClosed(EntityPlayer player) {
		super.onContainerClosed(player);
		inventory.closeInventory(player);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void updateProgressBar(int id, int val) {
		if (updateGui != null) updateGui.update(id, val);
	}
	
	@SideOnly(Side.CLIENT)
	public void setUpdateGui(GuiBetterStorage gui) {
		updateGui = gui;
	}
	
	/** Sends a packet to all players looking at
	 *  this GUI for them to update something. */
	public void sendUpdate(int id, int value) {
		for (Object c : crafters)
			((ICrafting)c).sendProgressBarUpdate(this, id, value);
	}
	/** Sends a packet to all players looking at this GUI for them to update
	 *  something, but only if the value is different from the previous one. */
	public void sendUpdateIfChanged(int id, int value, int previousValue) {
		if (value != previousValue)
			sendUpdate(id, value);
	}
	
}
