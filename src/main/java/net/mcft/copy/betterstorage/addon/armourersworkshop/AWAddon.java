package net.mcft.copy.betterstorage.addon.armourersworkshop;

import net.mcft.copy.betterstorage.addon.Addon;
import net.mcft.copy.betterstorage.api.stand.BetterStorageArmorStand;
import net.mcft.copy.betterstorage.api.stand.ClientArmorStandPlayer;
import net.mcft.copy.betterstorage.api.stand.IArmorStand;
import net.mcft.copy.betterstorage.api.stand.IArmorStandRenderHandler;
import net.minecraft.tileentity.TileEntity;

import org.lwjgl.opengl.GL11;

import riskyken.armourersWorkshop.api.client.render.IEquipmentRenderHandler;
import riskyken.armourersWorkshop.api.common.equipment.EnumEquipmentType;
import riskyken.armourersWorkshop.api.common.equipment.IEquipmentDataHandler;
import cpw.mods.fml.common.event.FMLInterModComms;

public class AWAddon extends Addon implements IArmorStandRenderHandler {
	
	public static IEquipmentDataHandler dataHandler;
	public static IEquipmentRenderHandler renderHandler;
	
	public static AWEquipmentHandler eqHandlerHead;
	public static AWEquipmentHandler eqHandlerChest;
	public static AWEquipmentHandler eqHandlerSkirt;
	public static AWEquipmentHandler eqHandlerLegs;
	public static AWEquipmentHandler eqHandlerFeet;
	
	public AWAddon() {
		super("armourersWorkshop");
	}
	
	@Override
	public void setupConfig() {
		FMLInterModComms.sendMessage("armourersWorkshop", "register", "net.mcft.copy.betterstorage.addon.armourersworkshop.AWDataManager");
		
		eqHandlerHead  = new AWEquipmentHandler(EnumEquipmentType.HEAD, 1);
		eqHandlerChest = new AWEquipmentHandler(EnumEquipmentType.CHEST, 1);
		eqHandlerSkirt = new AWEquipmentHandler(EnumEquipmentType.SKIRT, 2);
		eqHandlerLegs  = new AWEquipmentHandler(EnumEquipmentType.LEGS, 1);
		eqHandlerFeet  = new AWEquipmentHandler(EnumEquipmentType.FEET, 1);
		
		BetterStorageArmorStand.registerEquipHandler(eqHandlerHead);
		BetterStorageArmorStand.registerEquipHandler(eqHandlerChest);
		BetterStorageArmorStand.registerEquipHandler(eqHandlerSkirt);
		BetterStorageArmorStand.registerEquipHandler(eqHandlerLegs);
		BetterStorageArmorStand.registerEquipHandler(eqHandlerFeet);
		
		BetterStorageArmorStand.registerRenderHandler(this);
	}

	@Override
	public <T extends TileEntity & IArmorStand> void onPreRender(T armorStand, ClientArmorStandPlayer player) { 
		
	}

	@Override
	public <T extends TileEntity & IArmorStand> void onPostRender(T armorStand, ClientArmorStandPlayer player) {
		GL11.glTranslatef(0, 3 / 16F, 0);
		if(armorStand.getItem(eqHandlerHead) != null) 
			renderHandler.renderCustomEquipmentFromStack(armorStand.getItem(eqHandlerHead), 0, 0, 0, 0, 0);
		if(armorStand.getItem(eqHandlerChest) != null)
			renderHandler.renderCustomEquipmentFromStack(armorStand.getItem(eqHandlerChest), 0, 0, 0, 0, 0);
		if(armorStand.getItem(eqHandlerSkirt) != null)
			renderHandler.renderCustomEquipmentFromStack(armorStand.getItem(eqHandlerSkirt), 0, 0, 0, 0, 0);
		if(armorStand.getItem(eqHandlerLegs) != null)
			renderHandler.renderCustomEquipmentFromStack(armorStand.getItem(eqHandlerLegs), 0, 0, 0, 0, 0);
		if(armorStand.getItem(eqHandlerFeet) != null)
			renderHandler.renderCustomEquipmentFromStack(armorStand.getItem(eqHandlerFeet), 0, 0, 0, 0, 0);
		GL11.glTranslatef(0, -3 / 16F, 0);
	}
	
}
