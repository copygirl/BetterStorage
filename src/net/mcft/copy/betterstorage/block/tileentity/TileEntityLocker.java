package net.mcft.copy.betterstorage.block.tileentity;

import net.mcft.copy.betterstorage.utils.WorldUtils;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet132TileEntityData;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.ForgeDirection;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileEntityLocker extends TileEntityConnectable {
	
	private static final ForgeDirection[] neighbors = { ForgeDirection.DOWN, ForgeDirection.UP };
	
	public boolean mirror = false;
	
	@Override
	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getRenderBoundingBox() {
		return WorldUtils.getAABB(this, 0, 0, 0, 0, 1, 0);
	}
	
	@Override
	public ForgeDirection[] getPossibleNeighbors() { return neighbors; }
	@Override
	protected String getConnectableName() { return "container.locker"; }
	
	@Override
	public boolean canConnect(TileEntityConnectable connectable) {
		if (!(connectable instanceof TileEntityLocker)) return false;
		TileEntityLocker locker = (TileEntityLocker)connectable;
		return (super.canConnect(connectable) && (mirror == locker.mirror));
	}
	
	// TileEntity synchronization
	
	@Override
	public Packet getDescriptionPacket() {
		Packet132TileEntityData packet = (Packet132TileEntityData)super.getDescriptionPacket();
		NBTTagCompound compound = packet.customParam1;
		compound.setBoolean("mirror", mirror);
        return packet;
	}
	@Override
	public void onDataPacket(INetworkManager net, Packet132TileEntityData packet) {
		super.onDataPacket(net, packet);
		NBTTagCompound compound = packet.customParam1;
		mirror = compound.getBoolean("mirror");
	}
	
	// Reading from / writing to NBT
	
	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		mirror = compound.getBoolean("mirror");
	}
	@Override
	public void writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		compound.setBoolean("mirror", mirror);
	}
	
}
