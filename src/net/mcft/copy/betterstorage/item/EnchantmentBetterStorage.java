package net.mcft.copy.betterstorage.item;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import net.mcft.copy.betterstorage.Config;
import net.mcft.copy.betterstorage.api.BetterStorageEnchantment;
import net.mcft.copy.betterstorage.api.IKey;
import net.mcft.copy.betterstorage.api.ILock;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.EnumHelper;

public class EnchantmentBetterStorage extends Enchantment {
	
	private final int maxLevel;
	private final int minBase, minScaling;
	private final int maxBase, maxScaling;
	
	private List<Enchantment> incompatible = new ArrayList<Enchantment>(0);
	
	public static void init() {
		
		int baseId = Config.enchantmentBaseId;
		
		// Set up types and enchantments
		
		EnumEnchantmentType key  = EnumHelper.addEnchantmentType("key");
		EnumEnchantmentType lock = EnumHelper.addEnchantmentType("lock");
		
		EnchantmentBetterStorage unlocking   = new EnchantmentBetterStorage("unlocking",   key,  baseId + 0, 4, 5,  5, 10, 30, 0);
		EnchantmentBetterStorage lockpicking = new EnchantmentBetterStorage("lockpicking", key,  baseId + 1, 6, 5,  5,  8, 30, 0);
		EnchantmentBetterStorage morphing    = new EnchantmentBetterStorage("morphing",    key,  baseId + 2, 1, 5, 10, 12, 30, 0);
		
		EnchantmentBetterStorage persistance = new EnchantmentBetterStorage("persistance", lock, baseId + 4, 5, 5,  1,  8, 30, 0);
		EnchantmentBetterStorage security    = new EnchantmentBetterStorage("security",    lock, baseId + 6, 4, 5,  1, 10, 30, 0);
		EnchantmentBetterStorage shock       = new EnchantmentBetterStorage("shock",       lock, baseId + 7, 3, 3,  5, 10, 30, 0);
		EnchantmentBetterStorage trigger     = new EnchantmentBetterStorage("trigger",     lock, baseId + 8, 1, 1, 20,  0, 30, 0);
		
		lockpicking.setIncompatible(morphing);
		morphing.setIncompatible(lockpicking);
		
		// Add types and enchantments to API
		
		Map<String, EnumEnchantmentType> types = BetterStorageEnchantment.enchantmentTypes;
		Map<String, Enchantment> enchs = BetterStorageEnchantment.enchantments;
		
		types.put("key", key);
		types.put("lock", lock);
		
		enchs.put("unlocking", unlocking);
		enchs.put("lockpicking", lockpicking);
		enchs.put("morphing", morphing);

		enchs.put("persistance",persistance);
		enchs.put("security", security);
		enchs.put("shock", shock);
		enchs.put("trigger", trigger);
		
	}
	
	public EnchantmentBetterStorage(String name, EnumEnchantmentType type, int id, int weight, int maxLevel,
	                                int minBase, int minScaling, int maxBase, int maxScaling) {
		super(id, weight, type);
		setName(type.toString() + "." + name);
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
			return (key != null && key.canApplyEnchantment(stack, this));
		} else if (type == BetterStorageEnchantment.getType("lock")) {
			ILock lock = (stack.getItem() instanceof ILock ? (ILock)stack.getItem() : null);
			return (lock != null && lock.canApplyEnchantment(stack, this));
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
	
}
