package net.mcft.copy.betterstorage.addon.thaumcraft;

import net.mcft.copy.betterstorage.client.gui.GuiBetterStorage;
import net.mcft.copy.betterstorage.container.ContainerBetterStorage;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiThaumiumChest extends GuiBetterStorage {
	
	public GuiThaumiumChest(EntityPlayer player, int columns, int rows, String name, boolean localized) {
		super(new ContainerBetterStorage(player, new InventoryBasic(name, localized, columns * rows), columns, rows, 20));
	}
	
	@Override
	protected int getHeight() { return 229; }
	
	@Override
	protected int getTextureWidth() { return 512; }
	
	@Override
	protected ResourceLocation getResource() { return ThaumcraftResources.thaumiumChestContainer; }
	
	@Override
	protected void drawGuiContainerForegroundLayer(int par1, int par2) {
		fontRendererObj.drawString(title, 8, 6, 0x303030);
		fontRendererObj.drawString(StatCollector.translateToLocal("container.inventory"), 8 + (xSize - 176) / 2, ySize - 94, 0x404040);
	}
	
}
