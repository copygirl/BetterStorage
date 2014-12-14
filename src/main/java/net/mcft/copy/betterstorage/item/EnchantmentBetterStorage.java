package net.mcft.copy.betterstorage.item;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import net.mcft.copy.betterstorage.BetterStorage;
import net.mcft.copy.betterstorage.api.BetterStorageEnchantment;
import net.mcft.copy.betterstorage.api.lock.IKey;
import net.mcft.copy.betterstorage.api.lock.ILock;
import net.mcft.copy.betterstorage.config.GlobalConfig;
import net.mcft.copy.betterstorage.content.BetterStorageItems;
import net.mcft.copy.betterstorage.misc.Constants;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.EnumHelper;

public class EnchantmentBetterStorage extends Enchantment {
	
	private final int maxLevel;
	private final int minBase, minScaling;
	private final int maxBase, maxScaling;
	
	private List<Enchantment> incompatible = new ArrayList<Enchantment>(0);
	
	public static void initialize() {
		
		Map<String, EnumEnchantmentType> types = BetterStorageEnchantment.enchantmentTypes;
		Map<String, Enchantment> enchs = BetterStorageEnchantment.enchantments;
		
		// Add key enchantments
		if (BetterStorageItems.key != null) {
			EnumEnchantmentType key = EnumHelper.addEnchantmentType("key");
			
			//TODO (1.8): Investigate what the ResourceLocation is used for and if we need it
			EnchantmentBetterStorage unlocking   = conditialNew("unlocking",   key, null, GlobalConfig.enchUnlockingId,   8, 5,  5, 10, 30, 0);
			EnchantmentBetterStorage lockpicking = conditialNew("lockpicking", key, null, GlobalConfig.enchLockpickingId, 6, 5,  5,  8, 30, 0);
			EnchantmentBetterStorage morphing    = conditialNew("morphing",    key, null, GlobalConfig.enchMorphingId,    1, 5, 10, 12, 30, 0);
			
			if (lockpicking != null)
				lockpicking.setIncompatible(morphing);
			if (morphing != null)
				morphing.setIncompatible(lockpicking);
			
			types.put("key", key);
			
			enchs.put("unlocking", unlocking);
			enchs.put("lockpicking", lockpicking);
			enchs.put("morphing", morphing);
		}
		
		// Add lock enchantments
		if (BetterStorageItems.lock != null) {
			EnumEnchantmentType lock = EnumHelper.addEnchantmentType("lock");
			
			EnchantmentBetterStorage persistance = conditialNew("persistance", lock, null, GlobalConfig.enchPersistanceId, 20, 5,  1,  8, 30, 0);
			EnchantmentBetterStorage security    = conditialNew("security",    lock, null, GlobalConfig.enchSecurityId,    16, 5,  1, 10, 30, 0);
			EnchantmentBetterStorage shock       = conditialNew("shock",       lock, null, GlobalConfig.enchShockId,        5, 3,  5, 15, 30, 0);
			EnchantmentBetterStorage trigger     = conditialNew("trigger",     lock, null, GlobalConfig.enchTriggerId,     10, 1, 15,  0, 30, 0);
			
			types.put("lock", lock);
	
			enchs.put("persistance",persistance);
			enchs.put("security", security);
			enchs.put("shock", shock);
			enchs.put("trigger", trigger);
		}
		
	}
	private static EnchantmentBetterStorage conditialNew(String name, EnumEnchantmentType type, ResourceLocation loc, String configName, int weight, int maxLevel,
	                                                     int minBase, int minScaling, int maxBase, int maxScaling) {
		int id = BetterStorage.globalConfig.getInteger(configName);
		if (id <= 0) return null;
		return new EnchantmentBetterStorage(name, type, loc, id, weight, maxLevel, minBase, minScaling, maxBase, maxScaling);
	}
	
	public EnchantmentBetterStorage(String name, EnumEnchantmentType type, ResourceLocation loc, int id, int weight, int maxLevel,
	                                int minBase, int minScaling, int maxBase, int maxScaling) {
		super(id, loc, weight, type);
		setName(Constants.modId + "." + type.toString() + "." + name);
		this.maxLevel   = maxLevel;
		this.minBase    = minBase;
		this.minScaling = minScaling;
		this.maxBase    = maxBase;
		this.maxScaling = maxScaling;
	}
	
	public void setIncompatible(Enchantment... incompatible) {
		this.incompatible = Arrays.asList(incompatible);
	}
	
	@Override
	public int getMaxLevel() { return maxLevel; }
	@Override
	public int getMinEnchantability(int level) {
		return minBase + (level - 1) * minScaling;
	}
	@Override
	public int getMaxEnchantability(int level) {
		return getMinEnchantability(level) + maxBase + (level - 1) * maxScaling;
	}
	
	@Override
	public boolean canApplyAtEnchantingTable(ItemStack stack) {
		if (type == BetterStorageEnchantment.getType("key")) {
			IKey key = (stack.getItem() instanceof IKey ? (IKey)stack.getItem() : null);
			return ((key != null) && key.canApplyEnchantment(stack, this));
		} else if (type == BetterStorageEnchantment.getType("lock")) {
			ILock lock = (stack.getItem() instanceof ILock ? (ILock)stack.getItem() : null);
			return ((lock != null) && lock.canApplyEnchantment(stack, this));
		} else return false;
	}
	
	@Override
	public boolean canApply(ItemStack stack) {
		return canApplyAtEnchantingTable(stack);
	}
	
	@Override
	public boolean canApplyTogether(Enchantment other) {
		return (super.canApplyTogether(other) &&
		        !incompatible.contains(other));
	}
	
	@Override
	public boolean isAllowedOnBooks() { return false; }
	
}
