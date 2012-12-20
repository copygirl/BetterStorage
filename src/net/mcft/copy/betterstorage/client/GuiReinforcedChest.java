package net.mcft.copy.betterstorage.client;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.asm.SideOnly;

import net.mcft.copy.betterstorage.Constants;
import net.mcft.copy.betterstorage.blocks.ContainerReinforcedChest;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.GuiContainer;
import net.minecraft.src.IInventory;
import net.minecraft.src.StatCollector;
import net.minecraft.src.Tessellator;

@SideOnly(Side.CLIENT)
public class GuiReinforcedChest extends GuiContainer {
	
	// TODO: Add support for custom column / row sizes.
	
	private final static int fullWidth = 98;
	private final static int fullHeight = 258;
	
	private String name;
	private int columns, rows;
	
	public int getColumns() { return columns; }
	public int getRows() { return rows; }
	
	public GuiReinforcedChest(EntityPlayer player, int columns, int rows, IInventory inventory) {
		super(new ContainerReinforcedChest(player, columns, rows, inventory));
		this.name = inventory.getInvName();
		this.columns = columns;
		this.rows = rows;
		xSize = 14 + columns * 18;
		ySize = 114 + rows * 18;
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(int par1, int par2) {
		fontRenderer.drawString(StatCollector.translateToLocal(name), 8, 6, 0x404040);
		fontRenderer.drawString(StatCollector.translateToLocal("container.inventory"), 8 + (xSize - 176) / 2, ySize - 94, 0x404040);
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float var1, int var2, int var3) {
		GL11.glColor4f(1, 1, 1, 1);
		int texture;
		if (columns <= 9) texture = mc.renderEngine.getTexture("/gui/container.png");
		else texture = mc.renderEngine.getTexture(Constants.reinforcedChestContainer);
		mc.renderEngine.bindTexture(texture);
		int x = (width - xSize) / 2;
		int y = (height - ySize) / 2;
		drawTexturedModalRect(x, y, 0, 0, xSize, ySize - 107);
		drawTexturedModalRect(x, y + ySize - 107, 0, 115, xSize, 107);
	}
	
	@Override
	public void drawTexturedModalRect(int x, int y, int u, int v, int w, int h) {
		float scale = 0.00390625F;
		Tessellator tes = Tessellator.instance;
		tes.startDrawingQuads();
		tes.addVertexWithUV(x,     y + h, zLevel,       u * scale, (v + h) * scale);
		tes.addVertexWithUV(x + w, y + h, zLevel, (u + w) * scale, (v + h) * scale);
		tes.addVertexWithUV(x + w, y,     zLevel, (u + w) * scale,       v * scale);
		tes.addVertexWithUV(x,     y,     zLevel,       u * scale,       v * scale);
		tes.draw();
	}

}
