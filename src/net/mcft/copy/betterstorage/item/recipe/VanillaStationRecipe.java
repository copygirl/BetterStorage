package net.mcft.copy.betterstorage.item.recipe;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Random;

import net.mcft.copy.betterstorage.api.crafting.BetterStorageCrafting;
import net.mcft.copy.betterstorage.api.crafting.ICraftingSource;
import net.mcft.copy.betterstorage.api.crafting.IRecipeInput;
import net.mcft.copy.betterstorage.api.crafting.IStationRecipe;
import net.mcft.copy.betterstorage.api.crafting.RecipeInputItemStack;
import net.mcft.copy.betterstorage.inventory.InventoryCraftingStation;
import net.mcft.copy.betterstorage.utils.StackUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;

public class VanillaStationRecipe implements IStationRecipe {
	
	private final World world;
	private final IRecipe recipe;
	private final InventoryCrafting crafting;
	
	private final ItemStack output;
	private final boolean onCreatedOverridden;
	
	private VanillaStationRecipe(World world, IRecipe recipe, InventoryCrafting crafting) {
		this.world = world;
		this.recipe = recipe;
		this.crafting = crafting;
		
		output = recipe.getCraftingResult(crafting);
		boolean overridden = false;
		Class itemClass = output.getItem().getClass();
		for (String name : new String[]{ "onCreated", "func_77622_d" } ) {
			try {
				Method onCreatedMethod = itemClass.getMethod(name, ItemStack.class, World.class, EntityPlayer.class);
				overridden = (onCreatedMethod.getDeclaringClass() != Item.class);
				break;
			} catch (Exception e) {  }
		}
		onCreatedOverridden = overridden;
	}
	
	public static VanillaStationRecipe findVanillaRecipe(InventoryCraftingStation inv) {
		InventoryCrafting crafting = new InventoryCrafting(null, 3, 3);
		crafting.stackList = inv.crafting;
		World world = ((inv.entity != null) ? inv.entity.worldObj : Minecraft.getMinecraft().theWorld);
		IRecipe recipe = findRecipe(crafting, world);
		if (recipe == null) return null;
		return new VanillaStationRecipe(world, recipe, crafting);
	}
	
	private static IRecipe findRecipe(InventoryCrafting crafting, World world) {
		for (IRecipe recipe : (List<IRecipe>)CraftingManager.getInstance().getRecipeList())
			if (recipe.matches(crafting, world))
				return recipe;
		return null;
	}
	
	// IStationRecipe implementation
	
	@Override
	public boolean matches(ItemStack[] input) { return recipe.matches(crafting, world); }
	
	@Override
	public void getSampleInput(ItemStack[] input, Random rnd) {  }
	
	@Override
	public void getCraftRequirements(ItemStack[] currentInput, IRecipeInput[] requiredInput) {
		// TODO
		for (int i = 0; i < currentInput.length; i++) {
			ItemStack stack = StackUtils.copyStack(currentInput[i], 1);
			if (stack != null)
				requiredInput[i] = new RecipeInputItemStack(stack);
		}
	}
	
	@Override
	public int getExperienceDisplay(ItemStack[] input) { return 0; }
	
	@Override
	public int getCraftingTime(ItemStack[] input) { return 0; }
	
	@Override
	public ItemStack[] getOutput(ItemStack[] input) { return new ItemStack[]{ output }; }
	
	@Override
	public boolean canCraft(ItemStack[] input, ICraftingSource source) {
		return ((source.getPlayer() != null) || !onCreatedOverridden);
	}
	
	@Override
	public void craft(ItemStack[] input, ICraftingSource source) {
		BetterStorageCrafting.decreaseCraftingMatrix(input, source, this);
	}
	
}
