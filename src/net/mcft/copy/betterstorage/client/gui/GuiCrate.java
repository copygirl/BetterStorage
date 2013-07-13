package net.mcft.copy.betterstorage.client.gui;

import net.mcft.copy.betterstorage.misc.Resources;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiCrate extends GuiBetterStorage {
	
	private int fullness = 0;
	
	public GuiCrate(EntityPlayer player, int rows, String title, boolean localized) {
		super(player, 9, rows, (localized ? title : StatCollector.translateToLocal(title)));
	}
	
	@Override
	protected ResourceLocation getResource() { return Resources.crateContainer; }
	
	@Override
	public void update(int id, int val) {
		if (id == 0) { fullness = val; }
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float var1, int var2, int var3) {
		super.drawGuiContainerBackgroundLayer(var1, var2, var3);
		int x = (width - xSize) / 2;
		int y = (height - ySize) / 2;
		drawTexturedModalRect(x + 115, y + 7, 176, 0, fullness * 54 / 255, 6);
	}
	
}
