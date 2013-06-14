package net.mcft.copy.betterstorage.utils;

import java.util.List;

import net.mcft.copy.betterstorage.block.tileentity.TileEntityContainer;
import net.mcft.copy.betterstorage.container.ContainerBetterStorage;
import net.mcft.copy.betterstorage.inventory.InventoryTileEntity;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class WorldUtils {
	
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
	public static EntityItem spawnItem(World world, double x, double y, double z, ItemStack stack) {
		if (stack == null) return null;
		EntityItem item = new EntityItem(world, x, y, z, stack);
		world.spawnEntityInWorld(item);
		return item;
	}
	/** Spawns an ItemStack in the world with random motion. */
	public static EntityItem spawnItemWithMotion(World world, double x, double y, double z, ItemStack stack) {
		EntityItem item = spawnItem(world, x, y, z, stack);
		if (item != null) {
			item.motionX = RandomUtils.getGaussian() * 0.05F;
			item.motionY = RandomUtils.getGaussian() * 0.05F + 0.2F;
			item.motionZ = RandomUtils.getGaussian() * 0.05F;
		}
		return item;
	}
	/** Spawn an ItemStack dropping from a destroyed block. */
	public static EntityItem dropStackFromBlock(World world, int x, int y, int z, ItemStack stack) {
		float itemX = x + RandomUtils.getFloat(0.1F, 0.9F);
		float itemY = y + RandomUtils.getFloat(0.1F, 0.9F);
		float itemZ = z + RandomUtils.getFloat(0.1F, 0.9F);
		return spawnItemWithMotion(world, itemX, itemY, itemZ, stack);
	}
	/** Spawns an ItemStack as if it was dropped from an entity on death. */
	public static EntityItem dropStackFromEntity(Entity entity, ItemStack stack, float speed) {
		if (stack == null) return null;
		EntityPlayer player = ((entity instanceof EntityPlayer) ? (EntityPlayer)entity : null);
		EntityItem item;
		if (player == null) {
			double y = entity.posY + entity.getEyeHeight() - 0.3;
			item = spawnItem(entity.worldObj, entity.posX, y, entity.posZ, stack);
			item.delayBeforeCanPickup = 40;
			float f1 = RandomUtils.getFloat(0.5F);
			float f2 = RandomUtils.getFloat((float)Math.PI * 2.0F);
			item.motionX = -MathHelper.sin(f2) * f1;
			item.motionY = 0.2;
			item.motionZ =  MathHelper.cos(f2) * f1;
			return item;
		} else item = player.dropPlayerItemWithRandomChoice(stack, true);
		item.motionX *= speed / 4;
		item.motionZ *= speed / 4;
		return item;
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
	public static <T> boolean is(IBlockAccess world, int x, int y, int z, Class<T> tileClass) {
		return tileClass.isInstance(world.getBlockTileEntity(x, y, z));
	}
	/** Returns the TileEntity at the position if it's an instance of tileClass, null if not. */
	public static <T> T get(IBlockAccess world, int x, int y, int z, Class<T> tileClass) {
		TileEntity t = world.getBlockTileEntity(x, y, z);
		return (tileClass.isInstance(t) ? (T)t : null);
	}
	
	/** Returns if the TileEntity can be used by this player. */
	public static boolean isTileEntityUsableByPlayer(TileEntity entity, EntityPlayer player) {
		return (entity.worldObj.getBlockTileEntity(entity.xCoord, entity.yCoord, entity.zCoord) == entity &&
		        player.getDistanceSq(entity.xCoord + 0.5, entity.yCoord + 0.5, entity.zCoord + 0.5) <= 64.0);
	}
	
	/** Counts and returns the number of players who're accessing a tile entity. */
	public static int syncPlayersUsing(TileEntity te, int ticksSinceSync, int playersUsing, IInventory playerInventory) {
		World world = te.worldObj;
		ticksSinceSync += te.xCoord + te.yCoord + te.zCoord;
		if (!world.isRemote && (playersUsing != 0) && (ticksSinceSync % 200 == 0)) {
			playersUsing = 0;
			List players = world.getEntitiesWithinAABB(EntityPlayer.class, getAABB(te, 5));
			for (Object p : players) {
				EntityPlayer player = (EntityPlayer)p;
				if (player.openContainer instanceof ContainerBetterStorage) {
					IInventory inventory = ((ContainerBetterStorage)player.openContainer).inventory;
					if (inventory == playerInventory) playersUsing++;
					else if (inventory instanceof InventoryTileEntity)
						if (((InventoryTileEntity)inventory).mainTileEntity == te) playersUsing++;
				}
			}
		}
		return playersUsing;
	}
	/** Counts and returns the number of players who're accessing a tile entity. */
	public static int syncPlayersUsing(TileEntityContainer te, int ticksSinceSync, int numUsingPlayers) {
		return syncPlayersUsing(te, ticksSinceSync, numUsingPlayers, te.getPlayerInventory());
	}
	
}
