package net.mcft.copy.betterstorage.client.renderer;

import net.mcft.copy.betterstorage.attachment.LockAttachment;
import net.mcft.copy.betterstorage.client.model.ModelLargeLocker;
import net.mcft.copy.betterstorage.client.model.ModelLocker;
import net.mcft.copy.betterstorage.tile.entity.TileEntityLocker;
import net.mcft.copy.betterstorage.utils.DirectionUtils;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TileEntityLockerRenderer extends TileEntitySpecialRenderer {
	
	private ModelLocker lockerModel = new ModelLocker();
	private ModelLocker largeLockerModel = new ModelLargeLocker();
	
	public void renderTileEntityAt(TileEntityLocker locker, double x, double y, double z, float partialTicks, int par9) {
		
		float scale = 1.0F / 16;
		
		boolean large = locker.isConnected();
		if (large && !locker.isMain()) return;
		
		int index = (locker.mirror ? 1 : 0);
		ModelLocker model = (large ? largeLockerModel : lockerModel);
		bindTexture(locker.getResource());
		
		GlStateManager.pushMatrix();
		GlStateManager.enableRescaleNormal();
		
		float angle = locker.prevLidAngle + (locker.lidAngle - locker.prevLidAngle) * partialTicks;
		angle = 1.0F - angle;
		angle = 1.0F - angle * angle * angle;
		angle = angle * 90;
		
		GlStateManager.translate(x + 0.5, y + 0.5, z + 0.5);
		int rotation = DirectionUtils.getRotation(locker.getOrientation());
		GlStateManager.rotate(-rotation, 0.0F, 1.0F, 0.0F);
		
		GlStateManager.pushMatrix();
		GlStateManager.scale(scale, scale, scale);
			
		model.renderAll(locker.mirror, angle);
			
		GlStateManager.popMatrix();
		
		if (locker.canHaveLock()) {
			if (angle > 0) {
				double seven = 7 / 16.0D;
				GlStateManager.translate((locker.mirror ? seven : -seven), 0, seven);
				GlStateManager.rotate((locker.mirror ? angle : -angle), 0, 1, 0);
				GlStateManager.translate((locker.mirror ? -seven : seven), 0, -seven);
			}
			LockAttachment a = locker.lockAttachment;
			GlStateManager.translate(0.5 - a.getX(), 0.5 - a.getY(), 0.5 - a.getZ());
			a.getRenderer().render(a, partialTicks);
		}
		
		GlStateManager.disableRescaleNormal();
		GlStateManager.popMatrix();
		
	}
	
	@Override
	public void renderTileEntityAt(TileEntity entity, double x, double y, double z, float par8, int par9) {
		renderTileEntityAt((TileEntityLocker)entity, x, y, z, par8, par9);
	}
	
}
