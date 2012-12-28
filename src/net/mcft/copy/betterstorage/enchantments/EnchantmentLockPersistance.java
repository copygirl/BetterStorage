package net.mcft.copy.betterstorage.enchantments;

/** Makes the chest take longer to break. */
public class EnchantmentLockPersistance extends EnchantmentLock {
	
	public EnchantmentLockPersistance(int id, int weight) {
		super(id, weight, "lock.persistance");
	}
	
	@Override
	public int getMaxLevel() { return 4; }
	
	@Override
	public int getMinEnchantability(int level) {
		return 1 + (level - 1) * 8;
	}
	
}
