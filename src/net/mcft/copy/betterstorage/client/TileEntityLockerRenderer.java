package net.mcft.copy.betterstorage.client;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.mcft.copy.betterstorage.block.TileEntityLocker;
import net.mcft.copy.betterstorage.misc.Constants;
import net.mcft.copy.betterstorage.utils.DirectionUtils;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;

@SideOnly(Side.CLIENT)
public class TileEntityLockerRenderer extends TileEntitySpecialRenderer {
	
	private ModelLocker[] lockerModels = { new ModelLocker(false), new ModelLocker(true) };
	private ModelLocker[] largeLockerModels = { new ModelLargeLocker(false), new ModelLargeLocker(true) };
	
	public void renderTileEntityAt(TileEntityLocker locker, double x, double y, double z, float par8) {
		
		boolean large = locker.isConnected();
		if (large && !locker.isMain()) return;
		
		int index = (locker.mirror ? 1 : 0);
		ModelLocker model = (large ? largeLockerModels : lockerModels)[index];
		bindTextureByName((large ? Constants.largeLockerTexture : Constants.lockerTexture));
		
		GL11.glPushMatrix();
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glTranslated(x, y + 2.0, z + 1.0);
		GL11.glScalef(1.0F, -1.0F, -1.0F);
		GL11.glTranslated(0.5F, 0.5F, 0.5F);
		
		int rotation = DirectionUtils.getRotation(locker.orientation);
		GL11.glRotatef(rotation, 0.0F, 1.0F, 0.0F);
		
		float angle = locker.prevLidAngle + (locker.lidAngle - locker.prevLidAngle) * par8;
		angle = 1.0F - angle;
		angle = 1.0F - angle * angle * angle;
		model.setDoorRotation((float)(angle * Math.PI / 2.0));
		model.renderAll();
		
		GL11.glDisable(GL12.GL_RESCALE_NORMAL);
		GL11.glPopMatrix();
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		
	}
	
	@Override
	public void renderTileEntityAt(TileEntity entity, double x, double y, double z, float par8) {
		renderTileEntityAt((TileEntityLocker)entity, x, y, z, par8);
	}
	
}
