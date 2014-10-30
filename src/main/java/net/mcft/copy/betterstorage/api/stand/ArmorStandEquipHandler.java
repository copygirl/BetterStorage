package net.mcft.copy.betterstorage.api.stand;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public abstract class ArmorStandEquipHandler implements Comparable<ArmorStandEquipHandler> {
	
	public final String id;
	public final EnumArmorStandRegion region;
	public final int priority;
	
	public ArmorStandEquipHandler(String id, EnumArmorStandRegion region, int priority) {
		this.id = id;
		this.region = region;
		this.priority = priority;
	}
	
	
	/** Returns if the item (non-null) is valid and can
	 *  be put on the armor stand and equipped by players. */
	public abstract boolean isValidItem(EntityPlayer player, ItemStack item);
	
	
	/** Returns the player's equipment for this handler, null if none. */
	public abstract ItemStack getEquipment(EntityPlayer player);
	
	/** Returns if the player's equipment can be changed to this item (can be null). */
	public abstract boolean canSetEquipment(EntityPlayer player, ItemStack item);
	
	/** Sets the player's equipment to this item (can be null). */
	public abstract void setEquipment(EntityPlayer player, ItemStack item);
	
	
	@Override
	public int compareTo(ArmorStandEquipHandler other) {
		return (other.priority - priority);
	}
	
}
