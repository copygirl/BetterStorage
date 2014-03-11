package net.mcft.copy.betterstorage.misc.handlers;

import net.mcft.copy.betterstorage.content.BetterStorageItems;
import net.mcft.copy.betterstorage.item.locking.ItemKey;
import net.mcft.copy.betterstorage.utils.InventoryUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

/** Handles key and lock crafting. */
public class CraftingHandler implements ICraftingHandler {
	
	// FIXME
	
	@Override
	public void onCrafting(EntityPlayer player, ItemStack item, IInventory craftMatrix) {
		// If item crafted is a key ...
		if (item.getItem() instanceof ItemKey) {
			
			// See if a key was modified by checking if no gold was used in the recipe.
			boolean modifyKey = !InventoryUtils.hasItem(craftMatrix, Items.gold_ingot);
			
			// If it is, remove it from the crafting matrix.
			if (modifyKey) {
				int keyIndex = InventoryUtils.findItemSlot(craftMatrix, BetterStorageItems.key);
				craftMatrix.setInventorySlotContents(keyIndex, null);
			}
			
		}
	}
	
	@Override
	public void onSmelting(EntityPlayer player, ItemStack item) {  }
	
}
