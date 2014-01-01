package net.mcft.copy.betterstorage.content;

import net.mcft.copy.betterstorage.addon.Addon;
import net.mcft.copy.betterstorage.block.BlockArmorStand;
import net.mcft.copy.betterstorage.block.BlockBackpack;
import net.mcft.copy.betterstorage.block.BlockCardboardBox;
import net.mcft.copy.betterstorage.block.BlockCraftingStation;
import net.mcft.copy.betterstorage.block.BlockEnderBackpack;
import net.mcft.copy.betterstorage.block.BlockLocker;
import net.mcft.copy.betterstorage.block.BlockReinforcedChest;
import net.mcft.copy.betterstorage.block.BlockReinforcedLocker;
import net.mcft.copy.betterstorage.block.crate.BlockCrate;
import net.mcft.copy.betterstorage.config.GlobalConfig;
import net.mcft.copy.betterstorage.utils.MiscUtils;

public final class Blocks {
	
	public static BlockCrate crate;
	public static BlockReinforcedChest reinforcedChest;
	public static BlockLocker locker;
	public static BlockArmorStand armorStand;
	public static BlockBackpack backpack;
	public static BlockEnderBackpack enderBackpack;
	public static BlockCardboardBox cardboardBox;
	public static BlockReinforcedLocker reinforcedLocker;
	public static BlockCraftingStation craftingStation;
	
	private Blocks() {  }
	
	public static void initialize() {
		
		crate            = MiscUtils.conditionalNew(BlockCrate.class, GlobalConfig.crateId);
		reinforcedChest  = MiscUtils.conditionalNew(BlockReinforcedChest.class, GlobalConfig.reinforcedChestId);
		locker           = MiscUtils.conditionalNew(BlockLocker.class, GlobalConfig.lockerId);
		armorStand       = MiscUtils.conditionalNew(BlockArmorStand.class, GlobalConfig.armorStandId);
		backpack         = MiscUtils.conditionalNew(BlockBackpack.class, GlobalConfig.backpackId);
		enderBackpack    = MiscUtils.conditionalNew(BlockEnderBackpack.class, GlobalConfig.enderBackpackId);
		cardboardBox     = MiscUtils.conditionalNew(BlockCardboardBox.class, GlobalConfig.cardboardBoxId);
		reinforcedLocker = MiscUtils.conditionalNew(BlockReinforcedLocker.class, GlobalConfig.reinforcedLockerId);
		craftingStation  = MiscUtils.conditionalNew(BlockCraftingStation.class, GlobalConfig.craftingStationId);
		
		Addon.initializeBlocksAll();
		
	}
	
}
