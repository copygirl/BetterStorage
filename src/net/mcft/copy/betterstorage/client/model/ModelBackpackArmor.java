package net.mcft.copy.betterstorage.client.model;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;

@SideOnly(Side.CLIENT)
public class ModelBackpackArmor extends ModelBiped {
	
	public static final ModelBackpackArmor instance = new ModelBackpackArmor();
	
	private ModelRenderer main;
	private ModelRenderer top;
	private ModelRenderer front;
	
	public ModelBackpackArmor() {
		
		textureWidth = 32;
		textureHeight = 32;
		
		main = new ModelRenderer(this, 0, 8);
		main.addBox(-5F, 4F, 2F, 10, 9, 5);
		main.setRotationPoint(0F, 0F, 0F);
		bipedBody.addChild(main);
		
		top = new ModelRenderer(this, 0, 0);
		top.addBox(-5F, 1F, 2F, 10, 3, 5);
		top.setRotationPoint(0F, 0F, 0F);
		bipedBody.addChild(top);
		
		front = new ModelRenderer(this, 0, 22);
		front.addBox(-4F, 6F, 7F, 8, 6, 2);
		front.setRotationPoint(0F, 0F, 0F);
		bipedBody.addChild(front);
		
	}
	
	@Override
	public void render(Entity entity, float v1, float v2, float v3, float v4, float v5, float v6) {
		setRotationAngles(v1, v2, v3, v4, v5, v6, entity);
		float foo = 1 / 20.0F;
		bipedBody.render(foo);
	}
	
	@Override
	public void setRotationAngles(float v1, float v2, float v3, float v4, float v5, float v6, Entity entity) {
		// For some reason this is not properly updated.
		isSneak = ((EntityLiving)entity).isSneaking();
		super.setRotationAngles(v1, v2, v3, v4, v5, v6, entity);
	}
	
}
