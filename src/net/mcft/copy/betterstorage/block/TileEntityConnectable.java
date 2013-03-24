package net.mcft.copy.betterstorage.block;

import net.mcft.copy.betterstorage.BetterStorage;
import net.mcft.copy.betterstorage.utils.WorldUtils;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet132TileEntityData;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;

public abstract class TileEntityConnectable extends TileEntity {

	public ForgeDirection orientation = ForgeDirection.UNKNOWN;
	public ForgeDirection connected = ForgeDirection.UNKNOWN;
	
	public boolean isConnected() {
		return (connected != ForgeDirection.UNKNOWN);
	}
	
	public boolean isMain() {
		return (!isConnected() ||
		        connected.offsetX + connected.offsetY + connected.offsetZ > 0);
	}
	
	public TileEntityConnectable getMain() {
		if (isMain()) return this;
		TileEntityConnectable connectable = getConnected();
		if (connectable != null) return connectable;
		BetterStorage.log.warning("getConnected() returned null.");
		return this;
	}
	
	public TileEntityConnectable getConnected() {
		TileEntityConnectable connectable = WorldUtils.get(
				worldObj, xCoord + connected.offsetX,
				          yCoord + connected.offsetY,
				          zCoord + connected.offsetZ, getClass());
		return connectable;
	}
	
	public abstract ForgeDirection[] getPossibleNeighbors();
	
	public void checkForConnections() {
		TileEntityConnectable connectableFound = null;
		ForgeDirection dirFound = ForgeDirection.UNKNOWN;
		for (ForgeDirection dir : getPossibleNeighbors()) {
			TileEntityConnectable connectable =
					WorldUtils.get(worldObj,
					               xCoord + dir.offsetX, yCoord + dir.offsetY, zCoord + dir.offsetZ,
					               TileEntityConnectable.class);
			if (!canConnect(connectable)) continue;
			if (connectableFound != null) return;
			connectableFound = connectable;
			dirFound = dir;
		}
		if (connectableFound == null) return;
		connected = dirFound;
		connectableFound.connected = dirFound.getOpposite();
		// Mark the block for an update, sends description packet to players.
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		worldObj.markBlockForUpdate(connectableFound.xCoord, connectableFound.yCoord, connectableFound.zCoord);
	}
	
	public boolean canConnect(TileEntityConnectable connectable) {
		return (connectable != null &&                                  // check for null
				getBlockType() == connectable.getBlockType() &&         // check for same block id
				getBlockMetadata() == connectable.getBlockMetadata() && // check for same material
		        orientation == connectable.orientation &&               // check for same orientation
		        // Make sure the connectables are not already connected.
		        !isConnected() && !connectable.isConnected());
	}
	
	public void disconnect() {
		if (connected == ForgeDirection.UNKNOWN) return;
		TileEntityConnectable connectable = getConnected();
		connected = ForgeDirection.UNKNOWN;
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		if (connectable != null) {
			connectable.connected = ForgeDirection.UNKNOWN;
			worldObj.markBlockForUpdate(connectable.xCoord, connectable.yCoord, connectable.zCoord);
		} else BetterStorage.log.warning("getConnected() returned null.");
	}
	
	// Tile entity synchronization
	
	@Override
	public Packet getDescriptionPacket() {
		NBTTagCompound compound = new NBTTagCompound();
		compound.setByte("orientation", (byte)orientation.ordinal());
		compound.setByte("connected", (byte)connected.ordinal());
        return new Packet132TileEntityData(xCoord, yCoord, zCoord, 0, compound);
	}
	
	@Override
	public void onDataPacket(INetworkManager net, Packet132TileEntityData packet) {
		NBTTagCompound compound = packet.customParam1;
		orientation = ForgeDirection.getOrientation(compound.getByte("orientation"));
		connected = ForgeDirection.getOrientation(compound.getByte("connected"));
	}
	
	// Reading from / writing to NBT
	
	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		orientation = ForgeDirection.getOrientation(compound.getByte("orientation"));
		connected = ForgeDirection.getOrientation(compound.getByte("connected"));
	}
	
	@Override
	public void writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		compound.setByte("orientation", (byte)orientation.ordinal());
		compound.setByte("connected", (byte)connected.ordinal());
	}
	
}
