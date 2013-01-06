package net.mcft.copy.betterstorage.block;

import java.security.InvalidParameterException;
import java.util.List;
import net.mcft.copy.betterstorage.Config;
import net.mcft.copy.betterstorage.inventory.InventoryCombined;
import net.mcft.copy.betterstorage.inventory.InventoryWrapper;
import net.mcft.copy.betterstorage.item.ILockable;
import net.mcft.copy.betterstorage.item.ItemLock;
import net.mcft.copy.betterstorage.utils.InventoryUtils;
import net.mcft.copy.betterstorage.utils.WorldUtils;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet132TileEntityData;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.AxisAlignedBB;

public class TileEntityReinforcedChest extends TileEntityChest implements ILockable {

	public int id;
	
	private ItemStack[] contents;
	private InventoryWrapper wrapper;
	private ItemStack lock;
	
	private boolean powered;
	private int ticksSinceSync;
	
	/** Gets the chest's wrapper inventory. <br>
	 *  This is needed since the chest itself may be protected
	 *  from a lock so it's unable to be accessed by machines. */
	public InventoryWrapper getWrapper() { return wrapper; }
	
	public TileEntityReinforcedChest() { this(null); }
	public TileEntityReinforcedChest(Block block) {
		id = ((block != null) ? block.blockID : -1);
		contents = new ItemStack[getNumColumns() * getNumRows()];
		wrapper = new InventoryWrapper(contents, this);
	}
	
	/** Returns if the chest is emitting redstone. */
	public boolean isPowered() {
		return getMainChest().powered;
	}
	/** Sets if the chest is emitting redstone.
	 *  Updates all nearby blocks to make sure they notice it. */
	public void setPowered(boolean powered) {
		TileEntityReinforcedChest chest = getMainChest();
		if (chest != this) { chest.setPowered(powered); return; }
		
		if (this.powered == powered) return;
		this.powered = powered;
		
		// Schedule a block update to turn the redstone signal back off.
		if (powered) worldObj.scheduleBlockUpdate(xCoord, yCoord, zCoord, id, 10);
		
		// Notify nearby blocks
		worldObj.notifyBlocksOfNeighborChange(xCoord - 1, yCoord, zCoord, id);
		worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord + 1, zCoord, id);
		worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord - 1, zCoord, id);
		worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord, zCoord - 1, id);
		
		// Notify nearby blocks of adjacent chest
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
	
	private TileEntityReinforcedChest getMainChest() {
		if (adjacentChestXNeg != null)
			return (TileEntityReinforcedChest)adjacentChestXNeg;
		if (adjacentChestZNeg != null)
			return (TileEntityReinforcedChest)adjacentChestZNeg;
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
	public int getInventoryStackLimit() { return 64; }
	
	@Override
	public int getSizeInventory() { return (!isLocked() ? contents.length : 0); }
	
	@Override
	public ItemStack getStackInSlot(int slot) { return (!isLocked() ? contents[slot] : null); }
	@Override
	public void setInventorySlotContents(int slot, ItemStack stack) {
		if (!isLocked()) wrapper.setInventorySlotContents(slot, stack);
	}
	@Override
	public ItemStack decrStackSize(int slot, int amount) {
		return InventoryUtils.unsafeDecreaseStackSize(this, slot, amount);
	}
	@Override
	public ItemStack getStackInSlotOnClosing(int slot) {
		return ((!isLocked()) ? wrapper.getStackInSlotOnClosing(slot) : null);
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
	
	@Override
	public void checkForAdjacentChests() {
		if (adjacentChestChecked) return;
		
		adjacentChestChecked = true;
		adjacentChestZNeg = null;
		adjacentChestXPos = null;
		adjacentChestXNeg = null;
		adjacentChestZPosition = null;
		
		if (worldObj.getBlockId(xCoord, yCoord, zCoord - 1) == id)
			adjacentChestZNeg = WorldUtils.getChest(worldObj, xCoord, yCoord, zCoord - 1);
		if (worldObj.getBlockId(xCoord, yCoord, zCoord + 1) == id)
			adjacentChestZPosition = WorldUtils.getChest(worldObj, xCoord, yCoord, zCoord + 1);
		if (worldObj.getBlockId(xCoord - 1, yCoord, zCoord) == id)
			adjacentChestXNeg = WorldUtils.getChest(worldObj, xCoord - 1, yCoord, zCoord);
		if (worldObj.getBlockId(xCoord + 1, yCoord, zCoord) == id)
			adjacentChestXPos = WorldUtils.getChest(worldObj, xCoord + 1, yCoord, zCoord);
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
	public Block getBlockType() {
		super.getBlockType();
		if (blockType != null)
			id = blockType.blockID;
		return blockType;
	}
	
	// We need to override this from the TileEntityChest class
	// because we want to sync ContainerReinforcedChests, not ContainerChests.
	@Override
	public void updateEntity() {
		if (id == -1) getBlockType();
		checkForAdjacentChests();
		
		int numUsingPlayersBefore = numUsingPlayers;
		ticksSinceSync++;
		if (!worldObj.isRemote && numUsingPlayers != 0 &&
		    (ticksSinceSync + xCoord + yCoord + zCoord) % 200 == 0) {
			numUsingPlayers = 0;
			List players = worldObj.getEntitiesWithinAABB(
					EntityPlayer.class,
					AxisAlignedBB.getAABBPool().addOrModifyAABBInPool(
							xCoord - 5, yCoord - 5, zCoord - 5,
							xCoord + 6, yCoord + 6, zCoord + 6));
			for (Object p : players) {
				EntityPlayer player = (EntityPlayer)p;
				if (player.openContainer instanceof ContainerReinforcedChest) {
					IInventory inventory = ((ContainerReinforcedChest)player.openContainer).inventory;
					boolean isUsing = false;
					if (inventory instanceof InventoryWrapper &&
					    (InventoryWrapper)inventory == wrapper) isUsing = true;
					else if (inventory instanceof InventoryCombined)
						for (InventoryWrapper w : (InventoryCombined<InventoryWrapper>)inventory)
							if (w == wrapper) isUsing = true;
					if (isUsing)
						numUsingPlayers++;
				}
			}
		}
		
		prevLidAngle = lidAngle;
		float lidSpeed = 0.1F;
		double x = xCoord + ((adjacentChestXPos != null) ? 1 : 0.5);
		double z = zCoord + ((adjacentChestZPosition != null) ? 1 : 0.5);
		boolean isMainChest = (adjacentChestZNeg == null && adjacentChestXNeg == null);
		
		// Play sound when opening chest
		if (numUsingPlayers > 0 && lidAngle == 0.0F && isMainChest)
			worldObj.playSoundEffect(x, yCoord + 0.5, z, "random.chestopen", 0.5F,
			                         worldObj.rand.nextFloat() * 0.1F + 0.9F);
		
		if ((numUsingPlayers == 0 && lidAngle > 0.0F) ||
		    (numUsingPlayers >  0 && lidAngle < 1.0F)) {
			
			if (numUsingPlayers > 0) lidAngle = Math.min(1.0F, lidAngle + lidSpeed);
			else lidAngle = Math.max(0.0F, lidAngle - lidSpeed);
			
			// Play sound when closing chest
			if (lidAngle < 0.5F && prevLidAngle >= 0.5F && isMainChest)
				worldObj.playSoundEffect(x, yCoord + 0.5, z, "random.chestclosed", 0.5F,
                        worldObj.rand.nextFloat() * 0.1F + 0.9F);
		}
	}
	
	// Overwrites the TileEntityChest version
	// because we have a custom contents array.
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
	
	// Dropping stuff
	
	public void dropContents() {
		for (int slot = 0; slot < contents.length; slot++) {
			ItemStack stack = contents[slot];
			if (stack != null) continue;
			WorldUtils.dropStackFromBlock(worldObj, xCoord, yCoord, zCoord, stack);
		}
	}
	public void dropLock() {
		ItemStack lock = getLock();
		if (lock == null) return;
		WorldUtils.dropStackFromBlock(worldObj, xCoord, yCoord, zCoord, lock);
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
