package net.mcft.copy.betterstorage;

import java.io.File;

import net.mcft.copy.betterstorage.addon.Addon;
import net.minecraftforge.common.Configuration;

public class Config {
	
	private static final String categorySettings = "settings";
	
	// The following IDs are also used as default values.
	
	public static int crateId         = 2830;
	public static int chestId         = 2831;
	public static int lockerId        = 2832;
	public static int armorStandId    = 2833;
	public static int backpackId      = 2834;
	public static int enderBackpackId = 2835;
	public static int cardboardBoxId  = 2836;
	
	public static int keyId            = 28540;
	public static int lockId           = 28541;
	public static int keyringId        = 28542;
	public static int cardboardSheetId = 28543;
	public static int masterKeyId      = 28544;
	
	public static int enchantmentBaseId = 0;
	
	public static int backpackOpenDataWatcherId = 27;
	
	// More settings ...
	public static int reinforcedChestColumns = 13;
	public static int backpackRows = 3;
	public static boolean enableCrateInventoryInterface = false;
	public static boolean enableBackpackOpen = false;
	public static int backpackOpenKey = 48;
	
	public static void load(File file) {
		
		Configuration config = new Configuration(file);
		config.load();
		
		crateId         = config.getBlock("crate", crateId).getInt();
		chestId         = config.getBlock("chest", chestId).getInt();
		lockerId        = config.getBlock("locker", lockerId).getInt();
		armorStandId    = config.getBlock("armorStand", armorStandId).getInt();
		backpackId      = config.getBlock("backpack", backpackId).getInt();
		enderBackpackId = config.getBlock("enderBackpack", enderBackpackId).getInt();
		cardboardBoxId  = config.getBlock("cardboardBox", cardboardBoxId).getInt();
		
		keyId            = config.getItem("key", keyId).getInt();
		lockId           = config.getItem("lock", lockId).getInt();
		keyringId        = config.getItem("keyring", keyringId).getInt();
		cardboardSheetId = config.getItem("cardboardSheet", cardboardSheetId).getInt();
		masterKeyId      = config.getItem("masterKey", masterKeyId).getInt();
		
		enchantmentBaseId = config.get(Configuration.CATEGORY_GENERAL, "enchantmentBaseId", enchantmentBaseId,
		                               "Uses up about 10 IDs starting from this ID.").getInt();
		
		backpackOpenDataWatcherId = config.get(Configuration.CATEGORY_GENERAL, "backpackOpenDataWatcherId", backpackOpenDataWatcherId,
		                                       "Valid values are 0 to 31, though many are already occupied.").getInt();
		
		reinforcedChestColumns = config.get(categorySettings, "reinforcedChestColumns", reinforcedChestColumns,
		                                   "Number of colums in reinforced chests. Valid values are 9, 11 and 13.").getInt();
		
		backpackRows = config.get(categorySettings, "backpackRows", backpackRows,
		                          "Number of rows in backpacks. Valid values are 1 to 6.").getInt();
		
		enableCrateInventoryInterface = config.get(categorySettings, "enableCrateInventoryInterface", enableCrateInventoryInterface,
		                                           "Whether most machines can interact with crates (disabled because of dupe issues).").getBoolean(enableCrateInventoryInterface);
		
		enableBackpackOpen = config.get(categorySettings, "enableBackpackOpen", enableBackpackOpen,
		                                "Allows backpacks to be opened when equipped by pressing a key.").getBoolean(enableBackpackOpen);
		
		backpackOpenKey = config.get(categorySettings, "backpackOpenKey", backpackOpenKey,
		                             "The key to open a backpack while equipped, if enabled. (Default: B)").getInt();
		
		validate();
		
		Addon.loadConfigsAll(config);
		
		config.save();
		
	}
	
	private static void validate() {
		
		backpackOpenDataWatcherId = validateRange(backpackOpenDataWatcherId, "backpackOpenDataWatcherId", 0, 31);
		reinforcedChestColumns = validateColumnAmount(reinforcedChestColumns, "reinforcedChestColumns", 13);
		backpackRows = validateRange(backpackRows, "backpackRows", 1, 6);
		
	}
	
	public static int validateRange(int value, String name, int minValue, int maxValue) {
		if ((value >= minValue) && (value <= maxValue)) return value;
		BetterStorage.log.warning("Config value '" + name + "' is not valid, must be " + minValue + " min and " + maxValue + " max.");
		return Math.max(minValue, Math.min(maxValue, value));
	}
	
	public static int validateColumnAmount(int columns, String name, int defaultValue) {
		if ((columns == 9) || (columns == 11) || (columns == 13)) return columns;
		BetterStorage.log.warning("Config value '" + name + "' is not valid, must be 9, 11 or 13.");
		return defaultValue;
	}
	
}
