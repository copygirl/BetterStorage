package net.mcft.copy.betterstorage.misc;

import java.util.List;

import net.mcft.copy.betterstorage.api.BetterStorageEnchantment;
import net.mcft.copy.betterstorage.content.BetterStorageItems;
import net.mcft.copy.betterstorage.content.BetterStorageTiles;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class CreativeTabBetterStorage extends CreativeTabs {
	
	public CreativeTabBetterStorage() {
		super(Constants.modId);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public Item getTabIconItem() {
		if (BetterStorageTiles.crate != null)
			return Item.getItemFromBlock(BetterStorageTiles.crate);
		else if (BetterStorageTiles.backpack != null)
			return BetterStorageItems.itemBackpack;
		else if (BetterStorageTiles.reinforcedChest != null)
			return Item.getItemFromBlock(BetterStorageTiles.reinforcedChest);
		else return Item.getItemFromBlock(Blocks.chest);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void displayAllReleventItems(List list) {
		super.displayAllReleventItems(list);
		addEnchantmentBooksToList(list, BetterStorageEnchantment.getType("key"),
		                                BetterStorageEnchantment.getType("lock"));
	}
	
}
