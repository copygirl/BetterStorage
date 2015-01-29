package net.mcft.copy.betterstorage.addon.nei;

import net.mcft.copy.betterstorage.addon.Addon;
import net.mcft.copy.betterstorage.client.gui.GuiCraftingStation;
import net.mcft.copy.betterstorage.content.BetterStorageItems;
import net.mcft.copy.betterstorage.content.BetterStorageTiles;
import net.mcft.copy.betterstorage.misc.Constants;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.oredict.ShapedOreRecipe;
import codechicken.nei.api.API;
import codechicken.nei.recipe.DefaultOverlayHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.registry.GameRegistry;

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
		
		API.registerGuiOverlayHandler(GuiCraftingStation.class,
				new StationOverlayHandler(), Constants.modId + ".craftingStation");
		API.registerGuiOverlayHandler(GuiCraftingStation.class,
				new StationOverlayHandler(-8, 11), "crafting");
		
		API.hideItem(new ItemStack(BetterStorageTiles.lockableDoor));
		API.hideItem(new ItemStack(BetterStorageItems.presentBook));
		
		// Fake key recipes
		if (BetterStorageItems.key != null) {
			GameRegistry.addRecipe(new FakeShapedRecipe(new ItemStack(BetterStorageItems.key),
					".o",
					".o",
					" o", 'o', Items.gold_ingot,
					      '.', Items.gold_nugget));
			GameRegistry.addRecipe(new FakeShapedRecipe(new ItemStack(BetterStorageItems.key),
					".o ",
					".o ",
					" ok", 'o', Items.gold_ingot,
					       '.', Items.gold_nugget,
					       'k', BetterStorageItems.key));
		}
	}
	
	private static class StationOverlayHandler extends DefaultOverlayHandler {
		public StationOverlayHandler() {  }
		public StationOverlayHandler(int x, int y) { super(x, y); }
		
		@Override
		public boolean canMoveFrom(Slot slot, GuiContainer gui) {
			return (slot.slotNumber >= 18);
		}
	}
	
	private static class FakeShapedRecipe extends ShapedOreRecipe {
		public FakeShapedRecipe(ItemStack result, Object... recipe) { super(result, recipe); }
		
		@Override
		public boolean matches(InventoryCrafting crafting, World world) { return false; }
	}
}
