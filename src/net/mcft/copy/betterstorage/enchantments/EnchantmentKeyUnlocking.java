package net.mcft.copy.betterstorage.enchantments;

/** Can open more locks. The higher the level, the more locks
 *  this key will be able to open. <br>
 *  If the lock has security, the effectiveness of this enchantment
 *  is lowered for that lock. */
public class EnchantmentKeyUnlocking extends EnchantmentKey {
	
	public EnchantmentKeyUnlocking(int id, int weight) {
		super(id, weight, "key.unlocking");
	}
	
	@Override
	public int getMaxLevel() { return 5; }
	
	@Override
	public int getMinEnchantability(int level) {
		return 5 + (level - 1) * 10;
	}
	
}
