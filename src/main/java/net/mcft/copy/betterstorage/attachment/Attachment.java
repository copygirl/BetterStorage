package net.mcft.copy.betterstorage.attachment;

import net.mcft.copy.betterstorage.utils.DirectionUtils;
import net.mcft.copy.betterstorage.utils.MathUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class Attachment {
	
	public final TileEntity tileEntity;
	public final int subId;
	
	private AxisAlignedBB base = AxisAlignedBB.fromBounds(0, 0, 0, 0, 0, 0);
	private AxisAlignedBB box = AxisAlignedBB.fromBounds(0, 0, 0, 0, 0, 0);
	private EnumFacing direction = EnumFacing.NORTH;
	
	public double getX() { return base.minX; }
	public double getY() { return base.minY; }
	public double getZ() { return base.minZ; }
	
	public float getRotation() { return DirectionUtils.getRotation(direction); }
	
	public AxisAlignedBB getHighlightBox() {
		return box.offset(tileEntity.getPos().getX(), tileEntity.getPos().getY(), tileEntity.getPos().getZ());
	}
	
	public Attachment(TileEntity tileEntity, int subId) {
		this.tileEntity = tileEntity;
		this.subId = subId;
	}
	
	public void setBox(double x, double y, double z,
	                   double width, double height, double depth, double scale) {
		base = MathUtils.scaleAABB(MathUtils.createAABB(x, y, z, width, height, depth), scale);
		updateBox();
	}
	
	public void setBox(double x, double y, double z,
	                   double width, double height, double depth) {
		setBox(x, y, z, width, height, depth, 1 / 16.0F);
	}
	
	public void setBox(AxisAlignedBB aabb) {
		box = aabb;
	}
	
	public void setDirection(EnumFacing direction) {
		if(this.direction == direction) return;
		this.direction = direction;
		updateBox();
	}
	
	private void updateBox() {
		box = MathUtils.rotateAABB(base, direction);
	}
	
	public void update() {  }
	
	public boolean interact(EntityPlayer player, EnumAttachmentInteraction type) { return false; }
	
	public ItemStack pick() { return null; }
	
	public boolean boxVisible(EntityPlayer player) { return true; }
	
	@SideOnly(Side.CLIENT)
	public abstract IAttachmentRenderer getRenderer();
	
}
