package net.mcft.copy.betterstorage.utils;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.nbt.NBTTagByteArray;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagLong;
import net.minecraft.nbt.NBTTagShort;
import net.minecraft.nbt.NBTTagString;

public final class NbtUtils {
	
	private NbtUtils() {  }
	
	/** Returns the value of a tag. The type is determined by the generic type of the function. */
	public static <T> T getTagValue(NBTBase tag) {
		if (tag instanceof NBTTagByte)      return (T)(Object)((NBTTagByte)tag).getByte();
		if (tag instanceof NBTTagShort)     return (T)(Object)((NBTTagShort)tag).getShort();
		if (tag instanceof NBTTagInt)       return (T)(Object)((NBTTagInt)tag).getInt();
		if (tag instanceof NBTTagLong)      return (T)(Object)((NBTTagLong)tag).getLong();
		if (tag instanceof NBTTagFloat)     return (T)(Object)((NBTTagFloat)tag).getFloat();
		if (tag instanceof NBTTagDouble)    return (T)(Object)((NBTTagDouble)tag).getDouble();
		if (tag instanceof NBTTagString)    return (T)((NBTTagString)tag).getString();
		if (tag instanceof NBTTagByteArray) return (T)((NBTTagByteArray)tag).getByteArray();
		if (tag instanceof NBTTagIntArray)  return (T)((NBTTagIntArray)tag).getIntArray();
		return null;
	}
	
	/** Creates a tag from a value. The type is determined by the type of the value. */
	public static NBTBase createTag(Object value) {
		if (value instanceof Byte)    return new NBTTagByte((Byte)value);
		if (value instanceof Short)   return new NBTTagShort((Short)value);
		if (value instanceof Integer) return new NBTTagInt((Integer)value);
		if (value instanceof Long)    return new NBTTagLong((Long)value);
		if (value instanceof Float)   return new NBTTagFloat((Float)value);
		if (value instanceof Double)  return new NBTTagDouble((Double)value);
		if (value instanceof String)  return new NBTTagString((String)value);
		if (value instanceof byte[])  return new NBTTagByteArray((byte[])value);
		if (value instanceof int[])   return new NBTTagIntArray((int[])value);
		return null;
	}
	
	public static NBTTagList createList(Object... values) {
		NBTTagList list = new NBTTagList();
		for (Object value : values)
			list.appendTag((value instanceof NBTBase)
					? (NBTBase)value : createTag(value));
		return list;
	}
	
	public static NBTTagCompound createCompound(Object... nameValuePairs) {
		NBTTagCompound compound = new NBTTagCompound();
		for (int i = 0; i < nameValuePairs.length; i += 2) {
			String key = (String)nameValuePairs[i];
			Object value = nameValuePairs[i + 1];
			compound.setTag(key, (value instanceof NBTBase) ? (NBTBase)value : createTag(value));
		}
		return compound;
	}
	
	public static void readItems(ItemStack[] contents, NBTTagList items) {
		for (int i = 0; i < contents.length; i++)
			contents[i] = null;
		for (int i = 0; i < items.tagCount(); i++) {
			NBTTagCompound item = items.getCompoundTagAt(i);
			int slot = item.getByte("Slot") & 255;
			if (slot >= 0 && slot < contents.length)
				contents[slot] = ItemStack.loadItemStackFromNBT(item);
		}
	}
	public static void readItems(List<ItemStack> list, NBTTagList items) {
		for (int i = 0; i < items.tagCount(); i++)
			list.add(ItemStack.loadItemStackFromNBT(items.getCompoundTagAt(i)));
	}
	
	public static NBTTagList writeItems(ItemStack[] contents) {
		NBTTagList items = new NBTTagList();
		for (int i = 0; i < contents.length; i++) {
			if (contents[i] == null) continue;
			NBTTagCompound item = new NBTTagCompound();
			item.setByte("Slot", (byte)i);
			contents[i].writeToNBT(item);
			items.appendTag(item);
		}
		return items;
	}
	
}
