package net.mcft.copy.betterstorage;

import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraft.src.NBTTagCompound;

public class ItemIdentifier {
	
	private int id;
	private int damage;
	private NBTTagCompound data;
	
	public ItemIdentifier(ItemStack stack) {
		id = stack.itemID;
		damage = stack.getItemDamage();
		if (stack.hasTagCompound())
			data = (NBTTagCompound)stack.getTagCompound().copy();
	}
	
	public Item getItem() {
		return Item.itemsList[id];
	}
	
	/** Gets the number of stacks the item would
	 *  split into under normal circumstances. */
	public static int calcNumStacks(Item item, int count) {
		int maxStackSize = item.getItemStackLimit();
		return (count + maxStackSize - 1) / maxStackSize;
	}
	/** Gets the number of stacks the item would
	 *  split into under normal circumstances. */
	public static int calcNumStacks(ItemStack stack) {
		return calcNumStacks(stack.getItem(), stack.stackSize);
	}
	/** Gets the number of stacks the item would
	 *  split into under normal circumstances. */
	public int calcNumStacks(int count) {
		return calcNumStacks(getItem(), count);
	}
	
	@Override
	public int hashCode() {
		return (id << 16 ^ damage);
	}
	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof ItemIdentifier)) return false;
		ItemIdentifier other = (ItemIdentifier)obj;
		return (id == other.id && damage == other.damage &&
		        ((data == null && other.data == null) ||
		         (data != null && data.equals(other.data))));
	}
	@Override
	public String toString() {
		return getItem().getStatName() + ":" + damage;
	}
}
