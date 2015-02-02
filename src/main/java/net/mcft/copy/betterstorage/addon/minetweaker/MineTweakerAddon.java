package net.mcft.copy.betterstorage.addon.minetweaker;

import minetweaker.MineTweakerAPI;
import net.mcft.copy.betterstorage.addon.Addon;

public class MineTweakerAddon extends Addon {
	
	public MineTweakerAddon() {
		super("MineTweaker3");
	}
	
	@Override
	public void setupConfig() {
		MineTweakerAPI.registerClass(MTCraftingStation.class);
	}
	
}
