package net.mcft.copy.betterstorage.enchantments;

/** Makes the chest emit redstone when a player tries to open
 *  the lock (unsuccessfully) or pick it, or break the chest. */
public class EnchantmentLockTrigger extends EnchantmentLock {
	
	public EnchantmentLockTrigger(int id, int weight) {
		super(id, weight, "lock.trigger");
	}
	
	@Override
	public int getMinEnchantability(int level) {
		return 20;
	}
	
}
