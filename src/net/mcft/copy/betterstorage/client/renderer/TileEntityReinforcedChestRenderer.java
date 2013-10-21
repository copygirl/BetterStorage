package net.mcft.copy.betterstorage.client.renderer;

import net.mcft.copy.betterstorage.block.tileentity.TileEntityReinforcedChest;
import net.mcft.copy.betterstorage.utils.DirectionUtils;
import net.minecraft.client.model.ModelChest;
import net.minecraft.client.model.ModelLargeChest;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TileEntityReinforcedChestRenderer extends TileEntitySpecialRenderer {
	
	private ModelChest chestModel = new ModelChest();
	private ModelChest largeChestModel = new ModelLargeChest();
	
	public void renderTileEntityAt(TileEntityReinforcedChest chest, double x, double y, double z, float partialTicks) {
		
		boolean large = chest.isConnected();
		if (large && !chest.isMain()) return;
		
		ModelChest model = (large ? largeChestModel : chestModel);
		bindTexture(chest.getResource());
		
		GL11.glPushMatrix();
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glTranslated(x, y, z);
		
			GL11.glPushMatrix();
			GL11.glScalef(1.0F, -1.0F, -1.0F);
			GL11.glTranslatef(0.5F, -0.5F, -0.5F);
			
			int rotation = DirectionUtils.getRotation(chest.getOrientation());
			if ((rotation == 180) && large)
				GL11.glTranslatef(1.0F, 0.0F, 0.0F);
			if ((rotation == 270) && large)
				GL11.glTranslatef(0.0F, 0.0F, -1.0F);
			GL11.glRotatef(rotation, 0.0F, 1.0F, 0.0F);
			GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
			
			float angle = chest.prevLidAngle + (chest.lidAngle - chest.prevLidAngle) * partialTicks;
			angle = 1.0F - angle;
			angle = 1.0F - angle * angle * angle;
			model.chestLid.rotateAngleX = -(float)(angle * Math.PI / 2.0);
			model.renderAll();
			
			GL11.glPopMatrix();
		
		chest.getAttachments().render(partialTicks);
		
		GL11.glDisable(GL12.GL_RESCALE_NORMAL);
		GL11.glPopMatrix();
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		
	}
	
	@Override
	public void renderTileEntityAt(TileEntity entity, double x, double y, double z, float partialTicks) {
		renderTileEntityAt((TileEntityReinforcedChest) entity, x, y, z, partialTicks);
	}
	
}
