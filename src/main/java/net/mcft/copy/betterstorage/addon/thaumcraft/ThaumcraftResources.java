package net.mcft.copy.betterstorage.addon.thaumcraft;

import net.mcft.copy.betterstorage.misc.BetterStorageResource;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class ThaumcraftResources {
	
	public static final ResourceLocation thaumiumChestContainer = new BetterStorageResource("textures/gui/thaumiumChest.png");
	
	public static final ResourceLocation thaumcraftBackpackTexture = new BetterStorageResource("textures/models/thaumcraftBackpack.png");
	
	private ThaumcraftResources() {  }
	
}
