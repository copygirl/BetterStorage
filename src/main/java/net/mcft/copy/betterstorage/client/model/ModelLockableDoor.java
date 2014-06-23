package net.mcft.copy.betterstorage.client.model;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraftforge.common.util.ForgeDirection;

public class ModelLockableDoor extends ModelBase {

	private ModelRenderer main;
	
	public ModelLockableDoor() {
		
		textureWidth = 64;
		textureHeight = 64;
		
		main = new ModelRenderer(this, 0, 0);
		
		main.addBox(-1.5F, 0F, -14.5F, 3, 32, 16);
		main.setRotationPoint(-6.5F, -8F, 6.5F);
	}
	
	public void renderAll() {
		float foo = 1 / 16.0F;		
		main.render(foo);
	}
	
	public void setOrientation(ForgeDirection orientation) {
		float x, y, z;
		
		switch (orientation) {
		case WEST: x = -6.5F; y = -8F; z = 6.5F; break;
		case EAST: x = 6.5F; y = -8F; z = -6.5F; break;
		case SOUTH: x = -6.5F; y = -8F; z = -6.5F; break;
		default: x = 6.5F; y = -8F; z = 6.5F; break;
		}
		
		main.setRotationPoint(x, y, z);
	}
	
	public void setRotation(float rotation) {
		rotation = rotation / 360 * ((float)Math.PI * 2);
		main.rotateAngleY = rotation;
	}
	
}
