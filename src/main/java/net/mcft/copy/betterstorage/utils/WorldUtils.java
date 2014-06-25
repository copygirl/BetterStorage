package net.mcft.copy.betterstorage.utils;

import java.util.List;

import net.mcft.copy.betterstorage.attachment.Attachments;
import net.mcft.copy.betterstorage.container.ContainerBetterStorage;
import net.mcft.copy.betterstorage.inventory.InventoryTileEntity;
import net.mcft.copy.betterstorage.tile.entity.TileEntityContainer;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public final class WorldUtils {
	
	private WorldUtils() {  }
	
	@SideOnly(Side.CLIENT)
	public static World getLocalWorld() {
		return Minecraft.getMinecraft().theWorld;
	}
	
	public static AxisAlignedBB getAABB(TileEntity entity, double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
		double x = entity.xCoord;
		double y = entity.yCoord;
		double z = entity.zCoord;
		return AxisAlignedBB.getBoundingBox(x - minX, y - minY, z - minZ, x + maxX + 1, y + maxY + 1, z + maxZ + 1);
	}
	public static AxisAlignedBB getAABB(TileEntity entity, double radius) {
		return getAABB(entity, radius, radius, radius, radius, radius, radius);
	}
	
	// Item spawning related functions
	
	/** Spawns an ItemStack in the world. */
	public static EntityItem spawnItem(World world, double x, double y, double z, ItemStack stack) {
		if ((stack == null) || (stack.stackSize <= 0)) return null;
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
	/** Spawn an ItemStack dropping from a destroyed block. */
	public static EntityItem dropStackFromBlock(TileEntity te, ItemStack stack) {
		return dropStackFromBlock(te.getWorldObj(), te.xCoord, te.yCoord, te.zCoord, stack);
	}
	
	/** Spawns an ItemStack as if it was dropped from an entity on death. */
	public static EntityItem dropStackFromEntity(Entity entity, ItemStack stack, float speed) {
		EntityPlayer player = ((entity instanceof EntityPlayer) ? (EntityPlayer)entity : null);
		EntityItem item;
		if (player == null) {
			double y = entity.posY + entity.getEyeHeight() - 0.3;
			item = spawnItem(entity.worldObj, entity.posX, y, entity.posZ, stack);
			if (item == null) return null;
			item.delayBeforeCanPickup = 40;
			float f1 = RandomUtils.getFloat(0.5F);
			float f2 = RandomUtils.getFloat((float)Math.PI * 2.0F);
			item.motionX = -MathHelper.sin(f2) * f1;
			item.motionY = 0.2;
			item.motionZ =  MathHelper.cos(f2) * f1;
			return item;
		} else item = player.dropPlayerItemWithRandomChoice(stack, true);
		if (item != null) {
			item.motionX *= speed / 4;
			item.motionZ *= speed / 4;
		}
		return item;
	}
	
	// TileEntity related functions
	
	/** Returns whether the TileEntity at the position is an instance of tileClass. */
	public static <T> boolean is(IBlockAccess world, int x, int y, int z, Class<T> tileClass) {
		return tileClass.isInstance(world.getTileEntity(x, y, z));
	}
	/** Returns the TileEntity at the position if it's an instance of tileClass, null if not. */
	public static <T> T get(IBlockAccess world, int x, int y, int z, Class<T> tileClass) {
		TileEntity t = world.getTileEntity(x, y, z);
		return (tileClass.isInstance(t) ? (T)t : null);
	}
	
	/** Returns if the TileEntity can be used by this player. */
	public static boolean isTileEntityUsableByPlayer(TileEntity entity, EntityPlayer player) {
		return (entity.getWorldObj().getTileEntity(entity.xCoord, entity.yCoord, entity.zCoord) == entity &&
		        player.getDistanceSq(entity.xCoord + 0.5, entity.yCoord + 0.5, entity.zCoord + 0.5) <= 64.0);
	}
	
	/** Counts and returns the number of players who're accessing a tile entity. */
	public static int syncPlayersUsing(TileEntity te, int playersUsing, IInventory playerInventory) {
		if (!te.getWorldObj().isRemote && (playersUsing != 0)) {
			playersUsing = 0;
			List<EntityPlayer> players = te.getWorldObj().getEntitiesWithinAABB(EntityPlayer.class, getAABB(te, 5));
			for (EntityPlayer player : players) {
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
	public static int syncPlayersUsing(TileEntityContainer te, int numUsingPlayers) {
		return syncPlayersUsing(te, numUsingPlayers, te.getPlayerInventory());
	}
	
	/** This will perform a {@link World#notifyBlockOfNeighborChange()} on every adjacent block including the block at x|y|z.*/
	public static void notifyBlocksAround(World world, int x, int y, int z) {
		Block block = world.getBlock(x, y, z);
		world.notifyBlocksOfNeighborChange(x, y, z, block);
		world.notifyBlocksOfNeighborChange(x + 1, y, z, block);
		world.notifyBlocksOfNeighborChange(x - 1, y, z, block);
		world.notifyBlocksOfNeighborChange(x, y + 1, z, block);
		world.notifyBlocksOfNeighborChange(x, y - 1, z, block);
		world.notifyBlocksOfNeighborChange(x, y, z + 1, block);
		world.notifyBlocksOfNeighborChange(x, y, z - 1, block);	
	}
	
	// Misc functions
	
	public static MovingObjectPosition rayTrace(EntityPlayer player, float partialTicks) {
		Attachments.playerLocal.set(player);
		double range = ((player.worldObj.isRemote)
				? Minecraft.getMinecraft().playerController.getBlockReachDistance()
				: ((EntityPlayerMP)player).theItemInWorldManager.getBlockReachDistance());
		Vec3 start = Vec3.createVectorHelper(player.posX, player.posY + 1.62 - player.yOffset, player.posZ);
		Vec3 look = player.getLook(1.0F);
		Vec3 end = start.addVector(look.xCoord * range, look.yCoord * range, look.zCoord * range);
		MovingObjectPosition target = player.worldObj.rayTraceBlocks(start, end);
		Attachments.playerLocal.remove();
		return target;
	}
	
}
