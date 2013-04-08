package net.mcft.copy.betterstorage;

import java.io.File;

import net.minecraftforge.common.Configuration;

public class Config {
	
	// The following IDs are also used as default values.
	
	public static int crateId      = 2830;
	public static int chestId      = 2831;
	public static int lockerId     = 2832;
	public static int armorStandId = 2833;
	
	public static int keyId        = 28540;
	public static int lockId       = 28541;
	public static int keyringId    = 28542;
	
	// Uses 180 - 182 and 185 - 188 (or w/e the base id is).
	public static int enchantmentBaseId = 180;
	
	/** When set to true, will make all chests the vanilla chest size. */
	public static boolean normalSizedChests = false;
	
	private static Configuration config;
	
	public static void load(File file) {
		config = new Configuration(file);
		config.load();
		
		crateId      = config.getBlock("crate", crateId).getInt();
		chestId      = config.getBlock("chest", chestId).getInt();
		lockerId     = config.getBlock("locker", lockerId).getInt();
		armorStandId = config.getBlock("armorStand", armorStandId).getInt();
		
		keyId        = config.getItem("key", keyId).getInt();
		lockId       = config.getItem("lock", lockId).getInt();
		keyringId    = config.getItem("keyring", keyringId).getInt();
		
		enchantmentBaseId = config.get(Configuration.CATEGORY_GENERAL, "enchantmentBaseId", enchantmentBaseId).getInt();
		
		normalSizedChests = config.get(Configuration.CATEGORY_GENERAL, "normalSizedChests", normalSizedChests,
		                               "When set to true, will make all chests the vanilla chest size.").getBoolean(normalSizedChests);
	}
	
	public static void save() {
		config.save();
	}
	
}
