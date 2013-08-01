package net.mcft.copy.betterstorage.utils;

public final class MiscUtils {
	
	private MiscUtils() {  }
	
	public static <T> T conditionalNew(Class<T> theClass, int id) {
		if (id <= 0) return null;
		try { return theClass.getConstructor(int.class).newInstance(id); }
		catch (Exception e) { throw new RuntimeException(e); }
	}
	
}
