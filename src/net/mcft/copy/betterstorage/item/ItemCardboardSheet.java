package net.mcft.copy.betterstorage.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IconRegister;

public class ItemCardboardSheet extends ItemBetterStorage {
	
	public ItemCardboardSheet(int id) {
		super(id);
		setMaxStackSize(8);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister iconRegister) {
		itemIcon = iconRegister.registerIcon("betterstorage:cardboard_sheet");
	}
	
}
