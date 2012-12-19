package net.mcft.copy.betterstorage;

import net.mcft.copy.betterstorage.items.ItemKey;
import net.mcft.copy.betterstorage.items.ItemLock;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.IInventory;
import net.minecraft.src.ItemStack;
import cpw.mods.fml.common.ICraftingHandler;

/** Handles key and lock crafting. */
public class CraftingHandler implements ICraftingHandler {
	
	@Override
	public void onCrafting(EntityPlayer player, ItemStack item, IInventory craftMatrix) {
		// If item crafted is a key, set its damage value to ..
		if (item.getItem() instanceof ItemKey) {
			int damage;
			// .. another key's damage value, if it was crafted with one.
			ItemStack stack = craftMatrix.getStackInSlot(6);
			if (stack != null && stack.getItem() instanceof ItemKey)
				damage = stack.getItemDamage();
			// .. a random value, if not.
			else damage = -32000 + BetterStorage.random.nextInt(64000);
			item.setItemDamage(damage);
		}
		// If item crafted is a lock, copy the damage value from the key.
		if (item.getItem() instanceof ItemLock)
			item.setItemDamage(craftMatrix.getStackInSlot(4).getItemDamage());
	}
	@Override
	public void onSmelting(EntityPlayer player, ItemStack item) {  }
	
}
