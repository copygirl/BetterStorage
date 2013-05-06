package net.mcft.copy.betterstorage.block;

import net.mcft.copy.betterstorage.Config;
import net.mcft.copy.betterstorage.item.ItemBackpack;
import net.mcft.copy.betterstorage.misc.Constants;
import net.mcft.copy.betterstorage.utils.StackUtils;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet132TileEntityData;

public class TileEntityBackpack extends TileEntityContainer {
	
	public ItemStack stack;
	
	/** Affects if items drop when the backpack is destroyed. */
	public boolean equipped = false;
	
	public String getTexture() {
		if (stack == null) return Constants.backpackTexture;
		return ((ItemBackpack)stack.getItem()).getArmorTexture(stack, null, 0, 0);
	}
	
	// Equipping / unequipping
	
	public boolean equip(EntityPlayer player) {
		equipped = true;
		equipped = worldObj.setBlockToAir(xCoord, yCoord, zCoord);
		if (equipped) ItemBackpack.setBackpack(player, stack, contents);
		return equipped;
	}
	
	public void unequip(EntityPlayer player) {
		StackUtils.remove(stack, "hasItems");
		if (!worldObj.isRemote) {
			ItemStack[] backpackData = ItemBackpack.getBackpackData(player).contents;
			
			// For compatibility with previous versions:
			// If the stack still has the items tag,
			// use its items instead and remove the tag.
			if (StackUtils.has(stack, "Items")) {
				backpackData = StackUtils.getStackContents(stack, contents.length);
				StackUtils.remove(stack, "Items");
			}
			
			if (backpackData != null)
				System.arraycopy(backpackData, 0, contents, 0, backpackData.length);
		}
		ItemBackpack.removeBackpack(player);
	}
	
	// TileEntityContainer stuff
	
	@Override
	public String getName() { return "container.backpack"; }
	@Override
	public int getRows() { return Config.backpackRows; }
	
	// Update entity
	
	@Override
	protected float getLidSpeed() { return 0.2F; }
	
	@Override
	public void updateEntity() {
		super.updateEntity();
		
		double x = xCoord + 0.5;
		double y = yCoord + 0.5;
		double z = zCoord + 0.5;
		
		String sound = Block.soundSnowFootstep.getStepSound();
		// Play sound when opening
		if (lidAngle > 0.0F && prevLidAngle <= 0.0F)
			worldObj.playSoundEffect(x, y, z, sound, 1.0F, 0.5F);
		// Play sound when closing
		if (lidAngle < 0.2F && prevLidAngle >= 0.2F)
			worldObj.playSoundEffect(x, y, z, sound, 0.8F, 0.3F);
	}
	
	// Tile entity synchronization
	
	@Override
	public Packet getDescriptionPacket() {
		NBTTagCompound compound = new NBTTagCompound();
		compound.setCompoundTag("stack", stack.writeToNBT(new NBTTagCompound()));
        return new Packet132TileEntityData(xCoord, yCoord, zCoord, 0, compound);
	}
	@Override
	public void onDataPacket(INetworkManager net, Packet132TileEntityData packet) {
		NBTTagCompound compound = packet.customParam1;
		stack = ItemStack.loadItemStackFromNBT(compound.getCompoundTag("stack"));
	}
	
	// Reading from / writing to NBT
	
	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		stack = ItemStack.loadItemStackFromNBT(compound.getCompoundTag("stack"));
	}
	@Override
	public void writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		compound.setCompoundTag("stack", stack.writeToNBT(new NBTTagCompound()));
	}
	
}
