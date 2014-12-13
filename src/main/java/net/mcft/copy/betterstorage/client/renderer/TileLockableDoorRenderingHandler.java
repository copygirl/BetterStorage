package net.mcft.copy.betterstorage.client.renderer;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
@Deprecated
public class TileLockableDoorRenderingHandler /*implements ISimpleBlockRenderingHandler*/ {

	public static TileLockableDoorRenderingHandler instance = new TileLockableDoorRenderingHandler();
	
	/*@Override
	public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer) {}

	@Override
	public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
		renderer.renderBlockDoor(block, x, y, z);
		return true;
	}

	@Override
	public boolean shouldRender3DInInventory(int modelId) {
		return false;
	}

	@Override
	public int getRenderId() {
		return ClientProxy.lockableDoorRenderId;
	}
	*/
}
