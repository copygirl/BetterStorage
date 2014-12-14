package net.mcft.copy.betterstorage.tile;

import net.minecraft.block.material.Material;

public class TileFlintBlock extends TileBetterStorage {
	
	public TileFlintBlock() {
		super(Material.rock);
		
		setHardness(3.0F);
		setResistance(6.0F);
		setStepSound(soundTypeStone);
	}
	
<<<<<<< HEAD
	//TODO (1.8): Left for reference.
	/*@Override
=======
	/*
	@Override
>>>>>>> refs/remotes/Thog92/1.8
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister iconRegister) {
		blockIcon = iconRegister.registerIcon(Constants.modId + ":" + getTileName());
<<<<<<< HEAD
	}*/
=======
	}
	*/
>>>>>>> refs/remotes/Thog92/1.8
	
}
