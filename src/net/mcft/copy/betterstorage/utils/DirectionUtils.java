package net.mcft.copy.betterstorage.utils;

import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;
import net.minecraftforge.common.ForgeDirection;

public class DirectionUtils {
	
	public static ForgeDirection getOrientation(Entity entity) {
		int dir = MathHelper.floor_double(entity.rotationYaw * 4.0 / 360.0 + 0.5) & 3;
		switch (dir) {
			case 1: return ForgeDirection.SOUTH;
			case 2: return ForgeDirection.WEST;
			case 3: return ForgeDirection.NORTH;
			default: return ForgeDirection.EAST;
		}
	}
	
	public static int getRotation(ForgeDirection dir) {
		if (dir == ForgeDirection.SOUTH) return 90;
		else if (dir == ForgeDirection.WEST) return 180;
		else if (dir == ForgeDirection.NORTH) return 270;
		else return 0;
	}
	
}
