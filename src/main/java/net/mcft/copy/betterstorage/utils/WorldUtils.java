package net.mcft.copy.betterstorage.utils;

import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import net.mcft.copy.betterstorage.attachment.Attachments;
import net.mcft.copy.betterstorage.container.ContainerBetterStorage;
import net.mcft.copy.betterstorage.inventory.InventoryTileEntity;
import net.mcft.copy.betterstorage.tile.entity.TileEntityContainer;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public final class WorldUtils {
	
	private WorldUtils() {  }
	
	@SideOnly(Side.CLIENT)
	public static World getLocalWorld() {
		return Minecraft.getMinecraft().theWorld;
	}
	
	public static AxisAlignedBB getAABB(TileEntity entity, double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
		BlockPos pos = entity.getPos();
		double x = pos.getX();
		double y = pos.getY();
		double z = pos.getZ();
		return AxisAlignedBB.fromBounds(x - minX, y - minY, z - minZ, x + maxX + 1, y + maxY + 1, z + maxZ + 1);
	}
	public static AxisAlignedBB getAABB(TileEntity entity, double radius) {
		return getAABB(entity, radius, radius, radius, radius, radius, radius);
	}
	
	// Item spawning related functions
	
	/** Spawns an ItemStack in the world. */
	public static EntityItem spawnItem(World world, Vec3 pos, ItemStack stack) {
		if ((stack == null) || (stack.stackSize <= 0)) return null;
		EntityItem item = new EntityItem(world, pos.xCoord, pos.yCoord, pos.zCoord, stack);
		world.spawnEntityInWorld(item);
		return item;
	}
	/** Spawns an ItemStack in the world with random motion. */
	public static EntityItem spawnItemWithMotion(World world, Vec3 pos, ItemStack stack) {
		EntityItem item = spawnItem(world, pos, stack);
		if (item != null) {
			item.motionX = RandomUtils.getGaussian() * 0.05F;
			item.motionY = RandomUtils.getGaussian() * 0.05F + 0.2F;
			item.motionZ = RandomUtils.getGaussian() * 0.05F;
		}
		return item;
	}
	
	/** Spawn an ItemStack dropping from a destroyed block. */
	public static EntityItem dropStackFromBlock(World world, BlockPos pos, ItemStack stack) {
		return spawnItemWithMotion(world, MathUtils.fromVec3i(pos)
			.addVector(RandomUtils.getFloat(0.1F, 0.9F), RandomUtils.getFloat(0.1F, 0.9F), RandomUtils.getFloat(0.1F, 0.9F)), stack);
	}
	
	/** Spawn an ItemStack dropping from a destroyed block. */
	public static EntityItem dropStackFromBlock(TileEntity te, ItemStack stack) {
		return dropStackFromBlock(te.getWorld(), te.getPos(), stack);
	}
	
	/** Spawns an ItemStack as if it was dropped from an entity on death. */
	public static EntityItem dropStackFromEntity(Entity entity, ItemStack stack, float speed) {
		EntityPlayer player = ((entity instanceof EntityPlayer) ? (EntityPlayer)entity : null);
		EntityItem item;
		if (player == null) {
			double y = entity.posY + entity.getEyeHeight() - 0.3;
			item = spawnItem(entity.worldObj, new Vec3(entity.posX, y, entity.posZ), stack);
			if (item == null) return null;
			item.setPickupDelay(40);
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
	public static <T> boolean is(IBlockAccess world, BlockPos pos, Class<T> tileClass) {
		return tileClass.isInstance(world.getTileEntity(pos));
	}
	/** Returns the TileEntity at the position if it's an instance of tileClass, null if not. */
	public static <T> T get(IBlockAccess world, BlockPos pos, Class<T> tileClass) {
		TileEntity t = world.getTileEntity(pos);
		return (tileClass.isInstance(t) ? (T)t : null);
	}
	
	/** Returns if the TileEntity can be used by this player. */
	public static boolean isTileEntityUsableByPlayer(TileEntity entity, EntityPlayer player) {
		return (entity.getWorld().getTileEntity(entity.getPos()) == entity &&
		        player.getDistanceSqToCenter(entity.getPos()) <= 64.0);
	}
	
	/** Counts and returns the number of players who're accessing a tile entity. */
	public static int syncPlayersUsing(TileEntity te, int playersUsing, IInventory playerInventory) {
		if (!te.getWorld().isRemote && (playersUsing != 0)) {
			playersUsing = 0;
			List<EntityPlayer> players = te.getWorld().getEntitiesWithinAABB(EntityPlayer.class, getAABB(te, 5));
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
	public static void notifyBlocksAround(World world, BlockPos pos) {
		Block block = world.getBlockState(pos).getBlock();
		world.notifyNeighborsOfStateChange(pos, block);
		world.notifyBlockOfStateChange(pos, block);
	}
	
	public static void playSound(World world, Vec3 pos, String sound, float volume, float pitch, boolean distanceDelay) {
		world.playSound(pos.xCoord, pos.yCoord, pos.zCoord, "random.break", 0.5F, 2.5F, false);
	}
	
	/** Copys the property array of an {@link IBlockState} to a new one **/
	public static IBlockState cloneBlockState(IBlockState newState, IBlockState oldState) {
		for (Entry entry : (Set<Entry>) oldState.getProperties().entrySet())
			newState = newState.withProperty((IProperty) entry.getKey(), (Comparable) entry.getValue());
		return newState;
	}
	
	// Misc functions
	
	public static MovingObjectPosition rayTrace(EntityPlayer player, float partialTicks) {
		Attachments.playerLocal.set(player);
		double range = ((player.worldObj.isRemote)
				? Minecraft.getMinecraft().playerController.getBlockReachDistance()
				: ((EntityPlayerMP)player).theItemInWorldManager.getBlockReachDistance());
		Vec3 start = getPositionVector(player, partialTicks);
		Vec3 look = player.getLook(1.0F);
		Vec3 end = start.addVector(look.xCoord * range, look.yCoord * range, look.zCoord * range);
		MovingObjectPosition target = player.worldObj.rayTraceBlocks(start, end);
		Attachments.playerLocal.remove();
		return target;
	}
	
	public static Vec3 getPositionVector(EntityPlayer player, float partialTicks) {
		double d0 = player.prevPosX + (player.posX - player.prevPosX) * partialTicks;
		double d1 = player.prevPosY + (player.posY - player.prevPosY) * partialTicks + player.getEyeHeight();
		double d2 = player.prevPosZ + (player.posZ - player.prevPosZ) * partialTicks;
		
		if(!(player instanceof EntityPlayerMP))
			d1 -= player.getDefaultEyeHeight();
		
		return new Vec3(d0, d1, d2);
	}
	
}
