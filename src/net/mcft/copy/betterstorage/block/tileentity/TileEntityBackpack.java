package net.mcft.copy.betterstorage.block.tileentity;

import net.mcft.copy.betterstorage.Config;
import net.mcft.copy.betterstorage.item.ItemBackpack;
import net.mcft.copy.betterstorage.misc.Constants;
import net.mcft.copy.betterstorage.utils.WorldUtils;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet132TileEntityData;
import net.minecraft.util.MovingObjectPosition;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileEntityBackpack extends TileEntityContainer {
	
	/** Backpacks despawn after 5 minutes if dropped
	 *  by a mob on death and there's no player nearby. */
	private int despawnTime = -1;
	
	public ItemStack stack;
	
	/** Affects if items drop when the backpack is destroyed. */
	public boolean equipped = false;
	
	// Equipping / unequipping
	
	public void equip(EntityLivingBase carrier) {
		equipped = true;
		ItemBackpack.setBackpack(carrier, stack, contents);
	}
	
	public void unequip(EntityLivingBase carrier, boolean despawn) {
		if (!worldObj.isRemote) {
			// Move items from the player backpack data to this tile entity.
			ItemStack[] items = ItemBackpack.getBackpackData(carrier).contents;
			if (items != null) System.arraycopy(items, 0, contents, 0, items.length);
			if (despawn) despawnTime = 0;
		}
		ItemBackpack.removeBackpack(carrier);
	}
	
	// TileEntityContainer stuff
	
	@Override
	public String getName() { return Constants.containerBackpack; }
	@Override
	public int getRows() { return Config.backpackRows; }
	
	@Override
	public boolean onBlockBreak(EntityPlayer player) {
		// This currently only runs on the server. Would be nice if it worked on
		// the client, but if the client thinks e's equipped the backpack, and it's
		// already gone on the server e doesn't have a way to tell the client.
		if (!worldObj.isRemote && player.isSneaking() && (player.getCurrentArmor(2) == null))
			equip(player);
		return super.onBlockBreak(player);
	}
	
	@Override
	public void dropContents() {
		// If the backpack was equipped instead
		// of just broken, don't drop anything.
		if (equipped) return;
		// Drop the backpack item, unless
		// player is in creative mode.
		if (!brokenInCreative)
			WorldUtils.dropStackFromBlock(this, stack);
		// Drop actual backpack contents.
		super.dropContents();
	}
	
	@Override
	public ItemStack onPickBlock(ItemStack block, MovingObjectPosition target) { return stack.copy(); }
	
	@Override
	@SideOnly(Side.CLIENT)
	public void onBlockRenderInInventory(ItemStack stack) { this.stack = stack; }
	
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
		if ((lidAngle > 0.0F) && (prevLidAngle <= 0.0F))
			worldObj.playSoundEffect(x, y, z, sound, 1.0F, 0.5F);
		// Play sound when closing
		if ((lidAngle < 0.2F) && (prevLidAngle >= 0.2F))
			worldObj.playSoundEffect(x, y, z, sound, 0.8F, 0.3F);
		
		if (despawnTime < 0) return;
		if (despawnTime++ > 20 * 60 * 5) {
			equipped = true; // Prevents stuff from being dropped.
			worldObj.setBlockToAir(xCoord, yCoord, zCoord);
		} else if (((despawnTime % 40) == 0) &&
		           (worldObj.getClosestPlayer(x, y, z, 24) != null))
			despawnTime = -1;
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
		NBTTagCompound compound = packet.data;
		stack = ItemStack.loadItemStackFromNBT(compound.getCompoundTag("stack"));
	}
	
	// Reading from / writing to NBT
	
	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		stack = ItemStack.loadItemStackFromNBT(compound.getCompoundTag("stack"));
		despawnTime = compound.getInteger("despawnTime");
		if (despawnTime == 0) despawnTime = -1;
	}
	@Override
	public void writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		compound.setCompoundTag("stack", stack.writeToNBT(new NBTTagCompound()));
		compound.setInteger("despawnTime", despawnTime);
	}
	
}
