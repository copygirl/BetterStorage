package net.mcft.copy.betterstorage.tile;

import java.util.Random;

import net.mcft.copy.betterstorage.api.BetterStorageEnchantment;
import net.mcft.copy.betterstorage.attachment.Attachments;
import net.mcft.copy.betterstorage.attachment.EnumAttachmentInteraction;
import net.mcft.copy.betterstorage.attachment.IHasAttachments;
import net.mcft.copy.betterstorage.proxy.ClientProxy;
import net.mcft.copy.betterstorage.tile.entity.TileEntityLockableDoor;
import net.mcft.copy.betterstorage.utils.WorldUtils;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.IconFlipped;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileLockableDoor extends TileBetterStorage {

	private IIcon iconUpper;
	private IIcon iconLower;
	private IIcon iconUpperFlipped;
	private IIcon iconLowerFlipped;
	
	public TileLockableDoor() {
		super(Material.wood);
		
		setCreativeTab(null);
		setHardness(8.0F);
		setResistance(20.0F);
		setStepSound(soundTypeWood);	
		setHarvestLevel("axe", 2);
	}
	
	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
		int metadata = world.getBlockMetadata(x, y, z);
		float offset = metadata == 0 ? 0F : -1F;
		TileEntityLockableDoor te = WorldUtils.get(world, x, y + (int)offset, z, TileEntityLockableDoor.class);
		
		if (te == null) return;
		
		switch (te.orientation) {
		case WEST:
			if (te.isOpen) setBlockBounds(0F, 0F, 0.005F / 16F, 1F, 1F, 2.995F / 16F);
			else setBlockBounds(0.005F / 16F, 0F, 0F, 2.995F / 16F, 1F, 1F);
			break;
		case EAST:
			if (te.isOpen) setBlockBounds(0F, 0F, 13.005F / 16F, 1F, 1F, 15.995F / 16F);
			else setBlockBounds(13.005F / 16F, 0F, 0F, 15.995F / 16F, 1F, 1F);
			break;
		case SOUTH:
			if (te.isOpen) setBlockBounds(0.005F / 16F, 0F, 0F, 2.995F / 16F, 1F, 1F);
			else setBlockBounds(0F, 0F, 13.005F / 16F, 1F, 1F, 15.995F / 16F);
			break;
		default:
			if (te.isOpen) setBlockBounds(13.005F / 16F, 0F, 0F, 15.995F / 16F, 1F, 1F);
			else setBlockBounds(0F, 0F, 0.005F / 16F, 1F, 1F, 2.995F / 16F);
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
		iconUpper = iconRegister.registerIcon("door_iron_upper");
		iconLower = iconRegister.registerIcon("door_iron_lower");
		iconUpperFlipped = new IconFlipped(iconUpper, true, false);
		iconLowerFlipped = new IconFlipped(iconLower, true, false);
		blockIcon = iconUpper;
	}

	@Override
	public IIcon getIcon(IBlockAccess world, int x, int y, int z, int face) {
		int meta = world.getBlockMetadata(x, y, z);
		if (meta > 0) y -= 1;
		TileEntityLockableDoor lockable = WorldUtils.get(world, x, y, z, TileEntityLockableDoor.class);
		
		boolean flip = false;
		IIcon icon = iconUpper;
		
		if(meta == 0 || face == 1) {
			icon = iconLower;
		}
		
		switch(lockable.orientation) {
		case WEST: 
			if(face == 3 && !lockable.isOpen) flip = true;
			else if(face == 2 && lockable.isOpen) flip = true;
			break;
		case EAST:
			if(face == 4 && !lockable.isOpen) flip = true;
			else if(face == 3 && lockable.isOpen) flip = true;
			break;
		case SOUTH:
			if(face == 2 && !lockable.isOpen) flip = true;
			else if(face == 4 && lockable.isOpen) flip = true;
			break;
		default: 
			if(face == 3 && !lockable.isOpen) flip = true;
			else if(face == 5 && lockable.isOpen) flip = true;
			break;
		}

		icon = flip ? (icon == iconLower ? iconLowerFlipped : iconUpperFlipped) : icon;
		return icon;
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
		IHasAttachments te = WorldUtils.get(world, x, y - (metadata > 0 ? 1 : 0), z, IHasAttachments.class);
		if(te == null) return super.collisionRayTrace(world, x, y, z, start, end);
		MovingObjectPosition pos = te.getAttachments().rayTrace(world, x, y - (metadata > 0 ? 1 : 0), z, start, end);
		return pos != null ? pos : super.collisionRayTrace(world, x, y, z, start, end);
	}

	@Override
	public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z) {
		return new ItemStack(Items.iron_door);
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
		int targetMeta = ((metadata == 0) ? 8 : 0);
		if (world.getBlock(x, y - 1, z) == Blocks.air && metadata == 0) world.setBlockToAir(x, y, z);
		if ((world.getBlock(x, targetY, z) == this) && (world.getBlockMetadata(x, targetY, z) == targetMeta)) return;
		world.setBlockToAir(x, y, z);
		if (metadata == 0) WorldUtils.spawnItem(world, x, y, z, new ItemStack(Items.iron_door));
	}
	
	@Override
	public void onBlockPreDestroy(World world, int x, int y, int z, int meta) {
		if(meta == 0) {
			TileEntityLockableDoor te = WorldUtils.get(world, x, y, z, TileEntityLockableDoor.class);
			WorldUtils.dropStackFromBlock(te, te.getLock());
			te.setLockWithUpdate(null);
		}
		super.onBlockPreDestroy(world, x, y, z, meta);
	}

	@Override
	public boolean isOpaqueCube() { return false; }
	@Override
	public boolean renderAsNormalBlock() { return false; }

	@Override
	public int getRenderType() {
		return ClientProxy.lockableDoorRenderId;
	}

	@Override
	public int quantityDropped(int meta, int fortune, Random random) {
		return ((meta == 0) ? 1 : 0);
	} 
	
	@Override
	public boolean canProvidePower() { return true; }
	
	@Override
	public int isProvidingWeakPower(IBlockAccess world, int x, int y, int z, int side) {
		if (world.getBlockMetadata(x, y, z) > 0) y -= 1;
		TileEntityLockableDoor te = WorldUtils.get(world, x, y, z, TileEntityLockableDoor.class);
		return te == null ? 0 : (te.isPowered() ? 15 : 0);
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
	public TileEntity createTileEntity(World world, int metadata) {
		return ((metadata == 0) ? new TileEntityLockableDoor() : null);
	}

	@Override
	public boolean hasTileEntity(int metadata) { return true; }
	
}
