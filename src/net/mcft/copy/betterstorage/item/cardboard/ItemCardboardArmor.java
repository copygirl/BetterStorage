package net.mcft.copy.betterstorage.item.cardboard;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.mcft.copy.betterstorage.BetterStorage;
import net.mcft.copy.betterstorage.content.Items;
import net.mcft.copy.betterstorage.misc.Constants;
import net.mcft.copy.betterstorage.misc.Resources;
import net.mcft.copy.betterstorage.utils.StackUtils;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.item.EnumArmorMaterial;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.EnumHelper;

public class ItemCardboardArmor extends ItemArmor {
	
	private static final String[] armorText = { "Helmet", "Chestplate", "Leggings", "Boots" };
	
	public static final EnumArmorMaterial material = EnumHelper.addArmorMaterial(
			"cardboard", 3, new int[]{ 1, 2, 2, 1 }, 20);
	static { material.customCraftingMaterial = Items.cardboardSheet; }
	
	public ItemCardboardArmor(int id, int armorType) {
		super(id - 256, material, 0, armorType);
		setCreativeTab(BetterStorage.creativeTab);
		setUnlocalizedName(Constants.modId + ".cardboard" + armorText[armorType]);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister iconRegister) {
		itemIcon = iconRegister.registerIcon(Constants.modId + ":cardboard" + armorText[armorType]);
	}
	
	@Override
	public String getArmorTexture(ItemStack stack, Entity entity, int slot, String type) {
		return ((type != null) ? Resources.emptyTexture
		                       : ((slot == 2) ? Resources.cardboardLegginsTexture :
		                                        Resources.cardboardArmorTexture)).toString();
	}
	
	@Override
	public int getColor(ItemStack stack) { return StackUtils.get(stack, 0x705030, "display", "color"); }
	
}
