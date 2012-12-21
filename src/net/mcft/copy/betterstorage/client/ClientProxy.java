package net.mcft.copy.betterstorage.client;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.mcft.copy.betterstorage.BetterStorage;
import net.mcft.copy.betterstorage.CommonProxy;
import net.mcft.copy.betterstorage.Constants;
import net.mcft.copy.betterstorage.blocks.BlockReinforcedChest;
import net.mcft.copy.betterstorage.blocks.TileEntityReinforcedChest;
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
		preloadChestTextures(BetterStorage.reinforcedIronChest);
		preloadChestTextures(BetterStorage.reinforcedGoldChest);
		preloadChestTextures(BetterStorage.reinforcedDiamondChest);
		preloadChestTextures(BetterStorage.reinforcedEmeraldChest);
		preloadChestTextures(BetterStorage.reinforcedCopperChest);
		preloadChestTextures(BetterStorage.reinforcedTinChest);
		preloadChestTextures(BetterStorage.reinforcedSilverChest);
	}
	
	private void preloadChestTextures(BlockReinforcedChest block) {
		MinecraftForgeClient.preloadTexture(Constants.getReinforcedChestTexture(block.name, false));
		MinecraftForgeClient.preloadTexture(Constants.getReinforcedChestTexture(block.name, true));
	}
	
}
