package net.mcft.copy.betterstorage.client.model;

import net.mcft.copy.betterstorage.misc.Resources;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelChicken;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;

public class ModelCluckington extends ModelChicken {
	
	@Override
	public void render(Entity entity, float par2, float par3, float par4, float par5, float par6, float scale) {
		scale *= 1.2F;
		GlStateManager.pushMatrix();
        GlStateManager.translate(0.0F, -4.0F * scale, 0.0F);
		super.render(entity, par2, par3, par4, par5, par6, scale);
		Minecraft.getMinecraft().getTextureManager().bindTexture(Resources.textureCluckOverlay);
		GlStateManager.translate(0.0F, -0.05F, 0.0F);
		super.render(entity, par2, par3, par4, par5, par6, scale * 1.04F);
		GlStateManager.popMatrix();
	}
	
}
