package net.mcft.copy.betterstorage.misc;

import net.mcft.copy.betterstorage.BetterStorage;
import net.mcft.copy.betterstorage.network.packet.PacketBackpackIsOpen;
import net.mcft.copy.betterstorage.network.packet.PacketBackpackStack;
import net.mcft.copy.betterstorage.utils.NbtUtils;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;
import net.minecraftforge.common.util.Constants.NBT;

public class PropertiesBackpack implements IExtendedEntityProperties {
	
	public static final String identifier = Constants.modId + ".backpack";
	
	public ItemStack backpack = null;
	public ItemStack[] contents = null;
	
	/** If the backpack has been initialized for the entity yet. */
	public boolean initialized = false;
	/** When certain mobs spawn, they have a chance to
	 *  spawn with a backpack that contains some items. */
	public boolean spawnsWithBackpack = false;
	
	/** How many players are using the backpack.
	 *  On the client this is either 0 or 1. */
	public int playersUsing = 0;
	/** If the backpack contains any items (wearer only),
	 *  because the client doesn't have the contents. */
	public boolean hasItems = false;
	
	public float lidAngle = 0;
	public float prevLidAngle = 0;
	
	private ItemStack prevBackpack = null;
	private int prevPlayersUsing = 0;
	
	@Override
	public void init(Entity entity, World world) {  }
	
	@Override
	public void saveNBTData(NBTTagCompound compound) {
		if (contents == null) return;
		NBTTagCompound backpackCompound = new NBTTagCompound();
		backpackCompound.setInteger("count", contents.length);
		backpackCompound.setTag("Items", NbtUtils.writeItems(contents));
		if (backpack != null)
			backpackCompound.setTag("Stack", backpack.writeToNBT(new NBTTagCompound()));
		compound.setTag("Backpack", backpackCompound);
	}
	
	@Override
	public void loadNBTData(NBTTagCompound compound) {
		backpack = null;
		contents = null;
		if (compound.hasKey("Backpack")) {
			NBTTagCompound backpackCompound = compound.getCompoundTag("Backpack");
			backpack = ItemStack.loadItemStackFromNBT(backpackCompound.getCompoundTag("Stack"));
			contents = new ItemStack[backpackCompound.getInteger("count")];
			NbtUtils.readItems(contents, backpackCompound.getTagList("Items", NBT.TAG_COMPOUND));
		}
	}
	
	public void update(EntityLivingBase entity) {
		
		// Update equipped backpack animation.
		prevLidAngle = lidAngle;
		float lidSpeed = 0.2F;
		if (playersUsing > 0)
			lidAngle = Math.min(1.0F, lidAngle + lidSpeed);
		else lidAngle = Math.max(0.0F, lidAngle - lidSpeed);
		
		if (!entity.worldObj.isRemote) {
			
			// Play sound when equipped backpack opens / closes.
			String sound = Block.soundTypeSnow.getStepSound();
			if ((lidAngle > 0.0F) && (prevLidAngle <= 0.0F))
				entity.worldObj.playSoundEffect(entity.posX, entity.posY, entity.posZ, sound, 1.0F, 0.6F);
			if ((lidAngle < 0.2F) && (prevLidAngle >= 0.2F))
				entity.worldObj.playSoundEffect(entity.posX, entity.posY, entity.posZ, sound, 0.8F, 0.4F);
			
			boolean isOpen = (playersUsing > 0);
			if (isOpen != (prevPlayersUsing > 0)) {
				BetterStorage.networkChannel.sendToAndAllTracking(
						new PacketBackpackIsOpen(entity.getEntityId(), isOpen), entity);
				prevPlayersUsing = playersUsing;
			}
			if (!ItemStack.areItemStacksEqual(backpack, prevBackpack)) {
				BetterStorage.networkChannel.sendToAndAllTracking(
						new PacketBackpackStack(entity.getEntityId(), backpack), entity);
				prevBackpack = ItemStack.copyItemStack(backpack);
			}
			
		}
		
	}
	
	public void sendDataToPlayer(EntityLivingBase entity, EntityPlayer player) {
		// Sends any backpack data to the player.
		if (playersUsing > 0)
			BetterStorage.networkChannel.sendTo(
					new PacketBackpackIsOpen(entity.getEntityId(), true), player);
		if (backpack != null)
			BetterStorage.networkChannel.sendTo(
					new PacketBackpackStack(entity.getEntityId(), backpack), player);
	}
	
}
