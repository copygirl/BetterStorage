package net.mcft.copy.betterstorage.misc.handlers;

import net.mcft.copy.betterstorage.content.BetterStorageItems;
import net.mcft.copy.betterstorage.item.locking.ItemKey;
import net.mcft.copy.betterstorage.utils.InventoryUtils;
import net.minecraft.init.Items;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.ItemCraftedEvent;

/** Handles key and lock crafting. */
public class CraftingHandler {
	
	public CraftingHandler() {
		MinecraftForge.EVENT_BUS.register(this);
		FMLCommonHandler.instance().bus().register(this);
	}
	
	@SubscribeEvent
	public void onItemCrafted(ItemCraftedEvent event) {
		// If item crafted is a key ...
		if (event.crafting.getItem() instanceof ItemKey) {
			
			// See if a key was modified by checking if no gold was used in the recipe.
			boolean modifyKey = !InventoryUtils.hasItem(event.craftMatrix, Items.gold_ingot);
			
			// If it is, remove it from the crafting matrix.
			if (modifyKey) {
				int keyIndex = InventoryUtils.findItemSlot(event.craftMatrix, BetterStorageItems.key);
				event.craftMatrix.setInventorySlotContents(keyIndex, null);
			}
			
		}
	}
	
}
