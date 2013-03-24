package net.mcft.copy.betterstorage.inventory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import net.mcft.copy.betterstorage.BetterStorage;
import net.mcft.copy.betterstorage.block.crate.CratePileData;
import net.mcft.copy.betterstorage.block.crate.ICrateWatcher;
import net.mcft.copy.betterstorage.block.crate.TileEntityCrate;
import net.mcft.copy.betterstorage.misc.ItemIdentifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

/** An inventory interface built for players accessing crate piles. */
public class InventoryCratePlayerView extends InventoryBetterStorage implements ICrateWatcher {
	
	private static final int inventoryMaxSize = 9 * 6;
	
	private static class MapData {
		public int itemCount = 0;
	}
	private static class Entry {
		public ItemIdentifier item;
		public int numStacks;
		public Entry(ItemIdentifier item, int numStacks) {
			this.item = item;
			this.numStacks = numStacks;
		}
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
		LinkedList<Entry> stacks = new LinkedList<Entry>();
		for (ItemStack contentsStack : data) {
			ItemIdentifier item = new ItemIdentifier(contentsStack);
			int numStacks = ItemIdentifier.calcNumStacks(contentsStack);
			stacks.add(new Entry(item, numStacks));
		}
		for (int slot = 0; (totalStacks > 0) && (slot < size); slot++) {
			int randomStack = BetterStorage.random.nextInt(totalStacks--);
			for (ListIterator<Entry> iter = stacks.listIterator(); iter.hasNext(); ) {
				Entry entry = iter.next();
				if (randomStack < entry.numStacks) {
					ItemStack contentsStack = data.getItemStack(entry.item);
					MapData data = getMapData(entry.item);
					int count = Math.min(contentsStack.stackSize - data.itemCount, contentsStack.getMaxStackSize());
					ItemStack stack = entry.item.createStack(count);
					data.itemCount += stack.stackSize;
					tempContents[slot] = stack;
					if (--entry.numStacks <= 0) iter.remove();
					break;
				}
				randomStack -= entry.numStacks;
			}
		}
		
		openChest();
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
		if (slot < 0 || slot >= getSizeInventory()) return null;
		return tempContents[slot];
	}
	
	@Override
	public void setInventorySlotContents(int slot, ItemStack stack) {
		if (slot < 0 || slot >= getSizeInventory()) return;
		ItemStack oldStack = getStackInSlot(slot);
		ignoreModifiedItems = true;
		if (oldStack != null) {
			getMapData(oldStack).itemCount -= oldStack.stackSize;
			data.removeItems(oldStack, oldStack.stackSize);
		}
		if (stack != null) {
			stack.stackSize = Math.min(stack.stackSize, Math.min(data.spaceForItem(stack),
			                                                     stack.getMaxStackSize()));
			if (stack.stackSize == 0) return;
			getMapData(stack).itemCount += stack.stackSize;
			data.addItems(stack);
		}
		ignoreModifiedItems = false;
		tempContents[slot] = ItemStack.copyItemStack(stack);
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
		
		return result;
	}
	
	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		int x = crate.xCoord;
		int y = crate.yCoord;
		int z = crate.zCoord;
		return (player.worldObj.getBlockTileEntity(x, y, z) == crate &&
				player.getDistanceSq(x + 0.5, y + 0.5, z + 0.5) < 64.0 &&
				getSizeInventory() <= data.getCapacity());
	}
	
	@Override
	public void onInventoryChanged() { }
	
	@Override
	public void openChest() { data.addWatcher(this); }
	@Override
	public void closeChest() { data.removeWatcher(this); }
	
	// ICrateWatcher implementation
	
	@Override
	public void onCrateItemsModified(ItemIdentifier item, int amount) {
		if (ignoreModifiedItems) return;
		
		MapData data = getMapData(item);
		int totalStacks = 0;
		List<Integer> emptySlots = null;
		if (amount > 0) {
			totalStacks = Math.min(item.calcNumStacks(amount), tempContents.length);
			emptySlots = new ArrayList<Integer>(totalStacks);
		}
		
		for (int slot = 0; slot < tempContents.length; slot++) {
			ItemStack stack = tempContents[slot];
			if (stack == null) {
				if (emptySlots != null && emptySlots.size() < totalStacks)
					emptySlots.add(slot);
				continue;
			}
			if (!item.matches(stack)) continue;
			int delta = modifyItemsInSlot(slot, stack, amount); 
			data.itemCount += delta;
			if ((amount -= delta) == 0) return;
		}
		
		if (amount <= 0) return;
		
		for (int slot : emptySlots) {
			int count = Math.min(amount, item.getItem().getItemStackLimit());
			ItemStack stack = item.createStack(count);
			tempContents[slot] = stack;
			data.itemCount += count;
			if ((amount -= count) <= 0) return;
		}
	}
	
	private int modifyItemsInSlot(int slot, ItemStack stack, int amount) {
		int count = Math.max(-stack.stackSize, Math.min(amount, stack.getMaxStackSize() - stack.stackSize));
		if ((stack.stackSize += count) <= 0)
			tempContents[slot] = null;
		return count;
	}
	
}
