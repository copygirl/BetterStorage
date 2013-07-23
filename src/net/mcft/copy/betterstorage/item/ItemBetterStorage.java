package net.mcft.copy.betterstorage.item;

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
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister iconRegister) {
		String name = getUnlocalizedName();
		itemIcon = iconRegister.registerIcon(Constants.modName + ":" + name.replace("item." + Constants.modName + ".", ""));
	}
	
}
