package net.mcft.copy.betterstorage.client.renderer;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.mcft.copy.betterstorage.block.TileEntityBackpack;
import net.mcft.copy.betterstorage.proxy.ClientProxy;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;

@SideOnly(Side.CLIENT)
public class ItemRendererBackpack implements IItemRenderer {
	
	@Override
	public boolean handleRenderType(ItemStack item, ItemRenderType type) { return true; }
	@Override
	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) { return true; }
	
	@Override
	public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
		if ((type == ItemRenderType.ENTITY) || (type == ItemRenderType.EQUIPPED))
			GL11.glScalef(1.2F, 1.2F, 1.2F);
		if (type == ItemRenderType.EQUIPPED) {
			GL11.glTranslatef(0.5F, 0.5F, 0.5F);
			GL11.glRotatef(200.0F, 0.0F, 1.0F, 0.0F);
		}
		BetterStorageRenderingHandler renderingHanlder =
				ClientProxy.renderingHandlers.get(TileEntityBackpack.class);
		((TileEntityBackpack)renderingHanlder.tileEntity).stack = item;
		renderingHanlder.renderInventoryBlock(Block.blocksList[item.itemID], 0, 0, null);
	}
	
}
