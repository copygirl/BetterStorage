package net.mcft.copy.betterstorage.client.model;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;

@SideOnly(Side.CLIENT)
public class ModelBackpack extends ModelBase {
	
	ModelRenderer main;
	ModelRenderer top;
	ModelRenderer front;
	ModelRenderer right;
	ModelRenderer left;
	
	public ModelBackpack() {
		
		textureWidth = 32;
		textureHeight = 32;
		
		main = new ModelRenderer(this, 0, 8);
		main.addBox(-5F, 0F, -3F, 10, 9, 5);
		main.setRotationPoint(0F, 15F, 0F);
		
		top = new ModelRenderer(this, 0, 0);
		top.addBox(-5F, -3F, 0F, 10, 3, 5);
		top.setRotationPoint(0F, 15F, -3F);
		
		front = new ModelRenderer(this, 0, 22);
		front.addBox(-4F, 0F, 0F, 8, 6, 2);
		front.setRotationPoint(0F, 17F, 2F);
		
		right = new ModelRenderer(this, 20, 22);
		right.addBox(0F, 0F, -1F, 1, 8, 1);
		right.setRotationPoint(-4F, 13F, -3F);
		
		left = new ModelRenderer(this, 20, 22);
		left.addBox(0F, 0F, -1F, 1, 8, 1);
		left.setRotationPoint(3F, 13F, -3F);
		
	}
	
	protected void setRotation(ModelRenderer model, float x, float y, float z) {
		model.rotateAngleX = x;
		model.rotateAngleY = y;
		model.rotateAngleZ = z;
	}
	
	public void renderAll() {
		float foo = 1 / 16.0F;
		main.render(foo);
		top.render(foo);
		front.render(foo);
		right.render(foo);
		left.render(foo);
	}
	
	public void setLidRotation(float angle) {
		top.rotateAngleX = angle;
	}
	
}
