package net.mcft.copy.betterstorage.attachment;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public interface IAttachmentRenderer {
	
	public void render(Attachment attachment);
	
}
