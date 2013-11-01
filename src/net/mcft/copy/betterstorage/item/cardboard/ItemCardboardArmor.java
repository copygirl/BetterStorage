package net.mcft.copy.betterstorage.item.cardboard;

import net.mcft.copy.betterstorage.BetterStorage;
import net.mcft.copy.betterstorage.misc.Constants;
import net.mcft.copy.betterstorage.misc.Resources;
import net.mcft.copy.betterstorage.utils.StackUtils;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemCardboardArmor extends ItemArmor implements ICardboardItem {
	
	private static final String[] armorText = { "Helmet", "Chestplate", "Leggings", "Boots" };
	
	public ItemCardboardArmor(int id, int armorType) {
		super(id - 256, ItemCardboardSheet.armorMaterial, 0, armorType);
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
	
	@Override
	public boolean canDye(ItemStack stack) { return true; }
	
}
