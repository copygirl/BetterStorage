package net.mcft.copy.betterstorage.addon.minetweaker;

import java.util.concurrent.atomic.AtomicInteger;

import minetweaker.IUndoableAction;
import minetweaker.MineTweakerAPI;
import minetweaker.api.item.IIngredient;
import minetweaker.api.item.IItemStack;
import net.mcft.copy.betterstorage.api.crafting.BetterStorageCrafting;
import net.mcft.copy.betterstorage.api.crafting.IRecipeInput;
import net.mcft.copy.betterstorage.api.crafting.IStationRecipe;
import net.mcft.copy.betterstorage.api.crafting.ShapedStationRecipe;
import net.mcft.copy.betterstorage.api.crafting.ShapelessStationRecipe;
import net.minecraft.item.ItemStack;
import stanhebben.zenscript.annotations.Optional;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenClass("mods.betterstorage.CraftingStation")
public class MTCraftingStation {
	
	@ZenMethod
	public static void addShaped(IItemStack[] output, @Optional int experience, IIngredient[][] input) {
		addShaped(output, experience, false, input);
	}
	@ZenMethod
	public static void addShapedMirrored(IItemStack[] output, @Optional int experience, IIngredient[][] input) {
		addShaped(output, experience, true, input);
	}
	private static void addShaped(IItemStack[] output, int experience, boolean mirrored, IIngredient[][] input) {
		AtomicInteger width = new AtomicInteger();
		AtomicInteger height = new AtomicInteger();
		IRecipeInput[] recipeInput = MTHelper.toRecipeInputs(input, width, height);
		ItemStack[] recipeOutput = ((output.length > 1) ? MTHelper.toStacks(output)
		                                                : new ItemStack[]{ null, null, null, null, MTHelper.toStack(output[0]) });
		
		ShapedStationRecipe recipe = new ShapedStationRecipe(recipeInput, width.get(), height.get(), mirrored, recipeOutput).setRequiredExperience(experience);
		MineTweakerAPI.apply(new UndoableRecipeAddAction(recipe, recipeOutput));
	}
	
	@ZenMethod
	public static void addShapeless(IItemStack[] output, @Optional int experience, IIngredient[] input) {
		IRecipeInput[] recipeInput = MTHelper.toRecipeInputs(input);
		ItemStack[] recipeOutput = ((output.length > 1) ? MTHelper.toStacks(output)
		                                                : new ItemStack[]{ null, null, null, null, MTHelper.toStack(output[0]) });
		
		ShapelessStationRecipe recipe = new ShapelessStationRecipe(recipeInput, recipeOutput).setRequiredExperience(experience);
		MineTweakerAPI.apply(new UndoableRecipeAddAction(recipe, recipeOutput));
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
