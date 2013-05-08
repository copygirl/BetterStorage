package net.mcft.copy.betterstorage.inventory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Queue;

import net.mcft.copy.betterstorage.api.ICrateWatcher;
import net.mcft.copy.betterstorage.block.crate.CratePileData;
import net.mcft.copy.betterstorage.block.crate.TileEntityCrate;
import net.mcft.copy.betterstorage.misc.ItemIdentifier;
import net.mcft.copy.betterstorage.utils.RandomUtils;
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
		super("container.crate");
		this.data = crate.getPileData();
		this.crate = crate;
		
		int size = Math.min(data.getCapacity(), inventoryMaxSize);
		tempContents = new ItemStack[size];
		
		// Fill temp contents with some random items from the crate pile.
		int totalStacks = data.getOccupiedSlots();
		LinkedList<ItemStack> stacks = new LinkedList<ItemStack>();
		for (ItemStack contentsStack : data)
			stacks.add(contentsStack.copy());
		for (int slot = 0; (totalStacks > 0) && (slot < size); slot++) {
			int randomStack = RandomUtils.getInt(totalStacks--);
			for (ListIterator<ItemStack> iter = stacks.listIterator(); iter.hasNext(); ) {
				ItemStack contentsStack = iter.next();
				int numStacks = ItemIdentifier.calcNumStacks(contentsStack);
				if (randomStack < numStacks) {
					ItemStack stack = contentsStack.copy();
					stack.stackSize = Math.min(stack.stackSize, stack.getMaxStackSize());
					getMapData(stack).itemCount += stack.stackSize;
					tempContents[slot] = stack;
					if ((contentsStack.stackSize -= stack.stackSize) <= 0) iter.remove();
					break;
				}
				randomStack -= numStacks;
			}
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
			getMapData(oldStack).itemCount -= oldStack.stackSize;
			data.removeItems(oldStack);
		}
		if (stack != null) {
			stack = stack.copy();
			stack.stackSize = Math.min(stack.stackSize,
			                           Math.min(data.spaceForItem(stack),
			                                    stack.getMaxStackSize()));
			if (stack.stackSize == 0) return;
			getMapData(stack).itemCount += stack.stackSize;
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
		return ((player.worldObj.getBlockTileEntity(x, y, z) == crate) &&
				(player.getDistanceSq(x + 0.5, y + 0.5, z + 0.5) < 64.0) &&
				(getSizeInventory() <= data.getCapacity()));
	}
	
	@Override
	public void onInventoryChanged() { }
	
	@Override
	public void openChest() { data.addWatcher(this); }
	@Override
	public void closeChest() { data.removeWatcher(this); }
	
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
	
	private void onSlotEmptied(int slot) {
		int emptySlots = 0;
		for (int s = 0; s < tempContents.length; s++)
			if (tempContents[s] == null) emptySlots++;
		
		if (emptySlots <= data.getFreeSlots()) return;
		
		int size = data.getNumItems();
		List<Integer> randomIndexList = new ArrayList<Integer>(size);
		for (int i = 0; i < size; i++) randomIndexList.add(i);
		
		while ((emptySlots > data.getFreeSlots()) && (randomIndexList.size() > 0)) {
			
			int randomIndex = RandomUtils.getInt(randomIndexList.size());
			int index = randomIndexList.get(randomIndex);
			
			ItemStack stack = data.getItemStack(index);
			ItemIdentifier item = new ItemIdentifier(stack);
			MapData itemData = getMapData(item);
			
			int count = (stack.stackSize - itemData.itemCount);
			if (count <= 0) continue;
			setItemsInSlot(slot, item, itemData, count);
			break;
			
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
		int size = Math.min(maxAmount, item.getItem().getItemStackLimit());
		tempContents[slot] = item.createStack(size);
		itemData.itemCount += size;
		return size;
	}
	
}
