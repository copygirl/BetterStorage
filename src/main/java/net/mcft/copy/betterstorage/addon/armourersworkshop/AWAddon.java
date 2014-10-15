package net.mcft.copy.betterstorage.addon.armourersworkshop;

import net.mcft.copy.betterstorage.addon.Addon;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import riskyken.armourersWorkshop.api.client.render.IEquipmentRenderHandler;
import riskyken.armourersWorkshop.api.common.equipment.IEntityEquipment;
import riskyken.armourersWorkshop.api.common.equipment.IEquipmentDataHandler;
import riskyken.armourersWorkshop.api.common.equipment.armour.EnumEquipmentType;
import cpw.mods.fml.common.event.FMLInterModComms;

public class AWAddon extends Addon {
	
	public static boolean isLoaded;
	public static IEquipmentDataHandler dataHandler;
	public static IEquipmentRenderHandler renderHandler;
	
	public AWAddon() {
		super("armourersWorkshop");
		isLoaded = true;
	}
	
	@Override
	public void setupConfig() {
		FMLInterModComms.sendMessage("armourersWorkshop", "register", "net.mcft.copy.betterstorage.addon.armourersworkshop.AWDataManager");
	}

	public static Object getArmorInSlot(EntityPlayer player, int slot) {
		if (isLoaded) {
			IEntityEquipment equipment = dataHandler.getCustomEquipmentForEntity(player);
			switch (3 - slot) {
			case 0:
				if(equipment.haveEquipment(EnumEquipmentType.HEAD))
					return equipment.getEquipmentId(EnumEquipmentType.HEAD);
				break;
			case 1:
				if(equipment.haveEquipment(EnumEquipmentType.CHEST))
					return equipment.getEquipmentId(EnumEquipmentType.CHEST);
				break;
			case 2:
				if(equipment.haveEquipment(EnumEquipmentType.SKIRT))
					return equipment.getEquipmentId(EnumEquipmentType.SKIRT);
				else if(equipment.haveEquipment(EnumEquipmentType.LEGS))
					return equipment.getEquipmentId(EnumEquipmentType.LEGS);
				break;
			case 3:
				if(equipment.haveEquipment(EnumEquipmentType.FEET))
					return equipment.getEquipmentId(EnumEquipmentType.FEET);
				break;
			}
		}
		return player.inventory.armorInventory[slot];
	}
	
	public static boolean validateArmor(EntityPlayer player, Object armor, int slot) {
		if (armor instanceof ItemStack) {
			ItemStack stack = (ItemStack) armor;
			if (isLoaded) {
			    
			    EnumEquipmentType type = dataHandler.getEquipmentTypeFromStack(stack);
			    
			    if (type != EnumEquipmentType.NONE) {
			        return type.getSlotId() == 3 - slot; 
			    }

			}
			return stack.getItem().isValidArmor((ItemStack) armor, 3 - slot, player);
		}
		if (isLoaded) {
			EnumEquipmentType type = dataHandler.getEquipmentType((Integer) armor);
			return type.getSlotId() == 3 - slot;
		}
		return false;
	}
}
