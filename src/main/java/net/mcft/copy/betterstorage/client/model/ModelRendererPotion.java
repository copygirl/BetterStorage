package net.mcft.copy.betterstorage.client.model;

import net.mcft.copy.betterstorage.utils.RenderUtils;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ModelRendererPotion extends ModelRenderer {
	
	public ItemStack stack = null;
	
	public ModelRendererPotion(ModelBase modelBase, boolean right) {
		super(modelBase);
		addChild(new Potion(modelBase));
		
		setRotationPoint((right ? -5.5F : 5.5F), -7, 1);
		rotateAngleX = (float)Math.PI;
		rotateAngleY = (float)Math.PI / (right ? -2 : 2);
	}
	
	private class Potion extends ModelRenderer {
		public Potion(ModelBase modelBase) { super(modelBase); }
		@Override
		@SideOnly(Side.CLIENT)
		public void render(float par1) {
			if (stack == null) return;
			GlStateManager.pushMatrix();
			GlStateManager.pushAttrib();
			GlStateManager.scale(0.5F, 0.5F, 0.5F);
			RenderUtils.renderItemIn3d(stack);
			GlStateManager.popAttrib();
			GlStateManager.popMatrix();
		}
	}
	
}
