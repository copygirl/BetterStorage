package net.mcft.copy.betterstorage.tile;

import net.mcft.copy.betterstorage.attachment.IHasAttachments;
import net.mcft.copy.betterstorage.tile.entity.TileEntityContainer;
import net.mcft.copy.betterstorage.utils.WorldUtils;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public abstract class TileContainerBetterStorage extends TileBetterStorage {
	
	@Override
	public boolean hasTileEntity(IBlockState state) {
		return true;
	}

	protected TileContainerBetterStorage(Material material) {
		super(material);
		setDefaultState(blockState.getBaseState().withProperty(FACING_PROP, EnumFacing.NORTH));
		isBlockContainer = true;
	}

	@Override
	protected BlockState createBlockState() 
	{
		return new BlockState(this, FACING_PROP);
	}

	@Override
	public boolean onBlockEventReceived(World world, BlockPos pos, IBlockState state, int eventId, int eventPar) {
        TileEntity te = world.getTileEntity(pos);
        return ((te != null) ? te.receiveClientEvent(eventId, eventPar) : false);
	}
	
	// Pass actions to TileEntityContainer
	
	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase player, ItemStack stack) {
		getContainer(world, pos).onBlockPlaced(player, stack);
	}
	
	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player,
	                                EnumFacing side, float hitX, float hitY, float hitZ) {
		return getContainer(world, pos).onBlockActivated(player, side, hitX, hitY, hitZ);
	}
	
	@Override
	public boolean removedByPlayer(World world, BlockPos pos, EntityPlayer player, boolean par3) {
		if (!getContainer(world, pos).onBlockBreak(player)) return false;
		return super.removedByPlayer(world, pos, player, par3);
	}
	
	//TODO (1.8): Needed? Replaced?
	/*
	public void onBlockPreDestroy(World world, int x, int y, int z, int meta) {
		TileEntityContainer container = getContainer(world, x, y, z);
		if (container != null) container.onBlockDestroyed();
	}*/
	
	@Override
	public ItemStack getPickBlock(MovingObjectPosition target, World world, BlockPos pos) {
		TileEntityContainer container = getContainer(world, pos);
		if (container instanceof IHasAttachments) {
			ItemStack pick = ((IHasAttachments)container).getAttachments().pick(target);
			if (pick != null) return pick;
		}
		ItemStack pick = super.getPickBlock(target, world, pos);
		return container.onPickBlock(pick, target);
	}
	
	@Override
	public int getComparatorInputOverride(World world, BlockPos pos) {
		return TileEntityContainer.getContainerComparatorSignalStrength(world, pos);
	}
	
	private TileEntityContainer getContainer(World world, BlockPos pos) {
		return WorldUtils.get(world, pos, TileEntityContainer.class);
	}
	
	@Override
	public IBlockState getStateFromMeta(int meta) {
		EnumFacing enumfacing = EnumFacing.getFront(meta);
		if (enumfacing.getAxis() == EnumFacing.Axis.Y)
			enumfacing = EnumFacing.NORTH;
		return this.getDefaultState().withProperty(FACING_PROP, enumfacing);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return ((EnumFacing) state.getValue(FACING_PROP)).getIndex();
	}

}
