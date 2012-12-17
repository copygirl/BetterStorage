package net.mcft.copy.betterstorage;

import java.util.Comparator;

import net.minecraft.src.ItemStack;
import net.minecraft.src.NBTTagCompound;

public class ItemComparator implements Comparator<ItemStack> {
	
	public static final ItemComparator instance = new ItemComparator();
	
	@Override
	public int compare(ItemStack first, ItemStack second) {
		// Check for null.
		if (first == null)
			return ((second == null) ? 0 : 1);
		else if (second == null) return -1;
		// Compare IDs.
		if (first.itemID != second.itemID) return (first.itemID - second.itemID);
		// Compare damage.
		int damageDif = first.getItemDamage() - second.getItemDamage();
		if (damageDif != 0) return damageDif;
		// Check if compound tags exist.
		NBTTagCompound firstCompound = first.getTagCompound();
		NBTTagCompound secondCompound = second.getTagCompound();
		if (firstCompound == null)
			return ((secondCompound == null) ? 0 : -1);
		else if (secondCompound == null) return 1;
		// Compare item name.
		return first.getItemName().compareTo(second.getItemName());
	}
	
}
