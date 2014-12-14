package net.mcft.copy.betterstorage.tile;

import net.mcft.copy.betterstorage.attachment.IHasAttachments;
import net.mcft.copy.betterstorage.tile.entity.TileEntityContainer;
import net.mcft.copy.betterstorage.utils.WorldUtils;
import net.minecraft.block.material.Material;
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
<<<<<<< HEAD
	public boolean hasTileEntity(IBlockState state) {
=======
	public boolean hasTileEntity(IBlockState state)
	{
>>>>>>> refs/remotes/Thog92/1.8
		return true;
	}

	protected TileContainerBetterStorage(Material material) {
		super(material);
		isBlockContainer = true;
	}
	
	@Override
<<<<<<< HEAD
	public boolean onBlockEventReceived(World world, BlockPos pos, IBlockState state, int eventId, int eventPar) {
        TileEntity te = world.getTileEntity(pos);
        return ((te != null) ? te.receiveClientEvent(eventId, eventPar) : false);
=======
	public boolean onBlockEventReceived(World world, BlockPos pos, IBlockState state, int eventID, int eventParam)
	{
        TileEntity te = world.getTileEntity(pos);
        return ((te != null) ? te.receiveClientEvent(eventID, eventParam) : false);
>>>>>>> refs/remotes/Thog92/1.8
	}
	
	// Pass actions to TileEntityContainer
	
	@Override
<<<<<<< HEAD
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase player, ItemStack stack) {
		getContainer(world, pos).onBlockPlaced(player, stack);
=======
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		getContainer(world, pos).onBlockPlaced(placer, stack);
>>>>>>> refs/remotes/Thog92/1.8
	}
	
	@Override
<<<<<<< HEAD
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player,
	                                EnumFacing side, float hitX, float hitY, float hitZ) {
		return getContainer(world, pos).onBlockActivated(player, side, hitX, hitY, hitZ);
=======
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumFacing side, float hitX, float hitY, float hitZ) {
		return getContainer(world, pos).getBlockType().onBlockActivated(world, pos, state, playerIn, side, hitX, hitY, hitZ);
>>>>>>> refs/remotes/Thog92/1.8
	}
	
	@Override
<<<<<<< HEAD
	public boolean removedByPlayer(World world, BlockPos pos, EntityPlayer player, boolean par3) {
		if (!getContainer(world, pos).onBlockBreak(player)) return false;
		return super.removedByPlayer(world, pos, player, par3);
=======
	public boolean removedByPlayer(World world, BlockPos pos, EntityPlayer player, boolean willHarvest)
	{
		if (!getContainer(world, pos).onBlockBreak(player)) return false;
		return super.removedByPlayer(world, pos, player, willHarvest);
>>>>>>> refs/remotes/Thog92/1.8
	}
<<<<<<< HEAD
	
	//TODO (1.8): Needed? Replaced?
	/*
=======

//  Seem don't exist anymore
//	@Override
//	public void onBlockPreDestroy(World world, int x, int y, int z, int meta) {
//		TileEntityContainer container = getContainer(world, x, y, z);
//		if (container != null) container.onBlockDestroyed();
//	}
//	
>>>>>>> refs/remotes/Thog92/1.8
	@Override
<<<<<<< HEAD
	public void onBlockPreDestroy(World world, int x, int y, int z, int meta) {
		TileEntityContainer container = getContainer(world, x, y, z);
		if (container != null) container.onBlockDestroyed();
	}*/
	
	@Override
	public ItemStack getPickBlock(MovingObjectPosition target, World world, BlockPos pos) {
=======
	public ItemStack getPickBlock(MovingObjectPosition target, World world, BlockPos pos)
	{
>>>>>>> refs/remotes/Thog92/1.8
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
	
}
