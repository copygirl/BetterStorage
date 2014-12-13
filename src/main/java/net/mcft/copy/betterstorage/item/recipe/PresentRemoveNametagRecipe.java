package net.mcft.copy.betterstorage.item.recipe;

import java.util.Arrays;
import java.util.List;

import net.mcft.copy.betterstorage.api.crafting.ContainerInfo;
import net.mcft.copy.betterstorage.api.crafting.IRecipeInput;
import net.mcft.copy.betterstorage.api.crafting.RecipeBounds;
import net.mcft.copy.betterstorage.api.crafting.RecipeInputItemStack;
import net.mcft.copy.betterstorage.api.crafting.ShapelessStationRecipe;
import net.mcft.copy.betterstorage.api.crafting.StationCrafting;
import net.mcft.copy.betterstorage.content.BetterStorageTiles;
import net.mcft.copy.betterstorage.tile.entity.TileEntityPresent;
import net.mcft.copy.betterstorage.utils.RandomUtils;
import net.mcft.copy.betterstorage.utils.StackUtils;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemShears;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PresentRemoveNametagRecipe extends ShapelessStationRecipe {

	public PresentRemoveNametagRecipe() {
		super(new IRecipeInput[]{ new RecipeInputPresent(), new RecipeInputShears() },
		      new ItemStack[]{ new ItemStack(BetterStorageTiles.present), new ItemStack(Items.name_tag) });
	}
	
	@Override
	public StationCrafting checkMatch(ItemStack[] input, RecipeBounds bounds) {
		StationCrafting match = super.checkMatch(input, bounds);
		if (match == null) return null;
		ItemStack[] output = new ItemStack[input.length];
		int shearsIndex = -1;
		String nameTag = null;
		for (int i = 0; i < input.length; i++) {
			ItemStack stack = input[i];
			if (stack == null) continue;
			if (stack.getItem() == Item.getItemFromBlock(BetterStorageTiles.present)) {
				nameTag = StackUtils.get(stack, null, TileEntityPresent.TAG_NAMETAG);
				StackUtils.remove((output[i] = stack.copy()), TileEntityPresent.TAG_NAMETAG);
			} else if (stack.getItem() == Items.shears)
				shearsIndex = i;
		}
		(output[shearsIndex] = new ItemStack(Items.name_tag)).setStackDisplayName(nameTag);
		return new StationCrafting(output, match.getCraftRequirements(), 8);
	}
	
	private static class RecipeInputPresent extends RecipeInputItemStack {
		public RecipeInputPresent() {
			super(new ItemStack(BetterStorageTiles.present));
		}
		@Override
		public int getAmount() { return 1; }
		@Override
		public boolean matches(ItemStack stack) {
			return (super.matches(stack) && StackUtils.has(stack, TileEntityPresent.TAG_NAMETAG));
		}
		@Override
		@SideOnly(Side.CLIENT)
		public List<ItemStack> getPossibleMatches() {
			ItemStack stack = this.stack;
			StackUtils.set(stack, "(somebody)", TileEntityPresent.TAG_NAMETAG);
			return Arrays.asList(stack);
		}
	}
	
	private static class RecipeInputShears implements IRecipeInput {
		public RecipeInputShears() {  }
		@Override
		public int getAmount() { return 0; }
		@Override
		public boolean matches(ItemStack stack) { return (stack.getItem() instanceof ItemShears); }
		@Override
		public void craft(ItemStack input, ContainerInfo containerInfo) {
			input.attemptDamageItem(16, RandomUtils.random);
		}
		@Override
		@SideOnly(Side.CLIENT)
		public List<ItemStack> getPossibleMatches() {
			return Arrays.asList(new ItemStack(Items.shears));
		}
	}
	
}
