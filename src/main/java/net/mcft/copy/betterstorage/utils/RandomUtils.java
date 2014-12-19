package net.mcft.copy.betterstorage.utils;

import java.util.Random;

import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3;

public final class RandomUtils {
	
	private RandomUtils() {  }
	
	public static final Random random = new Random();
	public static Vec3 blockCenter = new Vec3(0.5, 0.5, 0.5);
	
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
	
	public static AxisAlignedBB copyAABB(AxisAlignedBB aabb) {
		return AxisAlignedBB.fromBounds(aabb.minX, aabb.minY, aabb.minZ, aabb.maxX, aabb.maxY, aabb.maxZ);
	}
	
	public static AxisAlignedBB getRotatedAABB(AxisAlignedBB aabb, EnumFacing rotation) {
		return getRotatedAABB(aabb, rotation, blockCenter);
	}
	
	public static AxisAlignedBB getRotatedAABB(AxisAlignedBB aabb, EnumFacing rotation, Vec3 offset) {
		aabb = copyAABB(aabb);
		aabb.offset(-offset.xCoord, -offset.yCoord, -offset.zCoord);
		switch (rotation) {
			//TODO (1.8): The facings are probably wrong.
			case EAST  : aabb = AxisAlignedBB.fromBounds(aabb.minZ     , aabb.minY, aabb.maxX * -1, aabb.maxZ     , aabb.maxY, aabb.minX * -1);
			case SOUTH : aabb = AxisAlignedBB.fromBounds(aabb.maxX * -1, aabb.minY, aabb.maxZ * -1, aabb.minX * -1, aabb.maxY, aabb.minZ * -1);
			case NORTH : aabb = AxisAlignedBB.fromBounds(aabb.maxZ * -1, aabb.minY, aabb.minX     , aabb.minZ * -1, aabb.maxY, aabb.maxX     );
			default: break;
		}
		aabb.offset(offset.xCoord, offset.yCoord, offset.zCoord);
		return aabb;
	}
}
