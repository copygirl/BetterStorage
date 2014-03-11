package net.mcft.copy.betterstorage.attachment;

import net.mcft.copy.betterstorage.utils.DirectionUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.util.ForgeDirection;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public abstract class Attachment {
	
	public final TileEntity tileEntity;
	public final int subId;
	
	private double x, y, z;
	private double width, height, depth;
	private AxisAlignedBB box = AxisAlignedBB.getBoundingBox(0, 0, 0, 0, 0, 0);
	private ForgeDirection direction = ForgeDirection.NORTH;
	
	public double getX() { return x; }
	public double getY() { return y; }
	public double getZ() { return z; }
	
	public double getWidth()  { return width; }
	public double getHeight() { return height; }
	public double getDepth()  { return depth; }
	
	public float getRotation() { return DirectionUtils.getRotation(direction); }
	
	public AxisAlignedBB getHighlightBox() {
		return box.copy().offset(tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord);
	}
	
	public Attachment(TileEntity tileEntity, int subId) {
		this.tileEntity = tileEntity;
		this.subId = subId;
	}
	
	public void setBox(double x, double y, double z,
	                   double width, double height, double depth, double scale) {
		this.x = x * scale;
		this.y = y * scale;
		this.z = z * scale;
		this.width = width * scale;
		this.height = height * scale;
		this.depth = depth * scale;
		updateBox();
	}
	public void setBox(double x, double y, double z,
	                   double width, double height, double depth) {
		setBox(x, y, z, width, height, depth, 1 / 16.0F);
	}
	
	public void setDirection(ForgeDirection direction) {
		this.direction = direction;
		updateBox();
	}
	
	private void updateBox() {
		double minX, minY = 1 - (y + height / 2), minZ;
		double maxX, maxY = 1 - (y - height / 2), maxZ;
		switch (direction) {
			case EAST:
				minX = 1 - (z + depth / 2); minZ = x - width / 2;
				maxX = 1 - (z - depth / 2); maxZ = x + width / 2;
				break;
			case SOUTH:
				minX = 1 - (x + width / 2); minZ = 1 - (z + depth / 2);
				maxX = 1 - (x - width / 2); maxZ = 1 - (z - depth / 2);
				break;
			case WEST:
				minX = z - depth / 2; minZ = 1 - (x + width / 2);
				maxX = z + depth / 2; maxZ = 1 - (x - width / 2);
				break;
			default:
				minX = x - width / 2; minZ = z - depth / 2;
				maxX = x + width / 2; maxZ = z + depth / 2;
				break;
		}
		box.setBounds(minX, minY, minZ, maxX, maxY, maxZ);
	}
	
	public void update() {  }
	
	public boolean interact(EntityPlayer player, EnumAttachmentInteraction type) { return false; }
	
	public ItemStack pick() { return null; }
	
	public boolean boxVisible(EntityPlayer player) { return true; }
	
	@SideOnly(Side.CLIENT)
	public abstract IAttachmentRenderer getRenderer();
	
}
