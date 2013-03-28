package net.mcft.copy.betterstorage.misc;

import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.mcft.copy.betterstorage.BetterStorage;
import net.mcft.copy.betterstorage.enchantment.EnchantmentBetterStorage;
import net.minecraft.creativetab.CreativeTabs;

public class CreativeTabBetterStorage extends CreativeTabs {
	
	public CreativeTabBetterStorage() {
		super("betterstorage");
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public int getTabIconItemIndex() {
		return BetterStorage.crate.blockID;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void displayAllReleventItems(List list) {
		super.displayAllReleventItems(list);
		func_92116_a(list, EnchantmentBetterStorage.key, EnchantmentBetterStorage.lock);
	}
	
}
