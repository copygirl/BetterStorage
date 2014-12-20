package net.mcft.copy.betterstorage.utils;

import java.util.Random;

public final class RandomUtils {
	
	private RandomUtils() {  }
	
	public static final Random random = new Random();
	public static int getInt(int max) { return random.nextInt(max); }
	public static int getInt(int min, int max) {
		return ((max > min) ? (min + getInt(max - min)) : min);
	}
	
	public static float getFloat() { return random.nextFloat(); }
	public static float getFloat(float max) { return getFloat() * max; }
	public static float getFloat(float min, float max) {
		return ((max > min) ? (min + getFloat(max - min)) : min);
	}
	
	public static double getDouble() { return random.nextDouble(); }
	public static double getDouble(double max) { return getDouble() * max; }
	public static double getDouble(double min, double max) {
		return ((max > min) ? (min + getDouble(max - min)) : min);
	}
	
	public static boolean getBoolean(double probability) { return (getDouble() < probability); }
	
	public static double getGaussian() { return random.nextGaussian(); }
}
