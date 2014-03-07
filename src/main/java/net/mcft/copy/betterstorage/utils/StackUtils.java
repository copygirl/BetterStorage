package net.mcft.copy.betterstorage.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.mcft.copy.betterstorage.api.IContainerItem;
import net.mcft.copy.betterstorage.api.lock.IKey;
import net.mcft.copy.betterstorage.api.lock.ILock;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public final class StackUtils {
	
	private StackUtils() {  }
	
	// NBT utility functions
	
	/** Gets the actual NBT tag from the ItemStack's custom NBT data. */
	public static NBTBase getTag(ItemStack stack, String... tags) {
		if (!stack.hasTagCompound()) return null;
		String tag = null;
		NBTTagCompound compound = stack.getTagCompound();
		for (int i = 0; i < tags.length; i++) {
			tag = tags[i];
			if (!compound.hasKey(tag)) return null;
			if (i == tags.length - 1) break;
			compound = compound.getCompoundTag(tag);
		}
		return compound.getTag(tag);
	}
	/** Gets the type of a tag from the ItemStack's custom NBT data. <br>
	 *  See {@link NBTBase#NBTTypes} for possible return values. <br>
	 *  Returns null if the tag doesn't exist. */
	public static String getType(ItemStack stack, String... tags) {
		NBTBase tag = getTag(stack, tags);
		return ((tag != null) ? NBTBase.NBTTypes[tag.getId()] : null);
	}
	/** Gets a value from the ItemStack's custom NBT data. Example: <br>
	 *  <code> int color = ItemUtils.get(stack, -1, "display", "color"); </code> <br>
	 *  Returns defaultValue if any parent compounds or the value tag don't exist. */
	public static <T> T get(ItemStack stack, T defaultValue, String... tags) {
		NBTBase tag = getTag(stack, tags);
		return (T)((tag != null) ? NbtUtils.getTagValue(tag) : defaultValue);
	}
	
	/** Sets a tag in the ItemStack's custom NBT data. <br>
	 *  Creates parent compounds if they don't exist. */
	public static void set(ItemStack stack, NBTBase nbtTag, String... tags) {
		String tag = null;
		NBTTagCompound compound;
		if (!stack.hasTagCompound()) {
			compound = new NBTTagCompound();
			stack.setTagCompound(compound);
		} else compound = stack.getTagCompound();
		for (int i = 0; i < tags.length; i++) {
			tag = tags[i];
			if (i == tags.length - 1) break;
			if (!compound.hasKey(tag)) {
				NBTTagCompound newCompound = new NBTTagCompound(tag);
				compound.setCompoundTag(tag, newCompound);
				compound = newCompound;
			} else compound = compound.getCompoundTag(tag);
		}
		compound.setTag(tag, nbtTag);
	}
	/** Sets a value in the ItemStack's custom NBT data. Example: <br>
	 *  <code> ItemUtils.set(stack, 0xFF0000, "display", "color"); </code> <br>
	 *  Creates parent compounds if they don't exist. */
	public static <T> void set(ItemStack stack, T value, String... tags) {
		set(stack, NbtUtils.createTag(null, value), tags);
	}
	
	/** Returns if the tag exists in the ItemStack's custom NBT data. Example: <br>
	 *  <code> if (ItemUtils.has(stack, "display", "color")) ... </code> */
	public static boolean has(ItemStack stack, String... tags) {
		if (!stack.hasTagCompound()) return false;
		String tag = null;
		NBTTagCompound compound = stack.getTagCompound();
		for (int i = 0; i < tags.length; i++) {
			tag = tags[i];
			if (!compound.hasKey(tag)) return false;
			if (i == tags.length - 1) break;
			compound = compound.getCompoundTag(tag);
		}
		return compound.hasKey(tag);
	}
	
	/** Removes a value from the ItemStack's custom NBT data. <br>
	 *  Gets rid of any empty parent compounds. Example: <br>
	 *  <code> ItemUtils.remove(stack, "display", "color"); </code> */
	public static void remove(ItemStack stack, String... tags) {
		if (!stack.hasTagCompound()) return;
		String tag = null;
		NBTTagCompound compound = stack.getTagCompound();
		for (int i = 0; i < tags.length; i++) {
			tag = tags[i];
			if (!compound.hasKey(tag)) return;
			if (i == tags.length - 1) break;
			compound = compound.getCompoundTag(tag);
		}
		compound.removeTag(tag);
	}
	
	// Stack copying
	
	/** Creates a copy of an item stack with a specific stack size. <br>
	 *  Returns null if the stack is null or stackSize is <= 0. */
	public static ItemStack copyStack(ItemStack stack, int stackSize) {
		return copyStack(stack, stackSize, true);
	}
	/** Creates a copy of an item stack with a specific stack size.
	 *  Returns null if the stack is null or stackSize is <= 0 and checkSize is true. */
	public static ItemStack copyStack(ItemStack stack, int stackSize, boolean checkSize) {
		if ((stack == null) || (checkSize && (stackSize <= 0))) return null;
		ItemStack copy = new ItemStack(stack.itemID, stackSize, stack.getItemDamage());
		if (stack.stackTagCompound != null)
			copy.stackTagCompound = (NBTTagCompound)stack.stackTagCompound.copy();
		return copy;
	}
	
	// Stack matching
	
	public static boolean matches(int id1, int damage1, NBTTagCompound data1, 
	                              int id2, int damage2, NBTTagCompound data2) {
		return ((id1 == id2) && (damage1 == damage2) && nbtEquals(data1, data2));
	}
	/** Returns if the two NBT compounds are equal.
	 *  Used instead of default comparison because this function
	 *  makes sure the names of the main NBT tags match before comparing. */
	public static boolean nbtEquals(NBTTagCompound nbt1, NBTTagCompound nbt2) {
		return ((nbt1 == nbt2) || (!((nbt1 == null) || (nbt2 == null)) &&
		                           nbt1.setName("tag").equals(nbt2.setName("tag"))));
	}
	/** Returns if the two item stacks match. <br>
	 *  True when they're both null or their ID, damage and optionally NBT data match. */
	public static boolean matches(ItemStack stack1, ItemStack stack2, boolean matchNBT) {
		return ((stack1 == null) ? (stack2 == null) : ((stack2 != null) && matches(
				stack1.itemID, stack1.getItemDamage(), (matchNBT ? stack1.getTagCompound() : null),
				stack2.itemID, stack2.getItemDamage(), (matchNBT ? stack2.getTagCompound() : null))));
	}
	/** Returns if the two item stacks match. <br>
	 *  True when they're both null or their ID, damage and NBT data match. */
	public static boolean matches(ItemStack stack1, ItemStack stack2) {
		return matches(stack1, stack2, true);
	}
	
	// Enchantment functions
	
	/** Returns the enchantments on the item stack. */
	public static Map<Integer, StackEnchantment> getEnchantments(ItemStack stack) {
		Map<Integer, StackEnchantment> enchantments = new HashMap<Integer, StackEnchantment>();
		NBTTagList list = ((stack.getItem() == Item.enchantedBook)
				? Item.enchantedBook.func_92110_g(stack)
				: stack.getEnchantmentTagList());
		if (list != null)
			for (int i = 0; i < list.tagCount(); i++) {
				StackEnchantment ench = new StackEnchantment(stack, (NBTTagCompound)list.tagAt(i));
				enchantments.put(ench.ench.effectId, ench);
			}
		return enchantments;
	}
	
	/** Returns if the enchantment can go on this item stack. */
	public static boolean isEnchantmentCompatible(ItemStack stack,
	                                              Collection<StackEnchantment> stackEnchants,
	                                              StackEnchantment newEnchant) {
		if (!newEnchant.ench.canApply(stack)) return false;
		for (StackEnchantment stackEnch : stackEnchants)
			if ((newEnchant.ench == stackEnch.ench)
					? (newEnchant.getLevel() <= stackEnch.getLevel())
					: (!newEnchant.ench.canApplyTogether(stackEnch.ench) ||
					   !stackEnch.ench.canApplyTogether(newEnchant.ench))) return false;
		return true;
	}
	/** Returns if the enchantment can go on this item stack. */
	public static boolean isEnchantmentCompatible(ItemStack stack, StackEnchantment newEnchant) {
		return isEnchantmentCompatible(stack, getEnchantments(stack).values(), newEnchant);
	}
	
	/** Represents an enchantment entry on an item stack. */
	public static class StackEnchantment {
		public final ItemStack stack;
		public final Enchantment ench;
		private final NBTTagCompound entry;
		
		public int getLevel() { return entry.getShort("lvl"); }
		public void setLevel(int level) { entry.setShort("lvl", (short)level); }
		
		public StackEnchantment(ItemStack stack, NBTTagCompound entry) {
			this.stack = stack;
			this.entry = entry;
			this.ench = Enchantment.enchantmentsList[entry.getShort("id")];
		}
	}
	
	// Other functions, mostly BetterStorage related
	
	/** Gets the number of stacks the item would
	 *  split into under normal circumstances. */
	public static int calcNumStacks(ItemStack stack, int count) {
		int maxStackSize = stack.getMaxStackSize();
		return (count + maxStackSize - 1) / maxStackSize;
	}
	/** Gets the number of stacks the item would
	 *  split into under normal circumstances. */
	public static int calcNumStacks(ItemStack stack) {
		return calcNumStacks(stack, stack.stackSize);
	}
	
	/** Stacks items from the ItemStack array into the list. <br>
	 *  Returns the number of stacks processed. */
	public static int stackItems(ItemStack[] contents, List<ItemStack> items) {
		int numStacks = 0;
		outerLoop:
		for (int i = 0; i < contents.length; i++) {
			ItemStack contentStack = contents[i];
			if (contentStack == null) continue;
			numStacks++;
			for (ItemStack itemsStack : items)
				if (StackUtils.matches(contentStack, itemsStack)) {
					itemsStack.stackSize += contentStack.stackSize;
					continue outerLoop;
				}
			items.add(contentStack);
		}
		return numStacks;
	}
	/** Returns items from the ItemStack array stacked into a list. */
	public static List<ItemStack> stackItems(ItemStack[] contents) {
		List<ItemStack> items = new ArrayList<ItemStack>();
		stackItems(contents, items);
		return items;
	}
	
	public static ItemStack[] getStackContents(ItemStack stack, int size) {
		ItemStack[] contents = new ItemStack[size];
		NBTTagCompound compound = stack.getTagCompound();
		if (compound != null && compound.hasKey("Items"))
			NbtUtils.readItems(contents, compound.getTagList("Items"));
		return contents;
	}
	
	public static void setStackContents(ItemStack stack, ItemStack[] contents) {
		set(stack, NbtUtils.writeItems(contents), "Items");
	}
	
	public static boolean isEmpty(ItemStack[] items) {
		for (int i = 0; i < items.length; i++)
			if (items[i] != null) return false;
		return true;
	}
	
	public static boolean isKey(ItemStack stack) {
		return (stack != null && stack.getItem() instanceof IKey);
	}
	public static boolean isLock(ItemStack stack) {
		return (stack != null && stack.getItem() instanceof ILock);
	}
	
	public static boolean canBeStoredInContainerItem(ItemStack item) {
		return (((item != null) && (item.getItem() instanceof IContainerItem))
				? ((IContainerItem)item.getItem()).canBeStoredInContainerItem(item)
				: true);
	}
	
}
