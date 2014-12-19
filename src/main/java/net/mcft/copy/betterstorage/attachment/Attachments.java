package net.mcft.copy.betterstorage.attachment;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class Attachments implements Iterable<Attachment> {
	
	public static final ThreadLocal<EntityPlayer> playerLocal = new ThreadLocal<EntityPlayer>();
	
	public final TileEntity tileEntity;
	private Map<Integer, Attachment> attachments = new HashMap<Integer, Attachment>();
	
	public Attachments(TileEntity tileEntity) {
		this.tileEntity = tileEntity;
	}
	
	// Called in CommonProxy.onPlayerInteract.
	public boolean interact(MovingObjectPosition target, EntityPlayer player,
	                        EnumAttachmentInteraction interactionType) {
		Attachment attachment = ((target != null) ? get(target.subHit) : null);
		return ((attachment != null) ? attachment.interact(player, interactionType) : false);
	}
	
	// Called in TileEntityContainer.onPickBlock.
	public ItemStack pick(MovingObjectPosition target) {
		Attachment attachment = ((target != null) ? get(target.subHit) : null);
		return ((attachment != null) ? attachment.pick() : null);
	}
	
	// Called in Block.collisionRayTrace.
	public MovingObjectPosition rayTrace(World world, BlockPos pos, Vec3 start, Vec3 end) {
		
		AxisAlignedBB aabb = tileEntity.getBlockType().getCollisionBoundingBox(world, pos, tileEntity.getWorld().getBlockState(pos));
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
			target = new MovingObjectPosition(target.typeOfHit, target.hitVec, target.field_178784_b, pos);
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
			GlStateManager.pushMatrix();
				GlStateManager.translate(0.5, 0.5, 0.5);
				GlStateManager.pushMatrix();
					GlStateManager.rotate(rotation, 0, -1, 0);
					GlStateManager.translate(0.5 - attachment.getX(), 0.5 - attachment.getY(), 0.5 - attachment.getZ());
					attachment.getRenderer().render(attachment, partialTicks);
				GlStateManager.popMatrix();
			GlStateManager.popMatrix();
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
