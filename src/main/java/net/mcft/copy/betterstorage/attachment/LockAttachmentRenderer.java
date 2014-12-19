package net.mcft.copy.betterstorage.attachment;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class LockAttachmentRenderer extends ItemAttachmentRenderer {
	
	public static final LockAttachmentRenderer instance = new LockAttachmentRenderer();
	
	@Override
	public void render(Attachment attachment, float partialTicks) {
		render((LockAttachment)attachment, partialTicks);
	}
	private void render(LockAttachment attachment, float partialTicks) {
		GlStateManager.pushMatrix();
		float hit = (float)Math.sin(Math.max(0, attachment.hit - partialTicks - 4) / 6 * Math.PI) * -20;
		float wiggle = (float)Math.sin(attachment.wiggle + partialTicks) * attachment.wiggleStrength;
		GlStateManager.translate(0.0, -0.05, 0.0);
		GlStateManager.rotate(hit, 1.0F, 0.0F, 0.0F);
		GlStateManager.rotate(wiggle, 0.0F, 0.0F, 1.0F);
		GlStateManager.translate(0.0, 0.05, 0.0);
		super.render(attachment, partialTicks);
		GlStateManager.popMatrix();
	}
	
}
