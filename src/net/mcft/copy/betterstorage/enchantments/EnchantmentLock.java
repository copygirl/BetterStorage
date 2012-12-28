package net.mcft.copy.betterstorage.enchantments;

import net.mcft.copy.betterstorage.items.ItemLock;
import net.minecraft.item.ItemStack;

public class EnchantmentLock extends EnchantmentBetterStorage {
	
	public EnchantmentLock(int id, int weight, String name) {
		super(id, weight, name);
	}
	
	@Override
	public boolean func_92037_a(ItemStack stack) {
		return (stack.getItem() instanceof ItemLock);
	}
	
}
