package net.mcft.copy.betterstorage.client.renderer;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;

@Deprecated
public class ItemRendererCardboardBox implements IItemRenderer {
	
	// This custom item renderer is only necessary because of a bug in Vanilla.
	// See RenderItem.renderItemIntoGUI, specifically RenderBlock's useInventoryTint.
	//
	// The color is retrieved from the item using getColorFromItemStack, set using
	// glColor, only to be overwritten by RenderBlocks.renderBlockAsItem using the
	// value returned from Block.getRenderColor, since useInventoyTint is true.
	
	public final Block block;
	
	public ItemRendererCardboardBox(Block block) {
		this.block = block;
	}
	
	@Override
	public boolean handleRenderType(ItemStack item, ItemRenderType type) { return true; }
	
	@Override
	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) { return true; }
	
	@Override
	public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
		/*int color = item.getItem().getColorFromItemStack(item, 0);
		RenderUtils.setColorFromInt(color);
		RenderBlocks render = RenderBlocks.getInstance();
		render.useInventoryTint = false;
		if ((type == ItemRenderType.EQUIPPED) ||
		    (type == ItemRenderType.EQUIPPED_FIRST_PERSON))
			GL11.glTranslatef(0.5F, 0.5F, 0.5F);
		render.renderBlockAsItem(block, item.getItemDamage(), 1.0F);
		render.useInventoryTint = true;*/
	}
	
}
