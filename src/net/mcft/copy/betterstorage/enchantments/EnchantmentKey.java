package net.mcft.copy.betterstorage.enchantments;

import net.mcft.copy.betterstorage.items.ItemKey;
import net.minecraft.item.ItemStack;

public abstract class EnchantmentKey extends EnchantmentBetterStorage {
	
	public EnchantmentKey(int id, int weight, String name) {
		super(id, weight, key, name);
	}
	
	@Override
	public boolean func_92037_a(ItemStack stack) {
		return (stack.getItem() instanceof ItemKey);
	}
	
}
