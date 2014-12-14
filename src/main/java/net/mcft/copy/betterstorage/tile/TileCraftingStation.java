package net.mcft.copy.betterstorage.tile;

<<<<<<< HEAD
import net.mcft.copy.betterstorage.BetterStorage;
import net.mcft.copy.betterstorage.config.GlobalConfig;
=======
>>>>>>> refs/remotes/Thog92/1.8
import net.mcft.copy.betterstorage.tile.entity.TileEntityCraftingStation;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class TileCraftingStation extends TileContainerBetterStorage {
	
<<<<<<< HEAD
	/*private IIcon topIcon;
	private IIcon bottomIconDisabled;
	private IIcon bottomIconEnabled;*/
=======
//	private IIcon topIcon;
//	private IIcon bottomIconDisabled;
//	private IIcon bottomIconEnabled;
>>>>>>> refs/remotes/Thog92/1.8
	
	public TileCraftingStation() {
		super(Material.iron);
		
		setHardness(1.5f);
		setStepSound(soundTypeStone);
	}
	
<<<<<<< HEAD
	/*@Override
=======
	/*
	@Override
>>>>>>> refs/remotes/Thog92/1.8
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister iconRegister) {
		blockIcon = iconRegister.registerIcon(Constants.modId + ":" + getTileName());
		topIcon = iconRegister.registerIcon(Constants.modId + ":" + getTileName() + "_top");
		bottomIconDisabled = iconRegister.registerIcon(Constants.modId + ":" + getTileName() + "_bottom_0");
		bottomIconEnabled = iconRegister.registerIcon(Constants.modId + ":" + getTileName() + "_bottom_1");
	}*/
	
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
