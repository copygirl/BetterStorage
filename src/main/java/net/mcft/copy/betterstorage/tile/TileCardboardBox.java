package net.mcft.copy.betterstorage.tile;

import java.util.Random;

import net.mcft.copy.betterstorage.item.tile.ItemCardboardBox;
import net.mcft.copy.betterstorage.tile.entity.TileEntityCardboardBox;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class TileCardboardBox extends TileContainerBetterStorage {
	
	//private IIcon sideIcon;
	
	public TileCardboardBox() {
		super(Material.wood);
		
		setHardness(0.8f);
		setStepSound(soundTypeWood);
		setBlockBounds(0.0625F, 0.0F, 0.0625F, 0.9375F, 0.875F, 0.9375F);
	}
	
	@Override
	public Class<? extends ItemBlock> getItemClass() { return ItemCardboardBox.class; }
	
	/*
	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister iconRegister) {
		blockIcon = iconRegister.registerIcon(Constants.modId + ":" + getTileName());
		sideIcon = iconRegister.registerIcon(Constants.modId + ":" + getTileName() + "_side");
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta) {
		return ((side < 2) ? blockIcon : sideIcon);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public int colorMultiplier(IBlockAccess world, int x, int y, int z) {
		TileEntityCardboardBox box = WorldUtils.get(world, x, y, z, TileEntityCardboardBox.class);
		return (((box != null) && (box.color >= 0)) ? box.color : 0x705030);
	}
	

	@Override
	public boolean renderAsNormalBlock() { return false; }
	*/
	
	@Override
	public boolean isOpaqueCube() { return false; }
	
	@Override
	public int quantityDropped(Random rand) { return 0; }
	
	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {
		return new TileEntityCardboardBox();
	}
	
}
