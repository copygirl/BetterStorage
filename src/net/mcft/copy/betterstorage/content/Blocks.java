package net.mcft.copy.betterstorage.content;

import net.mcft.copy.betterstorage.Config;
import net.mcft.copy.betterstorage.addon.Addon;
import net.mcft.copy.betterstorage.block.BlockArmorStand;
import net.mcft.copy.betterstorage.block.BlockBackpack;
import net.mcft.copy.betterstorage.block.BlockCardboardBox;
import net.mcft.copy.betterstorage.block.BlockEnderBackpack;
import net.mcft.copy.betterstorage.block.BlockLocker;
import net.mcft.copy.betterstorage.block.BlockReinforcedChest;
import net.mcft.copy.betterstorage.block.crate.BlockCrate;
import net.mcft.copy.betterstorage.utils.MiscUtils;

public final class Blocks {
	
	public static BlockCrate crate;
	public static BlockReinforcedChest reinforcedChest;
	public static BlockLocker locker;
	public static BlockArmorStand armorStand;
	public static BlockBackpack backpack;
	public static BlockEnderBackpack enderBackpack;
	public static BlockCardboardBox cardboardBox;
	
	private Blocks() {  }
	
	public static void initialize() {
		
		crate           = MiscUtils.conditionalNew(BlockCrate.class, Config.crateId);
		reinforcedChest = MiscUtils.conditionalNew(BlockReinforcedChest.class, Config.chestId);
		locker          = MiscUtils.conditionalNew(BlockLocker.class, Config.lockerId);
		armorStand      = MiscUtils.conditionalNew(BlockArmorStand.class, Config.armorStandId);
		backpack        = MiscUtils.conditionalNew(BlockBackpack.class, Config.backpackId);
		enderBackpack   = MiscUtils.conditionalNew(BlockEnderBackpack.class, Config.enderBackpackId);
		cardboardBox    = MiscUtils.conditionalNew(BlockCardboardBox.class, Config.cardboardBoxId);
		
		Addon.initializeBlocksAll();
		
	}
	
}
