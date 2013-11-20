package net.mcft.copy.betterstorage.item.cardboard;

import net.mcft.copy.betterstorage.BetterStorage;
import net.mcft.copy.betterstorage.misc.Constants;
import net.mcft.copy.betterstorage.misc.Resources;
import net.mcft.copy.betterstorage.utils.StackUtils;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraftforge.common.ISpecialArmor;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemCardboardArmor extends ItemArmor implements ICardboardItem, ISpecialArmor {
	
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
	
	// ISpecialArmor implementation
	// Makes sure cardboard armor doesn't get destroyed,
	// and is ineffective when durability is at 0..
	@Override public ArmorProperties getProperties(EntityLivingBase player, ItemStack armor, DamageSource source, double damage, int slot) {
		return new ArmorProperties(0, damageReduceAmount / 25D, armor.getMaxDamage() - armor.getItemDamage()); }
	@Override public int getArmorDisplay(EntityPlayer player, ItemStack armor, int slot) {
		return ((armor.getItemDamage() < armor.getMaxDamage()) ? damageReduceAmount : 0); }
	@Override public void damageArmor(EntityLivingBase entity, ItemStack stack, DamageSource source, int damage, int slot) {
		ItemCardboardSheet.damageItem(stack, damage, entity); }
	
}
