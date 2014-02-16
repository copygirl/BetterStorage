package net.mcft.copy.betterstorage.addon.thaumcraft;

import net.mcft.copy.betterstorage.misc.Constants;
import net.mcft.copy.betterstorage.tile.entity.TileEntityBackpack;

public class TileEntityThaumcraftBackpack extends TileEntityBackpack {
	
	@Override
	public String getName() { return Constants.containerThaumcraftBackpack; }
	
	@Override
	public int getColumns() { return 13; }
	
}
