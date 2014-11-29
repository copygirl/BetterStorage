package net.mcft.copy.betterstorage.item.recipe;

import net.mcft.copy.betterstorage.content.BetterStorageTiles;
import net.mcft.copy.betterstorage.tile.entity.TileEntityPresent;
import net.mcft.copy.betterstorage.utils.StackUtils;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ShapedRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class PresentRecipe extends ShapedRecipes {
	
	public PresentRecipe() {
		super(3, 3, createRecipeInput(), new ItemStack(BetterStorageTiles.present));
	}
	
	private static ItemStack[] createRecipeInput() {
		ItemStack box = new ItemStack(BetterStorageTiles.cardboardBox);
		ItemStack wool1 = new ItemStack(Blocks.wool, 1, 14);
		ItemStack wool2 = new ItemStack(Blocks.wool, 1, 0);
		return new ItemStack[]{
			wool1, wool2, wool1,
			wool1,  box,  wool1,
			wool1, wool2, wool1
		};
	}
	
	@Override
	public boolean matches(InventoryCrafting crafting, World world) {
		ItemStack box = crafting.getStackInSlot(4);
		if ((box == null) || (box.getItem() != Item.getItemFromBlock(BetterStorageTiles.cardboardBox)) ||
		    !StackUtils.has(box, "Items")) return false;
		
		int corners = checkWoolColor(crafting, 0, 2, 6, 8);
		if (corners >= 16) return false;
		int topBottom = checkWoolColor(crafting, 1, 7);
		int leftRight = checkWoolColor(crafting, 3, 5);
		
		if ((corners < 0) || (topBottom < 0) || (leftRight < 0) || (corners == topBottom) ||
		    ((leftRight != corners) && (leftRight != topBottom))) return false;
		return true;
	}
	
	@Override
	public ItemStack getCraftingResult(InventoryCrafting crafting) {
		int colorInner = checkWoolColor(crafting, 0);
		int colorOuter = checkWoolColor(crafting, 1);
		int leftRight = checkWoolColor(crafting, 3);
		boolean skojanzaMode = (leftRight == colorOuter);
		ItemStack box = crafting.getStackInSlot(4);
		
		ItemStack present = new ItemStack(BetterStorageTiles.present);
		NBTTagCompound compound = box.getTagCompound();
		compound.setByte(TileEntityPresent.TAG_COLOR_INNER, (byte)colorInner);
		compound.setByte(TileEntityPresent.TAG_COLOR_OUTER, (byte)colorOuter);
		compound.setBoolean(TileEntityPresent.TAG_SKOJANZA_MODE, skojanzaMode);
		int color = StackUtils.get(box, -1, "display", "color");
		if (color >= 0) compound.setInteger("color", color);
		present.setTagCompound(compound);
		return present;
	}
	
	private static int checkWoolColor(InventoryCrafting crafting, int... slots) {
		int color = -1;
		for (int i = 0; i < slots.length; i++) {
			int slot = slots[i];
			ItemStack stack = crafting.getStackInSlot(slot);
			int woolColor;
			if (stack == null) return -1;
			else if (stack.getItem() == Item.getItemFromBlock(Blocks.wool))
				woolColor = stack.getItemDamage();
			else if (stack.getItem() == Item.getItemFromBlock(Blocks.gold_block))
				woolColor = 16;
			else return -1;
			if (i <= 0) color = woolColor;
			else if (woolColor != color) return -1;
		}
		return color;
	}
	
}
