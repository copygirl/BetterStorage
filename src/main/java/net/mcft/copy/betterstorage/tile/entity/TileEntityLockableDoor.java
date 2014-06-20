package net.mcft.copy.betterstorage.tile.entity;

import net.mcft.copy.betterstorage.api.lock.EnumLockInteraction;
import net.mcft.copy.betterstorage.api.lock.ILock;
import net.mcft.copy.betterstorage.api.lock.ILockable;
import net.mcft.copy.betterstorage.attachment.Attachments;
import net.mcft.copy.betterstorage.attachment.IHasAttachments;
import net.mcft.copy.betterstorage.attachment.LockAttachment;
import net.mcft.copy.betterstorage.utils.DirectionUtils;
import net.mcft.copy.betterstorage.utils.WorldUtils;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileEntityLockableDoor extends TileEntity implements ILockable, IHasAttachments {

	private Attachments attachments = new Attachments(this);	
	public LockAttachment lockAttachment;
	public ForgeDirection orientation;
	
	private boolean powered = false;
	private boolean swing = false;
	
	public float angle = 0F;
	public float prevAngle = 0F;
	public boolean isOpen = false;
	
	public TileEntityLockableDoor() {
		
		lockAttachment = attachments.add(LockAttachment.class);
		lockAttachment.setScale(0.5F, 1.5F);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getRenderBoundingBox() {
		return WorldUtils.getAABB(this, 0, 0, 0, 0, 1, 0);
	}
	
	private void updateLockPosition() {
		//Maybe we should use the orientation that the attachment has by itself.
		switch (orientation) {
		case WEST:
			if (isOpen) lockAttachment.setBox(12.5, -1.5, 1.5, 5, 6, 3);
			else lockAttachment.setBox(1.5, -1.5, 12.5, 3, 6, 5);
			break;
		case EAST:
			if (isOpen) lockAttachment.setBox(3.5, -1.5, 14.5, 5, 6, 3);
			else lockAttachment.setBox(14.5, -1.5, 3.5, 3, 6, 5);
			break;
		case SOUTH:
			if (isOpen) lockAttachment.setBox(1.5, -1.5, 3.5, 3, 6, 5);
			else lockAttachment.setBox(12.5, -1.5, 14.5, 5, 6, 3);
			break;
		default:
			if (isOpen) lockAttachment.setBox(14.5, -1.5, 12.5, 3, 6, 5);
			else lockAttachment.setBox(3.5, -1.5, 1.5, 5, 6, 3);
			break;
		}		
	}
	
	@Override
	public Attachments getAttachments() {
		return attachments;
	}

	@Override
	public ItemStack getLock() {
		return lockAttachment.getItem();
	}

	@Override
	public boolean isLockValid(ItemStack lock) {
		return (lock == null) || (lock.getItem() instanceof ILock);
	}

	@Override
	public void setLock(ItemStack lock) {
		lockAttachment.setItem(lock);
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		markDirty();
	}

	@Override
	public boolean canUse(EntityPlayer player) {
		return isOpen || getLock() == null;
	}

	@Override
	public void useUnlocked(EntityPlayer player) {
		isOpen = !isOpen;
		player.worldObj.addBlockEvent(xCoord, yCoord, zCoord, getBlockType(), 0, isOpen ? 1 : 0);
		updateLockPosition();
	}

	@Override
	public void applyTrigger() {
		setPowered(true);
	}
	
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase player, ItemStack stack) {
		orientation = DirectionUtils.getOrientation(player).getOpposite();
		updateLockPosition();
	}
	
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
		if(world.isRemote) return true;
		if(canUse(player)) useUnlocked(player);
		else ((ILock)getLock().getItem()).applyEffects(getLock(), this, player, EnumLockInteraction.OPEN);
		return true;
	}
	
	@Override
	public boolean receiveClientEvent(int eventID, int par) {
		worldObj.playAuxSFX(1003, xCoord, yCoord, zCoord, 0);
		swing = true;
		isOpen = par == 1;
		updateLockPosition();
		return true;
	}

	@Override
	public void updateEntity() 
	{	
		attachments.update();
		prevAngle = angle;
		if (swing) {
			if (isOpen) {
				angle = Math.min(1.0F, angle + 0.2F);
				if (angle == 1.0F) {
					swing = false;
				}
			} else {
				angle = Math.max(0.0F, angle - 0.2F);
				if (angle == 0.0F) {
					swing = false;
				}
			}			
		}	
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		isOpen = compound.getBoolean("isOpen");
		orientation = ForgeDirection.getOrientation(compound.getByte("orientation"));
		if (compound.hasKey("lock")) lockAttachment.setItem(ItemStack.loadItemStackFromNBT(compound.getCompoundTag("lock")));
		updateLockPosition();
	}
	
	@Override
	public void writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		compound.setBoolean("isOpen", isOpen);
		compound.setByte("orientation", (byte) orientation.ordinal());
		if (lockAttachment.getItem() != null) compound.setTag("lock", lockAttachment.getItem().writeToNBT(new NBTTagCompound()));
	}

	@Override
	public Packet getDescriptionPacket() {
		NBTTagCompound compound = new NBTTagCompound();
		compound.setBoolean("isOpen", isOpen);
		compound.setByte("orientation", (byte) orientation.ordinal());
		if (lockAttachment.getItem() != null) compound.setTag("lock", lockAttachment.getItem().writeToNBT(new NBTTagCompound()));
		return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 0, compound);
	}

	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
		super.onDataPacket(net, pkt);
		NBTTagCompound compound = pkt.func_148857_g();
		if (!compound.hasKey("lock")) lockAttachment.setItem(null);
		else lockAttachment.setItem(ItemStack.loadItemStackFromNBT(compound.getCompoundTag("lock")));
		orientation = ForgeDirection.getOrientation(compound.getByte("orientation"));
		isOpen = compound.getBoolean("isOpen");
		angle = isOpen ? 1.0F : 0.0F;
		prevAngle = angle;
		updateLockPosition();
	}

	public boolean isPowered() {
		return powered;
	}
	
	public void setPowered(boolean powered) {
		
		if (this.powered == powered) return;
		this.powered = powered;
		
		if (powered) worldObj.scheduleBlockUpdate(xCoord, yCoord, zCoord, getBlockType(), 10);
		WorldUtils.notifyBlocksAround(worldObj, xCoord, yCoord, zCoord);
		WorldUtils.notifyBlocksAround(worldObj, xCoord, yCoord + 1, zCoord);
	}	
	
}
