package net.mcft.copy.betterstorage.misc;

import net.minecraft.util.ResourceLocation;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class Resources {
	
	public static final ResourceLocation crateContainer           = new ResourceLocation("betterstorage", "textures/gui/crate.png");
	public static final ResourceLocation reinforcedChestContainer = new ResourceLocation("betterstorage", "textures/gui/reinforced_chest.png");
	
	public static final ResourceLocation lockerTexture          = new ResourceLocation("betterstorage", "textures/models/locker.png");
	public static final ResourceLocation largeLockerTexture     = new ResourceLocation("betterstorage", "textures/models/locker_large.png");
	public static final ResourceLocation armorStandTexture      = new ResourceLocation("betterstorage", "textures/models/armorstand.png");
	public static final ResourceLocation backpackTexture        = new ResourceLocation("betterstorage", "textures/models/backpack.png");
	public static final ResourceLocation backpackOverlayTexture = new ResourceLocation("betterstorage", "textures/models/backpack_overlay.png");
	public static final ResourceLocation enderBackpackTexture   = new ResourceLocation("betterstorage", "textures/models/enderBackpack.png");
	
	
	private static final String modelbase = "/assets/betterstorage/models/";
	
	public static final String lockerModel      = modelbase + "locker.obj";
	public static final String largeLockerModel = modelbase + "locker_large.obj";
	
}
