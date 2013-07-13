package net.mcft.copy.betterstorage.client.renderer;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class BetterStorageRenderingHandler implements ISimpleBlockRenderingHandler {
	
	public TileEntity tileEntity;
	public TileEntitySpecialRenderer tileEntityRenderer;
	
	public final int renderId;
	public final boolean render3dInInventory;
	public final float rotation;
	public final float scale;
	public final float yOffset;
	
	public BetterStorageRenderingHandler(Class<? extends TileEntity> tileEntityClass,
	                                     TileEntitySpecialRenderer tileEntityRenderer,
	                                     boolean render3dInInventory, float rotation,
	                                     float scale, float yOffset) {
		try { tileEntity = tileEntityClass.newInstance(); }
		catch (Exception e) { throw new RuntimeException(e); }
		this.tileEntityRenderer = tileEntityRenderer;
		this.render3dInInventory = render3dInInventory;
		this.rotation = rotation;
		this.scale = scale;
		this.yOffset = yOffset;
		
		tileEntityRenderer.setTileEntityRenderer(TileEntityRenderer.instance);
		renderId = RenderingRegistry.getNextAvailableRenderId();
	}
	
	@Override
	public void renderInventoryBlock(Block block, int metadata, int modelID, RenderBlocks renderer) {
		tileEntity.blockType = block;
		tileEntity.blockMetadata = metadata;
		GL11.glTranslatef(0, yOffset, 0);
		GL11.glRotatef(rotation, 0.0F, 1.0F, 0.0F);
		GL11.glScalef(scale, scale, scale);
		tileEntityRenderer.renderTileEntityAt(tileEntity, -0.5, -0.5, -0.5, 0);
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
	}
	
	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) { return false; }
	
	@Override
	public boolean shouldRender3DInInventory() { return render3dInInventory; }
	
	@Override
	public int getRenderId() { return renderId; }
	
}
