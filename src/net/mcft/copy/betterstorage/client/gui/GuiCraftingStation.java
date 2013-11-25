package net.mcft.copy.betterstorage.client.gui;

import net.mcft.copy.betterstorage.container.ContainerCraftingStation;
import net.mcft.copy.betterstorage.inventory.InventoryCraftingStation;
import net.mcft.copy.betterstorage.misc.Resources;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

public class GuiCraftingStation extends GuiBetterStorage {
	
	public GuiCraftingStation(EntityPlayer player, String title, boolean localized) {
		super(new ContainerCraftingStation(player, new InventoryCraftingStation((localized ? title : StatCollector.translateToLocal(title)))));
	}
	
	@Override
	protected ResourceLocation getResource() { return Resources.containerCraftingStation; }
	
	@Override
	protected int getHeight() { return container.getHeight(); }
	
	@Override
	protected void drawGuiContainerForegroundLayer(int par1, int par2) {
		fontRenderer.drawString(title, 15, 6, 0x404040);
		fontRenderer.drawString(StatCollector.translateToLocal("container.inventory"), 8 + (xSize - 176) / 2, ySize - 95, 0x404040);
	}
	
}
