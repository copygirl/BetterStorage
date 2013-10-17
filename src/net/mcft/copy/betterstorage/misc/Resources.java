package net.mcft.copy.betterstorage.misc;

import net.minecraft.util.ResourceLocation;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class Resources {
	
	public static final ResourceLocation emptyTexture = new BetterStorageResource("textures/empty.png");;
	
	public static final ResourceLocation crateContainer           = new BetterStorageResource("textures/gui/crate.png");
	public static final ResourceLocation reinforcedChestContainer = new BetterStorageResource("textures/gui/reinforcedChest.png");
	
	public static final ResourceLocation lockerTexture           = new BetterStorageResource("textures/models/locker.png");
	public static final ResourceLocation largeLockerTexture      = new BetterStorageResource("textures/models/locker_large.png");
	public static final ResourceLocation armorStandTexture       = new BetterStorageResource("textures/models/armorstand.png");
	public static final ResourceLocation backpackTexture         = new BetterStorageResource("textures/models/backpack.png");
	public static final ResourceLocation backpackOverlayTexture  = new BetterStorageResource("textures/models/backpack_overlay.png");
	public static final ResourceLocation enderBackpackTexture    = new BetterStorageResource("textures/models/enderBackpack.png");
	public static final ResourceLocation drinkingHelmetTexture   = new BetterStorageResource("textures/models/drinkingHelmet.png");
	public static final ResourceLocation cardboardArmorTexture   = new BetterStorageResource("textures/models/cardboardArmor.png");
	public static final ResourceLocation cardboardLegginsTexture = new BetterStorageResource("textures/models/cardboardArmor_leggings.png");
	
	
	private static final String modelbase = "/assets/" + Constants.modId + "/models/";
	
	public static final String lockerModel      = modelbase + "locker.obj";
	public static final String largeLockerModel = modelbase + "locker_large.obj";
	
	
	private Resources() {  }
	
}
