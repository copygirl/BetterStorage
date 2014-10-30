package net.mcft.copy.betterstorage.api.stand;

import net.minecraft.tileentity.TileEntity;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public interface IArmorStandRenderHandler {
	
	/** Called before the fake player entity is rendered.
	 *  Allows for changing the player's data to allow the default renderer to use it,
	 *  for example Vanilla armor. Note that the entity is reused for all armor stands. */
	public <T extends TileEntity & IArmorStand> void onPreRender(T armorStand, ClientArmorStandPlayer player);
	
	/** Called after the fake player entity is rendered. */
	public <T extends TileEntity & IArmorStand> void onPostRender(T armorStand, ClientArmorStandPlayer player);
	
}
