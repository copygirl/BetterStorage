package net.mcft.copy.betterstorage.client;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.mcft.copy.betterstorage.CommonProxy;
import net.mcft.copy.betterstorage.Constants;
import net.mcft.copy.betterstorage.block.ChestMaterial;
import net.mcft.copy.betterstorage.block.TileEntityReinforcedChest;
import net.minecraftforge.client.MinecraftForgeClient;

@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy {
	
	@Override
	public void registerTileEntites() {
		super.registerTileEntites();
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityReinforcedChest.class,
				new TileEntityReinforcedChestRenderer());
		RenderingRegistry.registerBlockHandler(new ReinforcedChestRenderingHandler());
	}
	
	@Override
	public void preloadTextures() {
		MinecraftForgeClient.preloadTexture(Constants.terrain);
		MinecraftForgeClient.preloadTexture(Constants.items);
		MinecraftForgeClient.preloadTexture(Constants.reinforcedChestContainer);
		for (ChestMaterial material : ChestMaterial.materials)
			preloadChestTextures(material);
	}
	
	private void preloadChestTextures(ChestMaterial material) {
		MinecraftForgeClient.preloadTexture(material.getTexture(false));
		MinecraftForgeClient.preloadTexture(material.getTexture(true));
	}
	
}
