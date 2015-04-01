package net.mcft.copy.betterstorage.addon.minetweaker;

import java.util.Arrays;
import java.util.List;

import minetweaker.api.item.IIngredient;
import minetweaker.api.item.IItemStack;
import minetweaker.mc1710.item.MCItemStack;
import minetweaker.mc1710.player.MCPlayer;
import net.mcft.copy.betterstorage.api.crafting.ContainerInfo;
import net.mcft.copy.betterstorage.api.crafting.RecipeInputBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class RecipeInputIngredient extends RecipeInputBase {
	
	protected static EntityPlayer player;
	
	private final IIngredient ingredient;
	
	public RecipeInputIngredient(IIngredient ingredient) {
		this.ingredient = ingredient;
	}
	
	@Override
	public int getAmount() { return ingredient.getAmount(); }
	
	@Override
	public boolean matches(ItemStack stack) {
		return ((stack != null) ? ingredient.matches(new MCItemStack(stack)) : false);
	}
	
	@Override
	public void craft(ItemStack input, ContainerInfo containerInfo) {
		super.craft(input, containerInfo);
		ItemStack transformed = MTHelper.toStack(ingredient.applyTransform(
				new MCItemStack(input), ((player != null) ? new MCPlayer(player) : null)));
		if (transformed != input) {
			input.stackSize = 0;
			if (transformed.stackSize > 0)
				containerInfo.set(transformed, false);
		}
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public List<ItemStack> getPossibleMatches() {
		List<IItemStack> stacks = ingredient.getItems();
		return ((stacks != null) ? MTHelper.toStacks(stacks) : Arrays.asList(new ItemStack(Blocks.air)));
	}
	
}
