package net.mcft.copy.betterstorage.enchantment;

import net.minecraft.enchantment.Enchantment;

/** When used on a lock this key can't normally open, changes this
 *  key into the one that fits the lock. The key loses this enchantment. <br>
 *  If the lock has security, a higher level is required. */
public class EnchantmentKeyMorphing extends EnchantmentKey {
	
	public EnchantmentKeyMorphing(int id, int weight) {
		super(id, weight, "key.morphing");
	}
	
	@Override
	public int getMaxLevel() { return 5; }
	
	@Override
	public int getMinEnchantability(int level) {
		return 10 + (level - 1) * 12;
	}
	
	@Override
	public boolean canApplyTogether(Enchantment enchantment) {
		return (super.canApplyTogether(enchantment) &&
		        !(enchantment instanceof EnchantmentKeyLockpicking));
	}
	
}
