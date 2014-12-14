package net.mcft.copy.betterstorage.tile.stand;

import java.util.Random;

import net.mcft.copy.betterstorage.misc.SetBlockFlag;
import net.mcft.copy.betterstorage.proxy.ClientProxy;
import net.mcft.copy.betterstorage.tile.TileContainerBetterStorage;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TileArmorStand extends TileContainerBetterStorage {
	
	public TileArmorStand() {
		super(Material.rock);
		
		setHardness(2.5f);
		setBlockBounds(2 / 16.0F, 0, 2 / 16.0F, 14 / 16.0F, 2, 14 / 16.0F);
		
		setHarvestLevel("pickaxe", 0);
	}
	
	@Override
	public Class<? extends ItemBlock> getItemClass() { return ItemArmorStand.class; }
	
	
	/*@Override
	public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
		int metadata = world.getBlockMetadata(x, y, z);
		if (metadata == 0) setBlockBounds(2 / 16.0F, 0, 2 / 16.0F, 14 / 16.0F, 2, 14 / 16.0F);
		else setBlockBounds(2 / 16.0F, -1, 2 / 16.0F, 14 / 16.0F, 1, 14 / 16.0F);
	}*/
	
	@Override
	public boolean isOpaqueCube() { return false; }
	
	
	
	@Override
	@SideOnly(Side.CLIENT)
	public int getRenderType() { return ClientProxy.armorStandRenderId; }
	
	@Override
	public int quantityDropped(IBlockState state, int fortune, Random random) {
		EnumFacing side = (EnumFacing) state.getValue(FACING_PROP);
		return ((side.equals(EnumFacing.DOWN)) ? 1 : 0);
	}
	
	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		super.onBlockPlacedBy(world, pos, state, placer, stack);
		world.setBlockState(pos.add(0, 1, 0), state, SetBlockFlag.DEFAULT);
	}
	
	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumFacing side, float hitX, float hitY, float hitZ){
		if (world.isRemote) return true;
		if (((EnumFacing) world.getBlockState(pos).getValue(FACING_PROP)).getIndex() > 0) { pos = pos.offsetDown(); hitY += 1; }
		return super.onBlockActivated(world, pos, state, playerIn, side, hitX, hitY, hitZ);
	}
	@Override
	public ItemStack getPickBlock(MovingObjectPosition target, World world, BlockPos pos) {
		if (((EnumFacing) world.getBlockState(pos).getValue(FACING_PROP)).getIndex() > 0) { pos = pos.offsetDown(); }
		return super.getPickBlock(target, world, pos);
	}
	
	@Override
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state){
		if (((EnumFacing) state.getValue(FACING_PROP)).getIndex() > 0) return;
		super.breakBlock(worldIn, pos, state);
	}
	
	@Override
	public void onNeighborBlockChange(World world, BlockPos pos, IBlockState state, Block neighborBlock) {
		int metadata = ((EnumFacing) state.getValue(FACING_PROP)).getIndex();
		int yModifier = ((metadata == 0) ? 1 : -1);
		int targetMeta = ((metadata == 0) ? 1 : 0);
		if ((world.getBlockState(pos.offsetUp(yModifier)).getBlock() == this) &&
		    (((EnumFacing) world.getBlockState(pos.offsetUp(yModifier)).getValue(FACING_PROP)).getIndex()) == targetMeta) return;
		world.setBlockToAir(pos);
		if (metadata == 0)
			dropBlockAsItem(world, pos, state, 0);
	}
	
	@Override
	public TileEntity createTileEntity(World world, IBlockState state)
	{
		return new TileEntityArmorStand();
	}
	
	@Override
	public boolean hasComparatorInputOverride() { return true; }
	
}
