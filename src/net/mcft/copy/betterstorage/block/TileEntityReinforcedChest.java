package net.mcft.copy.betterstorage.block;

import java.security.InvalidParameterException;
import java.util.List;

import net.mcft.copy.betterstorage.BetterStorage;
import net.mcft.copy.betterstorage.Config;
import net.mcft.copy.betterstorage.ILockable;
import net.mcft.copy.betterstorage.inventory.InventoryCombined;
import net.mcft.copy.betterstorage.inventory.InventoryWrapper;
import net.mcft.copy.betterstorage.item.ItemLock;
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
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.ForgeDirection;

public class TileEntityReinforcedChest extends TileEntity implements IInventory, ILockable {
	
	private static ForgeDirection[] neighborChests = {
			ForgeDirection.EAST, ForgeDirection.NORTH,
			ForgeDirection.WEST, ForgeDirection.SOUTH };

	private ItemStack[] contents;
	private IInventory wrapper;
	private ItemStack lock;

	private int numUsingPlayers = 0;
	private boolean powered;
	private int ticksSinceSync;
	
	public float lidAngle = 0;
	public float prevLidAngle = 0;
	
	public ForgeDirection orientation = ForgeDirection.UNKNOWN;
	public ForgeDirection connected = ForgeDirection.UNKNOWN;
	
	/** Gets the chest's wrapper inventory. <br>
	 *  This is needed since the chest itself may be protected
	 *  from a lock so it's unable to be accessed by machines. */
	public IInventory getWrapper() { return wrapper; }
	
	public boolean isLarge() { return (connected != ForgeDirection.UNKNOWN); }
	public boolean isMainChest() { return (!isLarge() || (connected.offsetX + connected.offsetZ > 0)); }
	public TileEntityReinforcedChest getMainChest() {
		if (isMainChest()) return this;
		TileEntityReinforcedChest chest = getConnectedChest();
		if (chest != null) return chest;
		BetterStorage.log("Warning: getConnectedChest returned null.");
		return this;
	}
	public TileEntityReinforcedChest getConnectedChest() {
		return WorldUtils.getChest(worldObj, xCoord + connected.offsetX, yCoord, zCoord + connected.offsetZ);
	}
	
	public TileEntityReinforcedChest() { this(null); }
	public TileEntityReinforcedChest(Block block) {
		contents = new ItemStack[getNumColumns() * getNumRows()];
		wrapper = new InventoryWrapper(contents, this);
	}
	
	// Chest connecting stuff
	
	/** Connects chests that can be connected. */
	public void checkForAdjacentChests() {
		TileEntityReinforcedChest chestFound = null;
		ForgeDirection dirFound = ForgeDirection.UNKNOWN;
		for (ForgeDirection dir : neighborChests) {
			TileEntityReinforcedChest chest = WorldUtils.getChest(worldObj, xCoord + dir.offsetX, yCoord, zCoord + dir.offsetZ);
			if (!canConnectChests(chest)) continue;
			if (chestFound != null) return;
			chestFound = chest;
			dirFound = dir;
		}
		if (chestFound == null) return;
		connected = dirFound;
		chestFound.connected = dirFound.getOpposite();
		// Mark the block for an update, sends description packet to players.
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		worldObj.markBlockForUpdate(chestFound.xCoord, chestFound.yCoord, chestFound.zCoord);
	}
	
	public boolean canConnectChests(TileEntityReinforcedChest chest) {
		return (chest != null &&                                  // check for null
				getBlockType() == chest.getBlockType() &&         // check for same block id
				getBlockMetadata() == chest.getBlockMetadata() && // check for same material
		        orientation == chest.orientation &&               // check for same orientation
		        // Make sure the chest is to the left or right, not in front or behind.
		        ((xCoord != chest.xCoord && (orientation == ForgeDirection.EAST || orientation == ForgeDirection.WEST)) ||
		         (zCoord != chest.zCoord && (orientation == ForgeDirection.SOUTH || orientation == ForgeDirection.NORTH))) &&
		        // Make sure the chests are not already large or locked.
		        !isLarge() && !chest.isLarge() && !isLocked() && !chest.isLocked());
	}
	
	/** Disconnects chests, for example when they're destroyed. */
	public void disconnectChests() {
		if (connected == ForgeDirection.UNKNOWN) return;
		TileEntityReinforcedChest chest = getConnectedChest();
		connected = ForgeDirection.UNKNOWN;
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		if (chest != null) {
			chest.connected = ForgeDirection.UNKNOWN;
			worldObj.markBlockForUpdate(chest.xCoord, chest.yCoord, chest.zCoord);
		} else BetterStorage.log("Warning: getConnectedChest returned null.");
	}
	
	// Tile entity synchronization
	
	@Override
	public Packet getDescriptionPacket() {
		NBTTagCompound compound = new NBTTagCompound();
		compound.setByte("orientation", (byte)orientation.ordinal());
		compound.setByte("connected", (byte)connected.ordinal());
		if (lock != null) {
			NBTTagCompound lockCompound = new NBTTagCompound();
			lock.writeToNBT(lockCompound);
			compound.setCompoundTag("lock", lockCompound);
		}
        return new Packet132TileEntityData(xCoord, yCoord, zCoord, 0, compound);
	}
	
	@Override
	public void onDataPacket(INetworkManager net, Packet132TileEntityData packet) {
		NBTTagCompound compound = packet.customParam1;
		orientation = ForgeDirection.getOrientation(compound.getByte("orientation"));
		connected = ForgeDirection.getOrientation(compound.getByte("connected"));
		if (!compound.hasKey("lock")) lock = null;
		else lock = ItemStack.loadItemStackFromNBT(compound.getCompoundTag("lock"));
	}
	
	// Begin chest stuff
	
	public int getNumColumns() { return (Config.normalSizedChests ? 9 : 13); }
	public int getNumRows() { return 3; }
	
	@Override
	public String getInvName() { return "container.reinforcedChest"; }
	@Override
	public int getInventoryStackLimit() { return 64; }
	
	@Override
	public int getSizeInventory() { return (!isLocked() ? wrapper.getSizeInventory() : 0); }
	
	@Override
	public ItemStack getStackInSlot(int slot) { return (!isLocked() ? wrapper.getStackInSlot(slot) : null); }
	@Override
	public void setInventorySlotContents(int slot, ItemStack stack) {
		if (!isLocked()) wrapper.setInventorySlotContents(slot, stack);
	}
	@Override
	public ItemStack decrStackSize(int slot, int amount) {
		return (!isLocked() ? wrapper.decrStackSize(slot, amount) : null);
	}
	@Override
	public ItemStack getStackInSlotOnClosing(int slot) {
		return ((!isLocked()) ? wrapper.getStackInSlotOnClosing(slot) : null);
	}
	
	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		return (worldObj.getBlockTileEntity(xCoord, yCoord, zCoord) == this &&
		        player.getDistanceSq(xCoord + 0.5, yCoord + 0.5, zCoord + 0.5) <= 64.0);
	}
	
	@Override
	public void openChest() {
		numUsingPlayers++;
		worldObj.addBlockEvent(xCoord, yCoord, zCoord, getBlockType().blockID, 1, numUsingPlayers);
	}
	@Override
	public void closeChest() {
		numUsingPlayers--;
		worldObj.addBlockEvent(xCoord, yCoord, zCoord, getBlockType().blockID, 1, numUsingPlayers);
	}
	@Override
	public void receiveClientEvent(int eventId, int val) {
		numUsingPlayers = val;
	}
	
	@Override
	public void updateEntity() {
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
		double x = xCoord + 0.5;
		double y = yCoord + 0.5;
		double z = zCoord + 0.5;
		if (isLarge()) {
			TileEntityReinforcedChest connectedChest = getConnectedChest();
			if (connectedChest != null) {
				x = (x + connectedChest.xCoord + 0.5) / 2;
				z = (z + connectedChest.zCoord + 0.5) / 2;
			}
		}
		
		// Play sound when opening chest
		if (numUsingPlayers > 0 && lidAngle == 0.0F && isMainChest())
			worldObj.playSoundEffect(x, y, z, "random.chestopen", 0.5F,
			                         worldObj.rand.nextFloat() * 0.1F + 0.9F);
		
		if ((numUsingPlayers == 0 && lidAngle > 0.0F) ||
		    (numUsingPlayers >  0 && lidAngle < 1.0F)) {
			
			if (numUsingPlayers > 0) lidAngle = Math.min(1.0F, lidAngle + lidSpeed);
			else lidAngle = Math.max(0.0F, lidAngle - lidSpeed);
			
			// Play sound when closing chest
			if (lidAngle < 0.5F && prevLidAngle >= 0.5F && isMainChest())
				worldObj.playSoundEffect(x, y, z, "random.chestclosed", 0.5F,
                        worldObj.rand.nextFloat() * 0.1F + 0.9F);
		}
	}
	
	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		orientation = ForgeDirection.getOrientation(compound.getByte("orientation"));
		connected = ForgeDirection.getOrientation(compound.getByte("connected"));
		NBTTagList items = compound.getTagList("Items");
		contents = new ItemStack[contents.length];
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
		super.writeToNBT(compound);
		compound.setByte("orientation", (byte)orientation.ordinal());
		compound.setByte("connected", (byte)connected.ordinal());
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
			if (stack == null) continue;
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
	
	// Trigger enchantment related
	
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
		
		int id = getBlockType().blockID;
		// Schedule a block update to turn the redstone signal back off.
		if (powered) worldObj.scheduleBlockUpdate(xCoord, yCoord, zCoord, id, 10);
		
		// Notify nearby blocks
		worldObj.notifyBlocksOfNeighborChange(xCoord - 1, yCoord, zCoord, id);
		worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord + 1, zCoord, id);
		worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord - 1, zCoord, id);
		worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord, zCoord - 1, id);
		
		// Notify nearby blocks of adjacent chest
		if (isLarge() && connected == ForgeDirection.EAST) {
			worldObj.notifyBlocksOfNeighborChange(xCoord + 2, yCoord, zCoord, id);
			worldObj.notifyBlocksOfNeighborChange(xCoord + 1, yCoord + 1, zCoord, id);
			worldObj.notifyBlocksOfNeighborChange(xCoord + 1, yCoord - 1, zCoord, id);
			worldObj.notifyBlocksOfNeighborChange(xCoord + 1, yCoord, zCoord + 1, id);
			worldObj.notifyBlocksOfNeighborChange(xCoord + 1, yCoord, zCoord - 1, id);
		} else worldObj.notifyBlocksOfNeighborChange(xCoord + 1, yCoord, zCoord, id);
		if (isLarge() && connected == ForgeDirection.SOUTH) {
			worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord, zCoord + 2, id);
			worldObj.notifyBlocksOfNeighborChange(xCoord + 1, yCoord, zCoord + 1, id);
			worldObj.notifyBlocksOfNeighborChange(xCoord - 1, yCoord, zCoord + 1, id);
			worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord + 1, zCoord + 1, id);
			worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord - 1, zCoord + 1, id);
		} else worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord, zCoord + 1, id);
			
	}
	
}
