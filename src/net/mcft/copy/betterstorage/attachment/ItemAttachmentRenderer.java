package net.mcft.copy.betterstorage.attachment;

import net.mcft.copy.betterstorage.utils.RenderUtils;
import net.minecraft.item.ItemStack;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

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
		GL11.glPushMatrix();
			GL11.glScalef(attachment.scale, attachment.scale, attachment.scaleDepth);
			RenderUtils.renderItemIn3d(item);
		GL11.glPopMatrix();
	}
	
}
