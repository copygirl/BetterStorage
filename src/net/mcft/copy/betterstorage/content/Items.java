package net.mcft.copy.betterstorage.content;

import net.mcft.copy.betterstorage.BetterStorage;
import net.mcft.copy.betterstorage.addon.Addon;
import net.mcft.copy.betterstorage.config.GlobalConfig;
import net.mcft.copy.betterstorage.item.ItemBucketSlime;
import net.mcft.copy.betterstorage.item.ItemDrinkingHelmet;
import net.mcft.copy.betterstorage.item.cardboard.ItemCardboardArmor;
import net.mcft.copy.betterstorage.item.cardboard.ItemCardboardAxe;
import net.mcft.copy.betterstorage.item.cardboard.ItemCardboardHoe;
import net.mcft.copy.betterstorage.item.cardboard.ItemCardboardPickaxe;
import net.mcft.copy.betterstorage.item.cardboard.ItemCardboardSheet;
import net.mcft.copy.betterstorage.item.cardboard.ItemCardboardShovel;
import net.mcft.copy.betterstorage.item.cardboard.ItemCardboardSword;
import net.mcft.copy.betterstorage.item.locking.ItemKey;
import net.mcft.copy.betterstorage.item.locking.ItemKeyring;
import net.mcft.copy.betterstorage.item.locking.ItemLock;
import net.mcft.copy.betterstorage.item.locking.ItemMasterKey;
import net.mcft.copy.betterstorage.utils.MiscUtils;
import net.minecraftforge.oredict.OreDictionary;

public final class Items {
	
	public static ItemKey key;
	public static ItemLock lock;
	public static ItemKeyring keyring;
	public static ItemCardboardSheet cardboardSheet;
	public static ItemMasterKey masterKey;
	public static ItemDrinkingHelmet drinkingHelmet;
	public static ItemBucketSlime slimeBucket;
	
	public static ItemCardboardArmor cardboardHelmet;
	public static ItemCardboardArmor cardboardChestplate;
	public static ItemCardboardArmor cardboardLeggings;
	public static ItemCardboardArmor cardboardBoots;
	
	public static ItemCardboardSword cardboardSword;
	public static ItemCardboardPickaxe cardboardPickaxe;
	public static ItemCardboardShovel cardboardShovel;
	public static ItemCardboardAxe cardboardAxe;
	public static ItemCardboardHoe cardboardHoe;
	
	private Items() {  }
	
	public static void initialize() {
		
		key            = MiscUtils.conditionalNew(ItemKey.class, GlobalConfig.keyId);
		lock           = MiscUtils.conditionalNew(ItemLock.class, GlobalConfig.lockId);
		keyring        = MiscUtils.conditionalNew(ItemKeyring.class, GlobalConfig.keyringId);
		cardboardSheet = MiscUtils.conditionalNew(ItemCardboardSheet.class, GlobalConfig.cardboardSheetId);
		masterKey      = MiscUtils.conditionalNew(ItemMasterKey.class, GlobalConfig.masterKeyId);
		drinkingHelmet = MiscUtils.conditionalNew(ItemDrinkingHelmet.class, GlobalConfig.drinkingHelmetId);
		slimeBucket    = MiscUtils.conditionalNew(ItemBucketSlime.class, GlobalConfig.slimeBucketId);
		
		cardboardHelmet     = conditionalNewArmor(GlobalConfig.cardboardHelmetId, 0);
		cardboardChestplate = conditionalNewArmor(GlobalConfig.cardboardChestplateId, 1);
		cardboardLeggings   = conditionalNewArmor(GlobalConfig.cardboardLeggingsId, 2);
		cardboardBoots      = conditionalNewArmor(GlobalConfig.cardboardBootsId, 3);
		
		cardboardSword = MiscUtils.conditionalNew(ItemCardboardSword.class, GlobalConfig.cardboardSwordId);
		cardboardPickaxe = MiscUtils.conditionalNew(ItemCardboardPickaxe.class, GlobalConfig.cardboardPickaxeId);
		cardboardShovel = MiscUtils.conditionalNew(ItemCardboardShovel.class, GlobalConfig.cardboardShovelId);
		cardboardAxe = MiscUtils.conditionalNew(ItemCardboardAxe.class, GlobalConfig.cardboardAxeId);
		cardboardHoe = MiscUtils.conditionalNew(ItemCardboardHoe.class, GlobalConfig.cardboardHoeId);
		
		OreDictionary.registerOre("sheetCardboard", cardboardSheet);
		
		Addon.initializeItemsAll();
		
	}
	private static ItemCardboardArmor conditionalNewArmor(String configName, int armorType) {
		int id = BetterStorage.globalConfig.getInteger(configName);
		if (!MiscUtils.isEnabled(id)) return null;
		return new ItemCardboardArmor(id, armorType);
	}
	
}
