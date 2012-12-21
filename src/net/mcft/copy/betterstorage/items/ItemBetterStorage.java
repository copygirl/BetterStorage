package net.mcft.copy.betterstorage.items;

import net.mcft.copy.betterstorage.Constants;
import net.minecraft.item.Item;

public abstract class ItemBetterStorage extends Item {
	
	public ItemBetterStorage(int id) {
		// Adjusts the ID so the item's config ID
		// actually represents the actual ID of the item.
		super(id - 256);
		setMaxStackSize(1);
	}
	
	@Override
	public String getTextureFile() { return Constants.items; }
	
}
