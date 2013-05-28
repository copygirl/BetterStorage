package net.mcft.copy.betterstorage.addon.thaumcraft;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.mcft.copy.betterstorage.block.TileEntityReinforcedChest;
import net.mcft.copy.betterstorage.misc.Constants;

public class TileEntityThaumiumChest extends TileEntityReinforcedChest {

	@Override
	protected String getConnectableName() { return "container.thaumiumChest"; }
	
	@Override
	@SideOnly(Side.CLIENT)
	public String getTexture() {
		return Constants.gfxbase + "models/chest" + (isConnected() ? "_large/" : "/") + "thaumium.png";
	}
	
	@Override
	public int getColumns() { return 17; }
	
}
