package net.mcft.copy.betterstorage.tile.entity;

import net.mcft.copy.betterstorage.addon.armourersworkshop.AWAddon;
import net.mcft.copy.betterstorage.misc.Constants;
import net.mcft.copy.betterstorage.utils.WorldUtils;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S2FPacketSetSlot;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.common.util.Constants.NBT;
import riskyken.armourersWorkshop.api.common.equipment.IEntityEquipment;
import riskyken.armourersWorkshop.api.common.equipment.armour.EnumEquipmentType;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileEntityArmorStand extends TileEntityContainer {

	private ItemStack[] vanillaArmor = new ItemStack[4];
	private int[] awArmor = new int[5];
	
	public int rotation = 0;
	
	@Override
	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getRenderBoundingBox() {
		return WorldUtils.getAABB(this, 0, 0, 0, 0, 1, 0);
	}
	
	public ItemStack[] getVanillaArmor() {
		return vanillaArmor;
	}
	
	public int[] getAWArmor() {
		return awArmor;
	}
	
	public Object getFirstArmorPiece(int id) {
		if(AWAddon.isLoaded) {
			if(id == 1 && awArmor[4] != 0) return awArmor[4];
			if(awArmor[id] != 0) return awArmor[id];
		}
		return vanillaArmor[id];
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
		
		if (player.isSneaking()) {		
			if (AWAddon.isLoaded) {				
				EnumEquipmentType type = EnumEquipmentType.NONE;
				IEntityEquipment equipment = AWAddon.dataHandler.getCustomEquipmentForEntity(player);
				
				switch (3 - slot) {
				case 0:
					type = EnumEquipmentType.HEAD;
					break;
				case 1:
					type = EnumEquipmentType.CHEST;
					break;
				case 2:
					type = EnumEquipmentType.LEGS;
					break;
				case 3:
					type = EnumEquipmentType.FEET;
					break;
				}
				
				int a1 = awArmor[slot];
				int a2 = slot == 1 ? awArmor[4] : 0;
				int a3 = equipment.haveEquipment(type) ? 
					equipment.getEquipmentId(type) : 0;
				int a4 = slot == 1 ? 
					equipment.haveEquipment(EnumEquipmentType.SKIRT) ? 
					equipment.getEquipmentId(EnumEquipmentType.SKIRT) : 0 : 0;
				
				awArmor[slot] = a3;
				if (slot == 1) awArmor[4] = a4;
				
				if (type != EnumEquipmentType.NONE) equipment.addEquipment(type, a1);
				else equipment.removeEquipment(type);
				if (slot == 1) {
					if(a2 != 0) equipment.addEquipment(EnumEquipmentType.SKIRT, a2);
					else equipment.removeEquipment(EnumEquipmentType.SKIRT);
				}
				AWAddon.dataHandler.setCustomEquipmentOnEntity(player, equipment);
			}
			
			ItemStack playerArmor = player.getCurrentArmor(slot);
			ItemStack armor = vanillaArmor[slot];
			
			vanillaArmor[slot] = playerArmor;
			player.inventory.armorInventory[slot] = armor;
			
			((EntityPlayerMP)player).playerNetServerHandler.sendPacket(new S2FPacketSetSlot(player.openContainer.windowId, 8 - slot, armor));
			
			markForUpdate();
			markDirty();
		} else {
			Object armor = getFirstArmorPiece(slot);
			ItemStack holding = player.getCurrentEquippedItem();
			boolean replaced = false;
			
			boolean isAWArmor = false;
			if(AWAddon.isLoaded && holding != null) {
				if (AWAddon.dataHandler.hasItemStackGotEquipmentData(holding))
					isAWArmor = true;	
			}
			
			if (armor instanceof ItemStack && !isAWArmor) {
				vanillaArmor[slot] = null;
				player.inventory.mainInventory[player.inventory.currentItem] = (ItemStack) armor;
				replaced = true;
			}
			else if (armor instanceof Integer && AWAddon.isLoaded && (isAWArmor || holding == null)) {
				EnumEquipmentType type = AWAddon.dataHandler.getEquipmentType((Integer) armor);
				if (type == EnumEquipmentType.SKIRT) awArmor[4] = 0;
				else awArmor[slot] = 0;
				player.inventory.mainInventory[player.inventory.currentItem] = AWAddon.dataHandler.getCustomEquipmentItemStack((Integer) armor);
				replaced = true;
			}
			if ((holding != null) && AWAddon.validateArmor(player, holding, slot)) {
				if (isAWArmor) {
					int id = AWAddon.dataHandler.getEquipmentIdFromItemStack(holding);
					EnumEquipmentType type = AWAddon.dataHandler.getEquipmentType(id);
					if (type == EnumEquipmentType.SKIRT) awArmor[4] = id;
					else awArmor[slot] = id;		
				}
				else vanillaArmor[slot] = holding;
				if(!replaced) player.inventory.mainInventory[player.inventory.currentItem] = null;
			}
			
			markForUpdate();
			markDirty();
		}	
		return true;		
	}
	
	@Override
	public ItemStack onPickBlock(ItemStack block, MovingObjectPosition target) {
		int y = (int)((target.hitVec.yCoord - yCoord) * 2);
		ItemStack result = null;
		if ((y >= 0) && (y < 4)) {
			Object armor = getFirstArmorPiece(y);
			if (AWAddon.isLoaded && armor instanceof Integer) {
				result = AWAddon.dataHandler.getCustomEquipmentItemStack((Integer) armor);
			}
			else if (armor instanceof ItemStack)
				result = (ItemStack) armor;
		}
		return ((result != null) ? result.copy() : block);
	}
	
	@Override
	public void dropContents() {
		for (ItemStack stack : vanillaArmor)
			WorldUtils.dropStackFromBlock(worldObj, xCoord, yCoord, zCoord, stack);
		if (AWAddon.isLoaded) {
			for (int a : awArmor) {
				if (a != 0) {
					ItemStack stack = AWAddon.dataHandler.getCustomEquipmentItemStack(a);
					WorldUtils.dropStackFromBlock(worldObj, xCoord, yCoord, zCoord, stack);
				}
			}
		}
	}
	
	@Override
	protected int getComparatorSignalStengthInternal() {
		int count = 0;
		for (ItemStack stack : vanillaArmor)
			if (stack != null) count++;
		if (AWAddon.isLoaded) {
			for (int a : awArmor)
				if (a != 0) count++;
		}
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
		vanillaArmor = new ItemStack[vanillaArmor.length];
		for (int i = 0; i < items.tagCount(); i++) {
			NBTTagCompound item = items.getCompoundTagAt(i);
			int slot = item.getByte("Slot") & 255;
			if (slot >= 0 && slot < vanillaArmor.length)
				vanillaArmor[slot] = ItemStack.loadItemStackFromNBT(item);
		}
		if (compound.hasKey("awArmor") && AWAddon.isLoaded) {
			awArmor = compound.getIntArray("awArmor");
		}
	}
	
	public void write(NBTTagCompound compound) {
		compound.setByte("rotation", (byte)rotation);
		NBTTagList list = new NBTTagList();
		for (int i = 0; i < vanillaArmor.length; i++) {
			if (vanillaArmor[i] == null) continue;
			NBTTagCompound item = new NBTTagCompound();
			item.setByte("Slot", (byte)i);
			vanillaArmor[i].writeToNBT(item);
			list.appendTag(item);
		}
		compound.setTag("Items", list);
		if(AWAddon.isLoaded) {
			compound.setIntArray("awArmor", awArmor);
		}
	}	
}
