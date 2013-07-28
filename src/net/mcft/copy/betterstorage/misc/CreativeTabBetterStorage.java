package net.mcft.copy.betterstorage.misc;

import java.util.List;

import net.mcft.copy.betterstorage.api.BetterStorageEnchantment;
import net.mcft.copy.betterstorage.content.Blocks;
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
		if (Blocks.crate != null) return Blocks.crate.blockID;
		else if (Blocks.backpack != null) return Blocks.backpack.blockID;
		else if (Blocks.reinforcedChest != null) return Blocks.reinforcedChest.blockID;
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
