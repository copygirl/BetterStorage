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

public class NbtUtils {
	
	/** Returns the value of a tag. The type is determined by the generic type of the function. */
	public static <T> T getTagValue(NBTBase tag) {
		if (tag instanceof NBTTagByte)      return (T)(Object)((NBTTagByte)tag).data;
		if (tag instanceof NBTTagShort)     return (T)(Object)((NBTTagShort)tag).data;
		if (tag instanceof NBTTagInt)       return (T)(Object)((NBTTagInt)tag).data;
		if (tag instanceof NBTTagLong)      return (T)(Object)((NBTTagLong)tag).data;
		if (tag instanceof NBTTagFloat)     return (T)(Object)((NBTTagFloat)tag).data;
		if (tag instanceof NBTTagDouble)    return (T)(Object)((NBTTagDouble)tag).data;
		if (tag instanceof NBTTagString)    return (T)((NBTTagString)tag).data;
		if (tag instanceof NBTTagByteArray) return (T)((NBTTagByteArray)tag).byteArray;
		if (tag instanceof NBTTagIntArray)  return (T)((NBTTagIntArray)tag).intArray;
		return null;
	}
	
	/** Creates a tag from a value. The type is determined by the type of the value. */
	public static NBTBase createTag(String name, Object value) {
		if (value instanceof Byte)    return new NBTTagByte(name, (Byte)value);
		if (value instanceof Short)   return new NBTTagShort(name, (Short)value);
		if (value instanceof Integer) return new NBTTagInt(name, (Integer)value);
		if (value instanceof Long)    return new NBTTagLong(name, (Long)value);
		if (value instanceof Float)   return new NBTTagFloat(name, (Float)value);
		if (value instanceof Double)  return new NBTTagDouble(name, (Double)value);
		if (value instanceof String)  return new NBTTagString(name, (String)value);
		if (value instanceof byte[])  return new NBTTagByteArray(name, (byte[])value);
		if (value instanceof int[])   return new NBTTagIntArray(name, (int[])value);
		return null;
	}
	
	public static ItemStack[] readItems(NBTTagList items, int size) {
		ItemStack[] contents = new ItemStack[size];
		for (int i = 0; i < items.tagCount(); i++) {
			NBTTagCompound item = (NBTTagCompound)items.tagAt(i);
			int slot = item.getByte("Slot") & 255;
			if (slot >= 0 && slot < contents.length)
				contents[slot] = ItemStack.loadItemStackFromNBT(item);
		}
		return contents;
	}
	public static void readItems(NBTTagList items, List<ItemStack> list) {
		for (int i = 0; i < items.tagCount(); i++)
			list.add(ItemStack.loadItemStackFromNBT((NBTTagCompound)items.tagAt(i)));
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
