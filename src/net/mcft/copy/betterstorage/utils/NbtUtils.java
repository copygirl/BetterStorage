package net.mcft.copy.betterstorage.utils;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.nbt.NBTTagByteArray;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.nbt.NBTTagLong;
import net.minecraft.nbt.NBTTagShort;
import net.minecraft.nbt.NBTTagString;

public class NbtUtils {
	
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
	
}
