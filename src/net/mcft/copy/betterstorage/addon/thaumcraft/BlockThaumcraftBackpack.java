package net.mcft.copy.betterstorage.addon.thaumcraft;

import net.mcft.copy.betterstorage.block.BlockBackpack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockThaumcraftBackpack extends BlockBackpack {
	
	public BlockThaumcraftBackpack(int id) {
		super(id);
	}
	
	@Override
	public TileEntity createNewTileEntity(World world) {
		return new TileEntityThaumcraftBackpack();
	}
	
}
