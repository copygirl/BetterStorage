package net.mcft.copy.betterstorage.tile.entity;

import net.mcft.copy.betterstorage.misc.Constants;
import net.mcft.copy.betterstorage.misc.Resources;
import net.mcft.copy.betterstorage.utils.DirectionUtils;
import net.mcft.copy.betterstorage.utils.WorldUtils;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.ForgeDirection;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileEntityLocker extends TileEntityLockable {
	
	private static final ForgeDirection[] neighbors = { ForgeDirection.DOWN, ForgeDirection.UP };
	
	public boolean mirror = false;
	
	@Override
	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getRenderBoundingBox() {
		return WorldUtils.getAABB(this, 0, 0, 0, 0, 1, 0);
	}
	
	@SideOnly(Side.CLIENT)
	public ResourceLocation getResource() {
		return (isConnected() ? Resources.textureLockerLarge : Resources.textureLocker);
	}
	
	@Override
	public boolean canHaveLock() { return false; }
	@Override
	public boolean canHaveMaterial() { return false; }
	@Override
	public void setAttachmentPosition() {  }
	
	@Override
	public ForgeDirection[] getPossibleNeighbors() { return neighbors; }
	@Override
	protected String getConnectableName() { return Constants.containerLocker; }
	
	@Override
	public boolean canConnect(TileEntityConnectable connectable) {
		if (!(connectable instanceof TileEntityLocker)) return false;
		TileEntityLocker locker = (TileEntityLocker)connectable;
		return (super.canConnect(connectable) && (mirror == locker.mirror));
	}
	
	@Override
	public void onBlockPlacedBeforeCheckingConnections(EntityLivingBase player, ItemStack stack) {
		super.onBlockPlacedBeforeCheckingConnections(player, stack);
		double angle = DirectionUtils.getRotation(getOrientation().getOpposite());
		double yaw = ((player.rotationYaw % 360) + 360) % 360;
		mirror = (DirectionUtils.angleDifference(angle, yaw) > 0);
		setAttachmentPosition();
	}
	
	@Override
	public boolean onBlockActivated(EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
		if (getOrientation().ordinal() != side) return true;
		return super.onBlockActivated(player, side, hitX, hitY, hitZ);
	}
	
	// TileEntity synchronization

	@Override
	public NBTTagCompound getDescriptionPacketData(NBTTagCompound compound) {
		compound = super.getDescriptionPacketData(compound);
		compound.setBoolean("mirror", mirror);
		return compound;
	}

	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity packet) {
		super.onDataPacket(net, packet);
		NBTTagCompound compound = packet.func_148857_g();
		mirror = compound.getBoolean("mirror");
		setAttachmentPosition();
	}
	
	// Reading from / writing to NBT
	
	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		mirror = compound.getBoolean("mirror");
		setAttachmentPosition();
	}
	@Override
	public void writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		compound.setBoolean("mirror", mirror);
	}
	
}
