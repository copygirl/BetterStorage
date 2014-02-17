package net.mcft.copy.betterstorage.client.gui;

import net.mcft.copy.betterstorage.container.ContainerCraftingStation;
import net.mcft.copy.betterstorage.inventory.InventoryCraftingStation;
import net.mcft.copy.betterstorage.misc.Resources;
import net.mcft.copy.betterstorage.utils.RenderUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

import org.lwjgl.opengl.GL11;

public class GuiCraftingStation extends GuiBetterStorage {
	
	public final InventoryCraftingStation inv;
	
	public GuiCraftingStation(EntityPlayer player, String title, boolean localized) {
		super(new ContainerCraftingStation(player, new InventoryCraftingStation((localized ? title : StatCollector.translateToLocal(title)))));
		inv = (InventoryCraftingStation)((ContainerCraftingStation)inventorySlots).inventory;
	}
	
	@Override
	protected ResourceLocation getResource() { return Resources.containerCraftingStation; }
	
	@Override
	protected int getHeight() { return container.getHeight(); }
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float var1, int var2, int var3) {
		super.drawGuiContainerBackgroundLayer(var1, var2, var3);
		int x = (width - xSize) / 2;
		int y = (height - ySize) / 2;
		int maxProgress = Math.max(inv.craftingTime, 1);
		int progress = ((inv.progress <= maxProgress) ? (inv.progress * 28 / maxProgress) : 0);
		drawTexturedModalRect(x + 74, y + 34, 176, 0, progress, 18);
		if (inv.experience != 0) {
			String str = Integer.toString(inv.experience);
			int strX = x + (xSize - mc.fontRenderer.getStringWidth(str)) / 2;
			int strY = y + 58 - mc.fontRenderer.FONT_HEIGHT / 2;
			mc.fontRenderer.drawStringWithShadow(str, strX, strY, 0x80FF20);
		}
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(int par1, int par2) {
		fontRenderer.drawString(title, 15, 6, 0x404040);
		fontRenderer.drawString(StatCollector.translateToLocal("container.inventory"), 8 + (xSize - 176) / 2, ySize - 95, 0x404040);
	}
	
	@Override
	protected void drawSlotInventory(Slot slot) {
		super.drawSlotInventory(slot);
		
		if ((slot.slotNumber < 9) || (slot.slotNumber >= 18) ||
		    inv.outputIsReal || !slot.getHasStack()) return;
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		
		float a = ((inv.progress < inv.craftingTime) ? 0.5F : 1.0F);
		GL11.glColor4f(a, a, a, 0.6F);
		mc.renderEngine.bindTexture(getResource());
		int slotX = slot.xDisplayPosition - 1;
		int slotY = slot.yDisplayPosition - 1;
		RenderUtils.drawTexturedModalRect(slotX, slotY, slotX, slotY, 18, 18, 0,
		                                  getTextureWidth(), getTextureHeight());
		
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
	}
	
}
