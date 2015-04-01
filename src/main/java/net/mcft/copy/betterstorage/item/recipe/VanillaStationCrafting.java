package net.mcft.copy.betterstorage.item.recipe;

import java.util.List;

import net.mcft.copy.betterstorage.api.crafting.ICraftingSource;
import net.mcft.copy.betterstorage.api.crafting.IRecipeInput;
import net.mcft.copy.betterstorage.api.crafting.RecipeInputBase;
import net.mcft.copy.betterstorage.api.crafting.StationCrafting;
import net.mcft.copy.betterstorage.config.GlobalConfig;
import net.mcft.copy.betterstorage.inventory.InventoryCraftingStation;
import net.mcft.copy.betterstorage.utils.StackUtils;
import net.mcft.copy.betterstorage.utils.WorldUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class VanillaStationCrafting extends StationCrafting {
	
	public VanillaStationCrafting(World world, IRecipe recipe, ItemStack[] input, ItemStack output) {
		super(new ItemStack[]{ null, null, null, null, output }, createRecipeInput(world, recipe, input));
	}
	
	private static IRecipeInput[] createRecipeInput(World world, IRecipe recipe, ItemStack[] input) {
		IRecipeInput[] requiredInput = new IRecipeInput[9];
		for (int i = 0; i < input.length; i++)
			if (input[i] != null)
				requiredInput[i] = new VanillaRecipeInput(world, recipe, input, i);
		return requiredInput;
	}
	
	@Override
	public boolean canCraft(ICraftingSource source) {
		return ((source.getPlayer() != null) || !GlobalConfig.enableStationAutoCraftingSetting.getValue());
	}
	
	private static class VanillaRecipeInput extends RecipeInputBase {
		
		private final World world;
		private final IRecipe recipe;
		private final int slot;
		
		private final InventoryCrafting crafting;
		private final ItemStack expectedOutput;
		
		public VanillaRecipeInput(World world, IRecipe recipe, ItemStack[] input, int slot) {
			this.world = world;
			this.recipe = recipe;
			this.slot = slot;
			
			crafting = new InventoryCrafting(new FakeContainer(), 3, 3);
			for (int i = 0; i < input.length; i++)
				crafting.setInventorySlotContents(i, ItemStack.copyItemStack(input[i]));
			
			ItemStack output = recipe.getCraftingResult(crafting);
			if (output == null)
				throw new IllegalArgumentException(recipe.getClass() + " returned null for getCraftingResult.");
			expectedOutput = output.copy();
		}
		
		@Override
		public int getAmount() { return 1; }
		
		@Override
		public boolean matches(ItemStack stack) {
			ItemStack stackBefore = crafting.getStackInSlot(slot);
			crafting.setInventorySlotContents(slot, stack);
			boolean matches = (recipe.matches(crafting, world) &&
			                   StackUtils.matches(expectedOutput, recipe.getCraftingResult(crafting)));
			crafting.setInventorySlotContents(slot, stackBefore);
			return matches;
		}
		
		@Override
		@SideOnly(Side.CLIENT)
		public List<ItemStack> getPossibleMatches() { return null; }
		
	}
	
	public static VanillaStationCrafting findVanillaRecipe(InventoryCraftingStation inv) {
		World world = ((inv.entity != null) ? inv.entity.getWorldObj() : WorldUtils.getLocalWorld());
		
		InventoryCrafting crafting = new InventoryCrafting(new FakeContainer(), 3, 3);
		for (int i = 0; i < inv.crafting.length; i++)
			crafting.setInventorySlotContents(i, ItemStack.copyItemStack(inv.crafting[i]));
		
		IRecipe recipe = findRecipe(crafting, world);
		if (recipe == null) return null;
		return new VanillaStationCrafting(world, recipe, inv.crafting, recipe.getCraftingResult(crafting));
	}
	
	private static IRecipe findRecipe(InventoryCrafting crafting, World world) {
		for (IRecipe recipe : (List<IRecipe>)CraftingManager.getInstance().getRecipeList())
			if (recipe.matches(crafting, world))
				return recipe;
		return null;
	}
	
	private static class FakeContainer extends Container {
		@Override
		public boolean canInteractWith(EntityPlayer player) { return false; }
	}
	
}
