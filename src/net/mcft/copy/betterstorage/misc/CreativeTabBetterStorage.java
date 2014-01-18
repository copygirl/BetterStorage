package net.mcft.copy.betterstorage.misc;

import java.util.List;

import net.mcft.copy.betterstorage.api.BetterStorageEnchantment;
import net.mcft.copy.betterstorage.content.Tiles;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class CreativeTabBetterStorage extends CreativeTabs {
	
	public CreativeTabBetterStorage() {
		super(Constants.modId);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public int getTabIconItemIndex() {
		if (Tiles.crate != null) return Tiles.crate.blockID;
		else if (Tiles.backpack != null) return Tiles.backpack.blockID;
		else if (Tiles.reinforcedChest != null) return Tiles.reinforcedChest.blockID;
		else return Block.chest.blockID;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void displayAllReleventItems(List list) {
		super.displayAllReleventItems(list);
		addEnchantmentBooksToList(list, BetterStorageEnchantment.getType("key"),
		                                BetterStorageEnchantment.getType("lock"));
	}
	
}
