package net.mcft.copy.betterstorage.config;

import java.io.File;

import net.mcft.copy.betterstorage.config.setting.BooleanSetting;
import net.mcft.copy.betterstorage.config.setting.EnchantmentIdSetting;
import net.mcft.copy.betterstorage.config.setting.IntegerSetting;
import net.mcft.copy.betterstorage.config.setting.ItemIdSetting;
import net.mcft.copy.betterstorage.config.setting.TileIdSetting;

public class GlobalConfig extends Config {
	
	// Often accessed settings
	public static BooleanSetting enableCrateInventoryInterfaceSetting;
	public static BooleanSetting enableStationAutoCraftingSetting;
	public static IntegerSetting stationAutocraftDelaySetting;
	
	// TODO: Rename category to "tile"
	public static final String crateId            = "block.crate";
	public static final String reinforcedChestId  = "block.chest"; // TODO: Rename to "reinforcedChest"
	public static final String lockerId           = "block.locker";
	public static final String armorStandId       = "block.armorStand";
	public static final String backpackId         = "block.backpack";
	public static final String enderBackpackId    = "block.enderBackpack";
	public static final String cardboardBoxId     = "block.cardboardBox";
	public static final String reinforcedLockerId = "block.reinforcedLocker";
	public static final String craftingStationId  = "block.craftingStation";
	public static final String flintBlockId       = "block.flintBlock";
	
	public static final String keyId            = "item.key";
	public static final String lockId           = "item.lock";
	public static final String keyringId        = "item.keyring";
	public static final String cardboardSheetId = "item.cardboardSheet";
	public static final String masterKeyId      = "item.masterKey";
	public static final String drinkingHelmetId = "item.drinkingHelmet";
	public static final String slimeBucketId    = "item.slimeBucket";
	
	public static final String cardboardHelmetId     = "item.cardboardHelmet";
	public static final String cardboardChestplateId = "item.cardboardChestplate";
	public static final String cardboardLeggingsId   = "item.cardboardLeggings";
	public static final String cardboardBootsId      = "item.cardboardBoots";
	public static final String cardboardSwordId   = "item.cardboardSword";
	public static final String cardboardPickaxeId = "item.cardboardPickaxe";
	public static final String cardboardShovelId  = "item.cardboardShovel";
	public static final String cardboardAxeId     = "item.cardboardAxe";
	public static final String cardboardHoeId     = "item.cardboardHoe";
	
	// TODO: Rename category to "enchantment"
	public static final String enchUnlockingId   = "enchantments.unlocking";
	public static final String enchLockpickingId = "enchantments.lockpicking";
	public static final String enchMorphingId    = "enchantments.morphing";
	public static final String enchPersistanceId = "enchantments.persistance";
	public static final String enchSecurityId    = "enchantments.security";
	public static final String enchShockId       = "enchantments.shock";
	public static final String enchTriggerId     = "enchantments.trigger";
	
	// TODO: Rename category to "general"?
	public static final String reinforcedColumns             = "settings.reinforcedColums";
	public static final String enableCrateInventoryInterface = "settings.enableCrateInventoryInterface";
	public static final String backpackChestplate            = "settings.backpackChestplate";
	public static final String backpackRows                  = "settings.backpackRows";
	public static final String enableBackpackOpen            = "settings.enableBackpackOpen";
	public static final String enableBackpackInteraction     = "settings.enableBackpackInteraction";
	public static final String dropBackpackOnDeath           = "settings.dropBackpackOnDeath";
	public static final String enableStationAutoCrafting     = "settings.enableStationAutoCrafting";
	public static final String stationAutocraftDelay         = "settings.stationAutocraftDelay";
	public static final String enableHelpTooltips            = "settings.enableHelpTooltips";
	
	public GlobalConfig(File file) {
		super(file);
		
		// Block IDs
		new TileIdSetting(this, crateId, 2830);
		new TileIdSetting(this, reinforcedChestId, 2831);
		new TileIdSetting(this, lockerId, 2832);
		new TileIdSetting(this, armorStandId, 2833);
		new TileIdSetting(this, backpackId, 2834);
		new TileIdSetting(this, enderBackpackId, 2835);
		new TileIdSetting(this, cardboardBoxId, 2836);
		new TileIdSetting(this, reinforcedLockerId, 2837);
		new TileIdSetting(this, craftingStationId, 2838);
		new TileIdSetting(this, flintBlockId, 2839);
		
		// Item IDs
		new ItemIdSetting(this, keyId, 28540);
		new ItemIdSetting(this, lockId, 28541);
		new ItemIdSetting(this, keyringId, 28542);
		new ItemIdSetting(this, cardboardSheetId, 28543);
		new ItemIdSetting(this, masterKeyId, 28544);
		new ItemIdSetting(this, drinkingHelmetId, 28545);
		new ItemIdSetting(this, slimeBucketId, 28546);
		
		// Cardboard item IDs
		new ItemIdSetting(this, cardboardHelmetId, 28560);
		new ItemIdSetting(this, cardboardChestplateId, 28561);
		new ItemIdSetting(this, cardboardLeggingsId, 28562);
		new ItemIdSetting(this, cardboardBootsId, 28563);
		new ItemIdSetting(this, cardboardSwordId, 28564);
		new ItemIdSetting(this, cardboardPickaxeId, 28565);
		new ItemIdSetting(this, cardboardShovelId, 28566);
		new ItemIdSetting(this, cardboardAxeId, 28567);
		new ItemIdSetting(this, cardboardHoeId, 28568);
		
		// Enchantment IDs
		new EnchantmentIdSetting(this, enchUnlockingId, 170);
		new EnchantmentIdSetting(this, enchLockpickingId, 171);
		new EnchantmentIdSetting(this, enchMorphingId, 172);
		new EnchantmentIdSetting(this, enchPersistanceId, 173);
		new EnchantmentIdSetting(this, enchSecurityId, 174);
		new EnchantmentIdSetting(this, enchShockId, 175);
		new EnchantmentIdSetting(this, enchTriggerId, 176);
		
		// Reinforced chest settings
		new IntegerSetting(this, reinforcedColumns, 13).setValidValues(9, 11, 13).setComment(
				"Number of columns in reinforced chests and lockers. Valid values are 9, 11 and 13.");
		
		// Crate settings
		enableCrateInventoryInterfaceSetting =
		new BooleanSetting(this, enableCrateInventoryInterface, true).setComment(
				"If enabled, exposes a special block view of crates, so items can be moved in and out by automated systems.");
		
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
		
		// Crafting Station settings
		enableStationAutoCraftingSetting =
		new BooleanSetting(this, enableStationAutoCrafting, false).setSynced().setComment(
				"If enabled, automated systems can pull out of crafting stations and therefore auto-craft items.");
		stationAutocraftDelaySetting =
		new IntegerSetting(this, stationAutocraftDelay, 10).setValidRange(0, Integer.MAX_VALUE).setComment(
				"Delay between recipes being autocrafted in the crafting station, in ticks. (Default: 10)");
		
		// Miscellaneous settings
		new BooleanSetting(this, enableHelpTooltips, true).setComment(
				"If enabled, shows tooltips on some items to help players who're new to the mod.");
	
	}
	
}
