package net.mcft.copy.betterstorage.block;

import java.security.InvalidParameterException;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.mcft.copy.betterstorage.Config;
import net.mcft.copy.betterstorage.api.ILock;
import net.mcft.copy.betterstorage.api.ILockable;
import net.mcft.copy.betterstorage.utils.WorldUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet132TileEntityData;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.ForgeDirection;

public class TileEntityReinforcedChest extends TileEntityConnectable implements IInventory, ILockable {
	
	private ItemStack lock;
	private boolean powered;
	
	private static ForgeDirection[] neighbors = { ForgeDirection.EAST, ForgeDirection.NORTH,
	                                              ForgeDirection.WEST, ForgeDirection.SOUTH };
	
	@Override
	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getRenderBoundingBox() {
		return WorldUtils.getAABB(this, 0, 0, 0, 1, 1, 1);
	}
	
	@Override
	public ForgeDirection[] getPossibleNeighbors() { return neighbors; }
	@Override
	protected String getConnectableName() { return "container.reinforcedChest"; }
	
	@Override
	public boolean canConnect(TileEntityConnectable connectable) {
		if (!(connectable instanceof TileEntityReinforcedChest)) return false;
		TileEntityReinforcedChest chest = (TileEntityReinforcedChest)connectable;
		return (super.canConnect(connectable) &&
		        ((xCoord != chest.xCoord && (orientation == ForgeDirection.SOUTH || orientation == ForgeDirection.NORTH)) ||
		         (zCoord != chest.zCoord && (orientation == ForgeDirection.EAST || orientation == ForgeDirection.WEST))) &&
		        getLock() == null && chest.getLock() == null);
	}
	
	@Override
	public void dropContents() {
		super.dropContents();
		WorldUtils.dropStackFromBlock(worldObj, xCoord, yCoord, zCoord, lock);
	}
	
	// TileEntityContainer stuff

	@Override
	public int getColumns() { return Config.reinforcedChestColumns; }
	@Override
	public int getGuiId() { return (super.getGuiId() + getColumns() - 9); }
	@Override
	protected int getGuiColumns(int guiId) { return (guiId & ~1) + 9; }
	@Override
	protected int getGuiRows(int guiId) { return super.getGuiRows(guiId & 1); }
	
	// ILockable implementation
	
	@Override
	public ItemStack getLock() { return ((TileEntityReinforcedChest)getMain()).lock; }
	
	@Override
	public boolean isLockValid(ItemStack lock) { return (lock.getItem() instanceof ILock); }
	
	@Override
	public void setLock(ItemStack lock) {
		if (lock != null && !isLockValid(lock))
			throw new InvalidParameterException("Can't set lock to " + lock + ".");
		if (isMain()) {
			this.lock = lock;
			// Mark the block for an update, sends description packet to players.
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		} else ((TileEntityReinforcedChest)getMain()).setLock(lock);
	}
	
	@Override
	public boolean canUse(EntityPlayer player) { return (getPlayersUsing() > 0); }
	
	@Override
	public void applyTrigger() { setPowered(true); }
	
	// Trigger enchantment related
	
	/** Returns if the chest is emitting redstone. */
	public boolean isPowered() {
		return ((TileEntityReinforcedChest)getMain()).powered;
	}
	
	/** Sets if the chest is emitting redstone.
	 *  Updates all nearby blocks to make sure they notice it. */
	public void setPowered(boolean powered) {
		
		TileEntityReinforcedChest chest = (TileEntityReinforcedChest)getMain();
		if (chest != this) { chest.setPowered(powered); return; }
		
		if (this.powered == powered) return;
		this.powered = powered;
		
		int id = getBlockType().blockID;
		// Schedule a block update to turn the redstone signal back off.
		if (powered) worldObj.scheduleBlockUpdate(xCoord, yCoord, zCoord, id, 10);
		
		// Notify nearby blocks
		worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord, zCoord, id);
		worldObj.notifyBlocksOfNeighborChange(xCoord + 1, yCoord, zCoord, id);
		worldObj.notifyBlocksOfNeighborChange(xCoord - 1, yCoord, zCoord, id);
		worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord + 1, zCoord, id);
		worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord - 1, zCoord, id);
		worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord, zCoord + 1, id);
		worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord, zCoord - 1, id);
		
		// Notify nearby blocks of adjacent chest
		if (isConnected() && connected == ForgeDirection.EAST) {
			worldObj.notifyBlocksOfNeighborChange(xCoord + 2, yCoord, zCoord, id);
			worldObj.notifyBlocksOfNeighborChange(xCoord + 1, yCoord + 1, zCoord, id);
			worldObj.notifyBlocksOfNeighborChange(xCoord + 1, yCoord - 1, zCoord, id);
			worldObj.notifyBlocksOfNeighborChange(xCoord + 1, yCoord, zCoord + 1, id);
			worldObj.notifyBlocksOfNeighborChange(xCoord + 1, yCoord, zCoord - 1, id);
		}
		if (isConnected() && connected == ForgeDirection.SOUTH) {
			worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord, zCoord + 2, id);
			worldObj.notifyBlocksOfNeighborChange(xCoord + 1, yCoord, zCoord + 1, id);
			worldObj.notifyBlocksOfNeighborChange(xCoord - 1, yCoord, zCoord + 1, id);
			worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord + 1, zCoord + 1, id);
			worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord - 1, zCoord + 1, id);
		}
		
	}
	
	// TileEntity synchronization
	
	@Override
	public Packet getDescriptionPacket() {
		Packet132TileEntityData packet = (Packet132TileEntityData)super.getDescriptionPacket();
		NBTTagCompound compound = packet.customParam1;
		if (lock != null)
			compound.setCompoundTag("lock", lock.writeToNBT(new NBTTagCompound()));
        return packet;
	}
	@Override
	public void onDataPacket(INetworkManager net, Packet132TileEntityData packet) {
		super.onDataPacket(net, packet);
		NBTTagCompound compound = packet.customParam1;
		if (!compound.hasKey("lock")) lock = null;
		else lock = ItemStack.loadItemStackFromNBT(compound.getCompoundTag("lock"));
	}
	
	// Reading from / writing to NBT
	
	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		if (compound.hasKey("lock"))
			lock = ItemStack.loadItemStackFromNBT(compound.getCompoundTag("lock"));
	}
	@Override
	public void writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		if (lock != null)
			compound.setCompoundTag("lock", lock.writeToNBT(new NBTTagCompound("")));
	}
	
}
