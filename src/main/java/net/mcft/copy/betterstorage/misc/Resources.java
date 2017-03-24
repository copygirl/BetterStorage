package net.mcft.copy.betterstorage.misc;

import net.minecraft.util.ResourceLocation;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class Resources {
	
	public static final ResourceLocation textureEmpty  = new BetterStorageResource("textures/empty.png");
	public static final ResourceLocation enchantedEffect = new ResourceLocation("textures/misc/enchanted_item_glint.png");
	
	public static final ResourceLocation containerCrate           = new BetterStorageResource("textures/gui/crate.png");
	public static final ResourceLocation containerReinforcedChest = new BetterStorageResource("textures/gui/reinforcedChest.png");
	public static final ResourceLocation containerCraftingStation = new BetterStorageResource("textures/gui/craftingStation.png");
	
	public static final ResourceLocation textureLocker            = new BetterStorageResource("textures/models/locker/wood.png");
	public static final ResourceLocation textureLockerLarge       = new BetterStorageResource("textures/models/locker_large/wood.png");
	public static final ResourceLocation textureArmorStand        = new BetterStorageResource("textures/models/armorstand.png");
	public static final ResourceLocation textureBackpack          = new BetterStorageResource("textures/models/backpack.png");
	public static final ResourceLocation textureBackpackOverlay   = new BetterStorageResource("textures/models/backpack_overlay.png");
	public static final ResourceLocation textureEnderBackpack     = new BetterStorageResource("textures/models/enderBackpack.png");
	public static final ResourceLocation textureCardboardBackpack = new BetterStorageResource("textures/models/cardboardBackpack.png");
	public static final ResourceLocation textureDrinkingHelmet    = new BetterStorageResource("textures/models/drinkingHelmet.png");
	public static final ResourceLocation textureCardboardArmor    = new BetterStorageResource("textures/models/cardboardArmor.png");
	public static final ResourceLocation textureCardboardLeggins  = new BetterStorageResource("textures/models/cardboardArmor_leggings.png");
	public static final ResourceLocation textureCluckOverlay      = new BetterStorageResource("textures/models/cluck.png");
	public static final ResourceLocation texturePresentOverlay    = new BetterStorageResource("textures/models/present_overlay.png");
	
	public static final ResourceLocation modelLocker      = new BetterStorageResource("models/locker.obj");
	public static final ResourceLocation modelLockerLarge = new BetterStorageResource("models/locker_large.obj");
	
	public static final ResourceLocation modelPresent     = new BetterStorageResource("models/present.obj");
	
	
	private Resources() {  }
	
}
