package net.mcft.copy.betterstorage.addon;

import java.util.ArrayList;
import java.util.List;

import net.mcft.copy.betterstorage.addon.thaumcraft.ThaumcraftAddon;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public abstract class Addon {
	
	private static final List<Addon> addons = new ArrayList<Addon>();
	
	public static void initialize() {
		if (Loader.isModLoaded("Thaumcraft")) new ThaumcraftAddon();
	}
	
	public static void setupConfigsAll() {
		for (Addon addon : addons) addon.setupConfig();
	}
	public static void initializeTilesAll() {
		for (Addon addon : addons) addon.initializeBlocks();
	}
	public static void initializeItemsAll() {
		for (Addon addon : addons) addon.initializeItems();
	}
	public static void addRecipesAll() {
		for (Addon addon : addons) addon.addRecipes();
	}
	public static void registerEntitesAll() {
		for (Addon addon : addons) addon.registerEntities();
	}
	public static void registerTileEntitesAll() {
		for (Addon addon : addons) addon.registerTileEntities();
	}
	@SideOnly(Side.CLIENT)
	public static void registerRenderersAll() {
		for (Addon addon : addons) addon.registerRenderers();
	}
	public static void postInitializeAll() {
		for (Addon addon : addons) addon.postInitialize();
	}
	
	
	public final String modName;
	
	public Addon(String modName) {
		this.modName = modName;
		addons.add(this);
	}
	
	public void setupConfig() {  }
	
	public void initializeItems() {  }
	
	public void initializeBlocks() {  }
	
	public void addRecipes() {  }
	
	public void registerEntities() {  }
	
	public void registerTileEntities() {  }
	
	@SideOnly(Side.CLIENT)
	public void registerRenderers() {  }
	
	public void postInitialize() {  }
	
}
