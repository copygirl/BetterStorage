package net.mcft.copy.betterstorage.utils;

import java.util.List;
import java.util.Random;

import net.mcft.copy.betterstorage.BetterStorage;
import net.mcft.copy.betterstorage.api.ILockable;
import net.mcft.copy.betterstorage.block.TileEntityArmorStand;
import net.mcft.copy.betterstorage.block.TileEntityLocker;
import net.mcft.copy.betterstorage.block.TileEntityReinforcedChest;
import net.mcft.copy.betterstorage.block.crate.CratePileData;
import net.mcft.copy.betterstorage.block.crate.TileEntityCrate;
import net.mcft.copy.betterstorage.container.ContainerBetterStorage;
import net.mcft.copy.betterstorage.inventory.InventoryCombined;
import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class WorldUtils {
	
	public static Random random = BetterStorage.random;
	
	public static AxisAlignedBB getAABB(TileEntity entity, double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
		double x = entity.xCoord;
		double y = entity.yCoord;
		double z = entity.zCoord;
		return AxisAlignedBB.getAABBPool().getAABB(x - minX, y - minY, z - minZ, x + maxX + 1, y + maxY + 1, z + maxZ + 1);
	}
	public static AxisAlignedBB getAABB(TileEntity entity, double radius) {
		return getAABB(entity, radius, radius, radius, radius, radius, radius);
	}
	
	/** Spawns an ItemStack in the world. */
	public static EntityItem spawnItem(World world, float x, float y, float z, ItemStack stack) {
		if (stack == null) return null;
		EntityItem item = new EntityItem(world, x, y, z, stack);
		world.spawnEntityInWorld(item);
		return item;
	}
	/** Spawns an ItemStack in the world with random motion. */
	public static EntityItem spawnItemWithMotion(World world, float x, float y, float z, ItemStack stack) {
		EntityItem item = spawnItem(world, x, y, z, stack);
		if (item != null) {
			item.motionX = random.nextGaussian() * 0.05F;
			item.motionY = random.nextGaussian() * 0.05F + 0.2F;
			item.motionZ = random.nextGaussian() * 0.05F;
		}
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
	/** Returns the TileEntity at the position if it's an instance of tileClass, null if not. */
	public static <T extends TileEntity> T get(IBlockAccess world, int x, int y, int z, Class<T> tileClass) {
		TileEntity t = world.getBlockTileEntity(x, y, z);
		return (tileClass.isInstance(t) ? (T)t : null);
	}
	
	/** Returns the TileEntityCrate at a position, null if there's none. */
	public static TileEntityCrate getCrate(IBlockAccess world, int x, int y, int z) {
		return get(world, x, y, z, TileEntityCrate.class);
	}
	/** Returns the pile data of a crate at a position, null if there's none */
	public static CratePileData getCratePileData(IBlockAccess world, int x, int y, int z) {
		TileEntityCrate crate = getCrate(world, x, y, z);
		return ((crate != null) ? crate.getPileData() : null);
	}
	
	/** Returns the TileEntityReinforcedChest at a position, null if there's none. */
	public static TileEntityReinforcedChest getChest(IBlockAccess world, int x, int y, int z) {
		return get(world, x, y, z, TileEntityReinforcedChest.class);
	}
	
	/** Returns the TileEntityLocker at a position, null if there's none. */
	public static TileEntityLocker getLocker(IBlockAccess world, int x, int y, int z) {
		return get(world, x, y, z, TileEntityLocker.class);
	}
	
	/** Returns the TileEntityArmorStand at a position, null if there's none. */
	public static TileEntityArmorStand getArmorStand(World world, int x, int y, int z) {
		return get(world, x, y, z, TileEntityArmorStand.class);
	}
	
	/** Returns the ILockable at a position, null if there's none. */
	public static ILockable getLockable(World world, int x, int y, int z) {
		TileEntity entity = world.getBlockTileEntity(x, y, z);
		if (entity instanceof ILockable)
			return (ILockable)entity;
		else return null;
	}
	
	/** Returns if the TileEntity can be used by this player. */
	public static boolean isTileEntityUsableByPlayer(TileEntity entity, EntityPlayer player) {
		return (entity.worldObj.getBlockTileEntity(entity.xCoord, entity.yCoord, entity.zCoord) == entity &&
		        player.getDistanceSq(entity.xCoord + 0.5, entity.yCoord + 0.5, entity.zCoord + 0.5) <= 64.0);
	}
	
	/** Counts and returns the number of players who're accessing a tile entity. */
	public static int syncPlayersUsing(TileEntity entity, int ticksSinceSync, int numUsingPlayers, IInventory playerInventory) {
		World world = entity.worldObj;
		int x = entity.xCoord;
		int y = entity.yCoord;
		int z = entity.zCoord;
		if (!world.isRemote && numUsingPlayers != 0 &&
		    (ticksSinceSync + x + y + z) % 200 == 0) {
			numUsingPlayers = 0;
			List players = world.getEntitiesWithinAABB(EntityPlayer.class, getAABB(entity, 5));
			for (Object p : players) {
				EntityPlayer player = (EntityPlayer)p;
				if (player.openContainer instanceof ContainerBetterStorage) {
					IInventory inventory = ((ContainerBetterStorage)player.openContainer).inventory;
					if (inventory == playerInventory) numUsingPlayers++;
					else if (inventory instanceof InventoryCombined)
						for (IInventory inv : (InventoryCombined<IInventory>)inventory)
							if (inv == playerInventory) numUsingPlayers++;
				}
			}
		}
		return numUsingPlayers;
	}
	/** Counts and returns the number of players who're accessing a tile entity. */
	public static int syncPlayersUsing(TileEntity entity, int ticksSinceSync, int numUsingPlayers) {
		return syncPlayersUsing(entity, ticksSinceSync, numUsingPlayers, (IInventory)entity);
	}
	
}
