package net.mcft.copy.betterstorage;

import java.util.logging.Logger;

import net.mcft.copy.betterstorage.addon.Addon;
import net.mcft.copy.betterstorage.content.Blocks;
import net.mcft.copy.betterstorage.content.Entities;
import net.mcft.copy.betterstorage.content.Items;
import net.mcft.copy.betterstorage.content.TileEntities;
import net.mcft.copy.betterstorage.item.EnchantmentBetterStorage;
import net.mcft.copy.betterstorage.misc.Constants;
import net.mcft.copy.betterstorage.misc.CreativeTabBetterStorage;
import net.mcft.copy.betterstorage.misc.DungeonLoot;
import net.mcft.copy.betterstorage.misc.Recipes;
import net.mcft.copy.betterstorage.misc.handlers.CraftingHandler;
import net.mcft.copy.betterstorage.misc.handlers.PacketHandler;
import net.mcft.copy.betterstorage.proxy.CommonProxy;
import net.minecraft.creativetab.CreativeTabs;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.registry.GameRegistry;

@Mod(modid = Constants.modId,
     name = Constants.modName,
     version = "@VERSION@",
     useMetadata = true,
     dependencies = "after:Thaumcraft")
@NetworkMod(clientSideRequired = true,
            serverSideRequired = false,
            channels = { Constants.modId },
            packetHandler = PacketHandler.class)
public class BetterStorage {
	
	@Instance(Constants.modId)
	public static BetterStorage instance;
	
	@SidedProxy(serverSide = Constants.commonProxy,
	            clientSide = Constants.clientProxy)
	public static CommonProxy proxy;
	
	public static Logger log;
	
	public static CreativeTabs creativeTab;
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		
		log = event.getModLog();
		creativeTab = new CreativeTabBetterStorage();
		
		Addon.initialize();
		
		Config.load(event.getSuggestedConfigurationFile());
		
		BetterStorage.log.info(Constants.modName + " will overwrite some of its own items. Don't worry, this is normal.");
		Blocks.initialize();
		Items.initialize();
		
		EnchantmentBetterStorage.initialize();
		
		TileEntities.register();
		Entities.register();
		DungeonLoot.add();
		
	}
	
	@EventHandler
	public void load(FMLInitializationEvent event) {
		
		Recipes.add();
		proxy.initialize();
		GameRegistry.registerCraftingHandler(new CraftingHandler());
		
	}
	
	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		Addon.postInitializeAll();
	}
	
}
