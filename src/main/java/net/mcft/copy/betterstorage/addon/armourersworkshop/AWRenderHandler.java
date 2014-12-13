package net.mcft.copy.betterstorage.addon.armourersworkshop;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class AWRenderHandler /*implements IArmorStandRenderHandler*/ {
	
	/*@Override
	public <T extends TileEntity & IArmorStand> void onPreRender(T armorStand, ClientArmorStandPlayer player) {  }
	
	@Override
	public <T extends TileEntity & IArmorStand> void onPostRender(T armorStand, ClientArmorStandPlayer player) {
		GL11.glPushMatrix();
		GL11.glTranslatef(0, 3 / 16F, 0);
		renderIfNonNull(armorStand, AWAddon.eqHandlerHead);
		renderIfNonNull(armorStand, AWAddon.eqHandlerChest);
		renderIfNonNull(armorStand, AWAddon.eqHandlerSkirt);
		renderIfNonNull(armorStand, AWAddon.eqHandlerLegs);
		renderIfNonNull(armorStand, AWAddon.eqHandlerFeet);
		GL11.glPopMatrix();
	}
	
	private <T extends TileEntity & IArmorStand> void renderIfNonNull(T armorStand, AWEquipmentHandler handler) {
		ItemStack item = armorStand.getItem(handler);
		if (item != null)
			AWAddon.renderHandler.renderCustomEquipmentFromStack(item, 0, 0, 0, 0, 0);
	}*/
	
}
