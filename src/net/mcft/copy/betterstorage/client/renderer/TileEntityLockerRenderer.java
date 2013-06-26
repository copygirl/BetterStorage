package net.mcft.copy.betterstorage.client.renderer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.mcft.copy.betterstorage.block.tileentity.TileEntityLocker;
import net.mcft.copy.betterstorage.client.model.ModelLargeLocker;
import net.mcft.copy.betterstorage.client.model.ModelLocker;
import net.mcft.copy.betterstorage.misc.Constants;
import net.mcft.copy.betterstorage.utils.DirectionUtils;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;

@SideOnly(Side.CLIENT)
public class TileEntityLockerRenderer extends TileEntitySpecialRenderer {
	
	private ModelLocker lockerModel = new ModelLocker();
	private ModelLocker largeLockerModel = new ModelLargeLocker();
	
	public void renderTileEntityAt(TileEntityLocker locker, double x, double y, double z, float partialTicks) {
		
		float scale = 1.0F / 16;
		
		boolean large = locker.isConnected();
		if (large && !locker.isMain()) return;
		
		int index = (locker.mirror ? 1 : 0);
		ModelLocker model = (large ? largeLockerModel : lockerModel);
		bindTextureByName((large ? Constants.largeLockerTexture : Constants.lockerTexture));
		
		GL11.glPushMatrix();
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		
		GL11.glTranslated(x + 0.5, y + 0.5, z + 0.5);
		GL11.glScalef(scale, scale, scale);
		
		int rotation = DirectionUtils.getRotation(locker.getOrientation());
		GL11.glRotatef(-rotation, 0.0F, 1.0F, 0.0F);
		
		float angle = locker.prevLidAngle + (locker.lidAngle - locker.prevLidAngle) * partialTicks;
		angle = 1.0F - angle;
		angle = 1.0F - angle * angle * angle;
		angle = angle * 90;
		model.renderAll(locker.mirror, angle);
		
		GL11.glDisable(GL12.GL_RESCALE_NORMAL);
		GL11.glPopMatrix();
		
	}
	
	@Override
	public void renderTileEntityAt(TileEntity entity, double x, double y, double z, float par8) {
		renderTileEntityAt((TileEntityLocker)entity, x, y, z, par8);
	}
	
}
