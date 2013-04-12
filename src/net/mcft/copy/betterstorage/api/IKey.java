package net.mcft.copy.betterstorage.api;

import net.minecraft.item.ItemStack;

public interface IKey {
	
	/** Returns if the key is a normal key, instead of a
	 *  special item which has some key-like features. */
	public boolean isNormalKey();
	
	/** Gets called when a key is used to open a lock and returns if it's successful. <br>
	 *  If useAbility is true, the key will use up an ability, like lockpicking or morphing. */
	public boolean unlock(ItemStack key, ItemStack lock, boolean useAbility);
	
}
