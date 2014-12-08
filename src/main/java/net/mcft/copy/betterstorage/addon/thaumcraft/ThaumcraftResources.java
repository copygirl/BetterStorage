package net.mcft.copy.betterstorage.addon.thaumcraft;

import net.mcft.copy.betterstorage.misc.BetterStorageResource;
import net.minecraft.util.ResourceLocation;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class ThaumcraftResources {
	
	public static final ResourceLocation thaumiumChestContainer = new BetterStorageResource("textures/gui/thaumiumChest.png");
	
	public static final ResourceLocation thaumcraftBackpackTexture = new BetterStorageResource("textures/models/thaumcraftBackpack.png");
	public static final ResourceLocation thaumcraftBackpackTextureOverlay = new BetterStorageResource("textures/models/thaumcraftBackpack_overlay.png");
	
	private ThaumcraftResources() {  }
	
}
