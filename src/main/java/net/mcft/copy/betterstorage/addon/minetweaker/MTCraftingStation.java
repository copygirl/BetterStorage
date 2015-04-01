package net.mcft.copy.betterstorage.addon.minetweaker;

import minetweaker.IUndoableAction;
import minetweaker.MineTweakerAPI;
import minetweaker.api.item.IIngredient;
import minetweaker.api.item.IItemStack;
import net.mcft.copy.betterstorage.api.crafting.BetterStorageCrafting;
import net.mcft.copy.betterstorage.api.crafting.IStationRecipe;
import net.minecraft.item.ItemStack;
import stanhebben.zenscript.annotations.Optional;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenClass("mods.betterstorage.CraftingStation")
public class MTCraftingStation {
	
	@ZenMethod
	public static void addShaped(IItemStack[] output, IIngredient[][] input, @Optional int experience, @Optional int craftingTime) {
		addShaped(output, input, experience, craftingTime, false);
	}
	@ZenMethod
	public static void addShaped(IItemStack output, IIngredient[][] input, @Optional int experience, @Optional int craftingTime) {
		addShaped(new IItemStack[]{ null, null, null, null, output }, input, experience, craftingTime);
	}
	@ZenMethod
	public static void addShapedMirrored(IItemStack[] output, IIngredient[][] input, @Optional int experience, @Optional int craftingTime) {
		addShaped(output, input, experience, craftingTime, true);
	}
	@ZenMethod
	public static void addShapedMirrored(IItemStack output, IIngredient[][] input, @Optional int experience, @Optional int craftingTime) {
		addShapedMirrored(new IItemStack[]{ null, null, null, null, output }, input, experience, craftingTime);
	}
	private static void addShaped(IItemStack[] output, IIngredient[][] input, int experience, int craftingTime, boolean mirrored) {
		ItemStack[] recipeOutput = MTHelper.toStacks(output);
		IStationRecipe recipe = new MTShapedStationRecipe(input, recipeOutput, experience, craftingTime, mirrored);
		MineTweakerAPI.apply(new UndoableRecipeAddAction(recipe, recipeOutput));
	}
	
	@ZenMethod
	public static void addShapeless(IItemStack[] output, IIngredient[] input, @Optional int experience, @Optional int craftingTime) {
		ItemStack[] recipeOutput = MTHelper.toStacks(output);
		IStationRecipe recipe = new MTShapelessStationRecipe(input, recipeOutput, experience, craftingTime);
		MineTweakerAPI.apply(new UndoableRecipeAddAction(recipe, recipeOutput));
	}
	@ZenMethod
	public static void addShapeless(IItemStack output, IIngredient[] input, @Optional int experience, @Optional int craftingTime) {
		addShapeless(new IItemStack[]{ null, null, null, null, output }, input, experience, craftingTime);
	}
	
	private static class UndoableRecipeAddAction implements IUndoableAction {
		
		private final IStationRecipe recipe;
		private String desc;
		
		public UndoableRecipeAddAction(IStationRecipe recipe, ItemStack[] output) {
			this.recipe = recipe;
			desc = "[ ";
			for (int i = 0; i < output.length; i++) {
				if (output[i] == null) continue;
				desc += output[i].getDisplayName();
				if (i < output.length - 1)
					desc += ", ";
			}
			desc += " ]";
		}
		
		@Override
		public String describe() { return "Adding crafting station recipe for " + desc; }
		@Override
		public String describeUndo() { return "Removing crafting station recipe for " + desc; }
		@Override
		public boolean canUndo() { return true; }
		@Override
		public void apply() { BetterStorageCrafting.addStationRecipe(recipe); }
		@Override
		public void undo() { BetterStorageCrafting.recipes.remove(recipe); }
		@Override
		public Object getOverrideKey() { return null; }
		
	}
	
}
