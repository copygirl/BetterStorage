package net.mcft.copy.betterstorage.client.model;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;

@SideOnly(Side.CLIENT)
public class ModelLocker extends ModelBase {
	
	protected boolean mirror;
	
	protected ModelRenderer left;
	protected ModelRenderer back;
	protected ModelRenderer right;
	protected ModelRenderer bottom;
	protected ModelRenderer top;
	protected ModelRenderer front;
	protected ModelRenderer handle;
	
	protected int getTextureWidth() { return 64; }
	protected int getTextureHeight() { return 32; }
	
	public ModelLocker(boolean mirror) {
		
		this.mirror = mirror;
		
		textureWidth = getTextureWidth();
		textureHeight = getTextureHeight();
		
		left = new ModelRenderer(this, 0, 0);
		left.addBox(0F, 0F, 0F, 16, 16, 1);
		left.setRotationPoint(-8F, 8F, 8F);
		setRotation(left, 0F, 1.570796F, 0F);
		
		back = new ModelRenderer(this, 0, 0);
		back.addBox(0F, 0F, 0F, 16, 16, 1);
		back.setRotationPoint(8F, 8F, 8F);
		setRotation(back, 0F, 3.141593F, 0F);
		
		right = new ModelRenderer(this, 0, 0);
		right.addBox(0F, 0F, 0F, 16, 16, 1);
		right.setRotationPoint(8F, 8F, -8F);
		setRotation(right, 0F, -1.570796F, 0F);
		
		bottom = new ModelRenderer(this, 0, 0);
		bottom.addBox(0F, 0F, 0F, 16, 16, 1);
		bottom.setRotationPoint(-8F, 24F, -8F);
		setRotation(bottom, 1.570796F, 0F, 0F);
		
		top = new ModelRenderer(this, 0, 0);
		top.addBox(0F, 0F, 0F, 16, 16, 1);
		top.setRotationPoint(-8F, 8F, 8F);
		setRotation(top, -1.570796F, 0F, 0F);
		
		front = new ModelRenderer(this, 34, 3);
		if (mirror) {
			front.addBox(-13F, 0F, -1F, 14, 14, 1);
			front.setRotationPoint(6F, 9F, -6F);
		} else {
			front.addBox(-1F, 0F, -1F, 14, 14, 1);
			front.setRotationPoint(-6F, 9F, -6F);
		}
		
		handle = new ModelRenderer(this, 34, 0);
		handle.setTextureSize(64, 32);
		if (mirror) {
			handle.addBox(-12F, 6F, -2F, 1, 2, 1);
			handle.setRotationPoint(6F, 9F, -6F);
		} else {
			handle.addBox(11F, 6F, -2F, 1, 2, 1);
			handle.setRotationPoint(-6F, 9F, -6F);
		}

	}
	
	protected void setRotation(ModelRenderer model, float x, float y, float z) {
		model.rotateAngleX = x;
		model.rotateAngleY = y;
		model.rotateAngleZ = z;
	}
	
	public void renderAll() {
		float foo = 1 / 16.0F;
		left.render(foo);
		back.render(foo);
		right.render(foo);
		bottom.render(foo);
		top.render(foo);
		front.render(foo);
		handle.render(foo);
	}
	
	public void setDoorRotation(float angle) {
		if (mirror) angle *= -1;
		front.rotateAngleY = angle;
		handle.rotateAngleY = angle;
	}
	
}
