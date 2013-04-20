package net.mcft.copy.betterstorage.utils;

import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;
import net.minecraftforge.common.ForgeDirection;

public class DirectionUtils {
	
	/** Gets the ForgeDirection from the direction an entity is facing. */
	public static ForgeDirection getOrientation(Entity entity) {
		int dir = MathHelper.floor_double(entity.rotationYaw * 4.0 / 360.0 + 0.5) & 3;
		switch (dir) {
			case 1: return ForgeDirection.WEST;
			case 2: return ForgeDirection.NORTH;
			case 3: return ForgeDirection.EAST;
			default: return ForgeDirection.SOUTH;
		}
	}
	
	/** Gets the direction from a ForgeDirection in degrees. */
	public static int getRotation(ForgeDirection dir) {
		if (dir == ForgeDirection.WEST) return 90;
		else if (dir == ForgeDirection.NORTH) return 180;
		else if (dir == ForgeDirection.EAST) return 270;
		else return 0;
	}
	
	/** Returns the difference between the two angles in degrees (-180 to 180). */
	public static double angleDifference(double angle1, double angle2) {
		return (angle2 - angle1 + 180) % 360 - 180;
	}
	
	/** Returns the ForgeDirection from a vanilla side value. */
	public static ForgeDirection getDirectionFromSide(int side) {
		switch (side) {
			case 0: return ForgeDirection.DOWN;
			case 1: return ForgeDirection.UP;
			case 2: return ForgeDirection.NORTH;
			case 3: return ForgeDirection.SOUTH;
			case 4: return ForgeDirection.WEST;
			case 5: return ForgeDirection.EAST;
			default: return ForgeDirection.UNKNOWN;
		}
	}
	
}
