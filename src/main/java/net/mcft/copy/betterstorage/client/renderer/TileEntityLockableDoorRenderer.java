package net.mcft.copy.betterstorage.client.renderer;

import net.mcft.copy.betterstorage.attachment.LockAttachment;
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
		
		GL11.glPushMatrix();
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glTranslated(x, y + 2.0, z + 1.0);
		GL11.glScalef(1.0F, -1.0F, -1.0F);
		GL11.glTranslated(0.5F, 0.5F, 0.5F);
		
		float angle = arg0.isOpen ? 1F : 0F;
		
		switch (arg0.orientation) {
		case WEST: angle *= -90F; break;
		case EAST: angle *= -90F; angle += 180F; break;
		case SOUTH: angle *= -90F; angle -= 90F; break;
		default: angle = 1 - angle; angle *= 90F; break;
		}
		
		GL11.glPushMatrix();
		GL11.glRotatef(180F, 1, 0, 0);
		GL11.glRotatef(90F, 0, 1, 0);
		
		switch (arg0.orientation) {
		case WEST: GL11.glTranslatef(0.5F - 1.5F / 16F, -0.5F + 2 / 16F, -0.5F + 1.5F / 16F); break;
		case EAST: GL11.glTranslatef(-0.5F + 1.5F / 16F, -0.5F + 2 / 16F, 0.5F - 1.5F / 16F); break;
		case SOUTH: GL11.glTranslatef(-0.5F + 1.5F / 16F, -0.5F + 2 / 16F, -0.5F + 1.5F / 16F); break;
		default: GL11.glTranslatef(0.5F - 1.5F / 16F, -0.5F + 2 / 16F, 0.5F - 1.5F / 16F); break;
		}
		
		GL11.glRotatef(-angle, 0, 1, 0);
		GL11.glTranslatef(-0.5F - 3F / 16F, 0F, 0F);	
		GL11.glScalef(1F, 1F, 2.5F);
		
		LockAttachment a = arg0.lockAttachment;
		a.getRenderer().render(a, partialTicks);
		GL11.glPopMatrix();
		
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		bindTexture(Resources.textureLockableDoor);
		
		GL11.glPushMatrix();
		model.setOrientation(arg0.orientation);
		model.setRotation(angle);
		model.renderAll();
		GL11.glPopMatrix();
	
		GL11.glPopMatrix();
	}

	@Override
	public void renderTileEntityAt(TileEntity arg0, double arg1, double arg2, double arg3, float arg4) {
		renderTileEntityAt((TileEntityLockableDoor)arg0, arg1, arg2, arg3, arg4);
	}

}
