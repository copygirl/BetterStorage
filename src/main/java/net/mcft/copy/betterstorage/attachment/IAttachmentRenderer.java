package net.mcft.copy.betterstorage.attachment;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public interface IAttachmentRenderer {
	
	public void render(Attachment attachment, float partialTicks);
	
}
