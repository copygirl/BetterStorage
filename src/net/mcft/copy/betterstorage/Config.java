package net.mcft.copy.betterstorage;

import java.io.File;

import net.minecraftforge.common.Configuration;

public class Config {
	
	// The following IDs are also used as default values.
	
	public static int crateId      = 2830;
	public static int chestId      = 2831;
	public static int lockerId     = 2832;
	public static int armorStandId = 2833;
	public static int backpackId   = 2834;
	
	public static int keyId     = 28540;
	public static int lockId    = 28541;
	public static int keyringId = 28542;
	
	// Uses 180 - 182 and 185 - 188 (or w/e the base id is).
	public static int enchantmentBaseId = 180;
	
	// More settings ...
	public static int reinforcedChestColumns = 13;
	public static int backpackRows = 3;
	
	private static Configuration config;
	
	public static void load(File file) {
		
		config = new Configuration(file);
		config.load();
		
		crateId      = config.getBlock("crate", crateId).getInt();
		chestId      = config.getBlock("chest", chestId).getInt();
		lockerId     = config.getBlock("locker", lockerId).getInt();
		armorStandId = config.getBlock("armorStand", armorStandId).getInt();
		backpackId   = config.getBlock("backpack", backpackId).getInt();
		
		keyId          = config.getItem("key", keyId).getInt();
		lockId         = config.getItem("lock", lockId).getInt();
		keyringId      = config.getItem("keyring", keyringId).getInt();
		
		enchantmentBaseId = config.get(Configuration.CATEGORY_GENERAL, "enchantmentBaseId", enchantmentBaseId).getInt();
		
		reinforcedChestColumns = config.get(Configuration.CATEGORY_GENERAL, "reinforcedChestColumns", reinforcedChestColumns,
		                                   "Number of colums in reinforced chests. Valid values are 9, 11 and 13.").getInt(reinforcedChestColumns);
		
		backpackRows = config.get(Configuration.CATEGORY_GENERAL, "backpackRows",backpackRows,
		                          "Number of rows in backpacks. Valid values are 1 to 6.").getInt(backpackRows);
		
		config.save();
		
	}
	
	private static void validate() {
		
		reinforcedChestColumns = validateColumnAmount(reinforcedChestColumns, "reinforcedChestColumns", 13);
		backpackRows = validateRange(backpackRows, "backpackRows", 1, 6);
		
	}
	
	private static int validateRange(int value, String name, int minValue, int maxValue) {
		if (value >= minValue && value <= maxValue) return value;
		BetterStorage.log.warning("Config value '" + name + "' is not valid, must be " + minValue + " min and " + maxValue + " max.");
		return Math.max(minValue, Math.min(maxValue, value));
	}
	
	private static int validateColumnAmount(int columns, String name, int defaultValue) {
		if (columns == 9 || columns == 11 || columns == 13) return columns;
		BetterStorage.log.warning("Config value '" + name + "' is not valid, must be 9, 11 or 13.");
		return defaultValue;
	}
	
}
