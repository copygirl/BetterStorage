package net.mcft.copy.betterstorage.utils;

import java.util.Random;

import net.mcft.copy.betterstorage.BetterStorage;
import net.mcft.copy.betterstorage.block.CratePileData;
import net.mcft.copy.betterstorage.block.TileEntityCrate;
import net.mcft.copy.betterstorage.block.TileEntityReinforcedChest;
import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class WorldUtils {
	
	public static Random random = BetterStorage.random;
	
	/** Spawns an ItemStack in the world. */
	public static EntityItem spawnItem(World world, float x, float y, float z, ItemStack stack) {
		EntityItem item = new EntityItem(world, x, y, z, stack);
		world.spawnEntityInWorld(item);
		return item;
	}
	/** Spawns an ItemStack in the world with random motion. */
	public static EntityItem spawnItemWithMotion(World world, float x, float y, float z, ItemStack stack) {
		EntityItem item = spawnItem(world, x, y, z, stack);
		item.motionX = random.nextGaussian() * 0.05F;
		item.motionY = random.nextGaussian() * 0.05F + 0.2F;
		item.motionZ = random.nextGaussian() * 0.05F;
		return item;
	}
	/** Spawn an ItemStack dropping from a destroyed block. */
	public static EntityItem dropStackFromBlock(World world, int x, int y, int z, ItemStack stack) {
		float itemX = x + random.nextFloat() * 0.8F + 0.1F;
		float itemY = y + random.nextFloat() * 0.8F + 0.1F;
		float itemZ = z + random.nextFloat() * 0.8F + 0.1F;
		return spawnItemWithMotion(world, itemX, itemY, itemZ, stack);
	}
	
	/** Returns whether the Block at the position has this id. */
	public static boolean is(IBlockAccess world, int x, int y, int z, int id) {
		return (world.getBlockId(x, y, z) == id);
	}
	/** Returns whether the Block at the position is the same as block. */
	public static boolean is(IBlockAccess world, int x, int y, int z, Block block) {
		return is(world, x, y, z, block.blockID);
	}
	
	/** Returns whether the TileEntity at the position is an instance of tileClass. */
	public static <T extends TileEntity> boolean is(IBlockAccess world, int x, int y, int z, Class<T> tileClass) {
		return tileClass.isInstance(world.getBlockTileEntity(x, y, z));
	}
	/** Returns the TileEntity at the position, null if there's none. */
	public static <T extends TileEntity> T get(IBlockAccess world, int x, int y, int z, Class<T> tileClass) {
		TileEntity t = world.getBlockTileEntity(x, y, z);
		return (tileClass.isInstance(t) ? (T)t : null);
	}
	
	/** Returns the TileEntityCrate at a position in the world, null if there's none. */
	public static TileEntityReinforcedChest getChest(IBlockAccess world, int x, int y, int z) {
		return get(world, x, y, z, TileEntityReinforcedChest.class);
	}
	
	/** Returns the TileEntityCrate at a position in the world, null if there's none. */
	public static TileEntityCrate getCrate(IBlockAccess world, int x, int y, int z) {
		return get(world, x, y, z, TileEntityCrate.class);
	}
	/** Returns the pile data of a crate at a position in the world, null if there's none */
	public static CratePileData getCratePileData(IBlockAccess world, int x, int y, int z) {
		TileEntityCrate crate = getCrate(world, x, y, z);
		return ((crate != null) ? crate.getPileData() : null);
	}
	
}
