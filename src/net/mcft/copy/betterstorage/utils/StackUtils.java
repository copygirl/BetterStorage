package net.mcft.copy.betterstorage.utils;

import java.util.ArrayList;
import java.util.List;

import net.mcft.copy.betterstorage.api.IContainerItem;
import net.mcft.copy.betterstorage.api.IKey;
import net.mcft.copy.betterstorage.api.ILock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;

public class StackUtils {
	
	/** Gets a value from the ItemStack's custom NBT data. Example: <br>
	 *  <code> int color = ItemUtils.get(stack, -1, "display", "color"); </code> <br>
	 *  Returns defaultValue if any parent compounds or the value tag don't exist. */
	public static <T> T get(ItemStack stack, T defaultValue, String... tags) {
		if (!stack.hasTagCompound()) return defaultValue;
		String tag = null;
		NBTTagCompound compound = stack.getTagCompound();
		for (int i = 0; i < tags.length; i++) {
			tag = tags[i];
			if (!compound.hasKey(tag)) return defaultValue;
			if (i == tags.length - 1) break;
			compound = compound.getCompoundTag(tag);
		}
		return NbtUtils.getTagValue(compound.getTag(tag));
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
	
	public static boolean matches(int id1, int damage1, NBTTagCompound data1, 
	                              int id2, int damage2, NBTTagCompound data2) {
		return ((id1 == id2) && (damage1 == damage2) &&
		        ((data1 == null) && (data2 == null)) ||
		         ((data1 != null) && data1.equals(data2)));
	}
	/** Returns if the two item stacks match. <br>
	 *  True when they're both null or their ID, damage and NBT data match. */
	public static boolean matches(ItemStack stack1, ItemStack stack2) {
		return ((stack1 == null) ? (stack2 == null) : ((stack2 != null) &&
				matches(stack1.itemID, stack1.getItemDamage(), stack1.getTagCompound(),
				        stack2.itemID, stack2.getItemDamage(), stack2.getTagCompound())));
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
		NBTTagCompound compound = stack.getTagCompound();
		if (compound == null) {
			compound = new NBTTagCompound();
			stack.setTagCompound(compound);
		}
		compound.setTag("Items", NbtUtils.writeItems(contents));
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
