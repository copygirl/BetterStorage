package net.mcft.copy.betterstorage.attachment;

import net.mcft.copy.betterstorage.utils.RenderUtils;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ItemAttachmentRenderer implements IAttachmentRenderer {
	
	public static final ItemAttachmentRenderer instance = new ItemAttachmentRenderer();
	
	@Override
	public void render(Attachment attachment, float partialTicks) {
		render((ItemAttachment)attachment, partialTicks);
	}
	private void render(ItemAttachment attachment, float partialTicks) {
		ItemStack item = attachment.getItem();
		if (item == null) return;
		GlStateManager.pushMatrix();
			GlStateManager.scale(attachment.scale, attachment.scale, attachment.scaleDepth);
			RenderUtils.renderItemIn3d(item);
		GlStateManager.popMatrix();
	}
	
}
