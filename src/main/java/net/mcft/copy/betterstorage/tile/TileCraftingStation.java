package net.mcft.copy.betterstorage.tile;

import net.mcft.copy.betterstorage.tile.entity.TileEntityCraftingStation;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class TileCraftingStation extends TileContainerBetterStorage {
	
	/*private IIcon topIcon;
	private IIcon bottomIconDisabled;
	private IIcon bottomIconEnabled;*/
	
	public TileCraftingStation() {
		super(Material.iron);
		
		setHardness(1.5f);
		setStepSound(soundTypeStone);
	}
	
	/*@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister iconRegister) {
		blockIcon = iconRegister.registerIcon(Constants.modId + ":" + getTileName());
		topIcon = iconRegister.registerIcon(Constants.modId + ":" + getTileName() + "_top");
		bottomIconDisabled = iconRegister.registerIcon(Constants.modId + ":" + getTileName() + "_bottom_0");
		bottomIconEnabled = iconRegister.registerIcon(Constants.modId + ":" + getTileName() + "_bottom_1");
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta) {
		return ((side == 0)
				? (BetterStorage.globalConfig.getBoolean(GlobalConfig.enableStationAutoCrafting)
						? bottomIconEnabled : bottomIconDisabled)
				: ((side == 1) ? topIcon : blockIcon));
	}
	*/
	
	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {
		return new TileEntityCraftingStation();
	}
	
}
