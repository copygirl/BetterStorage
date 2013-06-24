package net.mcft.copy.betterstorage.item;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.mcft.copy.betterstorage.api.IContainerItem;
import net.mcft.copy.betterstorage.block.crate.CratePileData;
import net.mcft.copy.betterstorage.block.crate.TileEntityCrate;
import net.mcft.copy.betterstorage.misc.ItemIdentifier;
import net.mcft.copy.betterstorage.utils.StackUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class ItemCardboardBox extends ItemBlock implements IContainerItem {
	
	public ItemCardboardBox(int id) {
		super(id);
		setMaxStackSize(1);
	}
	
	// Item stuff
	
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean advancedTooltips) {
		// Show the contents in the cardboard box as tooltip.
		if (!StackUtils.has(stack, "Items")) return;
		
		ItemStack[] contents = StackUtils.getStackContents(stack, 9);
		int limit = (advancedTooltips ? 3 : 9);
		
		List<ItemStack> items = StackUtils.stackItems(contents);
		Collections.sort(items, new Comparator<ItemStack>() {
			@Override public int compare(ItemStack a, ItemStack b) {
				return (b.stackSize - a.stackSize); }
		});
		
		for (int i = 0; (i < items.size()) && (i < limit); i++) {
			ItemStack item = items.get(i);
			list.add(item.stackSize + "x " + item.getDisplayName());
		}
		
		if (items.size() <= limit) return;
		int count = 0;
		for (int i = 3; i < items.size(); i++)
			count += items.get(i).stackSize;
		 list.add(count + " more item" + ((count > 1) ? "s" : "") + " ...");
		 
	}
	
	@Override
	public boolean onItemUseFirst(ItemStack stack, EntityPlayer player,
	                              World world, int x, int y, int z, int side,
	                              float hitX, float hitY, float hitZ) {
		if (world.isRemote || player.isSneaking()) return false;
		TileEntity tileEntity = world.getBlockTileEntity(x, y, z);
		if (tileEntity == null) return false;
		boolean hasItems = StackUtils.has(stack, "Items");
		return ((hasItems) ? putItems(stack, player, tileEntity) :
		                     takeItems(stack, player, tileEntity));
	}
	
	// Using the cardboard box
	
	private boolean putItems(ItemStack stack, EntityPlayer player, TileEntity tileEntity) {
		
		ItemStack[] contents = StackUtils.getStackContents(stack, 9);
		
		// Put all items in a list.
		List<ItemStack> items = new ArrayList<ItemStack>();
		int numStacks = StackUtils.stackItems(contents, items);
		
		if (tileEntity instanceof TileEntityCrate) {
			
			TileEntityCrate crate = (TileEntityCrate)tileEntity;
			CratePileData data = crate.getPileData();
			
			int freeSlots = data.getFreeSlots();
			if (freeSlots < numStacks) {
				for (ItemStack itemsStack : items) {
					ItemIdentifier identifier = new ItemIdentifier(itemsStack);
					int count = data.getItemCount(identifier);
					int stacksBefore = identifier.calcNumStacks(count);
					int stacksAfter = identifier.calcNumStacks(count + itemsStack.stackSize);
					freeSlots -= (stacksAfter - stacksBefore);
					// If there's no space to put in all
					// items from the cardboard box, don't.
					if (freeSlots < 0) return false;
				}
			}
			
			// Add items to crate.
			data.addItems(items);
			// Destroy cardboard box item.
			player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
			
			return true;
			
		} else if (tileEntity instanceof IInventory) {
			
			IInventory inventory = (IInventory)tileEntity;
			List<SetSlotInfo> setSlots = new ArrayList<SetSlotInfo>();
			int stackLimit = inventory.getInventoryStackLimit();
			
			outerLoop:
			for (int slot = 0; slot < inventory.getSizeInventory(); slot++) {
				ItemStack slotStack = inventory.getStackInSlot(slot);
				// See if any items fit into this slot.
				for (Iterator<ItemStack> iter = items.iterator(); iter.hasNext(); ) {
					ItemStack itemsStack = iter.next();

					int maxStackSize = Math.min(stackLimit, itemsStack.getMaxStackSize());
					ItemStack testStack = itemsStack.copy();
					int count;
					
					if (slotStack == null) {
						count = Math.min(itemsStack.stackSize, maxStackSize);
						testStack.stackSize = count;
					} else if (StackUtils.matches(slotStack, itemsStack)) {
						count = Math.min(itemsStack.stackSize, (maxStackSize - slotStack.stackSize));
						if (count <= 0) continue;
						testStack.stackSize = slotStack.stackSize + count;
					} else continue;
					
					if (!inventory.isStackValidForSlot(slot, testStack)) continue;
					
					// Add the slot and test stack to the to-be-modified map.
					setSlots.add(new SetSlotInfo(slot, testStack));
					// If the items stack is used up, remove it from the list.
					if ((itemsStack.stackSize -= count) <= 0)
						iter.remove();
					
					continue outerLoop;
				}
			}
			
			// Abort if some items wouldn't fit in the inventory.
			if (items.size() > 0) return false;
			// Set all the slots in the inventory.
			for (SetSlotInfo setSlot : setSlots)
				inventory.setInventorySlotContents(setSlot.slot, setSlot.stack);
			// Destroy cardboard box item.
			player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
			
			return true;
			
		}
		
		return false;
		
	}
	
	private boolean takeItems(ItemStack stack, EntityPlayer player, TileEntity tileEntity) {
		
		List<ItemStack> items = null;
		
		if (tileEntity instanceof TileEntityCrate) {
			
			TileEntityCrate crate = (TileEntityCrate)tileEntity;
			// It's like stealing candy from a baby!
			items = crate.getPileData().pickAndRemoveItemStacks(9);
			
		} else if (tileEntity instanceof IInventory) {
			
			IInventory inventory = (IInventory)tileEntity;
			items = new ArrayList<ItemStack>();
			
			for (int slot = 0; slot < inventory.getSizeInventory(); slot++) {
				ItemStack slotStack = inventory.getStackInSlot(slot);
				if (slotStack == null) continue;
				for (int i = 0; i < items.size(); i++) {
					ItemStack itemsStack = items.get(i);
					int space = itemsStack.getMaxStackSize() - itemsStack.stackSize;
					if ((space <= 0) || !StackUtils.matches(itemsStack, slotStack)) continue;
					int count = Math.min(slotStack.stackSize, space);
					ItemStack addStack = inventory.decrStackSize(slot, count);
					if (addStack != null) {
						itemsStack.stackSize += addStack.stackSize;
						slotStack.stackSize -= addStack.stackSize;
						break;
					}
				}
				if ((slotStack.stackSize <= 0) || (items.size() >= 9)) continue;
				int count = Math.min(slotStack.stackSize, slotStack.getMaxStackSize());
				slotStack = inventory.decrStackSize(slot, count);
				if (slotStack == null) continue;
				items.add(slotStack);
			}
			
		}
		
		if ((items != null) && (items.size() > 0)) {
			
			ItemStack newStack = stack.copy();
			ItemStack[] contents = items.toArray(new ItemStack[9]);
			StackUtils.setStackContents(newStack, contents);
			player.inventory.setInventorySlotContents(player.inventory.currentItem, newStack);
			
			return true;
			
		} else return false;
		
	}
	
	private static class SetSlotInfo {
		
		public final int slot;
		public final ItemStack stack;
		public SetSlotInfo(int slot, ItemStack stack) {
			this.slot = slot;
			this.stack = stack;
		}
		
	}
	
	// IContainerItem implementation
	
	@Override
	public ItemStack[] getContainerItemContents(ItemStack container) {
		if (StackUtils.has(container, "Items"))
			return StackUtils.getStackContents(container, 9);
		else return null;
	}
	
	@Override
	public boolean canBeStoredInContainerItem(ItemStack item) {
		return !StackUtils.has(item, "Items");
	}
	
}
