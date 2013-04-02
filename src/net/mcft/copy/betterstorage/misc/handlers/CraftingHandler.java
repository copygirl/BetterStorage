package net.mcft.copy.betterstorage.misc.handlers;

import net.mcft.copy.betterstorage.BetterStorage;
import net.mcft.copy.betterstorage.item.ItemKey;
import net.mcft.copy.betterstorage.utils.InventoryUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.common.ICraftingHandler;

/** Handles key and lock crafting. */
public class CraftingHandler implements ICraftingHandler {
	
	@Override
	public void onCrafting(EntityPlayer player, ItemStack item, IInventory craftMatrix) {
		// If item crafted is a key ...
		if (item.getItem() instanceof ItemKey) {
			
			// See if a key was modified by checking if no gold was used in the recipe.
			boolean modifyKey = !InventoryUtils.hasItem(craftMatrix, Item.ingotGold);
			
			// If it is, remove it from the crafting matrix.
			if (modifyKey) {
				int keyIndex = InventoryUtils.findItemSlot(craftMatrix, BetterStorage.key);
				craftMatrix.setInventorySlotContents(keyIndex, null);
			}
			
		}
	}
	
	@Override
	public void onSmelting(EntityPlayer player, ItemStack item) {  }
	
}
