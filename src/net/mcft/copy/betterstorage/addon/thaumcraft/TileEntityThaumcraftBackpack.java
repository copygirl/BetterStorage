package net.mcft.copy.betterstorage.addon.thaumcraft;

import net.mcft.copy.betterstorage.block.tileentity.TileEntityBackpack;

public class TileEntityThaumcraftBackpack extends TileEntityBackpack {
	
	@Override
	public String getName() { return "container.thaumcraftBackpack"; }
	
	@Override
	public int getColumns() { return 13; }
	
}
