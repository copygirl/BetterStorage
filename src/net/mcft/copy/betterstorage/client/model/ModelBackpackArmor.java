package net.mcft.copy.betterstorage.client.model;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.mcft.copy.betterstorage.item.ItemBackpack;
import net.mcft.copy.betterstorage.misc.PropertiesBackpack;
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
		
		bipedBody = new ModelRenderer(this);
		
		main = new ModelRenderer(this, 0, 8);
		main.addBox(-5F, 4F, 2.5F, 10, 9, 5);
		bipedBody.addChild(main);
		
		top = new ModelRenderer(this, 0, 0);
		top.addBox(-5F, -3F, 0F, 10, 3, 5);
		top.setRotationPoint(0F, 4F, 2.5F);
		bipedBody.addChild(top);
		
		front = new ModelRenderer(this, 0, 22);
		front.addBox(-4F, 6F, 7.5F, 8, 6, 2);
		bipedBody.addChild(front);
		
	}
	
	@Override
	public void setLivingAnimations(EntityLiving entity, float par2, float par3, float partialTicks) {
		PropertiesBackpack backpack = ItemBackpack.getBackpackData(entity);
		float angle = backpack.prevLidAngle + (backpack.lidAngle - backpack.prevLidAngle) * partialTicks;
		angle = 1.0F - angle;
		angle = 1.0F - angle * angle;
		top.rotateAngleX = (float)(angle * Math.PI / 4.0);
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
