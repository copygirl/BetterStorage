package net.mcft.copy.betterstorage;

import java.io.File;

import net.minecraftforge.common.Configuration;

public class Config {
	
	// The following IDs are also used as default values.
	
	public static int crateId = 2830;
	// Defines the beginning of a ~20 id range.
	public static int chestBaseId = 2835;
	
	private static Configuration config;
	
	public static void load(File file) {
		config = new Configuration(file);
		config.load();
		
		crateId     = config.getBlock("crate", crateId).getInt();
		chestBaseId = config.getBlock("chestBaseId", chestBaseId).getInt();
	}
	
	public static void save() {
		config.save();
	}
	
}
