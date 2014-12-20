package net.mcft.copy.betterstorage.tile;

import static net.minecraft.util.EnumFacing.DOWN;

import java.util.List;
import java.util.Random;

import net.mcft.copy.betterstorage.api.BetterStorageEnchantment;
import net.mcft.copy.betterstorage.attachment.Attachments;
import net.mcft.copy.betterstorage.attachment.EnumAttachmentInteraction;
import net.mcft.copy.betterstorage.attachment.IHasAttachments;
import net.mcft.copy.betterstorage.tile.entity.TileEntityLockableDoor;
import net.mcft.copy.betterstorage.utils.WorldUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import com.google.common.collect.Lists;

public class TileLockableDoor extends BlockDoor {

	public TileLockableDoor() {
		super(Material.iron);
		
		setCreativeTab(null);
		setHardness(8.0F);
		setResistance(20.0F);
		setStepSound(soundTypeAnvil);
		setHarvestLevel("axe", 2);
	}

	@Override
	public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
		List<ItemStack> drops = Lists.newArrayList();
		TileEntityLockableDoor te = WorldUtils.get(world, pos, TileEntityLockableDoor.class);
		if (te == null) te = WorldUtils.get(world, pos.offsetDown(), TileEntityLockableDoor.class);
		ItemStack lock = te.getLock();
		if(lock != null) drops.add(lock);
		drops.add(new ItemStack(Items.iron_door));
		return drops;
	}

	@Override
	public void onNeighborBlockChange(World worldIn, BlockPos pos, IBlockState state, Block neighborBlock) {
		if (state.getValue(HALF_PROP) == BlockDoor.EnumDoorHalf.UPPER) {
			pos = pos.offsetDown();
			IBlockState lowerState = worldIn.getBlockState(pos);

			if (lowerState.getBlock() != this)
				worldIn.setBlockToAir(pos);
			else if (neighborBlock != this)
				onNeighborBlockChange(worldIn, pos, lowerState, neighborBlock);
		} else {
			boolean drop = false;
			pos = pos.offsetUp();
			IBlockState upperState = worldIn.getBlockState(pos);

			if (upperState.getBlock() != this) {
				worldIn.setBlockToAir(pos);
				drop = true;
			}

			if (!World.doesBlockHaveSolidTopSurface(worldIn, pos.offsetDown())) {
				worldIn.setBlockToAir(pos);
				drop = true;

				if (upperState.getBlock() == this)
					worldIn.setBlockToAir(pos);
			}

			if (drop && !worldIn.isRemote)
				dropBlockAsItem(worldIn, pos, state, 0);
		}
	}

	@Override
	 public float getBlockHardness(World world, BlockPos pos){
		EnumFacing side = (EnumFacing) world.getBlockState(pos).getValue(FACING_PROP);
		if (!side.equals(DOWN)) pos = pos.offsetDown();
		TileEntityLockableDoor lockable = WorldUtils.get(world, pos, TileEntityLockableDoor.class);
		if ((lockable != null) && (lockable.getLock() != null)) return -1;
		else return super.getBlockHardness(world, pos);
	}
	
	@Override
	public float getExplosionResistance(World world, BlockPos pos, Entity exploder, Explosion explosion){
		EnumFacing side = (EnumFacing) world.getBlockState(pos).getValue(FACING_PROP);
		if (!side.equals(DOWN)) pos = pos.offsetDown();
		float modifier = 1.0F;
		TileEntityLockableDoor lockable = WorldUtils.get(world, pos, TileEntityLockableDoor.class);
		if (lockable != null) {
			int persistance = BetterStorageEnchantment.getLevel(lockable.getLock(), "persistance");
			if (persistance > 0) modifier += Math.pow(2, persistance);
		}
		return super.getExplosionResistance(exploder) * modifier;
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumFacing side, float hitX, float hitY, float hitZ) {
		if (!side.equals(DOWN)) pos = pos.offsetDown();
		TileEntityLockableDoor te = WorldUtils.get(world, pos, TileEntityLockableDoor.class);
		return te.onBlockActivated(world, pos, player, side, hitX, hitY, hitZ);
	}
	
	@Override
	public void onBlockClicked(World world, BlockPos pos, EntityPlayer player) {
		EnumFacing side = (EnumFacing) world.getBlockState(pos).getValue(FACING_PROP);
		if (!side.equals(DOWN)) pos = pos.offsetDown();
		Attachments attachments = WorldUtils.get(world, pos, IHasAttachments.class).getAttachments();
		boolean abort = attachments.interact(WorldUtils.rayTrace(player, 1.0F), player, EnumAttachmentInteraction.attack);
	}
	
	@Override
	public MovingObjectPosition collisionRayTrace(World world, BlockPos pos, Vec3 start, Vec3 end) {
		EnumFacing side = (EnumFacing) world.getBlockState(pos).getValue(FACING_PROP);
		pos = pos.offsetDown((side != DOWN ? 1 : 0));
		IHasAttachments te = WorldUtils.get(world, pos, IHasAttachments.class);
		if(te == null) return super.collisionRayTrace(world, pos, start, end);
		MovingObjectPosition movPos = te.getAttachments().rayTrace(world, pos, start, end);
		return movPos != null ? movPos : super.collisionRayTrace(world, pos, start, end);
	}
	
	@Override
	public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
		state = state.withProperty(BlockDoor.POWERED_PROP, true);
		worldIn.setBlockState(pos, state);
		worldIn.scheduleUpdate(pos, this, 10);
	}

	@Override
	public boolean canProvidePower() {
		return true;
	}
	
	@Override
	public int isProvidingWeakPower(IBlockAccess worldIn, BlockPos pos, IBlockState state, EnumFacing side) {
		return isProvidingStrongPower(worldIn, pos, state, side);
	}

	@Override
	public int isProvidingStrongPower(IBlockAccess worldIn, BlockPos pos, IBlockState state, EnumFacing side) {
		state = getActualState(state, worldIn, pos);
		return (Boolean) state.getValue(POWERED_PROP) ? 15 : 0;
	}

	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {
		EnumFacing enumfacing = (EnumFacing)state.getValue(FACING_PROP);
		return ((enumfacing.equals(DOWN)) ? new TileEntityLockableDoor() : null);
	}

	@Override
	public boolean hasTileEntity(IBlockState state) { return true; }
}
