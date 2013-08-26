package net.mcft.copy.betterstorage.attachment;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class LockAttachmentRenderer extends ItemAttachmentRenderer {
	
	public static final LockAttachmentRenderer instance = new LockAttachmentRenderer();
	
	@Override
	public void render(Attachment attachment, float partialTicks) {
		render((LockAttachment)attachment, partialTicks);
	}
	private void render(LockAttachment attachment, float partialTicks) {
		GL11.glPushMatrix();
		float hit = (float)Math.sin(Math.max(0, attachment.hit - partialTicks - 4) / 6 * Math.PI) * -20;
		float wiggle = (float)Math.sin(attachment.wiggle + partialTicks) * attachment.wiggleStrength;
		GL11.glTranslated(0.0, -0.05, 0.0);
		GL11.glRotatef(hit, 1.0F, 0.0F, 0.0F);
		GL11.glRotatef(wiggle, 0.0F, 0.0F, 1.0F);
		GL11.glTranslated(0.0, 0.05, 0.0);
		super.render(attachment, partialTicks);
		GL11.glPopMatrix();
	}
	
}
