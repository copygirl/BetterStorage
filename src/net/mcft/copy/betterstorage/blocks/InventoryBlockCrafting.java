package net.mcft.copy.betterstorage.blocks;

import net.minecraft.src.InventoryCrafting;
import net.minecraft.src.ItemStack;

/** Simplifies looking up block recipes. */
public class InventoryBlockCrafting extends InventoryCrafting {
	
	private ItemStack stack;
	
	public InventoryBlockCrafting(ItemStack stack) {
		super(null, 3, 3);
		this.stack = stack;
	}
	
	@Override
	public ItemStack getStackInSlot(int slot) { return stack; }
	
}
