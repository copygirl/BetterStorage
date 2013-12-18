package net.mcft.copy.betterstorage.item;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import net.mcft.copy.betterstorage.Config;
import net.mcft.copy.betterstorage.api.BetterStorageEnchantment;
import net.mcft.copy.betterstorage.api.lock.IKey;
import net.mcft.copy.betterstorage.api.lock.ILock;
import net.mcft.copy.betterstorage.content.Items;
import net.mcft.copy.betterstorage.misc.Constants;
import net.mcft.copy.betterstorage.utils.MiscUtils;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.EnumHelper;

public class EnchantmentBetterStorage extends Enchantment {
	
	private final int maxLevel;
	private final int minBase, minScaling;
	private final int maxBase, maxScaling;
	
	private List<Enchantment> incompatible = new ArrayList<Enchantment>(0);
	
	public static void initialize() {
		
		Map<String, EnumEnchantmentType> types = BetterStorageEnchantment.enchantmentTypes;
		Map<String, Enchantment> enchs = BetterStorageEnchantment.enchantments;
		
		// Add key enchantments
		if (MiscUtils.isEnabled(Items.key)) {
			EnumEnchantmentType key = EnumHelper.addEnchantmentType("key");
			
			EnchantmentBetterStorage unlocking   = conditialNew("unlocking",   key, Config.enchantmentUnlockingId,   8, 5,  5, 10, 30, 0);
			EnchantmentBetterStorage lockpicking = conditialNew("lockpicking", key, Config.enchantmentLockpickingId, 6, 5,  5,  8, 30, 0);
			EnchantmentBetterStorage morphing    = conditialNew("morphing",    key, Config.enchantmentMorphingId,    1, 5, 10, 12, 30, 0);
			
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
		if (MiscUtils.isEnabled(Items.lock)) {
			EnumEnchantmentType lock = EnumHelper.addEnchantmentType("lock");
			
			EnchantmentBetterStorage persistance = conditialNew("persistance", lock, Config.enchantmentPersistanceId, 20, 5,  1,  8, 30, 0);
			EnchantmentBetterStorage security    = conditialNew("security",    lock, Config.enchantmentSecurityId,    16, 5,  1, 10, 30, 0);
			EnchantmentBetterStorage shock       = conditialNew("shock",       lock, Config.enchantmentShockId,        5, 3,  5, 15, 30, 0);
			EnchantmentBetterStorage trigger     = conditialNew("trigger",     lock, Config.enchantmentTriggerId,     10, 1, 15,  0, 30, 0);
			
			types.put("lock", lock);
	
			enchs.put("persistance",persistance);
			enchs.put("security", security);
			enchs.put("shock", shock);
			enchs.put("trigger", trigger);
		}
		
	}
	private static EnchantmentBetterStorage conditialNew(String name, EnumEnchantmentType type, int id, int weight, int maxLevel,
	                                                     int minBase, int minScaling, int maxBase, int maxScaling) {
		if (!MiscUtils.isEnabled(id)) return null;
		return new EnchantmentBetterStorage(name, type, id, weight, maxLevel, minBase, minScaling, maxBase, maxScaling);
	}
	
	public EnchantmentBetterStorage(String name, EnumEnchantmentType type, int id, int weight, int maxLevel,
	                                int minBase, int minScaling, int maxBase, int maxScaling) {
		super(id, weight, type);
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
