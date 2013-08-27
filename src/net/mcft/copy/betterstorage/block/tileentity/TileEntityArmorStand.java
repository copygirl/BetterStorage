package net.mcft.copy.betterstorage.block.tileentity;

import net.mcft.copy.betterstorage.utils.WorldUtils;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet132TileEntityData;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileEntityArmorStand extends TileEntity {

	public ItemStack[] armor = new ItemStack[4];
	public int rotation = 0;
	public int tickCounter = 0;
	
	@Override
	public void updateEntity() {
		tickCounter++;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getRenderBoundingBox() {
		return WorldUtils.getAABB(this, 0, 0, 0, 0, 1, 0);
	}
	
	@Override
	public Packet getDescriptionPacket() {
		NBTTagCompound compound = new NBTTagCompound();
		write(compound);
        return new Packet132TileEntityData(xCoord, yCoord, zCoord, 0, compound);
	}
	@Override
	public void onDataPacket(INetworkManager net, Packet132TileEntityData packet) {
		NBTTagCompound compound = packet.customParam1;
		read(compound);
	}
	
	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		read(compound);
	}
	@Override
	public void writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		write(compound);
	}
	
	public void read(NBTTagCompound compound) {
		rotation = compound.getByte("rotation");
		NBTTagList items = compound.getTagList("Items");
		armor = new ItemStack[armor.length];
		for (int i = 0; i < items.tagCount(); i++) {
			NBTTagCompound item = (NBTTagCompound)items.tagAt(i);
			int slot = item.getByte("Slot") & 255;
			if (slot >= 0 && slot < armor.length)
				armor[slot] = ItemStack.loadItemStackFromNBT(item);
		}
	}
	public void write(NBTTagCompound compound) {
		compound.setByte("rotation", (byte)rotation);
		NBTTagList list = new NBTTagList();
		for (int i = 0; i < armor.length; i++) {
			if (armor[i] == null) continue;
			NBTTagCompound item = new NBTTagCompound();
			item.setByte("Slot", (byte)i);
			armor[i].writeToNBT(item);
			list.appendTag(item);
		}
		compound.setTag("Items", list);
	}
	
	// Dropping stuff
	
	public void dropContents() {
		for (ItemStack stack : armor)
			WorldUtils.dropStackFromBlock(worldObj, xCoord, yCoord, zCoord, stack);
	}
	
}
