package net.mcft.copy.betterstorage.client.renderer;

import net.mcft.copy.betterstorage.tile.entity.TileEntityReinforcedChest;
import net.mcft.copy.betterstorage.utils.DirectionUtils;
import net.minecraft.client.model.ModelChest;
import net.minecraft.client.model.ModelLargeChest;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TileEntityReinforcedChestRenderer extends TileEntitySpecialRenderer {
	
	private ModelChest chestModel = new ModelChest();
	private ModelChest largeChestModel = new ModelLargeChest();
	
	public void renderTileEntityAt(TileEntityReinforcedChest chest, double x, double y, double z, float partialTicks, int par5) {
		
		boolean large = chest.isConnected();
		if (large && !chest.isMain()) return;
		
		ModelChest model = (large ? largeChestModel : chestModel);
		bindTexture(chest.getResource());
		
		GlStateManager.pushMatrix();
		GlStateManager.enableRescaleNormal();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.translate(x, y, z);
		
			GlStateManager.pushMatrix();
			GlStateManager.scale(1.0F, -1.0F, -1.0F);
			GlStateManager.translate(0.5F, -0.5F, -0.5F);
			
			int rotation = DirectionUtils.getRotation(chest.getOrientation());
			if ((rotation == 180) && large)
				GlStateManager.translate(1.0F, 0.0F, 0.0F);
			if ((rotation == 270) && large)
				GlStateManager.translate(0.0F, 0.0F, -1.0F);
			GlStateManager.rotate(rotation, 0.0F, 1.0F, 0.0F);
			GlStateManager.translate(-0.5F, -0.5F, -0.5F);
			
			float angle = chest.prevLidAngle + (chest.lidAngle - chest.prevLidAngle) * partialTicks;
			angle = 1.0F - angle;
			angle = 1.0F - angle * angle * angle;
			model.chestLid.rotateAngleX = -(float)(angle * Math.PI / 2.0);
			model.renderAll();
			
			GlStateManager.popMatrix();
		
		chest.getAttachments().render(partialTicks);
		
		GlStateManager.disableRescaleNormal();
		GlStateManager.popMatrix();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		
	}
	
	@Override
	public void renderTileEntityAt(TileEntity entity, double x, double y, double z, float partialTicks, int par5) {
		renderTileEntityAt((TileEntityReinforcedChest) entity, x, y, z, partialTicks, par5);
	}
	
}
