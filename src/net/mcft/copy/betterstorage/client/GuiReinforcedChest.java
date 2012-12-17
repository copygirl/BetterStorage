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
	
	private final static int fullWidth = 98;
	private final static int fullHeight = 258;
	
	private boolean reinforced, large;
	
	public GuiReinforcedChest(EntityPlayer player, IInventory inventory) {
		super(new ContainerReinforcedChest(player, inventory));
		reinforced = (inventory.getSizeInventory() % 13 == 0);
		int numColumns = (reinforced ? 13 : 15);
		int numRows = inventory.getSizeInventory() / numColumns;
		large = (numRows > 3);
		xSize = 14 + numColumns * 18;
		ySize = 114 + numRows * 18;
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(int par1, int par2) {
		String name = "container." + (reinforced ? "reinforcedChest" : "metalChest") + (large ? "Large" : "");
		fontRenderer.drawString(StatCollector.translateToLocal(name), 8, 6, 0x404040);
		fontRenderer.drawString(StatCollector.translateToLocal("container.inventory"), 8 + (xSize - 176) / 2, ySize - 94, 0x404040);
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float var1, int var2, int var3) {
		GL11.glColor4f(1, 1, 1, 1);
		int texture = mc.renderEngine.getTexture(Constants.reinforcedChestContainer);
		mc.renderEngine.bindTexture(texture);
		int x = (width - xSize) / 2;
		int y = (height - ySize) / 2;
		drawTexturedModalRect(x, y, 0, 0, xSize, ySize - 107);
		drawTexturedModalRect(x, y + ySize - 107, 0, 151, xSize, 107);
	}
	
	@Override
	public void drawTexturedModalRect(int par1, int par2, int par3, int par4,
			int par5, int par6) {
		float var7 = 0.00390625F;
		float var8 = 0.00390625F / 2;
		Tessellator var9 = Tessellator.instance;
		var9.startDrawingQuads();
		var9.addVertexWithUV((par1 + 0), (par2 + par6),
				this.zLevel, ((par3 + 0) * var7),
				((par4 + par6) * var8));
		var9.addVertexWithUV((par1 + par5), (par2 + par6),
				this.zLevel, ((par3 + par5) * var7),
				((par4 + par6) * var8));
		var9.addVertexWithUV((par1 + par5), (par2 + 0),
				this.zLevel, ((par3 + par5) * var7),
				((par4 + 0) * var8));
		var9.addVertexWithUV((par1 + 0), (par2 + 0),
				this.zLevel, ((par3 + 0) * var7),
				((par4 + 0) * var8));
		var9.draw();
	}

}
