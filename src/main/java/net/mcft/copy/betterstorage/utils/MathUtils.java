package net.mcft.copy.betterstorage.utils;

import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.Vec3;
import net.minecraft.util.Vec3i;

public class MathUtils {

	public static final Vec3 VEC_CENTER = new Vec3(0.5, 0.5, 0.5);
	
	public static AxisAlignedBB createAABB(double x, double y, double z, double w, double h, double d) {
		return AxisAlignedBB.fromBounds(x, y, z, x + w, y + h, z + d);
	}
	
	public static AxisAlignedBB copyAABB(AxisAlignedBB aabb) {
		return AxisAlignedBB.fromBounds(aabb.minX, aabb.minY, aabb.minZ, aabb.maxX, aabb.maxY, aabb.maxZ);
	}
	
	public static AxisAlignedBB scaleAABB(AxisAlignedBB aabb, double scale) {
		return scaleAABB(aabb, new Vec3(scale, scale, scale));
	}

	public static AxisAlignedBB scaleAABB(AxisAlignedBB aabb, Vec3 scale) {
		Vec3 center = getCenter(aabb);
		aabb = offsetAABB(aabb, multiplyVec3(center, -1));
		aabb = AxisAlignedBB.fromBounds(aabb.minX * scale.xCoord, aabb.minY * scale.yCoord, aabb.minZ * scale.zCoord, 
		                                aabb.maxX * scale.xCoord, aabb.maxY * scale.yCoord, aabb.maxZ * scale.zCoord);
		aabb = offsetAABB(aabb, multiplyVec3(center, scale));
		return aabb;
	}
	
	public static AxisAlignedBB offsetAABB(AxisAlignedBB aabb, Vec3 offset) {
		return aabb.offset(offset.xCoord, offset.yCoord, offset.zCoord);
	}
	
	public static Vec3 getCenter(AxisAlignedBB aabb) {
		return new Vec3((aabb.minX + aabb.maxX) / 2, 
		                (aabb.minY + aabb.maxY) / 2, 
		                (aabb.minZ + aabb.maxZ) / 2);
	}
	
	public static Vec3 fromVec3i(Vec3i pos) {
		return new Vec3(pos.getX(), pos.getY(), pos.getZ());
	}
	
	public static Vec3 multiplyVec3(Vec3 vec, double d) {
		return new Vec3(vec.xCoord * d, vec.yCoord * d, vec.zCoord * d);
	}
	
	public static Vec3 multiplyVec3(Vec3 vec, Vec3 vec2) {
		return new Vec3(vec.xCoord * vec2.xCoord, vec.yCoord * vec2.yCoord, vec.zCoord * vec2.zCoord);
	}

	public static AxisAlignedBB rotateAABB(AxisAlignedBB aabb, EnumFacing rotation) {
		return MathUtils.rotateAABB(aabb, rotation, MathUtils.VEC_CENTER);
	}

	public static AxisAlignedBB rotateAABB(AxisAlignedBB aabb, EnumFacing rotation, Vec3 offset) {
		aabb = copyAABB(aabb);
		aabb = aabb.offset(-offset.xCoord, -offset.yCoord, -offset.zCoord);
		switch (rotation) {
			//TODO (1.8): The facings are probably wrong.
			case NORTH : aabb = AxisAlignedBB.fromBounds( aabb.minZ, aabb.minY, -aabb.maxX,  aabb.maxZ, aabb.maxY, -aabb.minX);
			case EAST  : aabb = AxisAlignedBB.fromBounds(-aabb.maxX, aabb.minY, -aabb.maxZ, -aabb.minX, aabb.maxY, -aabb.minZ);
			case WEST  : aabb = AxisAlignedBB.fromBounds(-aabb.maxZ, aabb.minY,  aabb.minX, -aabb.minZ, aabb.maxY,  aabb.maxX);
			case SOUTH : break;
			default    : throw new IllegalArgumentException();
		}
		aabb = aabb.offset(offset.xCoord, offset.yCoord, offset.zCoord);
		return aabb;
	}

}
