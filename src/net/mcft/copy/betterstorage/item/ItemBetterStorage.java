package net.mcft.copy.betterstorage.item;

import java.util.Locale;

import net.mcft.copy.betterstorage.BetterStorage;
import net.mcft.copy.betterstorage.misc.Constants;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.item.Item;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public abstract class ItemBetterStorage extends Item {
	
	public ItemBetterStorage(int id) {
		
		// Adjusts the ID so the item's config ID
		// actually represents the actual ID of the item.
		super(id - 256);
		
		setMaxStackSize(1);
		setCreativeTab(BetterStorage.creativeTab);
		
		String name = getClass().getSimpleName();                                    // ItemMyItem
		name = name.substring(4, 5).toLowerCase(Locale.ENGLISH) + name.substring(5); // 'm' + "yItem"
		setUnlocalizedName(Constants.modId + "." + name);                          // modname.myItem
		
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister iconRegister) {
		String name = getUnlocalizedName();
		name = name.substring(name.lastIndexOf('.') + 1);
		itemIcon = iconRegister.registerIcon(Constants.modId + ":" + name);
	}
	
}
