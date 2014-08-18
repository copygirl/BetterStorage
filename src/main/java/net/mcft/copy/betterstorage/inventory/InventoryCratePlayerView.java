package net.mcft.copy.betterstorage.inventory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import net.mcft.copy.betterstorage.api.crate.ICrateWatcher;
import net.mcft.copy.betterstorage.misc.Constants;
import net.mcft.copy.betterstorage.misc.ItemIdentifier;
import net.mcft.copy.betterstorage.tile.crate.CratePileData;
import net.mcft.copy.betterstorage.tile.crate.TileEntityCrate;
import net.mcft.copy.betterstorage.utils.StackUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

/** An inventory interface built for players accessing crate piles. */
public class InventoryCratePlayerView extends InventoryBetterStorage implements ICrateWatcher {
	
	private static final int inventoryMaxSize = 9 * 6;
	
	private static class MapData {
		public int itemCount = 0;
	}
	
	public final CratePileData data;
	public final TileEntityCrate crate;
	
	private ItemStack[] tempContents;
	private Map<ItemIdentifier, MapData> countData = new HashMap<ItemIdentifier, MapData>();
	 
	private boolean ignoreModifiedItems = false;
	
	public InventoryCratePlayerView(TileEntityCrate crate) {
		super(Constants.containerCrate);
		this.data = crate.getPileData();
		this.crate = crate;
		
		int size = Math.min(data.getCapacity(), inventoryMaxSize);
		tempContents = new ItemStack[size];
		
		// Fill temp contents with some random items from the crate pile.
		List<Integer> slotAccess = new ArrayList<Integer>(size);
		for (int i = 0; i < size; i++) slotAccess.add(i);
		if (data.getOccupiedSlots() < size) Collections.shuffle(slotAccess);
		
		Iterator<ItemStack> iter = data.getContents().getRandomStacks().iterator();
		for (int slot = 0; ((slot < size) && iter.hasNext()); slot++) {
			ItemStack stack = iter.next();
			getMapData(stack).itemCount += stack.stackSize;
			tempContents[slotAccess.get(slot)] = stack;
		}
	}
	
	// Map data related functions
	
	private MapData getMapData(ItemIdentifier item) {
		MapData data = countData.get(item);
		if (data != null) return data;
		data = new MapData();
		countData.put(item, data);
		return data;
	}
	private MapData getMapData(ItemStack item) {
		return getMapData(new ItemIdentifier(item));
	}
	
	// IInventory implementation
	
	@Override
	public int getSizeInventory() { return tempContents.length; }
	
	@Override
	public ItemStack getStackInSlot(int slot) {
		if ((slot < 0) || (slot >= getSizeInventory())) return null;
		return tempContents[slot];
	}
	
	@Override
	public void setInventorySlotContents(int slot, ItemStack stack) {
		if ((slot < 0) || (slot >= getSizeInventory())) return;
		ItemStack oldStack = getStackInSlot(slot);
		ignoreModifiedItems = true;
		if (oldStack != null) {
			ItemIdentifier item = new ItemIdentifier(oldStack);
			getMapData(item).itemCount -= oldStack.stackSize;
			data.removeItems(item, oldStack.stackSize);
		}
		if (stack != null) {
			int amount = Math.min(stack.stackSize,
					Math.min(data.getSpaceForItem(stack),
					         stack.getMaxStackSize()));
			if (amount <= 0) return;
			stack = StackUtils.copyStack(stack.copy(), amount);
			getMapData(stack).itemCount += amount;
			data.addItems(stack);
		}
		ignoreModifiedItems = false;
		tempContents[slot] = stack;
		if (stack == null)
			onSlotEmptied(slot);
	}
	
	@Override
	public ItemStack decrStackSize(int slot, int amount) {
		ItemStack stack = getStackInSlot(slot);
		if (stack == null) return null;
		amount = Math.min(amount, stack.stackSize);
		
		ItemIdentifier item = new ItemIdentifier(stack);
		getMapData(item).itemCount -= amount;
		
		stack.stackSize -= amount;
		if (stack.stackSize <= 0)
			tempContents[slot] = null;
		
		ignoreModifiedItems = true;
		ItemStack result = data.removeItems(item, amount);
		ignoreModifiedItems = false;
		
		if (tempContents[slot] == null)
			onSlotEmptied(slot);
		
		return result;
	}
	
	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		int x = crate.xCoord;
		int y = crate.yCoord;
		int z = crate.zCoord;
		return ((player.worldObj.getTileEntity(x, y, z) == crate) &&
				(player.getDistanceSq(x + 0.5, y + 0.5, z + 0.5) < 64.0) &&
				(getSizeInventory() <= data.getCapacity()));
	}
	
	@Override
	public void markDirty() { }
	
	@Override
	public void openInventory() { data.addWatcher(this); }
	@Override
	public void closeInventory() { data.removeWatcher(this); }
	
	// ICrateWatcher implementation
	
	@Override
	public void onCrateItemsModified(ItemStack changed) {
		if (ignoreModifiedItems) return;
		
		ItemIdentifier item = new ItemIdentifier(changed);
		int amount = changed.stackSize;
		
		MapData itemData = getMapData(item);
		Queue<Integer> emptySlots = new LinkedList<Integer>();
		
		for (int slot = 0; slot < tempContents.length; slot++) {
			ItemStack stack = tempContents[slot];
			if (stack == null) { emptySlots.add(slot); continue; }
			if (!item.matches(stack)) continue;
			amount -= modifyItemsInSlot(slot, stack, itemData, amount);
			if (amount == 0) return;
		}
		
		while ((amount > 0) && (emptySlots.size() > 0))
			amount -= setItemsInSlot(emptySlots.poll(), item, itemData, amount);
	}
	
	// Misc functions
	
	/** Returns if at least 1 item from the stack could be
	 *  stored in the player view, but doesn't change anything. */
	public boolean canFitSome(ItemStack item) {
		for (ItemStack stack : tempContents) {
			if ((stack == null) || (StackUtils.matches(stack, item) &&
			                        (stack.stackSize < stack.getMaxStackSize())))
				return true;
		}
		return false;
	}
	
	private void onSlotEmptied(int slot) {
		int emptySlots = 0;
		for (int s = 0; s < tempContents.length; s++)
			if (tempContents[s] == null) emptySlots++;
		
		if (emptySlots <= data.getFreeSlots()) return;
		
		for (ItemStack stack : data.getContents().getItems()) {
			ItemIdentifier item = new ItemIdentifier(stack);
			MapData itemData = getMapData(item);
			
			int count = (stack.stackSize - itemData.itemCount);
			if (count <= 0) continue;
			setItemsInSlot(slot, item, itemData, count);
		}
	}
	
	private int modifyItemsInSlot(int slot, ItemStack stack, MapData itemData, int amount) {
		int count = Math.max(-stack.stackSize, Math.min(amount, stack.getMaxStackSize() - stack.stackSize));
		if ((stack.stackSize += count) <= 0)
			tempContents[slot] = null;
		itemData.itemCount += count;
		return count;
	}
	
	private int setItemsInSlot(int slot, ItemIdentifier item, MapData itemData, int maxAmount) {
		int size = Math.min(maxAmount, item.createStack(1).getMaxStackSize());
		tempContents[slot] = item.createStack(size);
		itemData.itemCount += size;
		return size;
	}
	
}
