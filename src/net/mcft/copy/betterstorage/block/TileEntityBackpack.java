package net.mcft.copy.betterstorage.block;

import net.mcft.copy.betterstorage.Config;
import net.mcft.copy.betterstorage.utils.StackUtils;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet132TileEntityData;

public class TileEntityBackpack extends TileEntityContainer {
	
	/** Stores the item while the backpack is a tile entity. */
	private ItemStack stack;
	
	/** Affects if items drop when the backpack is destroyed. */
	public boolean equipped = false;
	
	// TileEntityContainer stuff
	
	@Override
	public String getName() { return "container.backpack"; }

	@Override
	public int getRows() { return Config.backpackRows; }
	@Override
	public int getGuiId() { return getRows() - 1; }
	@Override
	protected int getGuiRows(int guiId) { return guiId + 1; }
	
	@Override
	public String getCustomTitle() {
		return StackUtils.get(stack, (String)null, "display", "Name");
	}
	
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
			worldObj.playSoundEffect(x, y, z, sound, 0.6F, 0.5F);
		// Play sound when closing
		if (lidAngle < 0.2F && prevLidAngle >= 0.2F)
			worldObj.playSoundEffect(x, y, z, sound, 0.4F, 0.3F);
	}
	
	// From and to ItemStack
	
	/** Creates an item from this backpack, to be dropped or equipped. */
	public ItemStack toItem() {
		if (equipped)
			StackUtils.setStackContents(stack, contents);
		return stack;
	}
	
	/** Fills the data of this backpack from the ItemStack. */
	public void fromItem(ItemStack stack) {
		this.stack = stack.copy();
		ItemStack[] itemContents = StackUtils.getStackContents(stack, contents.length);
		for (int i = 0; i < contents.length; i++)
			contents[i] = itemContents[i];
		StackUtils.remove(stack, "Items");
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
