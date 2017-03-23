package net.mcft.copy.betterstorage.config;

import java.io.File;

import net.mcft.copy.betterstorage.config.setting.BooleanSetting;
import net.mcft.copy.betterstorage.config.setting.EnchantmentIdSetting;
import net.mcft.copy.betterstorage.config.setting.IntegerSetting;

public class GlobalConfig extends Config {
	
	// Often accessed settings
	public static BooleanSetting enableCrateInventoryInterfaceSetting;
	public static BooleanSetting enableCrateStorageInterfaceSetting;
	public static BooleanSetting enableStationAutoCraftingSetting;
	public static BooleanSetting enableStationVanillaCraftingSetting;
	public static IntegerSetting stationAutocraftDelaySetting;
	
	// Tiles
	public static final String crateEnabled            = "tile.crate";
	public static final String reinforcedChestEnabled  = "tile.reinforcedChest";
	public static final String lockerEnabled           = "tile.locker";
	public static final String armorStandEnabled       = "tile.armorStand";
	public static final String backpackEnabled         = "tile.backpack";
	public static final String enderBackpackEnabled    = "tile.enderBackpack";
	public static final String cardboardBoxEnabled     = "tile.cardboardBox";
	public static final String reinforcedLockerEnabled = "tile.reinforcedLocker";
	public static final String craftingStationEnabled  = "tile.craftingStation";
	public static final String flintBlockEnabled       = "tile.flintBlock";
	public static final String lockableDoorEnabled     = "tile.lockableDoor";
	public static final String presentEnabled          = "tile.present";
	
	// Items
	public static final String keyEnabled            = "item.key";
	public static final String lockEnabled           = "item.lock";
	public static final String keyringEnabled        = "item.keyring";
	public static final String cardboardSheetEnabled = "item.cardboardSheet";
	public static final String masterKeyEnabled      = "item.masterKey";
	public static final String drinkingHelmetEnabled = "item.drinkingHelmet";
	public static final String slimeBucketEnabled    = "item.slimeBucket";
	
	// Cardboard items
	public static final String cardboardHelmetEnabled     = "item.cardboardHelmet";
	public static final String cardboardChestplateEnabled = "item.cardboardChestplate";
	public static final String cardboardLeggingsEnabled   = "item.cardboardLeggings";
	public static final String cardboardBootsEnabled      = "item.cardboardBoots";
	public static final String cardboardSwordEnabled   = "item.cardboardSword";
	public static final String cardboardPickaxeEnabled = "item.cardboardPickaxe";
	public static final String cardboardShovelEnabled  = "item.cardboardShovel";
	public static final String cardboardAxeEnabled     = "item.cardboardAxe";
	public static final String cardboardHoeEnabled     = "item.cardboardHoe";
	
	// Enchantments
	public static final String enchUnlockingId   = "enchantment.unlocking";
	public static final String enchLockpickingId = "enchantment.lockpicking";
	public static final String enchMorphingId    = "enchantment.morphing";
	public static final String enchPersistanceId = "enchantment.persistance";
	public static final String enchSecurityId    = "enchantment.security";
	public static final String enchShockId       = "enchantment.shock";
	public static final String enchTriggerId     = "enchantment.trigger";
	
	// General settings
	public static final String reinforcedColumns             = "general.reinforcedColumns";
	
	public static final String enableCrateInventoryInterface = "general.enableCrateInventoryInterface";
	public static final String enableCrateStorageInterface   = "general.enableCrateStorageInterface";
	public static final String crateDebugMessages            = "general.crateDebugMessages";
	
	public static final String backpackChestplate            = "general.backpackChestplate";
	public static final String backpackRows                  = "general.backpackRows";
	public static final String enableBackpackOpen            = "general.enableBackpackOpen";
	public static final String enableBackpackInteraction     = "general.enableBackpackInteraction";
	public static final String dropBackpackOnDeath           = "general.dropBackpackOnDeath";
	
	public static final String cardboardBoxRows              = "general.cardboardBoxRows";
	public static final String cardboardBoxUses              = "general.cardboardBoxUses";
	public static final String cardboardBoxShowContents      = "general.cardboardBoxShowContents";
	
	public static final String enableStationAutoCrafting     = "general.enableStationAutoCrafting";
	public static final String enableStationVanillaCrafting  = "general.enableStationVanillaCrafting";
	public static final String stationAutocraftDelay         = "general.stationAutocraftDelay";
	
	public static final String lockBreakable                 = "general.lockBreakable";
	
	public static final String enableHelpTooltips            = "general.enableHelpTooltips";
	public static final String enableWarningMessages         = "general.enableWarningMessages";
	public static final String enableChristmasEvent          = "general.enableChristmasEvent";
	
	
	public GlobalConfig(File file) {
		super(file);
		
		// Tiles
		new BooleanSetting(this, crateEnabled, true, "tile.betterstorage.crate.name").setHasComment(false).setRequiresMcRestart(true);
		new BooleanSetting(this, reinforcedChestEnabled, true, "tile.betterstorage.reinforcedChest.name").setHasComment(false).setRequiresMcRestart(true);
		new BooleanSetting(this, lockerEnabled, true, "tile.betterstorage.locker.name").setHasComment(false).setRequiresMcRestart(true);
		new BooleanSetting(this, armorStandEnabled, true, "tile.betterstorage.armorStand.name").setHasComment(false).setRequiresMcRestart(true);
		new BooleanSetting(this, backpackEnabled, true, "tile.betterstorage.backpack.name").setHasComment(false).setRequiresMcRestart(true);
		new BooleanSetting(this, enderBackpackEnabled, true, "tile.betterstorage.enderBackpack.name").setHasComment(false).setRequiresMcRestart(true);
		new BooleanSetting(this, cardboardBoxEnabled, true, "tile.betterstorage.cardboardBox.name").setHasComment(false).setRequiresMcRestart(true);
		new BooleanSetting(this, reinforcedLockerEnabled, true, "tile.betterstorage.reinforcedLocker.name").setHasComment(false).setRequiresMcRestart(true);
		new BooleanSetting(this, craftingStationEnabled, true, "tile.betterstorage.craftingStation.name").setHasComment(false).setRequiresMcRestart(true);
		new BooleanSetting(this, flintBlockEnabled, true, "tile.betterstorage.flintBlock.name").setHasComment(false).setRequiresMcRestart(true);
		new BooleanSetting(this, lockableDoorEnabled, true, "tile.betterstorage.lockableDoor.name").setHasComment(false).setRequiresMcRestart(true);
		new BooleanSetting(this, presentEnabled, true, "tile.betterstorage.present.name").setHasComment(false).setRequiresMcRestart(true);
		
		// Items
		new BooleanSetting(this, keyEnabled, true, "item.betterstorage.key.name").setHasComment(false).setRequiresMcRestart(true);
		new BooleanSetting(this, lockEnabled, true, "item.betterstorage.lock.name").setHasComment(false).setRequiresMcRestart(true);
		new BooleanSetting(this, keyringEnabled, true, "item.betterstorage.keyring.name").setHasComment(false).setRequiresMcRestart(true);
		new BooleanSetting(this, cardboardSheetEnabled, true, "item.betterstorage.cardboardSheet.name").setHasComment(false).setRequiresMcRestart(true);
		new BooleanSetting(this, masterKeyEnabled, true, "item.betterstorage.masterKey.name").setHasComment(false).setRequiresMcRestart(true);
		new BooleanSetting(this, drinkingHelmetEnabled, true, "item.betterstorage.drinkingHelmet.name").setHasComment(false).setRequiresMcRestart(true);
		new BooleanSetting(this, slimeBucketEnabled, true, "item.betterstorage.bucketSlime.name").setHasComment(false).setRequiresMcRestart(true);
		
		// Cardboard items
		new BooleanSetting(this, cardboardHelmetEnabled, true, "item.betterstorage.cardboardHelmet.name").setHasComment(false).setRequiresMcRestart(true);
		new BooleanSetting(this, cardboardChestplateEnabled, true, "item.betterstorage.cardboardChestplate.name").setHasComment(false).setRequiresMcRestart(true);
		new BooleanSetting(this, cardboardLeggingsEnabled, true, "item.betterstorage.cardboardLeggings.name").setHasComment(false).setRequiresMcRestart(true);
		new BooleanSetting(this, cardboardBootsEnabled, true, "item.betterstorage.cardboardBoots.name").setHasComment(false).setRequiresMcRestart(true);
		new BooleanSetting(this, cardboardSwordEnabled, true, "item.betterstorage.cardboardSword.name").setHasComment(false).setRequiresMcRestart(true);
		new BooleanSetting(this, cardboardPickaxeEnabled, true, "item.betterstorage.cardboardPickaxe.name").setHasComment(false).setRequiresMcRestart(true);
		new BooleanSetting(this, cardboardShovelEnabled, true, "item.betterstorage.cardboardShovel.name").setHasComment(false).setRequiresMcRestart(true);
		new BooleanSetting(this, cardboardAxeEnabled, true, "item.betterstorage.cardboardAxe.name").setHasComment(false).setRequiresMcRestart(true);
		new BooleanSetting(this, cardboardHoeEnabled, true, "item.betterstorage.cardboardHoe.name").setHasComment(false).setRequiresMcRestart(true);
		
		// Enchantments
		new EnchantmentIdSetting(this, enchUnlockingId, 170, "enchantment.betterstorage.key.unlocking");
		new EnchantmentIdSetting(this, enchLockpickingId, 171, "enchantment.betterstorage.key.lockpicking");
		new EnchantmentIdSetting(this, enchMorphingId, 172, "enchantment.betterstorage.key.morphing");
		new EnchantmentIdSetting(this, enchPersistanceId, 173, "enchantment.betterstorage.lock.persistance");
		new EnchantmentIdSetting(this, enchSecurityId, 174, "enchantment.betterstorage.lock.security");
		new EnchantmentIdSetting(this, enchShockId, 175, "enchantment.betterstorage.lock.shock");
		new EnchantmentIdSetting(this, enchTriggerId, 176, "enchantment.betterstorage.lock.trigger");
		
		// Reinforced chest settings
		new IntegerSetting(this, reinforcedColumns, 13).setValidValues(9, 11, 13).setComment(
				"Number of columns in reinforced chests and lockers. Valid values are 9, 11 and 13.");
		
		// Crate settings
		enableCrateInventoryInterfaceSetting =
		new BooleanSetting(this, enableCrateInventoryInterface, true).setComment(
				"If enabled, exposes a special block view of crates, so items can be moved in and out by automated systems.");
		enableCrateStorageInterfaceSetting =
		new BooleanSetting(this, enableCrateStorageInterface, true).setComment(
				"If disabled, prevents mods from using storage crates' special storage interface (like Applied Energistics).");
		new BooleanSetting(this, crateDebugMessages).setComment(
				"If enabled, logs crate related debug messages (like them saving).");
		
		// Backpack settings
		new BooleanSetting(this, backpackChestplate, true).setSynced().setComment(
				"If disabled, backpacks don't take up the player's chestplate armor slot.");
		new IntegerSetting(this, backpackRows, 4).setValidRange(1, 6).setComment(
				"Number of rows in backpacks. Valid values are 1 to 6.");
		new BooleanSetting(this, enableBackpackOpen, false).setSynced().setComment(
				"Allows equipped backpacks to be opened by pressing a key.");
		new BooleanSetting(this, enableBackpackInteraction, true).setComment(
				"Allows equipped backpacks to be opened by other players by right clicking them.");
		new BooleanSetting(this, dropBackpackOnDeath, true).setComment(
				"If enabled, drops backpacks as block instead of spilling the items around.");
		
		// Cardboard box settings
		new IntegerSetting(this, cardboardBoxRows, 1).setValidRange(1, 3).setComment(
				"Number of rows in cardboard boxes. Valid values are 1 to 3.");
		new IntegerSetting(this, cardboardBoxUses, 4).setValidRange(0, Integer.MAX_VALUE).setSynced().setComment(
				"Number of times cardboard boxes can be picked up with items before they break. Use 0 for infinite uses.");
		new BooleanSetting(this, cardboardBoxShowContents, true).setComment(
				"If disabled, doesn't show cardboard box contents in their tooltips.");
		
		// Crafting Station settings
		enableStationAutoCraftingSetting =
		new BooleanSetting(this, enableStationAutoCrafting, false).setSynced().setComment(
				"If enabled, automated systems can pull out of crafting stations and therefore auto-craft items.");
		enableStationVanillaCraftingSetting =
		new BooleanSetting(this, enableStationVanillaCrafting, true).setSynced().setComment(
				"If enabled, regular vanilla crafting recipes can be made inside crafting stations.");
		stationAutocraftDelaySetting =
		new IntegerSetting(this, stationAutocraftDelay, 10).setValidRange(0, Integer.MAX_VALUE).setComment(
				"Delay between recipes being autocrafted in the crafting station, in ticks. (Default: 10)");
		
		// Key / Lock settings
		new BooleanSetting(this, lockBreakable, true).setSynced().setComment(
				"If disabled, turns off the ability to break locks off of locked containers using tools.");
		
		// Miscellaneous settings
		new BooleanSetting(this, enableHelpTooltips, true).setComment(
				"If enabled, shows tooltips on some items to help players who're new to the mod.");
		new BooleanSetting(this, enableWarningMessages, true).setComment(
				"If disabled, prevents certain warning messages from being logged to the console.");
		new BooleanSetting(this, enableChristmasEvent, true).setComment(
				"If disabled, prevents the christmas event from happening. You monster.");
	
	}
	
}
