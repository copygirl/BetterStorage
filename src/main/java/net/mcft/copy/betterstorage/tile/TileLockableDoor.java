package net.mcft.copy.betterstorage.tile;

import java.util.Random;

import net.mcft.copy.betterstorage.api.BetterStorageEnchantment;
import net.mcft.copy.betterstorage.attachment.Attachments;
import net.mcft.copy.betterstorage.attachment.EnumAttachmentInteraction;
import net.mcft.copy.betterstorage.attachment.IHasAttachments;
import net.mcft.copy.betterstorage.misc.Constants;
import net.mcft.copy.betterstorage.misc.SetBlockFlag;
import net.mcft.copy.betterstorage.proxy.ClientProxy;
import net.mcft.copy.betterstorage.tile.entity.TileEntityLockableDoor;
import net.mcft.copy.betterstorage.utils.WorldUtils;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileLockableDoor extends TileBetterStorage {

	public TileLockableDoor() {
		super(Material.wood);
		
		setHardness(8.0F);
		setResistance(20.0F);
		setStepSound(soundTypeWood);	
		setBlockBounds(0F, 0F, 0F, 3 / 16F, 2F, 1F);
		setHarvestLevel("axe", 2);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public String getItemIconName() { return Constants.modId + ":lockableDoor"; }
	
	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
		int metadata = world.getBlockMetadata(x, y, z);
		float offset = metadata == 0 ? 0F : -1F;
		TileEntityLockableDoor te = WorldUtils.get(world, x, y + (int)offset, z, TileEntityLockableDoor.class);
		
		if (te == null) {
			setBlockBounds(0F, 0F + offset, 0F, 3 / 16F, 2F + offset, 1F);
			return;
		}
		
		switch (te.orientation) {
		case WEST:
			if (te.isOpen) setBlockBounds(0F, 0F + offset, 1 / 16F, 1F, 2F + offset, 2 / 16F);
			else setBlockBounds(1 / 16F, 0F + offset, 0F, 2 / 16F, 2F + offset, 1F);
			break;
		case EAST:
			if (te.isOpen) setBlockBounds(0F, 0F + offset, 14 / 16F, 1F, 2F + offset, 15 / 16F);
			else setBlockBounds(14 / 16F, 0F + offset, 0F, 15 / 16F, 2F + offset, 1F);
			break;
		case SOUTH:
			if (te.isOpen) setBlockBounds(1 / 16F, 0F + offset, 0F, 2 / 16F, 2F + offset, 1F);
			else setBlockBounds(0F, 0F + offset, 14 / 16F, 1F, 2F + offset, 15 / 16F);
			break;
		default:
			if (te.isOpen) setBlockBounds(14 / 16F, 0F + offset, 0F, 15 / 16F, 2F + offset, 1F);
			else setBlockBounds(0F, 0F + offset, 1 / 16F, 1F, 2F + offset, 2 / 16F);
			break;
		}		
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
		setBlockBoundsBasedOnState(world, x, y, z);
		return super.getCollisionBoundingBoxFromPool(world, x, y, z);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister iconRegister) {
		blockIcon = iconRegister.registerIcon("planks_oak");
	}
	
	@Override
	public float getBlockHardness(World world, int x, int y, int z) {
		if (world.getBlockMetadata(x, y, z) > 0) y -= 1;
		TileEntityLockableDoor lockable = WorldUtils.get(world, x, y, z, TileEntityLockableDoor.class);
		if ((lockable != null) && (lockable.getLock() != null)) return -1;
		else return super.getBlockHardness(world, x, y, z);
	}
	
	@Override
	public float getExplosionResistance(Entity entity, World world, int x, int y, int z, double explosionX, double explosionY, double explosionZ) {
		if (world.getBlockMetadata(x, y, z) > 0) y -= 1;
		float modifier = 1.0F;
		TileEntityLockableDoor lockable = WorldUtils.get(world, x, y, z, TileEntityLockableDoor.class);
		if (lockable != null) {
			int persistance = BetterStorageEnchantment.getLevel(lockable.getLock(), "persistance");
			if (persistance > 0) modifier += Math.pow(2, persistance);
		}
		return super.getExplosionResistance(entity) * modifier;
	}
	
	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase player, ItemStack stack) {
		super.onBlockPlacedBy(world, x, y, z, player, stack);
		TileEntityLockableDoor te = WorldUtils.get(world, x, y, z, TileEntityLockableDoor.class);
		te.onBlockPlacedBy(world, x, y, z, player, stack);
		world.setBlock(x, y + 1, z, this, 1, SetBlockFlag.DEFAULT);
	}
	
	@Override
	public boolean onBlockEventReceived(World world, int x, int y, int z, int eventId, int eventPar) {
		TileEntity te = world.getTileEntity(x, y, z);
        return ((te != null) ? te.receiveClientEvent(eventId, eventPar) : false);
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
		//TODO It will place a block in front of the bottom block when shift clicking the upper block.
		if (world.getBlockMetadata(x, y, z) > 0) y -= 1;
		TileEntityLockableDoor te = WorldUtils.get(world, x, y, z, TileEntityLockableDoor.class);
		return te.onBlockActivated(world, x, y, z, player, side, hitX, hitY, hitZ);
	}
	
	@Override
	public void onBlockClicked(World world, int x, int y, int z, EntityPlayer player) {
		if (world.getBlockMetadata(x, y, z) > 0) y -= 1;
		Attachments attachments = WorldUtils.get(world, x, y, z, IHasAttachments.class).getAttachments();
		boolean abort = attachments.interact(WorldUtils.rayTrace(player, 1.0F), player, EnumAttachmentInteraction.attack);
	}
	
	@Override
	public MovingObjectPosition collisionRayTrace(World world, int x, int y, int z, Vec3 start, Vec3 end) {
		int metadata = world.getBlockMetadata(x, y, z);
		if (metadata > 0) y -= 1;
		IHasAttachments te = WorldUtils.get(world, x, y, z, IHasAttachments.class);
		return te != null ? te.getAttachments().rayTrace(world, x, y, z, start, end) : super.collisionRayTrace(world, x, y, z, start, end);
	}
	
	@Override
	public boolean canPlaceBlockAt(World world, int x, int y, int z) {
		Block block = world.getBlock(x, y - 1, z);
		return !world.isAirBlock(x, y - 1, z) && block != this;
	}

	@Override
	public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z) {
		if (world.getBlockMetadata(x, y, z) > 0) y -= 1;
		return super.getPickBlock(target, world, x, y, z);
	}
	
	@Override
	public boolean removedByPlayer(World world, EntityPlayer player, int x, int y, int z) {
		return world.setBlockToAir(x, y, z);
	}
	
	@Override
	public void breakBlock(World world, int x, int y, int z, Block block, int meta) {
		if (meta > 0) return;
		super.breakBlock(world, x, y, z, block, meta);
	}
	
	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block block) {
		int metadata = world.getBlockMetadata(x, y, z);
		int targetY = y + ((metadata == 0) ? 1 : -1);
		int targetMeta = ((metadata == 0) ? 1 : 0);
		if (world.getBlock(x, y - 1, z) == Blocks.air && metadata == 0) world.setBlockToAir(x, y, z);
		if ((world.getBlock(x, targetY, z) == this) && (world.getBlockMetadata(x, targetY, z) == targetMeta)) return;
		world.setBlockToAir(x, y, z);
		if (metadata == 0) dropBlockAsItem(world, x, y, z, metadata, 0);
	}
	
	@Override
	public void onBlockPreDestroy(World world, int x, int y, int z, int meta) {
		if(meta == 0) {
			TileEntityLockableDoor te = WorldUtils.get(world, x, y, z, TileEntityLockableDoor.class);
			WorldUtils.dropStackFromBlock(te, te.getLock());
			te.setLock(null);
		}
		super.onBlockPreDestroy(world, x, y, z, meta);
	}

	@Override
	public boolean isOpaqueCube() { return false; }
	@Override
	public boolean renderAsNormalBlock() { return false; }
	
	@Override
	public int quantityDropped(int meta, int fortune, Random random) {
		return ((meta == 0) ? 1 : 0);
	} 
	
	@Override
	public boolean canProvidePower() { return true; }
	
	@Override
	public int isProvidingWeakPower(IBlockAccess world, int x, int y, int z, int side) {
		if (world.getBlockMetadata(x, y, z) > 0) y -= 1;
		return (WorldUtils.get(world, x, y, z, TileEntityLockableDoor.class).isPowered() ? 15 : 0);
	}
	@Override
	public int isProvidingStrongPower(IBlockAccess world, int x, int y, int z, int side) {
		return isProvidingWeakPower(world, x, y, z, side);
	}
	
	@Override
	public void updateTick(World world, int x, int y, int z, Random random) {
		if(world.getBlockMetadata(x, y, z) != 0) return;
		WorldUtils.get(world, x, y, z, TileEntityLockableDoor.class).setPowered(false);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public int getRenderType() { return ClientProxy.lockableDoorRenderId; }
	
	@Override
	public TileEntity createTileEntity(World world, int metadata) {
		return ((metadata == 0) ? new TileEntityLockableDoor() : null);
	}

	@Override
	public boolean hasTileEntity(int metadata) { return true; }
	
}
