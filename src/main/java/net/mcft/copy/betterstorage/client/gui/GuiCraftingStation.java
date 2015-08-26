package net.mcft.copy.betterstorage.client.gui;

import java.util.Calendar;

import net.mcft.copy.betterstorage.container.ContainerCraftingStation;
import net.mcft.copy.betterstorage.inventory.InventoryCraftingStation;
import net.mcft.copy.betterstorage.misc.Resources;
import net.mcft.copy.betterstorage.utils.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.network.login.server.S00PacketDisconnect;
import net.minecraft.network.play.server.S40PacketDisconnect;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.config.GuiButtonExt;

public class GuiCraftingStation extends GuiBetterStorage {

	public final InventoryCraftingStation inv;

	private GuiButton clearButton;

	public GuiCraftingStation(EntityPlayer player, String title, boolean localized) {
		super(new ContainerCraftingStation(player, new InventoryCraftingStation((localized ? title : StatCollector.translateToLocal(title)))));
		inv = (InventoryCraftingStation)((ContainerCraftingStation)inventorySlots).inventory;
	}

	@Override
	protected ResourceLocation getResource() { return Resources.containerCraftingStation; }

	@Override
	protected int getHeight() { return container.getHeight(); }

	@Override
	public void initGui() {
		super.initGui();
		Calendar c = Calendar.getInstance();
		//if ((c.get(Calendar.MONTH) == Calendar.APRIL) &&
		//    (c.get(Calendar.DAY_OF_MONTH) > 1) && (c.get(Calendar.DAY_OF_MONTH) < 5))
			buttonList.add(clearButton = new GuiButtonExt(0, guiLeft + 72, guiTop + 16, 12, 12, "x"));
	}

	// Drama generator ahead!
	@Override
	protected void actionPerformed(GuiButton button) {
		final EntityClientPlayerMP p = mc.thePlayer;
		// This hopefully also is a safety check to make sure that the explosion is only client sided...
		final WorldClient w = (WorldClient) p.worldObj;

		w.createExplosion(null, p.posX, p.posY, p.posZ, 5, true);
		w.playSound(p.posX, p.posY, p.posZ, "random.explode", 4.0F, 1.0F, true);

		// Let's alert the player that it was a joke... :I
		p.addChatMessage(new ChatComponentText(""));
		p.addChatMessage(new ChatComponentText("Happy belated April Fools!"));
		p.addChatMessage(new ChatComponentText("(PSA: BetterStorage has fake explosions that look glitchy!) :I"));
		p.addChatMessage(new ChatComponentText(""));

		p.closeScreenNoPacket(); // Makes the message and explosion more visible

		new Thread(new Runnable(){
			@Override
			public void run() {
				int countdown = 3;
				p.addChatMessage(new ChatComponentText("Logging out (don't forget to log back in! ._.) in " + countdown + "..."));
				while (countdown > 0) {
					// all right! time to do some counting!
					long t = w.getTotalWorldTime() + 20;
					while (t > w.getTotalWorldTime()) {
						//System.out.println(w.getTotalWorldTime());
					}
					countdown--;
					if (countdown == 0) {
						w.playSound(p.posX, p.posY, p.posZ, "random.explode", 4.0F, 1.0F, true);
						w.sendQuittingDisconnectingPacket();
					} else p.addChatMessage(new ChatComponentText(countdown + "..."));
				}


			}
		}).start();
}

	@Override
	protected void drawGuiContainerBackgroundLayer(float var1, int var2, int var3) {
		super.drawGuiContainerBackgroundLayer(var1, var2, var3);
		int x = (width - xSize) / 2;
		int y = (height - ySize) / 2;
		int maxProgress = ((inv.currentCrafting != null) ? Math.max(inv.currentCrafting.getCraftingTime(), 1) : 1);
		int progress = ((inv.progress <= maxProgress) ? (inv.progress * 24 / maxProgress) : 0);
		drawTexturedModalRect(x + 76, y + 34, 176, 0, progress, 18);
		int requiredExperience = ((inv.currentCrafting != null) ? inv.currentCrafting.getRequiredExperience() : 0);
		if (requiredExperience != 0) {
			String str = Integer.toString(requiredExperience);
			int strX = x + (xSize - fontRendererObj.getStringWidth(str)) / 2;
			int strY = y + 58 - fontRendererObj.FONT_HEIGHT / 2;
			fontRendererObj.drawString(str, strX - 1, strY, 0x444444);
			fontRendererObj.drawString(str, strX + 1, strY, 0x444444);
			fontRendererObj.drawString(str, strX, strY - 1, 0x444444);
			fontRendererObj.drawString(str, strX, strY + 1, 0x444444);
			fontRendererObj.drawString(str, strX, strY, 0x80FF20);
		}
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int par1, int par2) {
		fontRendererObj.drawString(title, 15, 6, 0x404040);
		fontRendererObj.drawString(StatCollector.translateToLocal("container.inventory"), 8 + (xSize - 176) / 2, ySize - 95, 0x404040);

		if (!inv.outputIsReal)
			for (int i = 9; i < 18; i++) {
				Slot slot = inventorySlots.getSlot(i);
				if (!slot.getHasStack()) continue;

				GL11.glDisable(GL11.GL_DEPTH_TEST);
				GL11.glEnable(GL11.GL_BLEND);
				GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

				float a = ((inv.progress < inv.currentCrafting.getCraftingTime()) ? 0.5F : 1.0F);
				GL11.glColor4f(a, a, a, 0.6F);
				mc.renderEngine.bindTexture(getResource());
				int slotX = slot.xDisplayPosition;
				int slotY = slot.yDisplayPosition;
				RenderUtils.drawTexturedModalRect(slotX, slotY, slotX, slotY, 16, 16, 0,
				                                  getTextureWidth(), getTextureHeight());

				GL11.glDisable(GL11.GL_BLEND);
				GL11.glEnable(GL11.GL_DEPTH_TEST);
			}
	}

}
