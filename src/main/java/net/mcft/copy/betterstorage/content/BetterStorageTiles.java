package net.mcft.copy.betterstorage.content;

import net.mcft.copy.betterstorage.addon.Addon;
import net.mcft.copy.betterstorage.config.GlobalConfig;
import net.mcft.copy.betterstorage.tile.TileArmorStand;
import net.mcft.copy.betterstorage.tile.TileBackpack;
import net.mcft.copy.betterstorage.tile.TileCardboardBox;
import net.mcft.copy.betterstorage.tile.TileCraftingStation;
import net.mcft.copy.betterstorage.tile.TileEnderBackpack;
import net.mcft.copy.betterstorage.tile.TileFlintBlock;
import net.mcft.copy.betterstorage.tile.TileLocker;
import net.mcft.copy.betterstorage.tile.TileReinforcedChest;
import net.mcft.copy.betterstorage.tile.TileReinforcedLocker;
import net.mcft.copy.betterstorage.tile.crate.TileCrate;
import net.mcft.copy.betterstorage.utils.MiscUtils;

public final class BetterStorageTiles {
	
	public static TileCrate crate;
	public static TileReinforcedChest reinforcedChest;
	public static TileLocker locker;
	public static TileArmorStand armorStand;
	public static TileBackpack backpack;
	public static TileEnderBackpack enderBackpack;
	public static TileCardboardBox cardboardBox;
	public static TileReinforcedLocker reinforcedLocker;
	public static TileCraftingStation craftingStation;
	public static TileFlintBlock flintBlock;
	
	private BetterStorageTiles() {  }
	
	public static void initialize() {
		
		crate            = MiscUtils.conditionalNew(TileCrate.class, GlobalConfig.crateId);
		reinforcedChest  = MiscUtils.conditionalNew(TileReinforcedChest.class, GlobalConfig.reinforcedChestId);
		locker           = MiscUtils.conditionalNew(TileLocker.class, GlobalConfig.lockerId);
		armorStand       = MiscUtils.conditionalNew(TileArmorStand.class, GlobalConfig.armorStandId);
		backpack         = MiscUtils.conditionalNew(TileBackpack.class, GlobalConfig.backpackId);
		enderBackpack    = MiscUtils.conditionalNew(TileEnderBackpack.class, GlobalConfig.enderBackpackId);
		cardboardBox     = MiscUtils.conditionalNew(TileCardboardBox.class, GlobalConfig.cardboardBoxId);
		reinforcedLocker = MiscUtils.conditionalNew(TileReinforcedLocker.class, GlobalConfig.reinforcedLockerId);
		craftingStation  = MiscUtils.conditionalNew(TileCraftingStation.class, GlobalConfig.craftingStationId);
		flintBlock       = MiscUtils.conditionalNew(TileFlintBlock.class, GlobalConfig.flintBlockId);
		
		Addon.initializeTilesAll();
		
	}
	
}
