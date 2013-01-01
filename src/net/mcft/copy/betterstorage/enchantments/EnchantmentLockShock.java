package net.mcft.copy.betterstorage.enchantments;

/** Damages any player who tries to (unsuccessfully) open
 *  or pick the lock, or break the chest. */
public class EnchantmentLockShock extends EnchantmentLock {
	
	public EnchantmentLockShock(int id, int weight) {
		super(id, weight, "lock.shock");
	}
	
	@Override
	public int getMaxLevel() { return 3; }
	
	@Override
	public int getMinEnchantability(int level) {
		return 5 + (level - 1) * 10;
	}
	
}
