package net.mcft.copy.betterstorage.client.renderer;

import net.mcft.copy.betterstorage.api.stand.BetterStorageArmorStand;
import net.mcft.copy.betterstorage.api.stand.ClientArmorStandPlayer;
import net.mcft.copy.betterstorage.api.stand.IArmorStandRenderHandler;
import net.mcft.copy.betterstorage.client.model.ModelArmorStand;
import net.mcft.copy.betterstorage.misc.Resources;
import net.mcft.copy.betterstorage.tile.stand.TileEntityArmorStand;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TileEntityArmorStandRenderer extends TileEntitySpecialRenderer {
	
	private ClientArmorStandPlayer playerDummy = null;
	private RenderArmor renderArmor = new RenderArmor(Minecraft.getMinecraft().getRenderManager());
	
	private ModelArmorStand armorStandModel = new ModelArmorStand();
	
	public void renderTileEntityAt(TileEntityArmorStand armorStand, double x, double y, double z, float par8, int par9) {
		
		int rotation = armorStand.rotation * 360 / 16;
		
		ModelArmorStand model = armorStandModel;
		bindTexture(Resources.textureArmorStand);
		
		GlStateManager.pushMatrix();
		GlStateManager.enableRescaleNormal();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.translate(x, y + 2.0, z + 1.0);
		GlStateManager.scale(1.0F, -1.0F, -1.0F);
		GlStateManager.translate(0.5F, 0.5F, 0.5F);
		
		model.setRotation(rotation);
		model.renderAll();
		
		GlStateManager.disableRescaleNormal();
		GlStateManager.popMatrix();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		
		if (armorStand.getWorld() == null) return;
		
		if (playerDummy == null)
			playerDummy = new ClientArmorStandPlayer(armorStand.getWorld());
		
		playerDummy.ticksExisted = armorStand.ticksExisted;
		playerDummy.worldObj = armorStand.getWorld();
		playerDummy.renderYawOffset = playerDummy.prevRenderYawOffset = rotation;
		playerDummy.rotationYawHead = playerDummy.prevRotationYawHead = rotation;
		
		for (IArmorStandRenderHandler handler : BetterStorageArmorStand.getRenderHandlers())
			handler.onPreRender(armorStand, playerDummy);
		
		renderArmor.doRender(playerDummy, x + 0.5, y + 27 / 16.0, z + 0.5, rotation, par8);
		
		GlStateManager.pushMatrix();
		GlStateManager.translate(x + 0.5, y + 27 / 16.0, z + 0.5);
		GlStateManager.rotate(180, 1, 0, 0);
		GlStateManager.rotate(rotation, 0, 1, 0);
		for (IArmorStandRenderHandler handler : BetterStorageArmorStand.getRenderHandlers())
			handler.onPostRender(armorStand, playerDummy);
		GlStateManager.popMatrix();
	}
	
	@Override
	public void renderTileEntityAt(TileEntity entity, double x, double y, double z, float par8, int par9) {
		renderTileEntityAt((TileEntityArmorStand)entity, x, y, z, par8, par9);
	}
	
}
