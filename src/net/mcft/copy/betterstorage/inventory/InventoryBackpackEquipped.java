package net.mcft.copy.betterstorage.inventory;

import net.mcft.copy.betterstorage.item.ItemBackpack;
import net.mcft.copy.betterstorage.utils.DirectionUtils;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;

public class InventoryBackpackEquipped extends InventoryWrapper {
	
	public final EntityPlayer player;
	public final EntityLiving carrier;
	
	public InventoryBackpackEquipped(EntityPlayer player, EntityLiving carrier) {
		super(ItemBackpack.getBackpackItems(carrier).contents);
		this.player = player;
		this.carrier = carrier;
	}
	
	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		if (!carrier.isEntityAlive() || player.getDistanceToEntity(carrier) > 1.5) return false;
		double direction = DirectionUtils.angleBetween(carrier, player);
		direction = DirectionUtils.angleDifference(carrier.renderYawOffset + 90.0F, direction);
		return (Math.abs(direction) > 135);
	}
	
}
