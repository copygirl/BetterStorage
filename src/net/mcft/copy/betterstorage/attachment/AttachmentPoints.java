package net.mcft.copy.betterstorage.attachment;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import net.minecraft.tileentity.TileEntity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class AttachmentPoints {
	
	public final TileEntity tileEntity;
	public List<AttachmentPoint> points = new ArrayList<AttachmentPoint>();
	
	public AttachmentPoints(TileEntity tileEntity) {
		this.tileEntity = tileEntity;
	}
	
	public AttachmentPoint add() {
		AttachmentPoint point = new AttachmentPoint();
		points.add(point);
		return point;
	}
	
	public void update() {
		for (AttachmentPoint point : points) {
			Attachment attach = point.attachment;
			if (attach == null) continue;
			attach.update();
		}
	}
	
	@SideOnly(Side.CLIENT)
	public void render() {
		for (AttachmentPoint point : points) {
			Attachment attach = point.attachment;
			if (attach == null) continue;
			float rotation = point.getRotation();
			GL11.glTranslated(0.5, 0.5, 0.5);
			GL11.glRotatef(rotation, 0, 1, 0);
			GL11.glTranslated(point.getX() - 0.5, point.getY() - 0.5, point.getZ() - 0.5);
			attach.getRenderer().render(attach);
		}
	}
	
}
