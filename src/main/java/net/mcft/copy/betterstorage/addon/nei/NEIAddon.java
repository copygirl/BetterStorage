package net.mcft.copy.betterstorage.addon.nei;


import net.mcft.copy.betterstorage.addon.Addon;
import net.mcft.copy.betterstorage.client.gui.GuiCraftingStation;
import net.mcft.copy.betterstorage.content.BetterStorageTiles;
import net.mcft.copy.betterstorage.misc.Constants;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import codechicken.nei.api.API;
import codechicken.nei.recipe.DefaultOverlayHandler;
import cpw.mods.fml.common.FMLCommonHandler;

public class NEIAddon extends Addon {
	
	public NEIAddon() {
		super("NotEnoughItems");
	}

	@Override
	public void postInitialize() {	
		if (FMLCommonHandler.instance().getEffectiveSide().isServer()) return;
		
		NEIRecipeHandler handler = new NEIRecipeHandler();
		API.registerRecipeHandler(handler);
		API.registerUsageHandler(handler);
		
		API.registerGuiOverlay(GuiCraftingStation.class, Constants.modId + ".craftingStation");
		API.registerGuiOverlay(GuiCraftingStation.class, "crafting", -8, 11);
		
		API.registerGuiOverlayHandler(GuiCraftingStation.class, new DefaultOverlayHandler() {
			
			@Override
			public boolean canMoveFrom(Slot slot, GuiContainer gui) {
				return slot.slotNumber >= 18;
			}
			
		}, Constants.modId + ".craftingStation");
		
		API.registerGuiOverlayHandler(GuiCraftingStation.class, new DefaultOverlayHandler(-8, 11) {
			
			@Override
			public boolean canMoveFrom(Slot slot, GuiContainer gui) {
				return slot.slotNumber >= 18;
			}
			
		}, "crafting");
		
		API.hideItem(new ItemStack(BetterStorageTiles.lockableDoor));
	}
}
