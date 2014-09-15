package net.mcft.copy.betterstorage.client.gui;

import net.minecraft.item.Item;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public interface IItemDnD {
	public void onHover(int x, int y, Item item);
	public boolean onItemDropped(int x, int y, Item item);
	public void onItemPicked(int x, int y);
	public Item getPickedItem(int x, int y);
}