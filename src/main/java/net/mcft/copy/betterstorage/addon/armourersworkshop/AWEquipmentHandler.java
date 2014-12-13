package net.mcft.copy.betterstorage.addon.armourersworkshop;


public class AWEquipmentHandler /*extends ArmorStandEquipHandler*/ {

	/*public final EnumEquipmentType type;
	
	public AWEquipmentHandler(EnumEquipmentType type, int priority) {
		super("ArmourersWorkshop " + priority, EnumArmorStandRegion.values()[3 - type.getVanillaSlotId()], priority);
		this.type = type;
	}

	@Override
	public boolean isValidItem(EntityPlayer player, ItemStack item) {
		return AWAddon.dataHandler.hasItemStackGotEquipmentData(item)
			&& AWAddon.dataHandler.getEquipmentTypeFromStack(item) == type;
	}

	@Override
	public ItemStack getEquipment(EntityPlayer player) {
		return AWAddon.dataHandler.getCustomEquipmentForPlayer(player, type);
	}

	@Override
	public boolean canSetEquipment(EntityPlayer player, ItemStack item) {
		return true;
	}

	@Override
	public void setEquipment(EntityPlayer player, ItemStack item) {
		if(item != null) AWAddon.dataHandler.setCustomEquipmentOnPlayer(player, item);
		else AWAddon.dataHandler.clearCustomEquipmentFromPlayer(player, type);
	}
	*/
}
