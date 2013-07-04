package net.mcft.copy.betterstorage.block.tileentity;

import java.security.InvalidParameterException;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.mcft.copy.betterstorage.Config;
import net.mcft.copy.betterstorage.api.ILock;
import net.mcft.copy.betterstorage.api.ILockable;
import net.mcft.copy.betterstorage.attachment.Attachments;
import net.mcft.copy.betterstorage.attachment.IHasAttachments;
import net.mcft.copy.betterstorage.attachment.LockAttachment;
import net.mcft.copy.betterstorage.block.ChestMaterial;
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

public class TileEntityReinforcedChest extends TileEntityConnectable
                                       implements IInventory, ILockable, IHasAttachments {
	
	private boolean powered;

	private Attachments attachments = new Attachments(this);
	private LockAttachment lockAttachment;
	
	private ItemStack getLockInternal() { return lockAttachment.getItem(); }
	private void setLockInternal(ItemStack lock) { lockAttachment.setItem(lock); }
	
	public TileEntityReinforcedChest() {
		lockAttachment = attachments.add(LockAttachment.class);
		lockAttachment.setBox(8, 6.5, 0.5, 7, 7, 1);
		lockAttachment.setScale(0.5F, 1.5F);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getRenderBoundingBox() {
		return WorldUtils.getAABB(this, 0, 0, 0, 1, 1, 1);
	}
	
	@SideOnly(Side.CLIENT)
	public String getTexture() {
		return ChestMaterial.get(getBlockMetadata()).getTexture(isConnected());
	}
	
	// Attachment points
	
	@Override
	public Attachments getAttachments() { return attachments; }
	
	@Override
	public void setOrientation(ForgeDirection orientation) {
		super.setOrientation(orientation);
		lockAttachment.setDirection(orientation);
	}
	
	@Override
	public void updateEntity() {
		super.updateEntity();
		attachments.update();
	}
	
	// TileEntityContainer stuff

	@Override
	public int getColumns() { return Config.reinforcedChestColumns; }
	
	@Override
	public void dropContents() {
		super.dropContents();
		WorldUtils.dropStackFromBlock(worldObj, xCoord, yCoord, zCoord, getLockInternal());
	}
	
	// TileEntityConnactable stuff
	
	private static ForgeDirection[] neighbors = { ForgeDirection.EAST, ForgeDirection.NORTH,
	                                              ForgeDirection.WEST, ForgeDirection.SOUTH };
	
	@Override
	protected String getConnectableName() { return "container.reinforcedChest"; }
	
	@Override
	protected boolean isAccessible() { return (getLock() == null); }
	
	@Override
	public ForgeDirection[] getPossibleNeighbors() { return neighbors; }
	
	@Override
	public boolean canConnect(TileEntityConnectable connectable) {
		if (!(connectable instanceof TileEntityReinforcedChest)) return false;
		TileEntityReinforcedChest chest = (TileEntityReinforcedChest)connectable;
		return (super.canConnect(connectable) &&
		        (((xCoord != chest.xCoord) && ((getOrientation() == ForgeDirection.SOUTH) ||
		                                       (getOrientation() == ForgeDirection.NORTH))) ||
		         ((zCoord != chest.zCoord) && ((getOrientation() == ForgeDirection.EAST) ||
		                                       (getOrientation() == ForgeDirection.WEST)))) &&
		        (getLock() == null) && (chest.getLock() == null));
	}
	
	// ILockable implementation
	
	@Override
	public ItemStack getLock() { return ((TileEntityReinforcedChest)getMain()).getLockInternal(); }
	
	@Override
	public boolean isLockValid(ItemStack lock) { return (lock.getItem() instanceof ILock); }
	
	@Override
	public void setLock(ItemStack lock) {
		if ((lock != null) && !isLockValid(lock))
			throw new InvalidParameterException("Can't set lock to " + lock + ".");
		if (isMain()) {
			setLockInternal(lock);
			// Mark the block for an update, sends description packet to players.
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		} else ((TileEntityReinforcedChest)getMain()).setLock(lock);
	}
	
	@Override
	public boolean canUse(EntityPlayer player) { return (getPlayersUsing() > 0); }
	
	@Override
	public void useUnlocked(EntityPlayer player) {
		openGui(player);
	}
	
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
		if (isConnected() && (getConnected() == ForgeDirection.EAST)) {
			worldObj.notifyBlocksOfNeighborChange(xCoord + 2, yCoord, zCoord, id);
			worldObj.notifyBlocksOfNeighborChange(xCoord + 1, yCoord + 1, zCoord, id);
			worldObj.notifyBlocksOfNeighborChange(xCoord + 1, yCoord - 1, zCoord, id);
			worldObj.notifyBlocksOfNeighborChange(xCoord + 1, yCoord, zCoord + 1, id);
			worldObj.notifyBlocksOfNeighborChange(xCoord + 1, yCoord, zCoord - 1, id);
		}
		if (isConnected() && (getConnected() == ForgeDirection.SOUTH)) {
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
		ItemStack lock = getLockInternal();
		if (lock != null)
			compound.setCompoundTag("lock", lock.writeToNBT(new NBTTagCompound()));
        return packet;
	}
	@Override
	public void onDataPacket(INetworkManager net, Packet132TileEntityData packet) {
		super.onDataPacket(net, packet);
		NBTTagCompound compound = packet.customParam1;
		if (!compound.hasKey("lock")) setLockInternal(null);
		else setLockInternal(ItemStack.loadItemStackFromNBT(compound.getCompoundTag("lock")));
	}
	
	// Reading from / writing to NBT
	
	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		if (compound.hasKey("lock"))
			setLockInternal(ItemStack.loadItemStackFromNBT(compound.getCompoundTag("lock")));
	}
	@Override
	public void writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		ItemStack lock = getLockInternal();
		if (lock != null)
			compound.setCompoundTag("lock", lock.writeToNBT(new NBTTagCompound("")));
	}
	
}
