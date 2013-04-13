package net.mcft.copy.betterstorage.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.mcft.copy.betterstorage.client.model.ModelBackpackArmor;
import net.mcft.copy.betterstorage.misc.Constants;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.item.EnumArmorMaterial;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;

public class ItemBackpack extends ItemArmor {
	
	@SideOnly(Side.CLIENT)
	private static final ModelBackpackArmor modelArmor = new ModelBackpackArmor();
	
	public ItemBackpack(int id) {
		super(id - 256, EnumArmorMaterial.CLOTH, 0, 1);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public ModelBiped getArmorModel(EntityLiving entity, ItemStack stack, int slot) { return modelArmor; }
	
	@Override
	public String getArmorTexture(ItemStack stack, Entity entity, int slot, int layer) { return Constants.backpackTexture; }
	
}
