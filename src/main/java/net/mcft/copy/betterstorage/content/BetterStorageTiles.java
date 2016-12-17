package net.mcft.copy.betterstorage.content;

import net.mcft.copy.betterstorage.addon.Addon;
import net.mcft.copy.betterstorage.config.GlobalConfig;
import net.mcft.copy.betterstorage.tile.TileBackpack;
import net.mcft.copy.betterstorage.tile.TileCardboardBackpack;
import net.mcft.copy.betterstorage.tile.TileCardboardBox;
import net.mcft.copy.betterstorage.tile.TileCraftingStation;
import net.mcft.copy.betterstorage.tile.TileEnderBackpack;
import net.mcft.copy.betterstorage.tile.TileFlintBlock;
import net.mcft.copy.betterstorage.tile.TileLockableDoor;
import net.mcft.copy.betterstorage.tile.TileLocker;
import net.mcft.copy.betterstorage.tile.TilePresent;
import net.mcft.copy.betterstorage.tile.TileReinforcedChest;
import net.mcft.copy.betterstorage.tile.TileReinforcedLocker;
import net.mcft.copy.betterstorage.tile.crate.TileCrate;
import net.mcft.copy.betterstorage.tile.stand.TileArmorStand;
import net.mcft.copy.betterstorage.utils.MiscUtils;

public final class BetterStorageTiles {
	
	public static TileCrate crate;
	public static TileReinforcedChest reinforcedChest;
	public static TileLocker locker;
	public static TileArmorStand armorStand;
	public static TileBackpack backpack;
	public static TileEnderBackpack enderBackpack;
	public static TileCardboardBackpack cardboardBackpack;
	public static TileCardboardBox cardboardBox;
	public static TileReinforcedLocker reinforcedLocker;
	public static TileCraftingStation craftingStation;
	public static TileFlintBlock flintBlock;
	public static TileLockableDoor lockableDoor;
	public static TilePresent present;
	
	private BetterStorageTiles() {  }
	
	public static void initialize() {
		
		crate             = MiscUtils.conditionalNew(TileCrate.class, GlobalConfig.crateEnabled);
		reinforcedChest   = MiscUtils.conditionalNew(TileReinforcedChest.class, GlobalConfig.reinforcedChestEnabled);
		locker            = MiscUtils.conditionalNew(TileLocker.class, GlobalConfig.lockerEnabled);
		armorStand        = MiscUtils.conditionalNew(TileArmorStand.class, GlobalConfig.armorStandEnabled);
		backpack          = MiscUtils.conditionalNew(TileBackpack.class, GlobalConfig.backpackEnabled);
		enderBackpack     = MiscUtils.conditionalNew(TileEnderBackpack.class, GlobalConfig.enderBackpackEnabled);
		cardboardBackpack = MiscUtils.conditionalNew(TileCardboardBackpack.class, GlobalConfig.cardboardBackpackEnabled);
		cardboardBox      = MiscUtils.conditionalNew(TileCardboardBox.class, GlobalConfig.cardboardBoxEnabled);
		reinforcedLocker  = MiscUtils.conditionalNew(TileReinforcedLocker.class, GlobalConfig.reinforcedLockerEnabled);
		craftingStation   = MiscUtils.conditionalNew(TileCraftingStation.class, GlobalConfig.craftingStationEnabled);
		flintBlock        = MiscUtils.conditionalNew(TileFlintBlock.class, GlobalConfig.flintBlockEnabled);
		lockableDoor      = MiscUtils.conditionalNew(TileLockableDoor.class, GlobalConfig.lockableDoorEnabled);
		present           = MiscUtils.conditionalNew(TilePresent.class, GlobalConfig.presentEnabled);
		
		Addon.initializeTilesAll();
		Addon.addRecipesAll();
		
	}
	
}
