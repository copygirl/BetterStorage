package net.mcft.copy.betterstorage.client.renderer;

import net.mcft.copy.betterstorage.attachment.LockAttachment;
import net.mcft.copy.betterstorage.block.tileentity.TileEntityLocker;
import net.mcft.copy.betterstorage.client.model.ModelLargeLocker;
import net.mcft.copy.betterstorage.client.model.ModelLocker;
import net.mcft.copy.betterstorage.utils.DirectionUtils;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

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
		bindTexture(locker.getResource());
		
		GL11.glPushMatrix();
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		
		float angle = locker.prevLidAngle + (locker.lidAngle - locker.prevLidAngle) * partialTicks;
		angle = 1.0F - angle;
		angle = 1.0F - angle * angle * angle;
		angle = angle * 90;
		
		GL11.glTranslated(x + 0.5, y + 0.5, z + 0.5);
		int rotation = DirectionUtils.getRotation(locker.getOrientation());
		GL11.glRotatef(-rotation, 0.0F, 1.0F, 0.0F);
		
			GL11.glPushMatrix();
			GL11.glScalef(scale, scale, scale);
			
			model.renderAll(locker.mirror, angle);
			
			GL11.glPopMatrix();
		
		if (locker.canHaveLock()) {
			if (angle > 0) {
				double seven = 7 / 16.0D;
				GL11.glTranslated((locker.mirror ? seven : -seven), 0, seven);
				GL11.glRotatef((locker.mirror ? angle : -angle), 0, 1, 0);
				GL11.glTranslated((locker.mirror ? -seven : seven), 0, -seven);
			}
			LockAttachment a = locker.lockAttachment;
			GL11.glTranslated(0.5 - a.getX(), 0.5 - a.getY(), 0.5 - a.getZ());
			a.getRenderer().render(a, partialTicks);
		}
		
		GL11.glDisable(GL12.GL_RESCALE_NORMAL);
		GL11.glPopMatrix();
		
	}
	
	@Override
	public void renderTileEntityAt(TileEntity entity, double x, double y, double z, float par8) {
		renderTileEntityAt((TileEntityLocker)entity, x, y, z, par8);
	}
	
}
