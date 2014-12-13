package net.mcft.copy.betterstorage.misc;

import net.mcft.copy.betterstorage.utils.StackUtils;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class ItemIdentifier {
	
	private final Item item;
	private final int damage;
	private final NBTTagCompound data;
	
	private int hashCode;
	private boolean calculatedHashCode = false;
	
	public ItemIdentifier(Item item, int damage, NBTTagCompound data) {
		this.item = item;
		this.damage = damage;
		this.data = data;
	}
	public ItemIdentifier(ItemStack stack) {
		this(stack.getItem(), StackUtils.getRealItemDamage(stack), (stack.hasTagCompound()
				? (NBTTagCompound)stack.getTagCompound().copy() : null));
	}
	
	public ItemStack createStack(int size) {
		ItemStack stack = new ItemStack(item, size, damage);
		if (data != null)
			stack.setTagCompound((NBTTagCompound)data.copy());
		return stack;
	}
	
	/** Returns if this item identifier matches the id, damage and data. */
	public boolean matches(Item item, int damage, NBTTagCompound data) {
		return StackUtils.matches(this.item, this.damage, this.data, item, damage, data);
	}
	/** Returns if this item identifier matches the ItemStack. */
	public boolean matches(ItemStack stack) {
		return matches(stack.getItem(), StackUtils.getRealItemDamage(stack), stack.getTagCompound());
	}
	
	@Override
	public int hashCode() {
		if (!calculatedHashCode) {
			hashCode = ((Item.getIdFromItem(item) << 16) | damage);
			if (data != null) hashCode ^= data.hashCode();
		}
		return hashCode;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof ItemIdentifier)) return false;
		ItemIdentifier other = (ItemIdentifier)obj;
		return matches(other.item, other.damage, other.data);
	}
	
	@Override
	public String toString() {
		return item.getUnlocalizedName() + ":" + damage;
	}
	
}
