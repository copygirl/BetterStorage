package net.mcft.copy.betterstorage.tile.crate;

import net.mcft.copy.betterstorage.misc.Region;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;

/** Keeps track of the crates in a crate pile and its bounding box. */
public class CratePileMap {
	
	public Region region;
	
	private byte[] map;
	private Region mapRegion;

	private CratePileMap(Region region, byte[] map, Region mapRegion) {
		this.region = region;
		this.map = map;
		this.mapRegion = mapRegion;
	}
	public CratePileMap(TileEntity entity) {
		region = new Region(entity);
		map = new byte[1];
		mapRegion = region.clone();
		mapRegion.minY /= 8;
		mapRegion.maxY /= 8;
		resize();
		add(entity);
	}
	
	private int getIndex(int width, int depth, int x, int y, int z) {
		return (y + x * depth + z * depth * width);
	}
	private int getIndex(int x, int y, int z) {
		return getIndex(mapRegion.width(), mapRegion.depth(), x, y, z);
	}
	private boolean internalGet(int x, int y, int z) {
		byte b = map[getIndex(x, y / 8, z)];
		return (((b >> (y % 8)) & 1) > 0);
	}
	private void internalSet(int x, int y, int z, boolean value) {
		int index = getIndex(x, y / 8, z);
		if (value) map[index] |= 1 << (y % 8);
		else map[index] &= ~(1 << (y % 8));
	}
	
	/** Resizes the internal map region, copying the old data over. */
	private void resize(Region region) {
		byte[] newMap = new byte[region.width() * region.depth() * region.height()];
		int minX = Math.max(mapRegion.minX, region.minX);
		int maxX = Math.min(mapRegion.maxX, region.maxX);
		int minY = Math.max(mapRegion.minY, region.minY);
		int maxY = Math.min(mapRegion.maxY, region.maxY);
		int minZ = Math.max(mapRegion.minZ, region.minZ);
		int maxZ = Math.min(mapRegion.maxZ, region.maxZ);
		for (int x = minX; x <= maxX; x++)
			for (int z = minZ; z <= maxZ; z++) {
				int srcPos  = getIndex(x - mapRegion.minX, minY - mapRegion.minY, z - mapRegion.minZ);
				int destPos = getIndex(region.width(), region.depth(),
				                       x - region.minX, minY - region.minY, z - region.minZ);
				int length = maxY - minY + 1;
				System.arraycopy(map, srcPos, newMap, destPos, length);
			}
		map = newMap;
		mapRegion = region;
	}
	/** Resizes the internal map region, copying the old data over. */
	private void resize() {
		Region newRegion = region.clone();
		newRegion.expand(2, 0, 2, 2, 2, 2);
		newRegion.minY /= 8;
		newRegion.maxY /= 8;
		resize(newRegion);
	}
	
	/** Returns if the cuboid contains this position and all values are not set. */
	@Deprecated
	//TODO (1.8): No.
	private boolean checkCuboid(int minX, int minY, int minZ,
	                            int maxX, int maxY, int maxZ) {
		for (int x = minX; x <= maxX; x++)
			for (int y = minY; y <= maxY; y++)
				for (int z = minZ; z <= maxZ; z++);
//					if (get(x, y, z)) return false;
		return true;
	}
	
	public boolean get(BlockPos pos) {
//		if (!region.contains(pos)) return false;
//		return internalGet(x - mapRegion.minX, y - mapRegion.minY * 8, z - mapRegion.minZ);
		return false;
	}
	public void set(BlockPos pos, boolean value) {
		/*if (region.contains(pos)) {
			internalSet(x - mapRegion.minX, y - mapRegion.minY * 8, z - mapRegion.minZ, value);
			if (!value) {
				int minX = region.minX, minY = region.minY, minZ = region.minZ;
				int maxX = region.maxX, maxY = region.maxY, maxZ = region.maxZ;
				if ((x == minX) && checkCuboid(minX, minY, minZ, minX, maxY, maxZ)) region.minX += 1;
				if ((y == minY) && checkCuboid(minX, minY, minZ, maxX, minY, maxZ)) region.minY += 1;
				if ((z == minZ) && checkCuboid(minX, minY, minZ, maxX, maxY, minZ)) region.minZ += 1;
				if ((x == maxX) && checkCuboid(maxX, minY, minZ, maxX, maxY, maxZ)) region.maxX -= 1;
				if ((y == maxY) && checkCuboid(minX, maxY, minZ, maxX, maxY, maxZ)) region.maxY -= 1;
				if ((z == maxZ) && checkCuboid(minX, minY, maxZ, maxX, maxY, maxZ)) region.maxZ -= 1;
				if (region.minX - mapRegion.minX > 4 || mapRegion.maxX - region.maxX > 4 ||
				    region.minY / 8 - mapRegion.minY > 2 || mapRegion.maxY / 8 - region.maxY > 2 ||
				    region.minZ - mapRegion.minZ > 4 || mapRegion.maxZ - region.maxZ > 4)
					resize();
			}
		} else if (value) {
			region.expandToContain(x, y, z);
			if (!mapRegion.contains(x, y / 8, z))
				resize();
			internalSet(x - mapRegion.minX, y - mapRegion.minY * 8, z - mapRegion.minZ, value);
		}*/
	}
	
	public void add(TileEntity entity) {
		set(entity.getPos(), true);
	}
	public void remove(TileEntity entity) {
		set(entity.getPos(), false);
	}
	
	/** Trims the bounding box to the actual size of the crate pile. <br>
	 *  This is needed when crates are not removed in order, for
	 *  example when they're split. */
	public void trim() {
		int minX = region.minX, minY = region.minY, minZ = region.minZ;
		int maxX = region.maxX, maxY = region.maxY, maxZ = region.maxZ;
		while (checkCuboid(minX, minY, minZ, minX, maxY, maxZ)) minX += 1;
		while (checkCuboid(minX, minY, minZ, maxX, minY, maxZ)) minY += 1;
		while (checkCuboid(minX, minY, minZ, maxX, maxY, minZ)) minZ += 1;
		while (checkCuboid(maxX, minY, minZ, maxX, maxY, maxZ)) maxX -= 1;
		while (checkCuboid(minX, maxY, minZ, maxX, maxY, maxZ)) maxY -= 1;
		while (checkCuboid(minX, minY, maxZ, maxX, maxY, maxZ)) maxZ -= 1;
		region.set(minX, minY, minZ, maxX, maxY, maxZ);
	}
	
	public NBTTagCompound toCompound() {
		NBTTagCompound compound = new NBTTagCompound();
		compound.setTag("region", region.toCompound());
		compound.setByteArray("map", map);
		compound.setTag("mapRegion", mapRegion.toCompound());
		return compound;
	}
	public static CratePileMap fromCompound(NBTTagCompound compound) {
		Region region = Region.fromCompound(compound.getCompoundTag("region"));
		byte[] map = compound.getByteArray("map");
		Region mapRegion = Region.fromCompound(compound.getCompoundTag("mapRegion"));
		return new CratePileMap(region, map, mapRegion);
	}
	
}
