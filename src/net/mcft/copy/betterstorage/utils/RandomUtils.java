package net.mcft.copy.betterstorage.utils;

import java.util.Random;

public class RandomUtils {
	
	private static final Random random = new Random();
	
	public static int getInt(int max) { return random.nextInt(max); }
	
	public static int getInt(int min, int max) { return min + getInt(max - min); }
	
	public static float getFloat() { return random.nextFloat(); }
	
	public static float getFloat(float max) { return getFloat() * max; }
	
	public static float getFloat(float min, float max) { return min + getFloat(max - min); }
	
	public static double getGaussian() { return random.nextGaussian(); }
	
}
