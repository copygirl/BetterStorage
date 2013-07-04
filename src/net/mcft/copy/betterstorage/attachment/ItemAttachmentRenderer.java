package net.mcft.copy.betterstorage.attachment;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.mcft.copy.betterstorage.utils.RenderUtils;
import net.minecraft.item.ItemStack;

@SideOnly(Side.CLIENT)
public class ItemAttachmentRenderer implements IAttachmentRenderer {
	
	public static final ItemAttachmentRenderer instance = new ItemAttachmentRenderer();
	
	@Override
	public void render(Attachment attachment) {
		render((ItemAttachment)attachment);
	}
	private void render(ItemAttachment attachment) {
		ItemStack item = attachment.getItem();
		if (item == null) return;
		GL11.glPushMatrix();
			GL11.glScalef(attachment.scale, attachment.scale, attachment.scaleDepth);
			RenderUtils.renderItemIn3d(item);
		GL11.glPopMatrix();
	}
	
}
