package net.mcft.copy.betterstorage;

import java.io.File;

import net.mcft.copy.betterstorage.addon.Addon;
import net.minecraftforge.common.Configuration;

public final class Config {
	
	private static final String categorySettings = "settings";
	private static final String categoryEnchantments = "enchantments";
	
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
	public static int drinkingHelmetId = 28545;
	
	public static int cardboardHelmetId     = 28560;
	public static int cardboardChestplateId = 28561;
	public static int cardboardLeggingsId   = 28562;
	public static int cardboardBootsId      = 28563;
	public static int cardboardSwordId   = 28564;
	public static int cardboardPickaxeId = 28565;
	public static int cardboardShovelId  = 28566;
	public static int cardboardAxeId     = 28567;
	public static int cardboardHoeId     = 28568;
	
	public static int enchantmentUnlockingId   = 170;
	public static int enchantmentLockpickingId = 171;
	public static int enchantmentMorphingId    = 172;
	public static int enchantmentPersistanceId = 173;
	public static int enchantmentSecurityId    = 174;
	public static int enchantmentShockId       = 175;
	public static int enchantmentTriggerId     = 176;
	
	public static int backpackOpenDataWatcherId = 27;
	
	// More settings ...
	public static int reinforcedChestColumns = 13;
	public static int backpackRows = 3;
	public static boolean enableCrateInventoryInterface = false;
	public static boolean enableBackpackOpen = false;
	public static boolean dropBackpackOnDeath = true;
	public static boolean enableHelpTooltips = true;
	
	public static int backpackOpenKey = 48;
	public static int drinkingHelmetKey = 33;
	
	
	private Config() {  }
	
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
		drinkingHelmetId = config.getItem("drinkingHelmet", drinkingHelmetId).getInt();
		
		cardboardHelmetId     = config.getItem("cardboardHelmet", cardboardHelmetId).getInt();
		cardboardChestplateId = config.getItem("cardboarChestplate", cardboardChestplateId).getInt();
		cardboardLeggingsId   = config.getItem("cardboardLeggings", cardboardLeggingsId).getInt();
		cardboardBootsId      = config.getItem("cardboardBoots", cardboardBootsId).getInt();
		cardboardSwordId   = config.getItem("cardboardSword", cardboardSwordId).getInt();
		cardboardPickaxeId = config.getItem("cardboardPickaxe", cardboardPickaxeId).getInt();
		cardboardShovelId  = config.getItem("cardboardShovel", cardboardShovelId).getInt();
		cardboardAxeId     = config.getItem("cardboardAxe", cardboardAxeId).getInt();
		cardboardHoeId     = config.getItem("cardboardHoe", cardboardHoeId).getInt();
		
		enchantmentUnlockingId   = config.get(categoryEnchantments, "unlocking", enchantmentUnlockingId).getInt();
		enchantmentLockpickingId = config.get(categoryEnchantments, "lockpicking", enchantmentLockpickingId).getInt();
		enchantmentMorphingId    = config.get(categoryEnchantments, "morphing", enchantmentMorphingId).getInt();
		enchantmentPersistanceId = config.get(categoryEnchantments, "persistance", enchantmentPersistanceId).getInt();
		enchantmentSecurityId    = config.get(categoryEnchantments, "security", enchantmentSecurityId).getInt();
		enchantmentShockId       = config.get(categoryEnchantments, "shock", enchantmentShockId).getInt();
		enchantmentTriggerId     = config.get(categoryEnchantments, "trigger", enchantmentTriggerId).getInt();
		
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
		
		dropBackpackOnDeath = config.get(categorySettings, "dropBackpackOnDeath", dropBackpackOnDeath,
		                                 "If enabled, drops backpacks as block instead of spilling the items around.").getBoolean(dropBackpackOnDeath);
		
		backpackOpenKey = config.get(categorySettings, "backpackOpenKey", backpackOpenKey,
		                             "The key to open a backpack while equipped, if enabled. (Default: B)").getInt();
		
		drinkingHelmetKey = config.get(categorySettings, "drinkingHelmetKey", drinkingHelmetKey,
		                               "The key to use a drinking helmet when equipped. (Default: F)").getInt();
		
		enableHelpTooltips = config.get(categorySettings, "enableHelpTooltips", enableHelpTooltips,
		                                "If enabled, shows tooltips on some items to help players who're new to the mod.").getBoolean(enableHelpTooltips);
		
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
