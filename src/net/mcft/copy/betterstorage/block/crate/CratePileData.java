package net.mcft.copy.betterstorage.block.crate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.mcft.copy.betterstorage.api.ICrateWatcher;
import net.mcft.copy.betterstorage.inventory.InventoryCrateBlockView;
import net.mcft.copy.betterstorage.misc.ItemIdentifier;
import net.mcft.copy.betterstorage.utils.RandomUtils;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

/** Holds data for a single crate pile, a multi-block
 *  structure made from individual crate blocks */
public class CratePileData implements Iterable<ItemStack> {
	
	private static final int maxCratePileSize = 8192;
	
	public final CratePileCollection collection;
	public final int id;
	
	private Map<ItemIdentifier, ItemStack> contents = new HashMap<ItemIdentifier, ItemStack>();
	private ItemStack[] contentsArray = new ItemStack[0];
	private int numCrates = 0;
	private int numSlots = 0;
	private boolean destroyed = false;
	private CratePileMap map;
	
	private Set<ICrateWatcher> watchers = new HashSet<ICrateWatcher>();
	
	/** An inventory interface built for machines accessing the crate pile. */
	public final IInventory blockView = new InventoryCrateBlockView(this);
	
	/** Returns the number of crates attached. */
	public int getNumCrates() { return numCrates; }
	
	/** Returns the items in this crate pile as a list. */
	public List<ItemStack> getContents() { return Arrays.asList(contentsArray); }
	/** Returns the maximum number of slots. */
	public int getCapacity() { return numCrates * TileEntityCrate.slotsPerCrate; }
	/** Returns the number of unique items. */
	public int getNumItems() { return contents.size(); }
	/** Returns the number of slots occupied. */
	public int getOccupiedSlots() { return numSlots; }
	/** Returns the number of slots free. Negative if there's any overflow. */
	public int getFreeSlots() { return getCapacity() - getOccupiedSlots(); }
	
	public CratePileData(CratePileCollection collection, int id, int numCrates) {
		this.collection = collection;
		this.id = id;
		this.numCrates = numCrates;
	}
	
	// CrateMap related functions
	
	/** Returns if the crate can be added to the crate pile. */
	public boolean canAdd(TileEntityCrate crate) {
		return ((map != null) && (numCrates < maxCratePileSize) &&
		        (map.region.contains(crate) || canExpand(crate)));
	}
	/** Returns if the crate can expand the crate pile. */
	private boolean canExpand(TileEntityCrate crate) {
		int volume = map.region.volume();
		// Can't expand if there's not enough crates in the bounding box.
		if (numCrates < Math.min((int)(volume * 0.8), volume - 5)) return false;
		
		if (crate.xCoord < map.region.minX || crate.xCoord > map.region.maxX) {
			int maxDiff = ((map.region.height() == 1) ? 1 : 3);
			if (map.region.width() >= maxDiff + Math.min(map.region.height(), map.region.depth()))
				return false;
		} else if (crate.zCoord < map.region.minZ || crate.zCoord > map.region.maxZ) {
			int maxDiff = ((map.region.width() == 1) ? 1 : 3);
			if (map.region.height() >= maxDiff + Math.min(map.region.width(), map.region.depth()))
				return false;
		} else if (crate.yCoord < map.region.minY || crate.yCoord > map.region.maxY) {
			int maxDiff = ((map.region.width() == 1 || map.region.height() == 1) ? 1 : 4);
			if (map.region.depth() >= maxDiff + Math.min(map.region.width(), map.region.height()))
				return false;
		}
		return true;
	}
	
	public void trimMap() {
		if (map != null)
			map.trim();
	}
	
	// Adding and removing crates
	
	/** Adds a crate to the crate pile, increasing the number
	 *  of crates and adding it to the crate pile map. */
	public void addCrate(TileEntityCrate crate) {
		if (numCrates == 0)
			map = new CratePileMap(crate);
		map.add(crate);
		numCrates++;
		markDirty();
	}
	/** Removes a crate from the crate pile, decreasing the number
	 *  of crates and removing it from the crate pile map. */
	public void removeCrate(TileEntityCrate crate) {
		if (--numCrates <= 0) {
			collection.removeCratePile(this);
			destroyed = true;
		} else {
			if (map != null)
				map.remove(crate);
			markDirty();
		}
	}
	
	// Getting items
	
	// For safety reasons, these functions don't
	// return the original item stacks publicly.
	/** Returns an ItemStack from the contents, null if none was found. */
	private ItemStack getItemStack(ItemIdentifier item, boolean copy) {
		if (item == null) return null;
		ItemStack stack = contents.get(item);
		return (copy ? ItemStack.copyItemStack(stack) : stack);
	}
	/** Returns an ItemStack from the contents, null if none was found. */
	public ItemStack getItemStack(ItemIdentifier item) {
		return getItemStack(item, true);
	}
	
	/** Returns an ItemStack from the contents, null if none was found. */
	private ItemStack getItemStack(ItemStack item, boolean copy) {
		if (item == null) return null;
		return getItemStack(new ItemIdentifier(item), copy);
	}
	/** Returns an ItemStack from the contents, null if none was found. */
	public ItemStack getItemStack(ItemStack item) {
		return getItemStack(item, true);
	}
	
	/** Returns an ItemStack from the contents. */
	public ItemStack getItemStack(int index) {
		if (index < 0 || index >= getNumItems()) return null;
		return contentsArray[index];
	}
	
	// Getting item count
	
	/** Returns the number of items of this type in the contents. */
	public int getItemCount(ItemIdentifier item) {
		ItemStack stack = getItemStack(item, false);
		return ((stack != null) ? stack.stackSize : 0);
	}
	/** Returns the number of items of this type in the contents. */
	public int getItemCount(ItemStack item) {
		return getItemCount(new ItemIdentifier(item));
	}
	
	// Adding items
	
	/** Tries to add a stack to the contents. <br>
	 *  Returns what could not be added, null if there was no overflow. */
	public ItemStack addItems(ItemStack stack) {
		if (stack == null) return null;
		ItemStack overflow = null;
		
		int space = spaceForItem(stack);
		if (space > 0) {
			
			if (space < stack.stackSize)
				overflow = stack.splitStack(stack.stackSize - space);
			ItemIdentifier item = new ItemIdentifier(stack);
			ItemStack contentsStack = getItemStack(item, false);
			
			int stacksBefore;
			// If there's no such item in contents yet, add it.
			if (contentsStack == null) {
				stacksBefore = 0;
				contentsStack = item.createStack(stack.stackSize);
				contents.put(item, contentsStack);
				updateContentsArray();
			// Otherwise just increase the stack size.
			} else {
				stacksBefore = calcNumStacks(contentsStack);
				contentsStack.stackSize += stack.stackSize;
			}
			
			int stacksAfter = calcNumStacks(contentsStack);
			numSlots += (stacksAfter - stacksBefore);
			
			for (ICrateWatcher watcher : watchers)
				watcher.onCrateItemsModified(stack);
			
		} else overflow = stack;
		
		markDirty();
		return overflow;
	}
	
	/** Tries to add some stacks to the contents. <br>
	 *  Returns what could not be added. */
	public List<ItemStack> addItems(Collection<ItemStack> stacks) {
		List<ItemStack> overflow = new ArrayList<ItemStack>();
		for (ItemStack stack : stacks) {
			ItemStack overflowStack = addItems(stack);
			if (overflowStack != null)
				overflow.add(overflowStack);
		}
		return overflow;
	}
	
	// Removing items
	
	/** Removes and returns a specific amount of items. <br>
	 *  Returns less than the requested amount when there's
	 *  not enough, or null if there's none at all. */
	public ItemStack removeItems(ItemIdentifier item, int amount) {
		ItemStack stack = getItemStack(item, false);
		if (stack == null || amount <= 0) return null;
		
		int stacksBefore = calcNumStacks(stack);
		int stacksAfter;
		
		if (amount < stack.stackSize) {
			stack.stackSize -= amount;
			stacksAfter = calcNumStacks(stack);
			stack = stack.copy();
			stack.stackSize = amount;
		} else {
			contents.remove(item);
			updateContentsArray();
			stacksAfter = 0;
		}
		
		numSlots -= (stacksBefore - stacksAfter);
		
		ItemStack removedStack = stack.copy();
		removedStack.stackSize = -stack.stackSize;
		for (ICrateWatcher watcher : watchers)
			watcher.onCrateItemsModified(removedStack);
		
		markDirty();
		return stack;
	}
	
	/** Removes and returns a specific amount of items. <br>
	 *  Returns less than the requested amount when there's
	 *  not enough, or null if there's none at all. */
	public ItemStack removeItems(ItemStack item, int amount) {
		if (item == null) return null;
		return removeItems(new ItemIdentifier(item), amount);
	}
	
	/** Removes and returns a specific amount of items. <br>
	 *  Returns less than the requested amount when there's
	 *  not enough, or null if there's none at all. */
	public ItemStack removeItems(ItemStack stack) {
		if (stack == null) return null;
		return removeItems(stack, stack.stackSize);
	}
	
	// Checking space
	
	/** Returns how much space there is left for a specific item. */
	public int spaceForItem(ItemIdentifier item) {
		if (item == null) return 0;
		int stackLimit = item.getItem().getItemStackLimit();
		int space = getFreeSlots() * stackLimit;
		ItemStack stack = getItemStack(item);
		if (stack != null)
			space += (calcNumStacks(stack) * stackLimit) - stack.stackSize;
		return space;
	}
	
	/** Returns how much space there is left for a specific item. */
	public int spaceForItem(ItemStack item) {
		if (item == null) return 0;
		return spaceForItem(new ItemIdentifier(item));
	}
	
	// Picking random items
	
	/** Picks random items from the pile. <br>
	 *  Each stack has a chance to get picked. */
	public List<ItemStack> pickItemStacks(int amount) {
		amount = Math.min(amount, getOccupiedSlots());
		if (amount <= 0) return new ArrayList<ItemStack>();
		int totalStacks = 0;
		List<ItemStack> stacks = new ArrayList<ItemStack>();
		for (ItemStack contentsStack : this) {
			int numStacks = ItemIdentifier.calcNumStacks(contentsStack);
			for (int i = 0; i < numStacks; i++) {
				ItemStack stack = contentsStack.copy();
				int maxStackSize = stack.getMaxStackSize();
				int max = Math.min(contentsStack.stackSize - maxStackSize * i, maxStackSize);
				stack.stackSize = Math.min(stack.stackSize, max);
				stacks.add(stack);
			}
			totalStacks += numStacks;
		}
		List<ItemStack> resultStacks = new ArrayList<ItemStack>();
		for (int i = 0; i < amount; i++)
			resultStacks.add(stacks.remove(RandomUtils.getInt(stacks.size())));
		return resultStacks;
	}
	/** Picks and removes random items from the pile. <br>
	 *  Each stack has a chance to get picked. */
	public List<ItemStack> pickAndRemoveItemStacks(int amount) {
		List<ItemStack> stacks = pickItemStacks(amount);
		for (ItemStack stack : stacks)
			removeItems(stack);
		return stacks;
	}
	
	// Crate watcher related functions
	
	/** Adds a crate watcher to the watchers list, so it
	 *  gets informed about any added or removed items. */
	public void addWatcher(ICrateWatcher watcher) {
		watchers.add(watcher);
	}
	/** Removes a crate watcher to the watchers list. */
	public void removeWatcher(ICrateWatcher watcher) {
		watchers.remove(watcher);
	}
	
	// Misc functions
	
	private void updateContentsArray() {
		contentsArray = contents.values().toArray(new ItemStack[getNumItems()]);
	}
	
	private static int calcNumStacks(ItemStack stack) {
		return ItemIdentifier.calcNumStacks(stack.getItem(), stack.stackSize);
	}
	
	/** Marks the pile data as dirty so it gets saved. */
	private void markDirty() {
		if (destroyed) return;
		collection.markDirty(this);
	}
	
	// NBT related functions
	
	public NBTTagCompound toCompound() {
		NBTTagCompound compound = new NBTTagCompound("");
		compound.setInteger("id", id);
		compound.setShort("numCrates", (short)getNumCrates());
		NBTTagList stacks = new NBTTagList("stacks");
		for (ItemStack stack : this) {
			NBTTagCompound stackCompound = new NBTTagCompound("");
			stackCompound.setShort("id", (short)stack.itemID);
			stackCompound.setInteger("Count", stack.stackSize);
			stackCompound.setShort("Damage", (short)stack.getItemDamage());
			if (stack.hasTagCompound())
				stackCompound.setCompoundTag("tag", stack.getTagCompound());
			stacks.appendTag(stackCompound);
		}
		compound.setTag("stacks", stacks);
		if (map != null)
			compound.setCompoundTag("map", map.toCompound());
		return compound;
	}
	
	public static CratePileData fromCompound(CratePileCollection collection, NBTTagCompound compound) {
		int cratePileId = compound.getInteger("id");
		int numCrates = compound.getShort("numCrates");
		CratePileData pileData = new CratePileData(collection, cratePileId, numCrates);
		NBTTagList stacks = compound.getTagList("stacks");
		for (int j = 0; j < stacks.tagCount(); j++) {
			NBTTagCompound stackCompound = (NBTTagCompound)stacks.tagAt(j);
			int id = stackCompound.getShort("id");
			int count = stackCompound.getInteger("Count");
			int damage = stackCompound.getShort("Damage");
			ItemStack stack = new ItemStack(id, count, damage);
			if (stackCompound.hasKey("tag"))
				stack.stackTagCompound = stackCompound.getCompoundTag("tag");
			if (stack.getItem() != null)
				pileData.addItems(stack);
		}
		if (compound.hasKey("map"))
			pileData.map = CratePileMap.fromCompound(compound.getCompoundTag("map"));
		return pileData;
	}
	
	// IIterable implementation
	
	@Override
	public Iterator<ItemStack> iterator() {
		return contents.values().iterator();
	}
	
}
