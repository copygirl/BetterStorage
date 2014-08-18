package net.mcft.copy.betterstorage.tile.crate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.TreeSet;

import net.mcft.copy.betterstorage.misc.ItemIdentifier;
import net.mcft.copy.betterstorage.utils.RandomUtils;
import net.mcft.copy.betterstorage.utils.StackUtils;
import net.minecraft.item.ItemStack;

public class CrateItems {
	
	private final Map<ItemIdentifier, ItemStack> itemsMap = new HashMap<ItemIdentifier, ItemStack>();
	
	private int totalStacks = 0;
	private NavigableMap<Integer, ItemStack> weightedMap = new TreeMap<Integer, ItemStack>();
	private boolean weightedMapDirty = false;
	
	// TODO: Find a better way than recreating the weighted map every time something changes.
	//       For now though, this is much better than how things were done before.
	
	/** Gets the number of unique items. */
	public int getUniqueItems() { return itemsMap.size(); }
	/** Gets the number of total stacks all items would occupy. */
	public int getTotalStacks() { return totalStacks; }
	
	/** Gets the amount of items of this type, 0 if none. */
	public int get(ItemIdentifier item) {
		ItemStack stack = itemsMap.get(item);
		return ((stack != null) ? stack.stackSize : 0);
	}
	/** Sets the amount of items of this type. <br>
	 *  If amount > 0 and item was previously not present, adds the item. <br>
	 *  If amount <= 0 and item was previously present, removes the item.
	 *  Automatically updates the number of total stacks
	 *  and marks the weighted map as dirty if necessary. */
	public void set(ItemIdentifier item, int amount) {
		ItemStack stack = itemsMap.get(item);
		int stacksBefore, stacksAfter;
		if (stack != null) {
			if (amount == stack.stackSize) return;
			stacksBefore = StackUtils.calcNumStacks(stack);
			if (amount > 0) {
				stack.stackSize = amount;
				stacksAfter = StackUtils.calcNumStacks(stack);
			} else {
				stacksAfter = 0;
				itemsMap.remove(item);
			}
		} else if (amount > 0) {
			stack = item.createStack(amount);
			stacksBefore = 0;
			stacksAfter = StackUtils.calcNumStacks(stack);
			itemsMap.put(item, stack);
		} else return;
		if (stacksBefore != stacksAfter) {
			totalStacks += (stacksAfter - stacksBefore);
			weightedMapDirty = true;
		}
	}
	
	/** Returns an iterable of all of the items.
	 *  Stack sizes might exceed usual stack limits. */
	public Iterable<ItemStack> getItems() { return itemsMap.values(); }
	
	/** Returns an iterable that allows iterating over all stacks in a
	 *  random order. Each stack has the same chance of getting picked. */
	public Iterable<ItemStack> getRandomStacks() {
		return new Iterable<ItemStack>() {
			@Override public Iterator<ItemStack> iterator() {
				return new ItemsIterator(getWeightedMap(), getTotalStacks());
			}
		};
	}
	
	/** Returns a list of random stacks with a maximum size of the specified amount.
	 *  May return less elements, or an empty list, if there's not enough stacks. */
	public List<ItemStack> getRandomStacks(int amount) {
		amount = Math.min(amount, getTotalStacks());
		List<ItemStack> stacks = new ArrayList<ItemStack>(amount);
		for (ItemStack stack : getRandomStacks()) {
			stacks.add(stack);
			if (stacks.size() >= amount) break;
		}
		return stacks;
	}
	
	private NavigableMap<Integer, ItemStack> getWeightedMap() {
		if (weightedMapDirty) {
			weightedMap.clear();
			int stacks = 0;
			for (ItemStack stack : getItems())
				weightedMap.put((stacks += StackUtils.calcNumStacks(stack)), stack);
		}
		return weightedMap;
	}
	
	private static class ItemsIterator implements Iterator<ItemStack> {
		private final NavigableMap<Integer, ItemStack> map;
		private final int stacks;
		private final TreeSet<Integer> picked = new TreeSet<Integer>();
		
		public ItemsIterator(NavigableMap<Integer, ItemStack> map, int stacks) {
			this.map = map;
			this.stacks = stacks;
		}
		
		@Override
		public boolean hasNext() { return (picked.size() < stacks); }
		
		@Override
		public ItemStack next() {
			int index = 1 + RandomUtils.getInt(stacks - picked.size());
			if (!picked.isEmpty())
				for (int p : picked)
					if (p > index) break;
					else index++;
			picked.add(index);
			Map.Entry<Integer, ItemStack> entry = map.ceilingEntry(index);
			ItemStack stack = entry.getValue();
			int maxStackSize = stack.getMaxStackSize();
			int amount = ((index != entry.getKey()) ? maxStackSize
					: ((stack.stackSize - 1) % maxStackSize + 1));
			return StackUtils.copyStack(stack, amount);
		}
		
		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
	}
	
}
