package net.mcft.copy.betterstorage.addon.armourersworkshop;

import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import riskyken.armourersWorkshop.api.client.render.IEquipmentRenderHandler;
import riskyken.armourersWorkshop.api.client.render.IEquipmentRenderManager;
import riskyken.armourersWorkshop.api.common.equipment.EnumEquipmentPart;
import riskyken.armourersWorkshop.api.common.equipment.EnumEquipmentType;
import riskyken.armourersWorkshop.api.common.equipment.IEquipmentDataHandler;
import riskyken.armourersWorkshop.api.common.equipment.IEquipmentDataManager;

import com.mojang.authlib.GameProfile;

public class AWDataManager implements IEquipmentDataManager, IEquipmentRenderManager {
	
	@Override
	public void onLoad(IEquipmentDataHandler dataHandler) {
		AWAddon.dataHandler = dataHandler;
	}
	
	@Override
	public void onLoad(IEquipmentRenderHandler renderHandler) {
		AWAddon.renderHandler = renderHandler;
	}
	
	@Override
	public void onRenderEquipment(Entity entity, EnumEquipmentType armourType) {  }
	
	@Override
	public void onRenderEquipmentPart(Entity entity, EnumEquipmentPart armourPart) {  }
	
	@Override
	public void onRenderMannequin(TileEntity TileEntity, GameProfile gameProfile) {  }
	
}
