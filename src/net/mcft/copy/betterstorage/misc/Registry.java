package net.mcft.copy.betterstorage.misc;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import net.mcft.copy.betterstorage.BetterStorage;
import net.mcft.copy.betterstorage.item.ItemArmorStand;
import net.mcft.copy.betterstorage.item.ItemReinforcedChest;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;

/** Handles the registering and naming of blocks and items. */
public class Registry {
	
	public static void doYourThing() {
		// Yes, ma'am!
		registerBlocks();
		registerItems();
	}
	
	public static void registerBlocks() {
		register(BetterStorage.crate, "crate", "Storage Crate");
		register(BetterStorage.reinforcedChest, "reinforcedChest", null, ItemReinforcedChest.class);
		register(BetterStorage.locker, "locker", "Locker");
		register(BetterStorage.armorStand, "armorStand", "Armor Stand", ItemArmorStand.class);
		register(BetterStorage.backpack, "backpack", "Backpack");
	}
	
	public static void registerItems() {
		register(BetterStorage.key, "key", "Key");
		register(BetterStorage.lock, "lock", "Lock");
		register(BetterStorage.keyring, "keyring", "Keyring");
	}
	
	public static void register(Block block, String name, String fullName, Class<? extends ItemBlock> itemBlockClass) {
		block.setUnlocalizedName(name);
		GameRegistry.registerBlock(block, itemBlockClass, name, "BetterStorage");
		if (fullName != null)
			LanguageRegistry.addName(block, fullName);
		block.setCreativeTab(BetterStorage.creativeTab);
	}
	public static void register(Block block, String name, String fullName) {
		register(block, name, fullName, ItemBlock.class);
	}
	
	public static void register(Item item, String name, String fullName) {
		item.setUnlocalizedName(name);
		LanguageRegistry.addName(item, fullName);
		item.setCreativeTab(BetterStorage.creativeTab);
	}
	
}
