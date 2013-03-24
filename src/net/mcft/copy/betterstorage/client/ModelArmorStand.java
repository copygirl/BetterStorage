package net.mcft.copy.betterstorage.client;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;

@SideOnly(Side.CLIENT)
public class ModelArmorStand extends ModelBase {
	
	ModelRenderer bottom;
	ModelRenderer middle;
	
	ModelRenderer head;
	ModelRenderer shoulder;
	ModelRenderer legs;
	
	public ModelArmorStand() {
		textureWidth = 64;
		textureHeight = 32;
		
		bottom = new ModelRenderer(this, 4, 0);
		bottom.addBox(-6F, 0F, -6F, 12, 1, 12);
		bottom.setRotationPoint(0F, 23F, 0F);
		bottom.setTextureSize(64, 32);
		
		middle = new ModelRenderer(this, 0, 0);
		middle.addBox(-0.5F, 0.5F, -0.5F, 1, 31, 1);
		middle.setRotationPoint(0F, -8F, 0F);
		middle.setTextureSize(64, 32);
		
		head = new ModelRenderer(this, 4, 9);
		head.addBox(-2F, 0F, -1F, 4, 1, 2);
		head.setRotationPoint(0F, -8F, 0F);
		head.setTextureSize(64, 32);
		
		shoulder = new ModelRenderer(this, 4, 13);
		shoulder.addBox(-6F, 0F, -1F, 12, 1, 2);
		shoulder.setRotationPoint(0F, 0F, 0F);
		shoulder.setTextureSize(64, 32);
		
		legs = new ModelRenderer(this, 4, 16);
		legs.addBox(-3F, 0F, -1F, 6, 1, 2);
		legs.setRotationPoint(0F, 8F, 0F);
		legs.setTextureSize(64, 32);
	}
	
	public void renderAll() {
		float foo = 1 / 16.0F;
		bottom.render(foo);
		middle.render(foo);
		head.render(foo);
		shoulder.render(foo);
		legs.render(foo);
	}
	
	private void setRotation(ModelRenderer model, float x, float y, float z) {
		model.rotateAngleX = x;
		model.rotateAngleY = y;
		model.rotateAngleZ = z;
	}
	
}
