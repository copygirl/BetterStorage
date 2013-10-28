package net.mcft.copy.betterstorage.item.recipe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.mcft.copy.betterstorage.utils.DyeUtils;
import net.mcft.copy.betterstorage.utils.StackUtils;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;

public class DyeRecipe implements IRecipe {
	
	private Map<Item, String[]> dyableItems = new HashMap<Item, String[]>();
	
	public DyeRecipe add(Item item, String... tags) {
		if (item != null) dyableItems.put(item, tags);
		return this;
	}
	public DyeRecipe add(Item... items) {
		for (Item item : items) add(item, "display", "color");
		return this;
	}
	
	@Override
	public int getRecipeSize() { return 10; }
	
	@Override
	public ItemStack getRecipeOutput() { return null; }
	
	@Override
	public boolean matches(InventoryCrafting crafting, World world) {
		boolean hasArmor = false;
		boolean hasDyes = false;
		for (int i = 0; i < crafting.getSizeInventory(); i++) {
			ItemStack stack = crafting.getStackInSlot(i);
			if (stack == null) continue;
			else if (dyableItems.containsKey(stack.getItem())) {
				if (hasArmor) return false;
				hasArmor = true;
			} else if (DyeUtils.isDye(stack)) hasDyes = true;
			else return false;
		}
		return (hasArmor && hasDyes);
	}
	
	@Override
	public ItemStack getCraftingResult(InventoryCrafting crafting) {
		ItemStack armor = null;
		String[] tags = null;
		List<ItemStack> dyes = new ArrayList<ItemStack>();
		for (int i = 0; i < crafting.getSizeInventory(); i++) {
			ItemStack stack = crafting.getStackInSlot(i);
			if (stack == null) continue;
			else if (dyableItems.containsKey(stack.getItem())) {
				if (armor != null) return null;
				armor = stack.copy();
				tags = dyableItems.get(stack.getItem());
			} else if (DyeUtils.isDye(stack)) dyes.add(stack);
			else return null;
		}
		if (dyes.isEmpty()) return null;
		StackUtils.set(armor, DyeUtils.getColorFromDyes(StackUtils.get(armor, -1, tags), dyes), tags);
		return armor;
	}
	
}
