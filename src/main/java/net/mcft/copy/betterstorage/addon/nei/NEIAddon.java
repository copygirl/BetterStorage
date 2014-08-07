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
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.world.World;
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
		
		//Fake key recipe
		GameRegistry.addRecipe(new FakedShapedRecipe(2, 3, new ItemStack[]{
				new ItemStack(Items.gold_nugget), new ItemStack(Items.gold_ingot),
				new ItemStack(Items.gold_nugget), new ItemStack(Items.gold_ingot),
				null, new ItemStack(Items.gold_ingot)}, 
				new ItemStack(BetterStorageItems.key)));
		
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
	}
	
	private static class StationOverlayHandler extends DefaultOverlayHandler {
		public StationOverlayHandler() {  }
		public StationOverlayHandler(int x, int y) { super(x, y); }
		
		@Override
		public boolean canMoveFrom(Slot slot, GuiContainer gui) {
			return (slot.slotNumber >= 18);
		}
	}
	
	private static class FakedShapedRecipe extends ShapedRecipes {
		public FakedShapedRecipe(int width, int height, ItemStack[] input, ItemStack output) {
			super(width, height, input, output);
		}

		@Override
		public boolean matches(InventoryCrafting crafting, World world) { return false; }
	}
}
