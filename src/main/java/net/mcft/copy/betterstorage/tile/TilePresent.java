package net.mcft.copy.betterstorage.tile;

import java.util.Random;

import net.mcft.copy.betterstorage.item.tile.ItemPresent;
import net.mcft.copy.betterstorage.proxy.ClientProxy;
import net.mcft.copy.betterstorage.tile.entity.TileEntityPresent;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TilePresent extends TileContainerBetterStorage {
	
	public TilePresent() {
		super(Material.cloth);
		setCreativeTab(null);
		
		setHardness(0.75f);
		setStepSound(soundTypeCloth);
		setBlockBounds(0.0625F, 0.0F, 0.0625F, 0.9375F, 0.875F, 0.9375F);
	}
	
	/*
	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister iconRegister) {  }
	
	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta) {
		return Blocks.wool.getIcon(side, meta);
	}
	*/
	
	@Override
	public Class<? extends ItemBlock> getItemClass() { return ItemPresent.class; }
	
	@Override
	public boolean isOpaqueCube() { return false; }
	
	/*
	@Override
	public boolean renderAsNormalBlock() { return false; }
	*/
	
	@Override
	@SideOnly(Side.CLIENT)
	public int getRenderType() { return ClientProxy.presentRenderId; }
	
	@Override
	public int quantityDropped(Random rand) { return 0; }
	
	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {
		return new TileEntityPresent();
	}
	
}
