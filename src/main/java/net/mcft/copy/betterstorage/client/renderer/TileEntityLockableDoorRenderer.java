package net.mcft.copy.betterstorage.client.renderer;

import net.mcft.copy.betterstorage.attachment.LockAttachment;
import net.mcft.copy.betterstorage.tile.entity.TileEntityLockableDoor;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;

public class TileEntityLockableDoorRenderer extends TileEntitySpecialRenderer {

	public void renderTileEntityAt(TileEntityLockableDoor door, double x, double y, double z, float partialTicks, int arg5) {
		
		IBlockState state = getWorld().getBlockState(door.getPos());
		state = state.getBlock().getActualState(state, getWorld(), door.getPos());
		
		GlStateManager.pushMatrix();
		GlStateManager.enableRescaleNormal();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		GlStateManager.translate(x, y + 2.0, z + 1.0);
		
		LockAttachment a = door.lockAttachment;
		a.getRenderer().render(a, partialTicks);
		
		GlStateManager.popMatrix();
		GlStateManager.disableRescaleNormal();
	}

	@Override
	public void renderTileEntityAt(TileEntity arg0, double arg1, double arg2, double arg3, float arg4, int arg5) {
		renderTileEntityAt((TileEntityLockableDoor)arg0, arg1, arg2, arg3, arg4, arg5);
	}

}
