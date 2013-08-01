package net.mcft.copy.betterstorage.content;

import net.mcft.copy.betterstorage.Config;
import net.mcft.copy.betterstorage.addon.Addon;
import net.mcft.copy.betterstorage.item.ItemCardboardSheet;
import net.mcft.copy.betterstorage.item.ItemKey;
import net.mcft.copy.betterstorage.item.ItemKeyring;
import net.mcft.copy.betterstorage.item.ItemLock;
import net.mcft.copy.betterstorage.item.ItemMasterKey;
import net.mcft.copy.betterstorage.utils.MiscUtils;

public final class Items {
	
	public static ItemKey key;
	public static ItemLock lock;
	public static ItemKeyring keyring;
	public static ItemCardboardSheet cardboardSheet;
	public static ItemMasterKey masterKey;
	
	private Items() {  }
	
	public static void initialize() {
		
		key            = MiscUtils.conditionalNew(ItemKey.class, Config.keyId);
		lock           = MiscUtils.conditionalNew(ItemLock.class, Config.lockId);
		keyring        = MiscUtils.conditionalNew(ItemKeyring.class, Config.keyringId);
		cardboardSheet = MiscUtils.conditionalNew(ItemCardboardSheet.class, Config.cardboardSheetId);
		masterKey      = MiscUtils.conditionalNew(ItemMasterKey.class, Config.masterKeyId);
		
		Addon.initializeItemsAll();
		
	}
	
}
