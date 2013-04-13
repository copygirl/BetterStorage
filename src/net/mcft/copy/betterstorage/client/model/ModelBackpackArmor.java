package net.mcft.copy.betterstorage.client.model;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.mcft.copy.betterstorage.BetterStorage;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.MathHelper;

@SideOnly(Side.CLIENT)
public class ModelBackpackArmor extends ModelBiped {
	
	private ModelRenderer main;
	private ModelRenderer top;
	private ModelRenderer front;
	
	public ModelBackpackArmor() {
		
		textureWidth = 32;
		textureHeight = 32;
		
		main = new ModelRenderer(this, 0, 8);
		main.addBox(-5F, 4F, 2F, 10, 9, 5);
		main.setRotationPoint(0F, 0F, 0F);
		
		top = new ModelRenderer(this, 0, 0);
		top.addBox(-5F, 1F, 2F, 10, 3, 5);
		top.setRotationPoint(0F, 0F, 0F);
		
		front = new ModelRenderer(this, 0, 22);
		front.addBox(-4F, 6F, 7F, 8, 6, 2);
		front.setRotationPoint(0F, 0F, 0F);
		
	}
	
	@Override
	public void render(Entity entity, float v1, float v2, float v3, float v4, float v5, float v6) {
		setRotationAngles(v1, v2, v3, v4, v5, v6, entity);
		float foo = 1 / 20.0F;
		main.render(foo);
		top.render(foo);
		front.render(foo);
	}
	
	@Override
	public void setRotationAngles(float v1, float v2, float v3, float v4, float v5, float v6, Entity entity) {
		// For some reason this is not properly updated.
		isSneak = ((EntityLiving)entity).isSneaking();
		
		super.setRotationAngles(v1, v2, v3, v4, v5, v6, entity);
		
		main.rotateAngleX = top.rotateAngleX = front.rotateAngleX = bipedBody.rotateAngleX;
		main.rotateAngleY = top.rotateAngleY = front.rotateAngleY = bipedBody.rotateAngleY;
		main.rotateAngleZ = top.rotateAngleZ = front.rotateAngleZ = bipedBody.rotateAngleZ;
	}
	
}
