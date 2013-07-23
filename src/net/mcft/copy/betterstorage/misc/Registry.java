package net.mcft.copy.betterstorage.misc;

import net.mcft.copy.betterstorage.BetterStorage;
import net.mcft.copy.betterstorage.addon.Addon;
import net.mcft.copy.betterstorage.item.ItemArmorStand;
import net.mcft.copy.betterstorage.item.ItemBackpack;
import net.mcft.copy.betterstorage.item.ItemCardboardBox;
import net.mcft.copy.betterstorage.item.ItemEnderBackpack;
import net.mcft.copy.betterstorage.item.ItemReinforcedChest;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import cpw.mods.fml.common.registry.GameRegistry;

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
		register(BetterStorage.crate, "crate");
		register(BetterStorage.reinforcedChest, "reinforcedChest", ItemReinforcedChest.class);
		register(BetterStorage.locker, "locker");
		register(BetterStorage.armorStand, "armorStand", ItemArmorStand.class);
		registerHacky(BetterStorage.backpack, "backpack", ItemBackpack.class);
		registerHacky(BetterStorage.enderBackpack, "enderBackpack", ItemEnderBackpack.class);
		register(BetterStorage.cardboardBox, "cardboardBox", ItemCardboardBox.class);
	}
	
	public static void registerItems() {
		register(BetterStorage.key, "key");
		register(BetterStorage.lock, "lock");
		register(BetterStorage.keyring, "keyring");
		register(BetterStorage.cardboardSheet, "cardboardSheet");
		register(BetterStorage.masterKey, "masterKey");
	}
	
	public static void register(Block block, String name, Class<? extends ItemBlock> itemBlockClass) {
		if (block == null) return;
		block.setUnlocalizedName(Constants.modName + "." + name);
		GameRegistry.registerBlock(block, itemBlockClass, name, Constants.modId);
		block.setCreativeTab(BetterStorage.creativeTab);
	}
	public static void registerHacky(Block block, String name, Class<? extends Item> itemClass) {
		if (block == null) return;
		block.setUnlocalizedName(Constants.modName + "." + name);
		GameRegistry.registerBlock(block, ItemBlock.class, name, Constants.modId);
		Item.itemsList[block.blockID] = null;
		try {
			Item item = itemClass.getConstructor(int.class).newInstance(block.blockID);
			item.setUnlocalizedName(Constants.modName + "." + name);
			item.setCreativeTab(BetterStorage.creativeTab);
		} catch (Exception e) { throw new RuntimeException(e); }
	}
	public static void register(Block block, String name) {
		register(block, name, ItemBlock.class);
	}
	
	public static void register(Item item, String name) {
		if (item == null) return;
		item.setUnlocalizedName(Constants.modName + "." + name);
		item.setCreativeTab(BetterStorage.creativeTab);
	}
	
}
