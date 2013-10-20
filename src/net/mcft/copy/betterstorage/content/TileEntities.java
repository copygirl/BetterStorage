package net.mcft.copy.betterstorage.content;

import net.mcft.copy.betterstorage.addon.Addon;
import net.mcft.copy.betterstorage.block.crate.TileEntityCrate;
import net.mcft.copy.betterstorage.block.tileentity.TileEntityArmorStand;
import net.mcft.copy.betterstorage.block.tileentity.TileEntityBackpack;
import net.mcft.copy.betterstorage.block.tileentity.TileEntityCardboardBox;
import net.mcft.copy.betterstorage.block.tileentity.TileEntityLocker;
import net.mcft.copy.betterstorage.block.tileentity.TileEntityReinforcedChest;
import net.mcft.copy.betterstorage.block.tileentity.TileEntityReinforcedLocker;
import net.mcft.copy.betterstorage.misc.Constants;
import cpw.mods.fml.common.registry.GameRegistry;

public final class TileEntities {
	
	private TileEntities() {  }
	
	public static void register() {
		
		GameRegistry.registerTileEntity(TileEntityCrate.class, Constants.containerCrate);
		GameRegistry.registerTileEntity(TileEntityReinforcedChest.class, Constants.containerReinforcedChest);
		GameRegistry.registerTileEntity(TileEntityLocker.class, Constants.containerLocker);
		GameRegistry.registerTileEntity(TileEntityArmorStand.class, Constants.containerArmorStand);
		GameRegistry.registerTileEntity(TileEntityBackpack.class, Constants.containerBackpack);
		GameRegistry.registerTileEntity(TileEntityCardboardBox.class, Constants.containerCardboardBox);
		GameRegistry.registerTileEntity(TileEntityReinforcedLocker.class, Constants.containerReinforcedLocker);
		
		Addon.registerTileEntitesAll();
		
	}
	
}
