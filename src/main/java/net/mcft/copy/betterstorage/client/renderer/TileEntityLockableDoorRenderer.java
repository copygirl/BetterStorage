package net.mcft.copy.betterstorage.client.renderer;

import net.mcft.copy.betterstorage.attachment.LockAttachment;
import net.mcft.copy.betterstorage.tile.entity.TileEntityLockableDoor;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;

public class TileEntityLockableDoorRenderer extends TileEntitySpecialRenderer {

	public void renderTileEntityAt(TileEntityLockableDoor arg0, double x, double y, double z, float partialTicks, int arg5) {
		
		GlStateManager.pushMatrix();
		GlStateManager.enableRescaleNormal();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.translate(x, y + 2.0, z + 1.0);
		GlStateManager.scale(1.0F, -1.0F, -1.0F);
		GlStateManager.translate(0.5F, 0.5F, 0.5F);
		
		float angle = arg0.isOpen ? 1F : 0F;
		
		switch (arg0.orientation) {
		case WEST: angle *= -90F; break;
		case EAST: angle *= -90F; angle += 180F; break;
		case SOUTH: angle *= -90F; angle -= 90F; break;
		default: angle = 1 - angle; angle *= 90F; break;
		}
		
		GlStateManager.rotate(180F, 1, 0, 0);
		GlStateManager.rotate(90F, 0, 1, 0);
		
		switch (arg0.orientation) {
		case WEST: GlStateManager.translate(0.5F - 1.5F / 16F, -0.5F + 2 / 16F, -0.5F + 1.5F / 16F); break;
		case EAST: GlStateManager.translate(-0.5F + 1.5F / 16F, -0.5F + 2 / 16F, 0.5F - 1.5F / 16F); break;
		case SOUTH: GlStateManager.translate(-0.5F + 1.5F / 16F, -0.5F + 2 / 16F, -0.5F + 1.5F / 16F); break;
		default: GlStateManager.translate(0.5F - 1.5F / 16F, -0.5F + 2 / 16F, 0.5F - 1.5F / 16F); break;
		}
		
		GlStateManager.rotate(-angle, 0, 1, 0);
		GlStateManager.translate(-0.5F - 3F / 16F, 0F, 0F);	
		GlStateManager.scale(1F, 1F, 2.5F);
		
		LockAttachment a = arg0.lockAttachment;
		a.getRenderer().render(a, partialTicks);
		GlStateManager.popMatrix();
		GlStateManager.disableRescaleNormal();
	}

	@Override
	public void renderTileEntityAt(TileEntity arg0, double arg1, double arg2, double arg3, float arg4, int arg5) {
		renderTileEntityAt((TileEntityLockableDoor)arg0, arg1, arg2, arg3, arg4, arg5);
	}

}
