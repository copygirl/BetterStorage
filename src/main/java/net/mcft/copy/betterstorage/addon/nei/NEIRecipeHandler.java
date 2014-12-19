package net.mcft.copy.betterstorage.addon.nei;


public class NEIRecipeHandler /*extends TemplateRecipeHandler*/ {
	
	/*private final Minecraft mc = Minecraft.getMinecraft();
	
	@Override
	public String getRecipeName() { return "Crafting Station"; }
	
	@Override
	public String getOverlayIdentifier() { return Constants.modId + ".craftingStation"; }
	
	@Override
	public Class<? extends GuiContainer> getGuiClass() { return GuiCraftingStation.class; }
	
	@Override
	public String getGuiTexture() { return Resources.containerCraftingStation.toString(); }
	
	@Override
	public void loadTransferRects() {
		transferRects.add(new RecipeTransferRect(new Rectangle(71, 23, 24, 18), getOverlayIdentifier()));
	}
	
	@Override
	public boolean hasOverlay(GuiContainer gui, Container container, int recipe) {
		return (RecipeInfo.hasDefaultOverlay(gui, getOverlayIdentifier()) ||
		        RecipeInfo.hasOverlayHandler(gui, getOverlayIdentifier()));
	}
	
	@Override
	public void drawBackground(int recipe) {
		GlStateManager.color(1, 1, 1, 1);
		mc.getTextureManager().bindTexture(Resources.containerCraftingStation);
		RenderUtils.drawTexturedModalRect(0, 0, 5, 11, 166, 64, 0, 256, 256);
	}
	
	@Override
	public void drawExtras(int recipe) {
		super.drawExtras(recipe);
		CachedStationRecipe cached = (CachedStationRecipe)arecipes.get(recipe);
		
		GlStateManager.color(1, 1, 1, 1);
		if (cached.craftingTime >= 20) {
			drawProgressBar(71, 22, 176, 0, 24, 18, cached.craftingTime, 0);
			int min = cached.craftingTime / 20 / 60;
			int sec = cached.craftingTime / 20 % 60;
			String str = "";
			if (min > 0) str += min + "m";
			if (sec > 0) str += sec + "s";
			int strX = (168 - mc.fontRendererObj.getStringWidth(str)) / 2;
			int strY = 17 - mc.fontRendererObj.FONT_HEIGHT / 2;
			mc.fontRendererObj.drawString(str, strX, strY, 0x444444);
		}
		
		if (cached.requiredExperience > 0) {
			String str = Integer.toString(cached.requiredExperience);
			int strX = (168 - mc.fontRendererObj.getStringWidth(str)) / 2;
			int strY = 47 - mc.fontRendererObj.FONT_HEIGHT / 2;
			mc.fontRendererObj.drawString(str, strX - 1, strY, 0x444444);
			mc.fontRendererObj.drawString(str, strX + 1, strY, 0x444444);
			mc.fontRendererObj.drawString(str, strX, strY - 1, 0x444444);
			mc.fontRendererObj.drawString(str, strX, strY + 1, 0x444444);
			mc.fontRendererObj.drawString(str, strX, strY, 0x80FF20);
		}
	}
	
	@Override
	public void loadCraftingRecipes(String outputId, Object... results) {
		if (outputId.equals(getOverlayIdentifier()))
			arecipes.addAll(getRecipes());
		else super.loadCraftingRecipes(outputId, results);
	}
	
	@Override
	public void loadCraftingRecipes(ItemStack result) {
		for (CachedStationRecipe recipe : getRecipes())
			if (recipe.outputs(result))
				arecipes.add(recipe);
	}
	
	@Override
	public void loadUsageRecipes(ItemStack ingredient) {
		for (CachedStationRecipe recipe : getRecipes())
			if (recipe.uses(ingredient))
				arecipes.add(recipe);
	}
	
	@Override
	public void onUpdate() {
		if (!NEIClientUtils.shiftKey() && ((++cycleticks % 20) == 0))
			for (CachedRecipe recipe : arecipes)
				((CachedStationRecipe)recipe).cycle();
	}
	
	private List<CachedStationRecipe> cachedRecipes = null;
	public List<CachedStationRecipe> getRecipes() {
		if (cachedRecipes == null) {
			cachedRecipes = new ArrayList<CachedStationRecipe>();
			for (IStationRecipe recipe : BetterStorageCrafting.recipes) {
				List<IRecipeInput[]> sampleInput = recipe.getSampleInputs();
				if (sampleInput == null) continue;
				cachedRecipes.add(new CachedStationRecipe(recipe));
			}
		}
		return cachedRecipes;
	}
	
	public class CachedStationRecipe extends CachedRecipe {
		
		private final IStationRecipe recipe;
		
		private final List<PositionedStack> ingridients = new ArrayList<PositionedStack>();
		private final List<PositionedStack> output = new ArrayList<PositionedStack>();
		
		private final List<IRecipeInput[]> sampleInput;
		private List<IRecipeInput> possibleInputs;
		private List<ItemStack> possibleOutputs;
		
		private int requiredExperience;
		private int craftingTime;
		
		int cycleIndex = 0;
		
		public CachedStationRecipe(IStationRecipe recipe) {
			this.recipe = recipe;
			sampleInput = recipe.getSampleInputs();
			possibleInputs = recipe.getPossibleInputs();
			possibleOutputs = recipe.getPossibleOutputs();
			
			if (possibleInputs == null) {
				possibleInputs = new ArrayList<IRecipeInput>();
				for (IRecipeInput[] inputArray : sampleInput)
					for (IRecipeInput input : inputArray)
						if (input != null)
							possibleInputs.add(input);
			}
			
			if (possibleOutputs == null) {
				possibleOutputs = new ArrayList<ItemStack>();
				for (IRecipeInput[] inputArray : sampleInput) {
					ItemStack[] stackArray = new ItemStack[inputArray.length];
					for (int i = 0; i < inputArray.length; i++)
						if (inputArray[i] != null)
							stackArray[i] = inputArray[i].getPossibleMatches().get(0);
					for (ItemStack stack : recipe.checkMatch(stackArray, new RecipeBounds(stackArray)).getOutput())
						if (stack != null)
							possibleOutputs.add(stack);
				}
			}
			
			cycle();
		}
		
		public void cycle() {
			cycleIndex = (cycleIndex + 1) % sampleInput.size();
			IRecipeInput[] inputArray = sampleInput.get(cycleIndex);
			ItemStack[] stackArray = new ItemStack[inputArray.length];
			
			ingridients.clear();
			for (int x = 0; x < 3; x++)
				for (int y = 0; y < 3; y++) {
					int index = x + y * 3;
					IRecipeInput input = inputArray[index];
					if (input == null) continue;
					List<ItemStack> possibleMatches = input.getPossibleMatches();
					ingridients.add(new PositionedStack(possibleMatches, x * 18 + 12, y * 18 + 6));
					stackArray[index] = possibleMatches.get(0);
				}
			
			StationCrafting crafting = recipe.checkMatch(stackArray, new RecipeBounds(stackArray));
			if (crafting == null)
				throw new Error("Recipe " + recipe.getClass().getSimpleName() + " didn't match sample input.");
			requiredExperience = crafting.getRequiredExperience();
			craftingTime = crafting.getCraftingTime();
			
			output.clear();
			ItemStack[] outputArray = crafting.getOutput();
			for (int x = 0; x < 3; x++)
				for (int y = 0; y < 3; y++)
					if (((x + y * 3) < outputArray.length) &&
					    (outputArray[x + y * 3] != null))
						output.add(new PositionedStack(outputArray[x + y * 3], x * 18 + 102, y * 18 + 6));
		}
		
		@Override
		public PositionedStack getResult() { return null; }
		
		@Override
		public List<PositionedStack> getIngredients() { return getCycledIngredients(cycleticks / 20, ingridients); }
		
		@Override
		public List<PositionedStack> getOtherStacks() { return output; }
		
		
		public boolean outputs(ItemStack result) {
			for (ItemStack output : possibleOutputs)
				if (NEIServerUtils.areStacksSameTypeCrafting(output, result))
					return true;
			return false;
		}
		
		public boolean uses(ItemStack ingredient) {
			for (IRecipeInput input : possibleInputs)
				if (input.matches(ingredient))
					return true;
			return false;
		}
		
	}*/
	
}
