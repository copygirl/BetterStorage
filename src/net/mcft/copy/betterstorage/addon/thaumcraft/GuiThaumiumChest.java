package net.mcft.copy.betterstorage.addon.thaumcraft;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.mcft.copy.betterstorage.client.gui.GuiBetterStorage;
import net.mcft.copy.betterstorage.container.ContainerBetterStorage;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.util.StatCollector;

@SideOnly(Side.CLIENT)
public class GuiThaumiumChest extends GuiBetterStorage {
	
	public GuiThaumiumChest(EntityPlayer player, int columns, int rows, String name, boolean localized) {
		super(new ContainerBetterStorage(player, new InventoryBasic(name, localized, columns * rows), columns, rows, 20));
	}
	
	@Override
	protected int getTextureWidth() { return 512; }
	
	@Override
	protected String getTexture() { return ThaumcraftAddon.thaumiumChestContainer; }
	
	@Override
	protected void drawGuiContainerForegroundLayer(int par1, int par2) {
		fontRenderer.drawString(title, 8, 6, 0x303030);
		fontRenderer.drawString(StatCollector.translateToLocal("container.inventory"), 8 + (xSize - 176) / 2, ySize - 92, 0x404040);
	}
	
}
