package net.mcft.copy.betterstorage.misc;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

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
	
	public ItemStack createStack(int size) {
		ItemStack stack = new ItemStack(id, size, damage);
		if (data != null)
			stack.stackTagCompound = (NBTTagCompound)data.copy();
		return stack;
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
	
	/** Returns if this item identifier matches the id, damage and data. */
	public boolean matches(int id, int damage, NBTTagCompound data) {
		return (this.id == id && this.damage == damage &&
		        ((this.data == null && data == null) ||
		         (this.data != null && this.data.equals(data))));
	}
	/** Returns if this item identifier matches the ItemStack. */
	public boolean matches(ItemStack stack) {
		return matches(stack.itemID, stack.getItemDamage(), stack.stackTagCompound);
	}
	
	@Override
	public int hashCode() {
		return (id << 16 ^ damage);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof ItemIdentifier)) return false;
		ItemIdentifier other = (ItemIdentifier)obj;
		return matches(other.id, other.damage, other.data);
	}
	
	@Override
	public String toString() {
		return getItem().getStatName() + ":" + damage;
	}
	
}
