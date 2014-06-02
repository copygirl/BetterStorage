package net.mcft.copy.betterstorage.utils;

import java.util.Locale;

import net.mcft.copy.betterstorage.BetterStorage;
import net.minecraft.block.Block;
import net.minecraft.item.Item;

public final class MiscUtils {
	
	private MiscUtils() {  }
	
	public static <T> T conditionalNew(Class<T> theClass, String configName) {
		if (!BetterStorage.globalConfig.getBoolean(configName)) return null;
		try { return theClass.getConstructor().newInstance(); }
		catch (Exception e) { throw new RuntimeException(e); }
	}
	
	/** Gets the name of an item from its class name. <br>
	 *  For example: <code>ItemDrinkingHelmet</code> => <code>drinkingHelmet</code> */
	public static String getName(Item item) {
		return getName(item, 4);
	}
	/** Gets the name of a block from its class name. <br>
	 *  For example: <code>BlockCraftingStation</code> => <code>craftingStation</code> */
	public static String getName(Block block) {
		return getName(block, 4);
	}
	
	private static String getName(Object object, int begin) {
		String fullName = object.getClass().getSimpleName();
		return (fullName.substring(begin, begin + 1).toLowerCase(Locale.ENGLISH) +
		        fullName.substring(begin + 1));
	}
	
}
