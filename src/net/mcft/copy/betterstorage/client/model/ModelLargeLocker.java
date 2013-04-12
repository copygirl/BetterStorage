package net.mcft.copy.betterstorage.client.model;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.model.ModelRenderer;

@SideOnly(Side.CLIENT)
public class ModelLargeLocker extends ModelLocker {
	
	@Override
	protected int getTextureHeight() { return 64; }
	
	public ModelLargeLocker(boolean mirror) {
		
		super(mirror);
		
		left = new ModelRenderer(this, 0, 17);
		left.addBox(0F, 0F, 0F, 16, 32, 1);
		left.setRotationPoint(-8F, -8F, 8F);
		setRotation(left, 0F, 1.570796F, 0F);
		
		back = new ModelRenderer(this, 0, 17);
		back.addBox(0F, 0F, 0F, 16, 32, 1);
		back.setRotationPoint(8F, -8F, 8F);
		setRotation(back, 0F, 3.141593F, 0F);
		
		right = new ModelRenderer(this, 0, 17);
		right.addBox(0F, 0F, 0F, 16, 32, 1);
		right.setRotationPoint(8F, -8F, -8F);
		setRotation(right, 0F, -1.570796F, 0F);
		
		bottom.setTextureSize(64, 64);
		
		top.setRotationPoint(-8F, -8F, 8F);
		top.setTextureSize(64, 64);
		
		front = new ModelRenderer(this, 34, 3);
		if (mirror) {
			front.addBox(-13F, 0F, -1F, 14, 30, 1);
			front.setRotationPoint(6F, -7F, -6F);
		} else {
			front.addBox(-1F, 0F, -1F, 14, 30, 1);
			front.setRotationPoint(-6F, -7F, -6F);
		}
		
		handle = new ModelRenderer(this, 34, 0);
		if (mirror) {
			handle.addBox(-12F, 14F, -2F, 1, 2, 1);
			handle.setRotationPoint(6F, -7F, -6F);
		} else {
			handle.addBox(11F, 14F, -2F, 1, 2, 1);
			handle.setRotationPoint(-6F, -7F, -6F);
		}
		
	}
	
}
