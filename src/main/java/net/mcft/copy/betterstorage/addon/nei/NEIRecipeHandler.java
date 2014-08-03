package net.mcft.copy.betterstorage.addon.nei;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.mcft.copy.betterstorage.api.crafting.BetterStorageCrafting;
import net.mcft.copy.betterstorage.api.crafting.IRecipeInput;
import net.mcft.copy.betterstorage.api.crafting.IStaticStationRecipe;
import net.mcft.copy.betterstorage.api.crafting.IStationRecipe;
import net.mcft.copy.betterstorage.api.crafting.RecipeInputItemStack;
import net.mcft.copy.betterstorage.api.crafting.RecipeInputOreDict;
import net.mcft.copy.betterstorage.api.crafting.ShapedStationRecipe;
import net.mcft.copy.betterstorage.client.gui.GuiCraftingStation;
import net.mcft.copy.betterstorage.misc.Constants;
import net.mcft.copy.betterstorage.misc.Resources;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import org.lwjgl.opengl.GL11;

import codechicken.lib.gui.GuiDraw;
import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.RecipeInfo;
import codechicken.nei.recipe.TemplateRecipeHandler;

public class NEIRecipeHandler extends TemplateRecipeHandler {

	private static HashMap<RecipeInputItemStack, ArrayList<IStaticStationRecipe>> cachedRecipes = 
			new HashMap<RecipeInputItemStack, ArrayList<IStaticStationRecipe>>();
	
	//Used because the recipes have to be cached after every other mod has loaded.
	private static boolean initialized = false;
	
	private static void initialize() {
		for (IStationRecipe recipe : BetterStorageCrafting.recipes) {
			if (recipe instanceof IStaticStationRecipe) {
				IStaticStationRecipe r2 = (IStaticStationRecipe) recipe;
				for (ItemStack res : r2.getRecipeOutput()) {
					if (res == null) continue;
					ArrayList<IStaticStationRecipe> rlist = 
							cachedRecipes.get(res) == null ? new ArrayList<IStaticStationRecipe>() : cachedRecipes.get(res);
					rlist.add(r2);
					cachedRecipes.put(new RecipeInputItemStack(res, true), rlist);
				}
			}
		}
	}
	
	@Override
	public void loadTransferRects() {
		transferRects.add(new RecipeTransferRect(new Rectangle(71, 23, 24, 18), getOverlayIdentifier()));
	}

	@Override
	public String getRecipeName() {
		return "Crafting Station";
	}

	@Override
	public String getGuiTexture() {
		return Resources.containerCraftingStation.toString();
	}
	
	@Override
	public void drawBackground(int recipe) {
		GL11.glColor4f(1, 1, 1, 1);
		GuiDraw.changeTexture(getGuiTexture());
		GuiDraw.drawTexturedModalRect(0, 0, 5, 11, 166, 64);
	}

	@Override
	public boolean hasOverlay(GuiContainer gui, Container container, int recipe) {
		return RecipeInfo.hasDefaultOverlay(gui, getOverlayIdentifier()) || RecipeInfo.hasOverlayHandler(gui, getOverlayIdentifier());
	}
	
	@Override
	public String getOverlayIdentifier() {
		return Constants.modId + ".craftingStation";
	}

	@Override
	public void drawExtras(int recipe) 
	{
		CachedCraftingStationRecipe cached = (CachedCraftingStationRecipe) arecipes.get(recipe);
		if(cached.recipe.getRequiredExperience() > 0) {
			String lvl = String.valueOf(cached.recipe.getRequiredExperience());
			int width = GuiDraw.fontRenderer.getStringWidth(lvl);
			int x = 71 + 25 / 2 - width / 2;
			GuiDraw.fontRenderer.drawString(lvl, x - 1, 10, 0);
			GuiDraw.fontRenderer.drawString(lvl, x + 1, 10, 0);
			GuiDraw.fontRenderer.drawString(lvl, x, 10 - 1, 0);
			GuiDraw.fontRenderer.drawString(lvl, x, 10 + 1, 0);
			GuiDraw.fontRenderer.drawString(lvl, x, 10, 0x80FF20);	
		}
		GL11.glColor4f(1, 1, 1, 1);
		GuiDraw.changeTexture("textures/gui/container/furnace.png");
		int delay = 20;//cached.recipe.getCraftingTime();
		if(delay >= 20) {
			drawProgressBar(72, 24, 176, 14, 24, 16, delay, 0);
			int sec = (int)(delay * 5 / 100) % 60;
			int min = (int)((delay * 5 / (100) / 60));
			String s = String.format("%1$02d", min) + ":" + String.format("%1$02d", sec);
			int width = GuiDraw.fontRenderer.getStringWidth(s);
			int x = 71 + 25 / 2 - width / 2;
			GuiDraw.fontRenderer.drawString(s, x, 45, 0x444444);
		}
		
		super.drawExtras(recipe);
	}

	@Override
	public void loadCraftingRecipes(ItemStack result) {
		System.out.println(result);
		for (RecipeInputItemStack input : cachedRecipes.keySet()) {
			if(input.matches(result)) {
				for (IStaticStationRecipe recipe : cachedRecipes.get(input)) {
					arecipes.add(new CachedCraftingStationRecipe(recipe));
				}
			}
		}
	}

	@Override
	public void loadCraftingRecipes(String outputId, Object... results) {
		
		if (!initialized) {
			//Cache the recipes if they haven't been yet.
			initialize();
			initialized = true;
		}
		
		if (outputId.equals("item")) loadCraftingRecipes((ItemStack) results[0]);
		else if(outputId.equals(getOverlayIdentifier())) {
			for (RecipeInputItemStack input : cachedRecipes.keySet()) {
				for (IStaticStationRecipe recipe : cachedRecipes.get(input)) {
					arecipes.add(new CachedCraftingStationRecipe(recipe));
				}
			}
		}
	}

	@Override
	public Class<? extends GuiContainer> getGuiClass() 
	{
		return GuiCraftingStation.class;
	}

	public class CachedCraftingStationRecipe extends CachedRecipe {

		int height = 3;
		int width = 3;
		
		IStaticStationRecipe recipe;
		List<PositionedStack> ingridients;
		List<PositionedStack> output;
		
		public CachedCraftingStationRecipe(IStaticStationRecipe recipe) {
			
			this.recipe = recipe;
			if (recipe instanceof ShapedStationRecipe) {
				height = ((ShapedStationRecipe) recipe).recipeHeight;
				width = ((ShapedStationRecipe) recipe).recipeWidth;
			}
			ingridients = new ArrayList<PositionedStack>();
			
			outer:
			for (int x = 0; x < width; x++) {
				for (int y = 0; y < height; y++) {
					int index = x + y * width;
					if (index >= recipe.getRecipeInput().length) break outer;
					IRecipeInput input = recipe.getRecipeInput()[index];
					if (input instanceof RecipeInputItemStack) {
						ingridients.add(new PositionedStack(((RecipeInputItemStack) input).stack, x * 18 + 12, y * 18 + 6));
					} else if (input instanceof RecipeInputOreDict) {
						ingridients.add(new PositionedStack(OreDictionary.getOres(((RecipeInputOreDict) input).name), x * 18 + 12, y * 18 + 6));
					}
				}
			}
			
			output = new ArrayList<PositionedStack>();
			for (int i = 0; i < 9; i++) {
				if(i >= recipe.getRecipeOutput().length) break;
				ItemStack stack = recipe.getRecipeOutput()[i];
				if(stack == null) continue;
				int i2 = i++;
				output.add(new PositionedStack(stack, (i2 - i2 / 3 * 3) * 18 + 102, i2 / 3 * 18 + 6));
			}
		}

		@Override
		public List<PositionedStack> getIngredients() {
			return ingridients;
		}
		
		@Override
		public List<PositionedStack> getOtherStacks() {
			return output;
		}
		
		@Override
		public PositionedStack getResult() {
			return null;
		}
	}
}
