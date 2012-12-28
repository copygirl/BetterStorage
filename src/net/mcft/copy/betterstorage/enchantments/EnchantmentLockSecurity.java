package net.mcft.copy.betterstorage.enchantments;

/** Protects the lock against Lockpicking, Unlocking and Morphing. */
public class EnchantmentLockSecurity extends EnchantmentLock {
	
	public EnchantmentLockSecurity(int id, int weight) {
		super(id, weight, "lock.security");
	}
	
	@Override
	public int getMaxLevel() { return 5; }
	
	@Override
	public int getMinEnchantability(int level) {
		return 1 + (level - 1) * 10;
	}
	
}
