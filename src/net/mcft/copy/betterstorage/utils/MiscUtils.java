package net.mcft.copy.betterstorage.utils;

import java.util.Locale;

import net.mcft.copy.betterstorage.BetterStorage;
import net.minecraft.block.Block;
import net.minecraft.item.Item;

public final class MiscUtils {
	
	private MiscUtils() {  }
	
	public static <T> T conditionalNew(Class<T> theClass, int id) {
		if (!isEnabled(id)) return null;
		try { return theClass.getConstructor(int.class).newInstance(id); }
		catch (Exception e) { throw new RuntimeException(e); }
	}
	public static <T> T conditionalNew(Class<T> theClass, String configName) {
		return conditionalNew(theClass, BetterStorage.globalConfig.getInteger(configName));
	}
	
	/** Returns if the arguments are enabled
	 *  (non-null for objects, > 0 for ints). */
	public static boolean isEnabled(Object... objects) {
		for (Object object : objects) {
			if (object instanceof Integer) {
				if ((Integer)object <= 0)
					return false;
			} else if (object == null)
				return false;
		}
		return true;
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
