package net.mcft.copy.betterstorage.client.renderer;

import net.mcft.copy.betterstorage.client.model.ModelBackpack;
import net.mcft.copy.betterstorage.item.ItemBackpack;
import net.mcft.copy.betterstorage.misc.Resources;
import net.mcft.copy.betterstorage.tile.TileBackpack;
import net.mcft.copy.betterstorage.tile.entity.TileEntityBackpack;
import net.mcft.copy.betterstorage.utils.DirectionUtils;
import net.mcft.copy.betterstorage.utils.RenderUtils;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.ForgeDirection;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TileEntityBackpackRenderer extends TileEntitySpecialRenderer {
	
	public void renderTileEntityAt(TileEntityBackpack backpack, double x, double y, double z, float partialTicks) {
		
		if ((backpack.getWorldObj() == null) && (backpack.blockType == null)) return;
		ItemBackpack item = ((TileBackpack)backpack.getBlockType()).getItemType();
		ItemStack stack = ((backpack.stack != null) ? backpack.stack : new ItemStack(item));
		ModelBackpack backpackModel = item.getModel();
		
		GL11.glPushMatrix();
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		GL11.glTranslated(x, y + 2.0, z + 1.0);
		GL11.glScalef(1.0F, -1.0F, -1.0F);
		GL11.glTranslated(0.5, 0.5, 0.5);
		
		ForgeDirection orientation = ForgeDirection.getOrientation(backpack.getBlockMetadata());
		int rotation = DirectionUtils.getRotation(orientation);
		GL11.glRotatef(rotation, 0.0F, 1.0F, 0.0F);
		
		float angle = backpack.prevLidAngle + (backpack.lidAngle - backpack.prevLidAngle) * partialTicks;
		angle = 1.0F - angle;
		angle = 1.0F - angle * angle;
		backpackModel.setLidRotation((float)(angle * Math.PI / 4.0));
		
		int renderPasses = item.getRenderPasses(0);
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

			GL11.glEnable(GL11.GL_BLEND);
			GL11.glColor4f(0.5F, 0.5F, 0.5F, 1.0F);
			GL11.glDepthMask(false);
			GL11.glPolygonOffset(-1.0F, -1.0F);
			GL11.glEnable(GL11.GL_POLYGON_OFFSET_FILL);
			for (int k = 0; k < 2; ++k) {
				GL11.glDisable(GL11.GL_LIGHTING);
				float f11 = 0.65F;
				GL11.glColor4f(0.5F * f11, 0.25F * f11, 0.8F * f11, 1.0F);
				GL11.glBlendFunc(GL11.GL_SRC_COLOR, GL11.GL_ONE);
				GL11.glMatrixMode(GL11.GL_TEXTURE);
				GL11.glLoadIdentity();
				float f12 = f9 * (0.001F + k * 0.003F) * 20.0F;
				float f13 = 0.33333334F;
				GL11.glScalef(f13, f13, f13);
				GL11.glRotatef(30.0F - k * 60.0F, 0.0F, 0.0F, 1.0F);
				GL11.glTranslatef(0.0F, f12, 0.0F);
				GL11.glMatrixMode(GL11.GL_MODELVIEW);
				backpackModel.renderAll();
			}
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			GL11.glMatrixMode(GL11.GL_TEXTURE);
			GL11.glDisable(GL11.GL_POLYGON_OFFSET_FILL);
			GL11.glPolygonOffset(0.0F, 0.0F);
			GL11.glDepthMask(true);
			GL11.glLoadIdentity();
			GL11.glMatrixMode(GL11.GL_MODELVIEW);
			GL11.glEnable(GL11.GL_LIGHTING);
			GL11.glDisable(GL11.GL_BLEND);
		}
		
		GL11.glDisable(GL12.GL_RESCALE_NORMAL);
		GL11.glPopMatrix();
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		
	}
	
	@Override
	public void renderTileEntityAt(TileEntity entity, double x, double y, double z, float par8) {
		renderTileEntityAt((TileEntityBackpack)entity, x, y, z, par8);
	}
	
}
