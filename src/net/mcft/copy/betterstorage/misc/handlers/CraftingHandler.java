package net.mcft.copy.betterstorage.misc.handlers;

import net.mcft.copy.betterstorage.BetterStorage;
import net.mcft.copy.betterstorage.item.ItemKey;
import net.mcft.copy.betterstorage.item.ItemLock;
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
			
			// Get the key used in the crafting recipe, if there was one.
			int keyIndex = InventoryUtils.findItemSlot(craftMatrix, BetterStorage.key);
			ItemStack key = ((keyIndex >= 0) ? craftMatrix.getStackInSlot(keyIndex) : null);
			
			// See if a key was modified by checking if no gold was used in the recipe.
			boolean modifyKey = !InventoryUtils.hasItem(craftMatrix, Item.ingotGold);
			
			// If a key is being modified, remove it from the crafting matrix.
			if (modifyKey) craftMatrix.setInventorySlotContents(keyIndex, null);
			// Otherwise, if a new key is crafted (not duplicated),
			// set the the damege of the key to a random value.
			else if (key == null) item.setItemDamage(-32000 + BetterStorage.random.nextInt(64000));
			
		}
		// If item crafted is a lock, copy the damage value from the key.
		if (item.getItem() instanceof ItemLock)
			item.setItemDamage(craftMatrix.getStackInSlot(4).getItemDamage());
	}
	@Override
	public void onSmelting(EntityPlayer player, ItemStack item) {  }
	
}
