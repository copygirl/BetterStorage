package net.mcft.copy.betterstorage.block.crate;

import net.mcft.copy.betterstorage.misc.ItemIdentifier;

public interface ICrateWatcher {
	
	public void onCrateItemsModified(ItemIdentifier item, int amount);
	
}
