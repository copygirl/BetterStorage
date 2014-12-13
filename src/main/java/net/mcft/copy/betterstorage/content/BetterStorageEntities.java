package net.mcft.copy.betterstorage.content;

import net.mcft.copy.betterstorage.BetterStorage;
import net.mcft.copy.betterstorage.addon.Addon;
import net.mcft.copy.betterstorage.entity.EntityCluckington;
import net.mcft.copy.betterstorage.entity.EntityFrienderman;
import net.minecraftforge.fml.common.registry.EntityRegistry;

public final class BetterStorageEntities {
	
	private BetterStorageEntities() {  }
	
	public static void register() {
		
		EntityRegistry.registerModEntity(EntityFrienderman.class, "Frienderman", 1, BetterStorage.instance, 64, 4, true);
		EntityRegistry.registerModEntity(EntityCluckington.class, "Cluckington", 2, BetterStorage.instance, 64, 4, true);
		
		Addon.registerEntitesAll();
		
	}
	
}
