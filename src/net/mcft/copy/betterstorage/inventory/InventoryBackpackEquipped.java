package net.mcft.copy.betterstorage.inventory;

import net.mcft.copy.betterstorage.item.ItemBackpack;
import net.mcft.copy.betterstorage.utils.DirectionUtils;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;

public class InventoryBackpackEquipped extends InventoryWrapper {
	
	public final EntityPlayer player;
	public final EntityLiving carrier;
	
	public InventoryBackpackEquipped(EntityPlayer player, EntityLiving carrier) {
		super(ItemBackpack.getBackpackData(carrier).contents);
		this.player = player;
		this.carrier = carrier;
	}
	
	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		boolean carriesBackpack = (ItemBackpack.getBackpackData(carrier).contents == allContents[0]); 
		double distance = player.getDistanceToEntity(carrier);
		double direction = DirectionUtils.angleDifference(carrier.renderYawOffset + 90.0F,
		                                                  DirectionUtils.angleBetween(carrier, player));
		return (carrier.isEntityAlive() && carriesBackpack &&
		        (distance < 1.5) && (Math.abs(direction) > 135));
	}
	
	@Override
	public void openChest() {
		int playersUsing = ++ItemBackpack.getBackpackData(carrier).playersUsing;
		ItemBackpack.setBackpackOpen(carrier, (playersUsing > 0));
	}
	@Override
	public void closeChest() {
		int playersUsing = --ItemBackpack.getBackpackData(carrier).playersUsing;
		ItemBackpack.setBackpackOpen(carrier, (playersUsing > 0));
	}
	
}
