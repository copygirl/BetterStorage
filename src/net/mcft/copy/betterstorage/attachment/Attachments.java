package net.mcft.copy.betterstorage.attachment;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import net.mcft.copy.betterstorage.utils.WorldUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class Attachments implements Iterable<Attachment> {
	
	public static final ThreadLocal<EntityPlayer> playerLocal = new ThreadLocal<EntityPlayer>();
	
	public final TileEntity tileEntity;
	private Map<Integer, Attachment> attachments = new HashMap<Integer, Attachment>();
	
	public Attachments(TileEntity tileEntity) {
		this.tileEntity = tileEntity;
	}

	// Called in CommonProxy.onPlayerInteract.
	public boolean interact(EntityPlayer player, EnumAttachmentInteraction interactionType) {
		MovingObjectPosition target = WorldUtils.rayTrace(player, 1.0F);
		Attachment attachment = ((target != null) ? get(target.subHit) : null);
		return ((attachment != null) ? attachment.interact(player, interactionType) : false);
	}
	
	// Called in Block.collisionRayTrace.
	public MovingObjectPosition rayTrace(World world, int x, int y, int z, Vec3 start, Vec3 end) {
		
		AxisAlignedBB aabb = tileEntity.getBlockType().getCollisionBoundingBoxFromPool(world, x, y, z);
		MovingObjectPosition target = aabb.calculateIntercept(start, end);
		EntityPlayer player = playerLocal.get();
		
		double distance = ((target != null) ? start.distanceTo(target.hitVec) : Double.MAX_VALUE);
		for (Attachment attachment : this) {
			if (!attachment.boxVisible(player)) continue;
			AxisAlignedBB attachmentBox = attachment.getHighlightBox();
			MovingObjectPosition attachmentTarget = attachmentBox.calculateIntercept(start, end);
			if (attachmentTarget == null) continue;
			double attachmentDistance = start.distanceTo(attachmentTarget.hitVec);
			if (attachmentDistance >= distance) continue;
			distance = attachmentDistance;
			target = attachmentTarget;
			target.subHit = attachment.subId;
		}
		
		if (target != null) {
			target.blockX = x;
			target.blockY = y;
			target.blockZ = z;
		}
		return target;
		
	}

	// Called in TileEntity.updateEntity.
	public void update() {
		for (Attachment attachment : this)
			attachment.update();
	}
	
	// Called in TileEntityRenderer.renderTileEntityAt.
	@SideOnly(Side.CLIENT)
	public void render(float partialTicks) {
		for (Attachment attachment : this) {
			float rotation = attachment.getRotation();
			GL11.glPushMatrix();
				GL11.glTranslated(0.5, 0.5, 0.5);
				GL11.glPushMatrix();
					GL11.glRotatef(rotation, 0, 1, 0);
					GL11.glTranslated(0.5 - attachment.getX(), attachment.getY() - 0.5, attachment.getZ() - 0.5);
					attachment.getRenderer().render(attachment, partialTicks);
				GL11.glPopMatrix();
			GL11.glPopMatrix();
		}
	}
	
	public <T extends Attachment> T add(Class<T> attachmentClass) {
		try {
			Constructor<T> constructor = attachmentClass.getConstructor(TileEntity.class, int.class);
			T attachment = constructor.newInstance(tileEntity, getFreeSubId());
			attachments.put(attachment.subId, attachment);
			return attachment;
		} catch (Exception e) { throw new RuntimeException(e); }
	}
	
	public Attachment get(int subId) { return attachments.get(subId); }
	
	public void remove(Attachment attachment) { attachments.remove(attachment.subId); }
	
	public boolean has(Attachment attachment) { return attachments.containsKey(attachment.subId); }

	@Override
	public Iterator<Attachment> iterator() { return attachments.values().iterator(); }
	
	private int getFreeSubId() {
		int freeSubId = 0;
		Set<Integer> takenSet = new HashSet<Integer>(attachments.keySet());
		while (takenSet.remove(++freeSubId)) {  }
		return freeSubId;
	}
	
}
