package net.mcft.copy.betterstorage.enchantments;

import net.minecraft.enchantment.Enchantment;

/** Can pick a lock once, then loses one level of the enchantment. <br>
 *  If the lock has security, a higher level is required. */
public class EnchantmentKeyLockpicking extends EnchantmentKey {
	
	public EnchantmentKeyLockpicking(int id, int weight) {
		super(id, weight, "key.lockpicking");
	}
	
	@Override
	public int getMaxLevel() { return 5; }
	
	@Override
	public int getMinEnchantability(int level) {
		return 5 + (level - 1) * 8;
	}
	
	@Override
	public boolean canApplyTogether(Enchantment enchantment) {
		return (super.canApplyTogether(enchantment) &&
		        !(enchantment instanceof EnchantmentKeyMorphing));
	}
	
}
