package net.mcft.copy.betterstorage.misc;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import net.mcft.copy.betterstorage.BetterStorage;
import net.mcft.copy.betterstorage.addon.Addon;
import net.mcft.copy.betterstorage.item.ItemArmorStand;
import net.mcft.copy.betterstorage.item.ItemBackpack;
import net.mcft.copy.betterstorage.item.ItemReinforcedChest;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;

/** Handles the registering and naming of blocks and items. */
public class Registry {
	
	public static void doYourThing() {
		// Yes, ma'am!
		BetterStorage.log.info("BetterStorage will overwrite some of its own items. Don't worry, this is normal.");
		registerBlocks();
		registerItems();
		Addon.registerAllItems();
	}
	
	public static void registerBlocks() {
		register(BetterStorage.crate, "crate", "Storage Crate");
		register(BetterStorage.reinforcedChest, "reinforcedChest", "Reinforced Chest", ItemReinforcedChest.class);
		register(BetterStorage.locker, "locker", "Locker");
		register(BetterStorage.armorStand, "armorStand", "Armor Stand", ItemArmorStand.class);
		registerHacky(BetterStorage.backpack, "backpack", "Backpack", ItemBackpack.class);
	}
	
	public static void registerItems() {
		register(BetterStorage.key, "key", "Key");
		register(BetterStorage.lock, "lock", "Lock");
		register(BetterStorage.keyring, "keyring", "Keyring");
	}
	
	public static void register(Block block, String name, String fullName, Class<? extends ItemBlock> itemBlockClass) {
		block.setUnlocalizedName(name);
		GameRegistry.registerBlock(block, itemBlockClass, name, "BetterStorage");
		if (fullName != null) {
			LanguageRegistry.addName(block, fullName);
			block.setCreativeTab(BetterStorage.creativeTab);
		}
	}
	public static void registerHacky(Block block, String name, String fullName, Class<? extends Item> itemClass) {
		register(block, name, null);
		Item.itemsList[block.blockID] = null;
		try {
			Item item = itemClass.getConstructor(int.class).newInstance(block.blockID);
			item.setUnlocalizedName(name);
			LanguageRegistry.addName(item, fullName);
			item.setCreativeTab(BetterStorage.creativeTab);
		} catch (Exception e) { throw new RuntimeException(e); }
	}
	public static void register(Block block, String name, String fullName) {
		register(block, name, fullName, ItemBlock.class);
	}
	
	public static void register(Item item, String name, String fullName) {
		item.setUnlocalizedName(name);
		if (fullName != null) {
			LanguageRegistry.addName(item, fullName);
			item.setCreativeTab(BetterStorage.creativeTab);
		}
	}
	
}
