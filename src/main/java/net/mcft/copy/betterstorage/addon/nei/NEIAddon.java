package net.mcft.copy.betterstorage.addon.nei;


import net.mcft.copy.betterstorage.addon.Addon;
import net.mcft.copy.betterstorage.client.gui.GuiCraftingStation;
import net.mcft.copy.betterstorage.content.BetterStorageTiles;
import net.mcft.copy.betterstorage.misc.Constants;
import net.minecraft.item.ItemStack;
import codechicken.nei.api.API;
import codechicken.nei.recipe.DefaultOverlayHandler;

public class NEIAddon extends Addon {
	
	public NEIAddon() {
		super("NotEnoughItems");
	}

	@Override
	public void postInitialize() {	
		
		NEIRecipeHandler handler = new NEIRecipeHandler();
		API.registerRecipeHandler(handler);
		API.registerUsageHandler(handler);
		
		API.registerGuiOverlay(GuiCraftingStation.class, Constants.modId + ".craftingStation");
		API.registerGuiOverlay(GuiCraftingStation.class, "crafting", -8, 11);
		API.registerGuiOverlayHandler(GuiCraftingStation.class, new DefaultOverlayHandler(), Constants.modId + ".craftingStation");
		API.registerGuiOverlayHandler(GuiCraftingStation.class, new DefaultOverlayHandler(-8, 11), "crafting");
		
		API.hideItem(new ItemStack(BetterStorageTiles.lockableDoor));
	}
}
