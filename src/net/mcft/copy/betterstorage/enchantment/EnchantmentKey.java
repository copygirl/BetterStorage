package net.mcft.copy.betterstorage.enchantment;

import net.mcft.copy.betterstorage.item.ItemKey;
import net.minecraft.item.ItemStack;

public abstract class EnchantmentKey extends EnchantmentBetterStorage {
	
	public EnchantmentKey(int id, int weight, String name) {
		super(id, weight, key, name);
	}
	
	@Override
	public boolean canApplyAtEnchantingTable(ItemStack stack) {
		return (stack.getItem() instanceof ItemKey);
	}
	
}
