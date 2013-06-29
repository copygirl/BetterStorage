package net.mcft.copy.betterstorage.attachment;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.lwjgl.opengl.GL11;

import net.minecraft.tileentity.TileEntity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class Attachments implements Iterable<Attachment> {
	
	public final TileEntity tileEntity;
	private Set<Attachment> attachments = new HashSet<Attachment>();
	
	public Attachments(TileEntity tileEntity) {
		this.tileEntity = tileEntity;
	}
	
	public void update() {
		for (Attachment attachment : this)
			attachment.update();
	}
	
	@SideOnly(Side.CLIENT)
	public void render() {
		for (Attachment attachment : this) {
			float rotation = attachment.getRotation();
			GL11.glTranslated(0.5, 0.5, 0.5);
			GL11.glRotatef(rotation, 0, 1, 0);
			GL11.glTranslated(attachment.getX() - 0.5, attachment.getY() - 0.5, attachment.getZ() - 0.5);
			attachment.getRenderer().render(attachment);
		}
	}
	
	public void add(Attachment attachment) { attachments.add(attachment); }
	
	public void remove(Attachment attachment) { attachments.remove(attachment); }
	
	public boolean has(Attachment attachment) { return attachments.contains(attachment); }

	@Override
	public Iterator<Attachment> iterator() { return attachments.iterator(); }
	
}
