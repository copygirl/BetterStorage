package net.mcft.copy.betterstorage.api.crafting;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.world.World;

public interface ICraftingSource {
	
	/** Returns the player currently crafting, null if none. */
	EntityPlayer getPlayer();
	
	/** Returns the inventory of the object that's crafting the recipe. <br>
	 *  May return null if unsupported. Always returns null on client-side. */
	IInventory getInventory();
	
	/** Returns the world the recipe is being crafted in. <br>
	 *  Returns null on client-side. */
	World getWorld();
	
	double getX();
	double getY();
	double getZ();
	
}
