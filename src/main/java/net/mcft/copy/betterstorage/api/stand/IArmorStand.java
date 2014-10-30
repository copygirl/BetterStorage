package net.mcft.copy.betterstorage.api.stand;

import net.minecraft.item.ItemStack;

public interface IArmorStand {
	
	/** Returns an item on the armor stand for this handler. */
	public ItemStack getItem(ArmorStandEquipHandler handler);
	
	/** Sets an item on the armor stand for this handler. */
	public void setItem(ArmorStandEquipHandler handler, ItemStack item);
	
}
