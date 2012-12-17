package net.mcft.copy.betterstorage.client;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import net.mcft.copy.betterstorage.blocks.TileEntityReinforcedChest;
import net.minecraft.src.Block;
import net.minecraft.src.IBlockAccess;
import net.minecraft.src.RenderBlocks;
import net.minecraft.src.TileEntityRenderer;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.asm.SideOnly;

@SideOnly(Side.CLIENT)
public class ReinforcedChestRenderingHandler implements ISimpleBlockRenderingHandler {
	
	private static TileEntityReinforcedChestRenderer reinforcedChestRenderer = new TileEntityReinforcedChestRenderer();
	static { reinforcedChestRenderer.setTileEntityRenderer(TileEntityRenderer.instance); }
	private static TileEntityReinforcedChest reinforcedChest = new TileEntityReinforcedChest();
	
	public static int renderId;
	
	public ReinforcedChestRenderingHandler() {
		renderId = RenderingRegistry.getNextAvailableRenderId();
	}
	
	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks renderer) {
		reinforcedChest.blockType = block;
		reinforcedChestRenderer.renderTileEntityAt(reinforcedChest, -0.5, -0.5, -0.5, 0);
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
	}
	
	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) { return false; }
	
	@Override
	public boolean shouldRender3DInInventory() { return true; }
	
	@Override
	public int getRenderId() { return renderId; }
	
}
