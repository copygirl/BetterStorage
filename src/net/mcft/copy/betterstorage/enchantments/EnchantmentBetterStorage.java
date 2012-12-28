package net.mcft.copy.betterstorage.enchantments;

import net.mcft.copy.betterstorage.Config;
import net.minecraft.enchantment.Enchantment;

public abstract class EnchantmentBetterStorage extends Enchantment {
	
	// Key enchantments
	public static Enchantment unlocking;
	public static Enchantment lockpicking;
	public static Enchantment morphing;
	
	// Lock enchantments
	public static Enchantment persistance;
	public static Enchantment security;
	public static Enchantment shock;
	public static Enchantment trigger;
	
	public static void init() {
		
		int baseId = Config.enchantmentBaseId;
		
		unlocking   = new EnchantmentKeyUnlocking(baseId + 0, 4);
		lockpicking = new EnchantmentKeyLockpicking(baseId + 1, 6);
		morphing    = new EnchantmentKeyMorphing(baseId + 2, 1);
		
		persistance = new EnchantmentLockPersistance(baseId + 5, 5);
		security    = new EnchantmentLockSecurity(baseId + 6, 4);
		shock       = new EnchantmentLockShock(baseId + 7, 3);
		trigger     = new EnchantmentLockTrigger(baseId + 8, 1);
		
	}
	
	public EnchantmentBetterStorage(int id, int weight, String name) {
		super(id, weight, null);
		setName(name);
	}
	
	@Override
	public int getMaxEnchantability(int level) {
		return getMinEnchantability(level) + 30;
	}
	
}
