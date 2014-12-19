package net.mcft.copy.betterstorage.tile.entity;

import net.mcft.copy.betterstorage.BetterStorage;
import net.mcft.copy.betterstorage.config.GlobalConfig;
import net.mcft.copy.betterstorage.item.ItemBackpack;
import net.mcft.copy.betterstorage.misc.Constants;
import net.mcft.copy.betterstorage.misc.PropertiesBackpack;
import net.mcft.copy.betterstorage.utils.WorldUtils;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

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
		if (worldObj.isRemote) return;
		// Move items from the player backpack data to this tile entity.
		PropertiesBackpack backpackData = ItemBackpack.getBackpackData(carrier);
		if (backpackData.contents != null) {
			System.arraycopy(backpackData.contents, 0, contents, 0, Math.min(contents.length, backpackData.contents.length));
			backpackData.contents = null;
		}
		if (despawn) despawnTime = 0;
	}
	
	// TileEntityContainer stuff
	
	@Override
	public String getName() { return Constants.containerBackpack; }
	@Override
	public int getRows() { return BetterStorage.globalConfig.getInteger(GlobalConfig.backpackRows); }
	@Override
	protected boolean doesSyncPlayers() { return true; }
	
	@Override
	public boolean onBlockBreak(EntityPlayer player) {
		// This currently only runs on the server. Would be nice if it worked on
		// the client, but if the client thinks e's equipped the backpack, and it's
		// already gone on the server e doesn't have a way to tell the client.
		if (!worldObj.isRemote && player.isSneaking() &&
		    ItemBackpack.canEquipBackpack(player))
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
	public ItemStack onPickBlock(ItemStack block, MovingObjectPosition target) {
		return ItemStack.copyItemStack(stack);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void onBlockRenderAsItem(ItemStack stack) { this.stack = stack; }
	
	// Update entity
	
	@Override
	protected float getLidSpeed() { return 0.2F; }
	
	@Override
	public void update() {
		super.update();
		
		double x = getPos().getX() + 0.5;
		double y = getPos().getY() + 0.5;
		double z = getPos().getZ() + 0.5;
		
		String sound = Block.soundTypeSnow.getStepSound();
		// Play sound when opening
		if ((lidAngle > 0.0F) && (prevLidAngle <= 0.0F))
			worldObj.playSoundEffect(x, y, z, sound, 1.0F, 0.6F);
		// Play sound when closing
		if ((lidAngle < 0.2F) && (prevLidAngle >= 0.2F))
			worldObj.playSoundEffect(x, y, z, sound, 0.8F, 0.4F);
		
		if (despawnTime < 0) return;
		if (despawnTime++ > 20 * 60 * 5) {
			equipped = true; // Prevents stuff from being dropped.
			worldObj.setBlockToAir(getPos());
		} else if (((despawnTime % 40) == 0) &&
		           (worldObj.getClosestPlayer(x, y, z, 24) != null))
			despawnTime = -1;
	}
	
	// Tile entity synchronization
	
	@Override
	public Packet getDescriptionPacket() {
		NBTTagCompound compound = new NBTTagCompound();
		compound.setTag("stack", stack.writeToNBT(new NBTTagCompound()));
		return new S35PacketUpdateTileEntity(getPos(), 0, compound);
	}
	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity packet) {
		NBTTagCompound compound = packet.getNbtCompound();
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
		compound.setTag("stack", stack.writeToNBT(new NBTTagCompound()));
		compound.setInteger("despawnTime", despawnTime);
	}
	
}
