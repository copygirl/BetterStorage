package net.mcft.copy.betterstorage.tile.entity;

import net.mcft.copy.betterstorage.api.lock.ILockable;
import net.mcft.copy.betterstorage.attachment.Attachments;
import net.mcft.copy.betterstorage.attachment.IHasAttachments;
import net.mcft.copy.betterstorage.attachment.LockAttachment;
import net.mcft.copy.betterstorage.utils.WorldUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileEntityLockableDoor extends TileEntity implements ILockable, IHasAttachments {

	private Attachments attachments = new Attachments(this);	
	private LockAttachment lockAttachment;
	
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
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setLock(ItemStack lock) {
		lockAttachment.setItem(lock);
	}

	@Override
	public boolean canUse(EntityPlayer player) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void useUnlocked(EntityPlayer player) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void applyTrigger() {
		// TODO Auto-generated method stub
		
	}
	
	public void onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {	
		isOpen = !isOpen;
		world.addBlockEvent(x, y, z, getBlockType(), 0, isOpen ? 1 : 0);
	}

	@Override
	public boolean receiveClientEvent(int eventID, int par) {
		swing = true;
		isOpen = par == 1;
		return true;
	}

	private boolean swing = false;
	
	@Override
	public void updateEntity() 
	{	
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
		if (compound.hasKey("lock")) lockAttachment.setItem(ItemStack.loadItemStackFromNBT(compound.getCompoundTag("lock")));
	}
	
	@Override
	public void writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		compound.setBoolean("isOpen", isOpen);
		if (lockAttachment.getItem() != null) compound.setTag("lock", lockAttachment.getItem().writeToNBT(new NBTTagCompound()));
	}

	@Override
	public Packet getDescriptionPacket() {
		NBTTagCompound compound = new NBTTagCompound();
		compound.setBoolean("isOpen", isOpen);
		return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 0, compound);
	}

	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
		super.onDataPacket(net, pkt);
		NBTTagCompound compound = pkt.func_148857_g();
		isOpen = compound.getBoolean("isOpen");
		angle = isOpen ? 1.0F : 0.0F;
		prevAngle = angle;
	}	
	
}
