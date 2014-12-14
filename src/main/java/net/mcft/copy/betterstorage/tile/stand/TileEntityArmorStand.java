package net.mcft.copy.betterstorage.tile.stand;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import net.mcft.copy.betterstorage.api.stand.ArmorStandEquipHandler;
import net.mcft.copy.betterstorage.api.stand.BetterStorageArmorStand;
import net.mcft.copy.betterstorage.api.stand.EnumArmorStandRegion;
import net.mcft.copy.betterstorage.api.stand.IArmorStand;
import net.mcft.copy.betterstorage.misc.Constants;
import net.mcft.copy.betterstorage.misc.EquipmentSlot;
import net.mcft.copy.betterstorage.tile.entity.TileEntityContainer;
import net.mcft.copy.betterstorage.utils.WorldUtils;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TileEntityArmorStand extends TileEntityContainer implements IArmorStand {
	
	private Map<EnumArmorStandRegion, Map<ArmorStandEquipHandler, ItemStack>> equipment =
			new HashMap<EnumArmorStandRegion, Map<ArmorStandEquipHandler, ItemStack>>();
	
	public int rotation = 0;
	
	public TileEntityArmorStand() {
		for (EnumArmorStandRegion region : EnumArmorStandRegion.values())
			equipment.put(region, new HashMap<ArmorStandEquipHandler, ItemStack>());
	}
	
	private void clearItems() {
		for (EnumArmorStandRegion region : EnumArmorStandRegion.values())
			equipment.get(region).clear();
	}
	
	// IArmorStand implementation
	
	@Override
	public ItemStack getItem(ArmorStandEquipHandler handler) {
		return equipment.get(handler.region).get(handler);
	}
	
	@Override
	public void setItem(ArmorStandEquipHandler handler, ItemStack item) {
		Map<ArmorStandEquipHandler, ItemStack> items = equipment.get(handler.region);
		if (item == null) items.remove(handler);
		else items.put(handler, item);
		
		if (worldObj != null) {
			markForUpdate();
			markDirty();
		}
	}
	
	// TileEntity stuff
	
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
	public boolean onBlockActivated(EntityPlayer player, EnumFacing side, float hitX, float hitY, float hitZ) {
		if (worldObj.isRemote) return true;
		
		int slot = Math.max(0, Math.min(3, (int)(hitY * 2)));
		EnumArmorStandRegion region = EnumArmorStandRegion.values()[slot];
		
		for (ArmorStandEquipHandler handler : BetterStorageArmorStand.getEquipHandlers(region)) {
			ItemStack item = getItem(handler);
			if (player.isSneaking()) {
				
				// Swap player's equipped armor with armor stand's.
				ItemStack equipped = handler.getEquipment(player);
				if (((item == null) && (equipped == null)) ||
				    ((item != null) && !handler.isValidItem(player, item)) ||
				    ((equipped != null) && !handler.isValidItem(player, equipped)) ||
				    !handler.canSetEquipment(player, item)) continue;
				
				setItem(handler, equipped);
				handler.setEquipment(player, item);
				
			} else {
				
				// Swap player's held item with armor stand's.
				ItemStack holding = player.getCurrentEquippedItem();
				if (((item == null) && (holding == null)) ||
				    ((holding != null) && !handler.isValidItem(player, holding))) continue;
				
				setItem(handler, holding);
				player.setCurrentItemOrArmor(EquipmentSlot.HELD, item);
				break;
				
			}
		}
		
		return true;
		
	}
	@Override
	public ItemStack onPickBlock(ItemStack block, MovingObjectPosition target) {
		int slot = Math.max(0, Math.min(3, (int)((target.hitVec.yCoord - getPos().getY()) * 2)));
		EnumArmorStandRegion region = EnumArmorStandRegion.values()[slot];
		
		ItemStack item;
		for (ArmorStandEquipHandler handler : BetterStorageArmorStand.getEquipHandlers(region))
			if ((item = getItem(handler)) != null)
				return item;
		
		return block;
	}
	
	@Override
	public void dropContents() {
		ItemStack item;
		for (EnumArmorStandRegion region : EnumArmorStandRegion.values())
			for (ArmorStandEquipHandler handler : BetterStorageArmorStand.getEquipHandlers(region))
				if ((item = getItem(handler)) != null)
					WorldUtils.dropStackFromBlock(worldObj, getPos(), item);
		clearItems();
	}
	
	@Override
	protected int getComparatorSignalStengthInternal() {
		int count = 0;
		for (EnumArmorStandRegion region : EnumArmorStandRegion.values())
			for (ArmorStandEquipHandler handler : BetterStorageArmorStand.getEquipHandlers(region))
				if (getItem(handler) != null) count++;
		return count;
	}
	
	// TileEntity synchronization
	
	@Override
	public Packet getDescriptionPacket() {
		NBTTagCompound compound = new NBTTagCompound();
		write(compound);
        return new S35PacketUpdateTileEntity(getPos(), 0, compound);
	}
	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity packet) {
		read(packet.getNbtCompound());
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
		
		clearItems();
		NBTBase itemsTag = compound.getTag("Items");
		if (itemsTag instanceof NBTTagList) {
			
			// Backward compatibility.
			NBTTagList items = (NBTTagList)itemsTag;
			for (int i = 0; i < items.tagCount(); i++) {
				NBTTagCompound item = items.getCompoundTagAt(i);
				int slot = item.getByte("Slot") & 255;
				if ((slot < 0) || (slot >= EnumArmorStandRegion.values().length)) continue;
				EnumArmorStandRegion region = EnumArmorStandRegion.values()[slot];
				ArmorStandEquipHandler handler = BetterStorageArmorStand.getEquipHandler(
						region, VanillaArmorStandEquipHandler.ID);
				if (handler != null)
					setItem(handler, ItemStack.loadItemStackFromNBT(item));
			}
			
		} else {
			
			NBTTagCompound items = (NBTTagCompound)itemsTag;
			for (EnumArmorStandRegion region : EnumArmorStandRegion.values()) {
				NBTTagCompound regionItems = items.getCompoundTag(region.toString());
				for (String id : (Set<String>)regionItems.getKeySet()) {
					ItemStack item = ItemStack.loadItemStackFromNBT(regionItems.getCompoundTag(id));
					ArmorStandEquipHandler handler = BetterStorageArmorStand.getEquipHandler(region, id);
					setItem(handler, item);
				}
			}
		}
	}
	
	public void write(NBTTagCompound compound) {
		compound.setByte("rotation", (byte)rotation);
		NBTTagCompound items = new NBTTagCompound();
		for (EnumArmorStandRegion region : EnumArmorStandRegion.values()) {
			NBTTagCompound regionCompound = new NBTTagCompound();
			
			ItemStack item;
			for (ArmorStandEquipHandler handler : BetterStorageArmorStand.getEquipHandlers(region))
				if ((item = getItem(handler)) != null)
					regionCompound.setTag(handler.id, item.writeToNBT(new NBTTagCompound()));
			
			items.setTag(region.toString(), regionCompound);
		}
		compound.setTag("Items", items);
	}
	
}
