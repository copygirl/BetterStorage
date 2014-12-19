package net.mcft.copy.betterstorage.client.renderer;

import net.mcft.copy.betterstorage.tile.entity.TileEntityContainer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/** Item renderer that'll tell the container class which stack is being rendered,
 *  to make sure it's got a chance to set members based on the stack's NBT data. */
@SideOnly(Side.CLIENT)
@Deprecated
public class ItemRendererContainer implements IItemRenderer {
	
	private final Class<? extends TileEntityContainer> tileEntityClass;
	private BetterStorageRenderingHandler renderingHandler = null;
	
	public ItemRendererContainer(Class<? extends TileEntityContainer> tileEntityClass) {
		this.tileEntityClass = tileEntityClass;
	}
	
	@Override
	public boolean handleRenderType(ItemStack item, ItemRenderType type) { return true; }
	@Override
	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) { return true; }
	
	@Override
	public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
		/*if (renderingHandler == null)
			renderingHandler = ClientProxy.renderingHandlers.get(tileEntityClass);
		((TileEntityContainer)renderingHandler.tileEntity).onBlockRenderAsItem(item);
		GlStateManager.pushMatrix();
		if ((type == ItemRenderType.EQUIPPED) ||
		    (type == ItemRenderType.EQUIPPED_FIRST_PERSON))
			GlStateManager.translate(0.5F, 0.5F, 0.5F);
		renderingHandler.renderInventoryBlock(Block.getBlockFromItem(item.getItem()), 0, 0, null);
		GlStateManager.popMatrix();*/
	}
	
}
