package net.mcft.copy.betterstorage.misc;

import java.util.List;

import net.mcft.copy.betterstorage.BetterStorage;
import net.mcft.copy.betterstorage.api.BetterStorageEnchantment;
import net.minecraft.creativetab.CreativeTabs;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

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
		addEnchantmentBooksToList(list, BetterStorageEnchantment.getType("key"),
		                                BetterStorageEnchantment.getType("lock"));
	}
	
}
