package net.mcft.copy.betterstorage.utils;

import net.mcft.copy.betterstorage.BetterStorage;

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
	
}
