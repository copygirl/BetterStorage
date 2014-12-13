package net.mcft.copy.betterstorage.content;

import net.mcft.copy.betterstorage.addon.Addon;
import net.mcft.copy.betterstorage.misc.Constants;
import net.mcft.copy.betterstorage.tile.crate.TileEntityCrate;
import net.mcft.copy.betterstorage.tile.entity.TileEntityBackpack;
import net.mcft.copy.betterstorage.tile.entity.TileEntityCardboardBox;
import net.mcft.copy.betterstorage.tile.entity.TileEntityCraftingStation;
import net.mcft.copy.betterstorage.tile.entity.TileEntityLockableDoor;
import net.mcft.copy.betterstorage.tile.entity.TileEntityLocker;
import net.mcft.copy.betterstorage.tile.entity.TileEntityPresent;
import net.mcft.copy.betterstorage.tile.entity.TileEntityReinforcedChest;
import net.mcft.copy.betterstorage.tile.entity.TileEntityReinforcedLocker;
import net.mcft.copy.betterstorage.tile.stand.TileEntityArmorStand;
import net.minecraftforge.fml.common.registry.GameRegistry;

public final class BetterStorageTileEntities {
	
	private BetterStorageTileEntities() {  }
	
	public static void register() {
		
		GameRegistry.registerTileEntity(TileEntityCrate.class, Constants.containerCrate);
		GameRegistry.registerTileEntity(TileEntityReinforcedChest.class, Constants.containerReinforcedChest);
		GameRegistry.registerTileEntity(TileEntityLocker.class, Constants.containerLocker);
		GameRegistry.registerTileEntity(TileEntityArmorStand.class, Constants.containerArmorStand);
		GameRegistry.registerTileEntity(TileEntityBackpack.class, Constants.containerBackpack);
		GameRegistry.registerTileEntity(TileEntityCardboardBox.class, Constants.containerCardboardBox);
		GameRegistry.registerTileEntity(TileEntityReinforcedLocker.class, Constants.containerReinforcedLocker);
		GameRegistry.registerTileEntity(TileEntityCraftingStation.class, Constants.containerCraftingStation);
		GameRegistry.registerTileEntity(TileEntityPresent.class, Constants.containerPresent);
		GameRegistry.registerTileEntity(TileEntityLockableDoor.class, Constants.lockableDoor);

		Addon.registerTileEntitesAll();
		
	}
	
}
