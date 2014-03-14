package net.mcft.copy.betterstorage.tile.entity;

import net.mcft.copy.betterstorage.misc.Constants;
import net.mcft.copy.betterstorage.utils.WorldUtils;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.common.util.Constants.NBT;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileEntityArmorStand extends TileEntityContainer {

	public ItemStack[] armor = new ItemStack[4];
	public int rotation = 0;
	
	@Override
	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getRenderBoundingBox() {
		return WorldUtils.getAABB(this, 0, 0, 0, 0, 1, 0);
	}
	
	// TileEntityContainer stuff
	
	@Override
	public String getName() { return Constants.containerArmorStand; }
	@Override
	public boolean canSetCustomTitle() { return false; }
	
	@Override
	protected int getSizeContents() { return 0; }
	
	@Override
	public void onBlockPlaced(EntityLivingBase player, ItemStack stack) {
		super.onBlockPlaced(player, stack);
		rotation = Math.round((player.rotationYawHead + 180) * 16 / 360);
	}
	
	@Override
	public boolean onBlockActivated(EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
		
		int slot = Math.min(3, (int)(hitY * 2));
		
		ItemStack item = armor[slot];
		ItemStack holding = player.getCurrentEquippedItem();
		ItemStack playerArmor = player.inventory.armorInventory[slot];
		
		if (player.isSneaking()) {
			if (((item != null) || (playerArmor != null)) &&
			    ((playerArmor == null) || playerArmor.getItem().isValidArmor(playerArmor, 3 - slot, player))) {
				armor[slot] = playerArmor;
				player.inventory.armorInventory[slot] = item;
				// Shouldn't this be done automatically?
				//((EntityPlayerMP)player).playerNetServerHandler.sendPacket(
				//		new S2FPacketSetSlot(player.openContainer.windowId, 8 - slot, item));
				markForUpdate();
				markDirty();
			}
		} else if (((item != null) && (holding == null)) ||
		           ((holding != null) && holding.getItem().isValidArmor(holding, 3 - slot, player))) {
			armor[slot] = holding;
			player.inventory.mainInventory[player.inventory.currentItem] = item;
			markForUpdate();
			markDirty();
		}
		
		return true;
		
	}
	@Override
	public ItemStack onPickBlock(ItemStack block, MovingObjectPosition target) {
		int y = (int)((target.hitVec.yCoord - yCoord) * 2);
		ItemStack result = (((y >= 0) && (y < 4)) ? armor[y] : null);
		return ((result != null) ? result.copy() : block);
	}
	
	@Override
	public void dropContents() {
		for (ItemStack stack : armor)
			WorldUtils.dropStackFromBlock(worldObj, xCoord, yCoord, zCoord, stack);
	}
	
	@Override
	protected int getComparatorSignalStengthInternal() {
		int count = 0;
		for (ItemStack stack : armor)
			if (stack != null) count++;
		return count;
	}
	
	// TileEntity synchronization
	
	@Override
	public Packet getDescriptionPacket() {
		NBTTagCompound compound = new NBTTagCompound();
		write(compound);
        return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 0, compound);
	}
	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity packet) {
		read(packet.func_148857_g());
	}
	
	// Reading from / writing to NBT
	
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
		NBTTagList items = compound.getTagList("Items", NBT.TAG_COMPOUND);
		armor = new ItemStack[armor.length];
		for (int i = 0; i < items.tagCount(); i++) {
			NBTTagCompound item = items.getCompoundTagAt(i);
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
	
}
