package net.mcft.copy.betterstorage.blocks;

import java.security.InvalidParameterException;
import java.util.Random;

import net.mcft.copy.betterstorage.BetterStorage;
import net.mcft.copy.betterstorage.Config;
import net.mcft.copy.betterstorage.items.ILockable;
import net.mcft.copy.betterstorage.items.ItemLock;
import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet132TileEntityData;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.world.IBlockAccess;

public class TileEntityReinforcedChest extends TileEntityChest implements IInventory, ILockable {
	
	// Code from this class is mainly stolen from the TileEntityChest class.
	// A little prettified here and there, but basically the same.
	
	public ItemStack[] contents;
	public int id;
	
	private ItemStack lock;
	private boolean powered;
	
	public TileEntityReinforcedChest() { this(null); }
	public TileEntityReinforcedChest(Block block) {
		id = ((block != null) ? block.blockID : -1);
		contents = new ItemStack[getNumColumns() * getNumRows()];
	}
	
	public boolean isPowered() {
		return getMainChest().powered;
	}
	public void setPowered(boolean powered) {
		TileEntityReinforcedChest chest = getMainChest();
		if (chest != this) {
			chest.setPowered(powered);
			return;
		}
		
		if (this.powered == powered) return;
		this.powered = powered;
		
		if (powered) worldObj.scheduleBlockUpdate(xCoord, yCoord, zCoord, id, 10);
		
		worldObj.notifyBlocksOfNeighborChange(xCoord - 1, yCoord, zCoord, id);
		worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord + 1, zCoord, id);
		worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord - 1, zCoord, id);
		worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord, zCoord - 1, id);
		
		if (adjacentChestXPos != null) {
			worldObj.notifyBlocksOfNeighborChange(xCoord + 2, yCoord, zCoord, id);
			worldObj.notifyBlocksOfNeighborChange(xCoord + 1, yCoord + 1, zCoord, id);
			worldObj.notifyBlocksOfNeighborChange(xCoord + 1, yCoord - 1, zCoord, id);
			worldObj.notifyBlocksOfNeighborChange(xCoord + 1, yCoord, zCoord + 1, id);
			worldObj.notifyBlocksOfNeighborChange(xCoord + 1, yCoord, zCoord - 1, id);
		} else worldObj.notifyBlocksOfNeighborChange(xCoord + 1, yCoord, zCoord, id);
		
		if (adjacentChestZPosition != null) {
			worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord, zCoord + 2, id);
			worldObj.notifyBlocksOfNeighborChange(xCoord + 1, yCoord, zCoord + 1, id);
			worldObj.notifyBlocksOfNeighborChange(xCoord - 1, yCoord, zCoord + 1, id);
			worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord + 1, zCoord + 1, id);
			worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord - 1, zCoord + 1, id);
		} else worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord, zCoord + 1, id);
			
	}
	
	public static TileEntityReinforcedChest getChestAt(IBlockAccess world, int x, int y, int z) {
		TileEntity tileEntity = world.getBlockTileEntity(x, y, z);
		if (tileEntity == null || !(tileEntity instanceof TileEntityReinforcedChest)) return null;
		return (TileEntityReinforcedChest)tileEntity;
	}
	
	private TileEntityReinforcedChest getMainChest() {
		if (adjacentChestXNeg != null) return (TileEntityReinforcedChest)adjacentChestXNeg;
		if (adjacentChestZNeg != null) return (TileEntityReinforcedChest)adjacentChestZNeg;
		return this;
	}
	
	// Tile entity synchronization
	
	@Override
	public Packet getDescriptionPacket() {
		NBTTagCompound compound = new NBTTagCompound();
		if (lock != null) lock.writeToNBT(compound);
        return new Packet132TileEntityData(xCoord, yCoord, zCoord, 0, compound);
	}
	@Override
	public void onDataPacket(INetworkManager net, Packet132TileEntityData packet) {
		if (packet.customParam1.hasNoTags()) lock = null;
		lock = ItemStack.loadItemStackFromNBT(packet.customParam1);
	}
	
	// Begin chest stuff
	
	public int getNumColumns() { return (Config.normalSizedChests ? 9 : 13); }
	public int getNumRows() { return 3; }
	
	@Override
	public String getInvName() { return "container.reinforcedChest"; }
	
	@Override
	public int getSizeInventory() {
		return (isLocked() ? 0 : contents.length);
	}
	@Override
	public ItemStack getStackInSlot(int slot) {
		return (isLocked() ? null : contents[slot]);
	}
	@Override
	public ItemStack decrStackSize(int slot, int amount) {
		if (isLocked()) return null;
		ItemStack stack = getStackInSlot(slot);
		if (stack == null) return null;
		ItemStack returnStack;
		if (stack.stackSize <= amount) {
			returnStack = stack;
			contents[slot] = null;
		} else returnStack = stack.splitStack(amount);
		onInventoryChanged();
		return returnStack;
	}
	@Override
	public ItemStack getStackInSlotOnClosing(int slot) {
		if (isLocked()) return null;
		ItemStack stack = contents[slot];
		contents[slot] = null;
		return stack;
	}
	@Override
	public void setInventorySlotContents(int slot, ItemStack stack) {
		if (isLocked()) return;
		contents[slot] = stack;
		if (stack != null && stack.stackSize > getInventoryStackLimit())
			stack.stackSize = getInventoryStackLimit();
		onInventoryChanged();
	}
	
	@Override
	public void readFromNBT(NBTTagCompound compound) {
        xCoord = compound.getInteger("x");
        yCoord = compound.getInteger("y");
        zCoord = compound.getInteger("z");
		NBTTagList items = compound.getTagList("Items");
		contents = new ItemStack[getSizeInventory()];
		for (int i = 0; i < items.tagCount(); i++) {
			NBTTagCompound item = (NBTTagCompound)items.tagAt(i);
			int slot = item.getByte("Slot") & 255;
			if (slot >= 0 && slot < contents.length)
				contents[slot] = ItemStack.loadItemStackFromNBT(item);
		}
		if (compound.hasKey("lock"))
			lock = ItemStack.loadItemStackFromNBT(compound.getCompoundTag("lock"));
	}
	@Override
	public void writeToNBT(NBTTagCompound compound) {
        compound.setString("id", "container.reinforcedChest");
        compound.setInteger("x", this.xCoord);
        compound.setInteger("y", this.yCoord);
        compound.setInteger("z", this.zCoord);
		NBTTagList list = new NBTTagList();
		for (int i = 0; i < contents.length; i++) {
			if (contents[i] == null) continue;
			NBTTagCompound item = new NBTTagCompound();
			item.setByte("Slot", (byte)i);
			contents[i].writeToNBT(item);
			list.appendTag(item);
		}
		compound.setTag("Items", list);
		if (lock != null)
			compound.setCompoundTag("lock", lock.writeToNBT(new NBTTagCompound("")));
	}
	
	@Override
	public int getInventoryStackLimit() { return 64; }
	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		return super.isUseableByPlayer(player);
	}
	@Override
	public void checkForAdjacentChests() {
		if (adjacentChestChecked) return;
		
		adjacentChestChecked = true;
		adjacentChestZNeg = null;
		adjacentChestXPos = null;
		adjacentChestXNeg = null;
		adjacentChestZPosition = null;
		
		if (worldObj.getBlockId(xCoord, yCoord, zCoord - 1) == id)
			adjacentChestZNeg = getChestAt(worldObj, xCoord, yCoord, zCoord - 1);
		if (worldObj.getBlockId(xCoord, yCoord, zCoord + 1) == id)
			adjacentChestZPosition = getChestAt(worldObj, xCoord, yCoord, zCoord + 1);
		if (worldObj.getBlockId(xCoord - 1, yCoord, zCoord) == id)
			adjacentChestXNeg = getChestAt(worldObj, xCoord - 1, yCoord, zCoord);
		if (worldObj.getBlockId(xCoord + 1, yCoord, zCoord) == id)
			adjacentChestXPos = getChestAt(worldObj, xCoord + 1, yCoord, zCoord);
		if (adjacentChestZNeg != null)
			adjacentChestZNeg.updateContainingBlockInfo();
		if (adjacentChestZPosition != null)
			adjacentChestZPosition.updateContainingBlockInfo();
		if (adjacentChestXNeg != null)
			adjacentChestXNeg.updateContainingBlockInfo();
		if (adjacentChestXPos != null)
			adjacentChestXPos.updateContainingBlockInfo();
	}
	@Override
	public void updateEntity() {
		if (id == -1) getBlockType();
		super.updateEntity();
	}
	@Override
	public Block getBlockType() {
		super.getBlockType();
		if (blockType != null)
			id = blockType.blockID;
		return blockType;
	}
	
	@Override
	public void openChest() {
		numUsingPlayers++;
		worldObj.addBlockEvent(xCoord, yCoord, zCoord, id, 1, numUsingPlayers);
	}
	@Override
	public void closeChest() {
		numUsingPlayers--;
		worldObj.addBlockEvent(xCoord, yCoord, zCoord, id, 1, numUsingPlayers);
	}
	
	// Dropping stuff
	
	private void dropItemStack(ItemStack stack) {
		Random random = BetterStorage.random;
		float x = random.nextFloat() * 0.8F + 0.1F;
		float y = random.nextFloat() * 0.8F + 0.1F;
		float z = random.nextFloat() * 0.8F + 0.1F;
		EntityItem item = new EntityItem(worldObj, xCoord + x, yCoord + y, zCoord + z, stack);
		item.motionX = random.nextGaussian() * 0.05F;
		item.motionY = random.nextGaussian() * 0.05F + 0.2F;
		item.motionZ = random.nextGaussian() * 0.05F;
		worldObj.spawnEntityInWorld(item);
	}
	public void dropContents() {
		for (int slot = 0; slot < contents.length; slot++) {
			ItemStack stack = contents[slot];
			if (stack == null) continue;
			dropItemStack(stack);
		}
	}
	public void dropLock() {
		ItemStack lock = getLock();
		if (lock == null) return;
		dropItemStack(lock);
		setLock(null);
	}
	
	// ILockable implementation
	
	@Override
	public ItemStack getLock() { return getMainChest().lock; }
	@Override
	public void setLock(ItemStack lock) {
		if (lock != null && !ItemLock.isLock(lock))
			throw new InvalidParameterException("lock is not an ItemStack of ItemLock.");
		TileEntityReinforcedChest chest = getMainChest();
		chest.lock = lock;
		// Mark the block for an update, sends description packet to players.
		worldObj.markBlockForUpdate(chest.xCoord, chest.yCoord, chest.zCoord);
	}
	
	@Override
	public boolean isLocked() { return (getLock() != null); }
	@Override
	public boolean canLock(ItemStack lock) {
		if (!ItemLock.isLock(lock)) return false;
		return !isLocked();
	}
	@Override
	public boolean canUse(EntityPlayer player) {
		return (!isLocked() || numUsingPlayers > 0);
	}
	
}
