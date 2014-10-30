package net.mcft.copy.betterstorage.tile.stand;

import net.mcft.copy.betterstorage.api.stand.ArmorStandEquipHandler;
import net.mcft.copy.betterstorage.api.stand.EnumArmorStandRegion;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.S2FPacketSetSlot;

public class VanillaArmorStandEquipHandler extends ArmorStandEquipHandler {
	
	public static final String ID = "Vanilla";
	public static final int PRIORITY = 0;
	
	
	public final int armorType;
	
	public VanillaArmorStandEquipHandler(EnumArmorStandRegion region) {
		super(ID, region, PRIORITY);
		armorType = (3 - region.ordinal());
	}
	
	
	@Override
	public boolean isValidItem(EntityPlayer player, ItemStack item) {
		return item.getItem().isValidArmor(item, armorType, player);
	}
	
	
	@Override
	public ItemStack getEquipment(EntityPlayer player) {
		return player.getCurrentArmor(region.ordinal());
	}
	
	@Override
	public boolean canSetEquipment(EntityPlayer player, ItemStack item) {
		return true;
	}
	
	@Override
	public void setEquipment(EntityPlayer player, ItemStack item) {
		player.setCurrentItemOrArmor(region.ordinal() + 1, item);
		// Shouldn't this be done automatically?
		((EntityPlayerMP)player).playerNetServerHandler.sendPacket(
				new S2FPacketSetSlot(player.openContainer.windowId, 8 - region.ordinal(), item));
	}
	
}
