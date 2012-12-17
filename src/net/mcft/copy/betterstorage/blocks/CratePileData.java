package net.mcft.copy.betterstorage.blocks;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.mcft.copy.betterstorage.BetterStorage;
import net.mcft.copy.betterstorage.ItemIdentifier;
import net.minecraft.src.IInventory;
import net.minecraft.src.ItemStack;
import net.minecraft.src.NBTTagCompound;
import net.minecraft.src.NBTTagList;

/** Holds data for a single crate pile, a multi-block
 *  structure made from individual crate blocks */
public class CratePileData implements Iterable<ItemStack> {
	
	public final CratePileCollection collection;
	public final int id;
	
	private Map<ItemIdentifier, ItemStack> contents = new HashMap<ItemIdentifier, ItemStack>();
	private ItemStack[] contentsArray;
	private int numCrates = 0;
	private int numSlots = 0;
	private boolean destroyed = false;
	
	/** An inventory interface built for machines accessing the crate pile. */
	public final IInventory blockView = new InventoryCrateBlockView(this);
	
	/** Returns the number of crates attached. */
	public int getNumCrates() { return numCrates; }
	
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
	
	public void addCrate(TileEntityCrate crate) {
		numCrates++;
		markDirty();
	}
	
	public void removeCrate(TileEntityCrate crate) {
		if (--numCrates <= 0) {
			collection.removeCratePile(this);
			destroyed = true;
		} else markDirty();
	}
	
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
	
	/** Returns the number of items of this type in the contents. */
	public int getItemCount(ItemIdentifier item) {
		ItemStack stack = getItemStack(item);
		return ((stack != null) ? stack.stackSize : 0);
	}
	/** Returns the number of items of this type in the contents. */
	public int getItemCount(ItemStack item) {
		return getItemCount(new ItemIdentifier(item));
	}
	
	/** Tries to add a stack to the contents. <br>
	 *  Returns what could not be added, null if there was no overflow. */
	public ItemStack addItems(ItemStack stack) {
		if (stack == null) return null;
		ItemStack overflow = null;
		int space = spaceForItem(stack);
		if (space > 0) {
			if (space < stack.stackSize)
				overflow = stack.splitStack(stack.stackSize - space);
			ItemStack contentsStack = getItemStack(stack, false);
			int stacksBefore;
			// If there's no such item in contents yet, add it.
			if (contentsStack == null) {
				stacksBefore = 0;
				contentsStack = stack.copy();
				contents.put(new ItemIdentifier(contentsStack), contentsStack);
				updateContentsArray();
			// Otherwise just increase the stack size.
			} else {
				stacksBefore = calcNumStacks(contentsStack);
				contentsStack.stackSize += stack.stackSize;
			}
			int stacksAfter = calcNumStacks(contentsStack);
			numSlots += (stacksAfter - stacksBefore);
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
	
	/** Removes and returns a specific amount of items. <br>
	 *  Returns less than the requested amount when there's
	 *  not enough, or null if there's none at all. */
	public ItemStack removeItems(ItemIdentifier item, int amount) {
		ItemStack stack = getItemStack(item, false);
		if (stack == null) return null;
		if (amount <= 0) return null;
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
	
	private void updateContentsArray() {
		contentsArray = contents.values().toArray(new ItemStack[getNumItems()]);
	}
	
	/** Gets the number of stacks this ItemStack
	 *  would split into under normal circumstances. */
	public static int calcNumStacks(ItemStack stack) {
		return ItemIdentifier.calcNumStacks(stack.getItem(), stack.stackSize);
	}
	
	/** Marks the pile data as dirty so it gets saved. */
	public void markDirty() {
		if (destroyed) return;
		collection.markDirty(this);
	}
	
	@Override
	public Iterator<ItemStack> iterator() {
		return contents.values().iterator();
	}
	
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
				ItemStack stack = contentsStack;
				int maxStackSize = stack.getMaxStackSize();
				int max = Math.min(contentsStack.stackSize - maxStackSize * i, maxStackSize);
				stack.stackSize = Math.min(stack.stackSize, max);
				stacks.add(stack);
			}
			totalStacks += numStacks;
		}
		List<ItemStack> resultStacks = new ArrayList<ItemStack>();
		for (int i = 0; i < amount; i++)
			resultStacks.add(stacks.remove(BetterStorage.random.nextInt(stacks.size())));
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
	
}
