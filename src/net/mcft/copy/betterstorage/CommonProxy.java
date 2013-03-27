package net.mcft.copy.betterstorage;

import net.mcft.copy.betterstorage.block.TileEntityArmorStand;
import net.mcft.copy.betterstorage.block.TileEntityLocker;
import net.mcft.copy.betterstorage.block.TileEntityReinforcedChest;
import net.mcft.copy.betterstorage.block.crate.CratePileCollection;
import net.mcft.copy.betterstorage.block.crate.TileEntityCrate;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.world.WorldEvent.Save;
import net.minecraftforge.event.world.WorldEvent.Unload;
import cpw.mods.fml.common.registry.GameRegistry;

public class CommonProxy {
	
	public void init() {
		MinecraftForge.EVENT_BUS.register(this);
		registerTileEntites();
	}
	
	public void registerTileEntites() {
		GameRegistry.registerTileEntity(TileEntityCrate.class, "container.crate");
		GameRegistry.registerTileEntity(TileEntityReinforcedChest.class, "container.reinforcedChest");
		GameRegistry.registerTileEntity(TileEntityLocker.class, "container.locker");
		GameRegistry.registerTileEntity(TileEntityArmorStand.class, "container.armorStand");
	}
	
	@ForgeSubscribe
	public void onWorldSave(Save event) {
		CratePileCollection.saveAll(event.world);
	}
	
	@ForgeSubscribe
	public void onWorldUnload(Unload event) {
		CratePileCollection.unload(event.world);
	}
	
}
