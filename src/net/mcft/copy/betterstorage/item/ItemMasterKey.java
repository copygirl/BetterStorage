package net.mcft.copy.betterstorage.item;

import java.util.List;

import net.mcft.copy.betterstorage.api.BetterStorageEnchantment;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemMasterKey extends ItemKey {
	
	public ItemMasterKey(int id) {
		super(id);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(int id, CreativeTabs creativeTab, List list) {
		ItemStack item = new ItemStack(this, 1, 0);
		Enchantment ench = BetterStorageEnchantment.get("unlocking");
		if (ench != null) item.addEnchantment(ench, 10);
		list.add(item);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public EnumRarity getRarity(ItemStack stack) { return EnumRarity.rare; }
	
	@Override
	@SideOnly(Side.CLIENT)
	public boolean hasEffect(ItemStack stack, int pass) { return true; }
	
	@Override
	public void onCreated(ItemStack stack, World world, EntityPlayer player) {  }
	@Override
	public void onUpdate(ItemStack stack, World world, Entity entity, int slot, boolean isBeingHeld) {  }
	
	@SideOnly(Side.CLIENT)
	@Override
	public boolean requiresMultipleRenderPasses() { return false; }
	@SideOnly(Side.CLIENT)
	@Override
	public int getColorFromItemStack(ItemStack stack, int renderPass) { return 0xFFFFFF; }
	@Override
	public Icon getIcon(ItemStack stack, int renderPass) { return itemIcon; }
	
	// IKey implementation
	
	@Override
	public boolean unlock(ItemStack key, ItemStack lock, boolean useAbility) { return true; }
	@Override
	public boolean canApplyEnchantment(ItemStack key, Enchantment enchantment) { return false; }
	
}
