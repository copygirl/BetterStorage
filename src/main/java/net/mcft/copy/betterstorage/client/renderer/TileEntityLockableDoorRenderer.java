package net.mcft.copy.betterstorage.client.renderer;

import net.mcft.copy.betterstorage.client.model.ModelLockableDoor;
import net.mcft.copy.betterstorage.misc.Resources;
import net.mcft.copy.betterstorage.tile.entity.TileEntityLockableDoor;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

public class TileEntityLockableDoorRenderer extends TileEntitySpecialRenderer {

	private ModelLockableDoor model = new ModelLockableDoor();
	
	public void renderTileEntityAt(TileEntityLockableDoor arg0, double x, double y, double z, float partialTicks) {
		
		bindTexture(Resources.textureLockableDoor);
		
		GL11.glPushMatrix();
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glTranslated(x, y + 2.0, z + 1.0);
		GL11.glScalef(1.0F, -1.0F, -1.0F);
		GL11.glTranslated(0.5F, 0.5F, 0.5F);
		
		float angle = arg0.prevAngle + (arg0.angle - arg0.prevAngle) * partialTicks;
		angle = 1.0F - angle;
		angle = 1.0F - angle * angle * angle;
		
		switch (arg0.orientation) {
		case WEST: angle *= -90F; break;
		case EAST: angle *= -90F; angle += 180F; break;
		case SOUTH: angle *= -90F; angle -= 90F; break;
		default: angle = 1 - angle; angle *= 90F; break;
		}
		
		model.setOrientation(arg0.orientation);
		model.setRotation(angle);
		model.renderAll();
		
		GL11.glDisable(GL12.GL_RESCALE_NORMAL);
		GL11.glPopMatrix();
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
	}

	@Override
	public void renderTileEntityAt(TileEntity arg0, double arg1, double arg2, double arg3, float arg4) {
		renderTileEntityAt((TileEntityLockableDoor)arg0, arg1, arg2, arg3, arg4);
	}

}
