package net.mcft.copy.betterstorage.addon.armourersworkshop;

import net.mcft.copy.betterstorage.api.stand.ArmorStandEquipHandler;
import net.mcft.copy.betterstorage.api.stand.EnumArmorStandRegion;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import riskyken.armourersWorkshop.api.common.skin.type.ISkinType;

public class AWEquipmentHandler extends ArmorStandEquipHandler {

	public final ISkinType type;
	
	public AWEquipmentHandler(ISkinType type, int priority) {
		super("ArmourersWorkshop " + type.getRegistryName(), EnumArmorStandRegion.values()[3 - type.getVanillaArmourSlotId()], priority);
		this.type = type;
	}

	@Override
	public boolean isValidItem(EntityPlayer player, ItemStack item) {
		return AWAddon.dataHandler.isValidEquipmentSkin(item)
			&& AWAddon.dataHandler.getSkinPointerFromStack(item).getSkinType() == type;
	}

	@Override
	public ItemStack getEquipment(EntityPlayer player) {
		return AWAddon.dataHandler.getSkinFormPlayer(player, type);
	}

	@Override
	public boolean canSetEquipment(EntityPlayer player, ItemStack item) {
		return true;
	}

	@Override
	public void setEquipment(EntityPlayer player, ItemStack item) {
		if(item != null) AWAddon.dataHandler.setSkinOnPlayer(player, item);
		else AWAddon.dataHandler.removeSkinFromPlayer(player, type);
	}

}
