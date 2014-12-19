package net.mcft.copy.betterstorage.client.renderer;

import net.mcft.copy.betterstorage.client.model.ModelBackpack;
import net.mcft.copy.betterstorage.item.ItemBackpack;
import net.mcft.copy.betterstorage.misc.Resources;
import net.mcft.copy.betterstorage.tile.TileBackpack;
import net.mcft.copy.betterstorage.tile.entity.TileEntityBackpack;
import net.mcft.copy.betterstorage.utils.DirectionUtils;
import net.mcft.copy.betterstorage.utils.RenderUtils;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class TileEntityBackpackRenderer extends TileEntitySpecialRenderer {
	
	public void renderTileEntityAt(TileEntityBackpack backpack, double x, double y, double z, float partialTicks, int par9) {
		
		if ((backpack.getWorld() == null) && (backpack.getBlockType() == null)) return;
		ItemBackpack item = ((TileBackpack)backpack.getBlockType()).getItemType();
		ItemStack stack = ((backpack.stack != null) ? backpack.stack : new ItemStack(item));
		ModelBackpack backpackModel = item.getModel();
		
		GlStateManager.pushMatrix();
		GlStateManager.enableRescaleNormal();
		GlStateManager.translate(x, y + 2.0, z + 1.0);
		GlStateManager.scale(1.0F, -1.0F, -1.0F);
		GlStateManager.translate(0.5, 0.5, 0.5);
		
		EnumFacing orientation = (EnumFacing) backpack.getState().getValue(TileBackpack.FACING_PROP);
		int rotation = DirectionUtils.getRotation(orientation);
		GlStateManager.rotate(rotation, 0.0F, 1.0F, 0.0F);
		
		float angle = backpack.prevLidAngle + (backpack.lidAngle - backpack.prevLidAngle) * partialTicks;
		angle = 1.0F - angle;
		angle = 1.0F - angle * angle;
		backpackModel.setLidRotation((float)(angle * Math.PI / 4.0));
		
		int renderPasses = item.getRenderPasses();
		for (int pass = 0; pass < renderPasses; pass++) {
			String type = ((pass == 0) ? null : "overlay");
			bindTexture(new ResourceLocation(item.getArmorTexture(stack, null, 0, type)));
			RenderUtils.setColorFromInt(item.getColorFromItemStack(stack, pass));
			backpackModel.renderAll();
		}
				
		if ((backpack.stack != null) &&
		    (backpack.stack.isItemEnchanted())) {
			float f9 = (backpack.ticksExisted + partialTicks) / 3;

			RenderUtils.bindTexture(Resources.enchantedEffect);
			
			GlStateManager.enableBlend();
			GlStateManager.color(0.5F, 0.5F, 0.5F, 1.0F);
			GlStateManager.depthMask(false);
			GlStateManager.disableDepth();
			GlStateManager.enablePolygonOffset();
			GlStateManager.doPolygonOffset(-1.0F, -1.0F);

			for (int k = 0; k < 2; ++k) {
				GlStateManager.disableLighting();
				float f11 = 0.65F;
				GlStateManager.color(0.5F * f11, 0.25F * f11, 0.8F * f11, 1.0F);
				GlStateManager.blendFunc(GL11.GL_SRC_COLOR, GL11.GL_ONE);
				GlStateManager.matrixMode(GL11.GL_TEXTURE);
				GlStateManager.loadIdentity();
				
				float f12 = f9 * (0.001F + k * 0.003F) * 20.0F;
				float f13 = 0.33333334F;
				GlStateManager.scale(f13, f13, f13);
				GlStateManager.rotate(30.0F - k * 60.0F, 0.0F, 0.0F, 1.0F);
				GlStateManager.translate(0.0F, f12, 0.0F);
				GlStateManager.matrixMode(GL11.GL_MODELVIEW);
				backpackModel.renderAll();
			}
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			GlStateManager.matrixMode(GL11.GL_TEXTURE);
			GlStateManager.disablePolygonOffset();
			GlStateManager.doPolygonOffset(0.0F, 0.0F);
			GlStateManager.depthMask(true);
			GlStateManager.loadIdentity();
			GlStateManager.matrixMode(GL11.GL_MODELVIEW);
			GlStateManager.enableLighting();
			GlStateManager.disableBlend();
		}
		
		GlStateManager.disableRescaleNormal();
		GlStateManager.pushMatrix();
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		
	}
	
	@Override
	public void renderTileEntityAt(TileEntity entity, double x, double y, double z, float par8, int par9) {
		renderTileEntityAt((TileEntityBackpack)entity, x, y, z, par8, par9);
	}
	
}
