package net.mcft.copy.betterstorage.client.model;

import net.mcft.copy.betterstorage.item.ItemDrinkingHelmet;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelDrinkingHelmet extends ModelBiped {
	
	public static final ModelDrinkingHelmet instance = new ModelDrinkingHelmet();
	
	private final ModelRendererPotion left;
	private final ModelRendererPotion right;
	
	private int pass = 0;
	
	public ModelDrinkingHelmet() {
		super(1);
		left = new ModelRendererPotion(this, true);
		right = new ModelRendererPotion(this, false);
		bipedHead.addChild(left);
		bipedHead.addChild(right);
	}
	@Override
	public void render(Entity entity, float v1, float v2, float v3, float v4, float v5, float v6) {
		ItemStack[] potions = new ItemStack[2];
		if (entity instanceof EntityLivingBase) {
			ItemStack drinkingHelmet = ItemDrinkingHelmet.getDrinkingHelmet((EntityLivingBase)entity);
			// Ugly hack-ish thing to prevent potions from
			// rendering in the enchantment effect render pass. 
			if (!drinkingHelmet.isItemEnchanted() || (pass++ == 0))
				potions = ItemDrinkingHelmet.getPotions(drinkingHelmet);
			else if (pass > 2) pass = 0;
		}
		left.stack = potions[0];
		right.stack = potions[1];
		
		setRotationAngles(v1, v2, v3, v4, v5, v6, entity);
		bipedHead.render(v6);
	}
	
}
