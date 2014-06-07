package net.mcft.copy.betterstorage.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;

public class ModelLockableDoor extends ModelBase {

	private ModelRenderer main;
	
	private ModelRenderer frameUpper;
	private ModelRenderer frameLower;
	private ModelRenderer frameLeft;
	private ModelRenderer frameRight;
	
	public ModelLockableDoor() {
		
		textureWidth = 64;
		textureHeight = 64;
		
		main = new ModelRenderer(this, 0, 0);
		frameUpper = new ModelRenderer(this, 30, 34);
		frameLower = new ModelRenderer(this, 30, 34);
		frameLeft = new ModelRenderer(this, 30, 0);
		frameRight = new ModelRenderer(this, 39, 0);
		
		main.addBox(-0.5F, 1F, -13.5F, 1, 30, 14);
		main.setRotationPoint(-6.5F,  -8F, 6.5F);
			
		frameUpper.addBox(-1.5F, 0F, -13.5F, 3, 1, 14);
		frameUpper.setRotationPoint(-6.5F, -8F, 6.5F);
		
		frameLower.addBox(-1.5F, 31F, -13.5F, 3, 1, 14);
		frameLower.setRotationPoint(-6.5F, -8F, 6.5F);
		
		frameLeft.addBox(-1.5F, 0F, 0.5F, 3, 32, 1);
		frameLeft.setRotationPoint(-6.5F, -8F, 6.5F);
		
		frameRight.addBox(-1.5F, 0F, -14.5F, 3, 32, 1);
		frameRight.setRotationPoint(-6.5F, -8F, 6.5F);
		
	}
	
	public void renderAll() {
		float foo = 1 / 16.0F;
		
		main.render(foo);
		frameUpper.render(foo);
		frameLower.render(foo);
		frameLeft.render(foo);
		frameRight.render(foo);
	}
	
	public void setRotation(float rotation) {
		rotation = rotation / 360 * ((float)Math.PI * 2);
		main.rotateAngleY = rotation;
		frameUpper.rotateAngleY = rotation;
		frameLower.rotateAngleY = rotation;
		frameLeft.rotateAngleY = rotation;
		frameRight.rotateAngleY = rotation;
	}
	
}
