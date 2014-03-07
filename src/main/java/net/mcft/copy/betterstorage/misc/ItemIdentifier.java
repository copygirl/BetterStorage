package net.mcft.copy.betterstorage.misc;

import net.mcft.copy.betterstorage.utils.StackUtils;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class ItemIdentifier {
	
	private final int id;
	private final int damage;
	private final NBTTagCompound data;
	
	public ItemIdentifier(int id, int damage, NBTTagCompound data) {
		this.id = id;
		this.damage = damage;
		this.data = data;
	}
	public ItemIdentifier(ItemStack stack) {
		this(stack.itemID, stack.getItemDamage(), (stack.hasTagCompound()
				? (NBTTagCompound)stack.getTagCompound().copy() : null));
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
	
	/** Returns if this item identifier matches the id, damage and data. */
	public boolean matches(int id, int damage, NBTTagCompound data) {
		return StackUtils.matches(this.id, this.damage, this.data, id, damage, data);
	}
	/** Returns if this item identifier matches the ItemStack. */
	public boolean matches(ItemStack stack) {
		return matches(stack.itemID, stack.getItemDamage(), stack.stackTagCompound);
	}
	
	@Override
	public int hashCode() { return (id << 16 ^ damage); }
	
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
