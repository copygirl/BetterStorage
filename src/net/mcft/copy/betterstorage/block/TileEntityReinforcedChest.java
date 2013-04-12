package net.mcft.copy.betterstorage.block;

import java.security.InvalidParameterException;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.mcft.copy.betterstorage.Config;
import net.mcft.copy.betterstorage.api.ILock;
import net.mcft.copy.betterstorage.api.ILockable;
import net.mcft.copy.betterstorage.inventory.InventoryWrapper;
import net.mcft.copy.betterstorage.utils.NbtUtils;
import net.mcft.copy.betterstorage.utils.WorldUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet132TileEntityData;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.ForgeDirection;

public class TileEntityReinforcedChest extends TileEntityConnectable implements IInventory, ILockable {

	private ItemStack[] contents;
	private IInventory wrapper;
	private ItemStack lock;

	private int numUsingPlayers = 0;
	private boolean powered;
	private int ticksSinceSync;
	
	public float lidAngle = 0;
	public float prevLidAngle = 0;
	
	/** Gets the chest's wrapper inventory. <br>
	 *  This is needed since the chest itself may be protected
	 *  from a lock so it's unable to be accessed by machines. */
	public IInventory getWrapper() { return wrapper; }
	
	public TileEntityReinforcedChest() {
		contents = new ItemStack[getNumColumns() * getNumRows()];
		wrapper = new InventoryWrapper(contents, this);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getRenderBoundingBox() {
		return WorldUtils.getAABB(this, 0, 0, 0, 1, 1, 1);
	}
	
	// TileEntityConnectable stuff
	
	public TileEntityReinforcedChest getMainChest() {
		return (TileEntityReinforcedChest)getMain();
	}
	public TileEntityReinforcedChest getConnectedChest() {
		return (TileEntityReinforcedChest)getConnected();
	}
	
	private static ForgeDirection[] neighbors = {
		ForgeDirection.EAST, ForgeDirection.NORTH,
		ForgeDirection.WEST, ForgeDirection.SOUTH };
	@Override
	public ForgeDirection[] getPossibleNeighbors() { return neighbors; }
	
	@Override
	public boolean canConnect(TileEntityConnectable connectable) {
		if (!(connectable instanceof TileEntityReinforcedChest)) return false;
		TileEntityReinforcedChest chest = (TileEntityReinforcedChest)connectable;
		return (super.canConnect(connectable) &&
		        ((xCoord != chest.xCoord && (orientation == ForgeDirection.EAST || orientation == ForgeDirection.WEST)) ||
		         (zCoord != chest.zCoord && (orientation == ForgeDirection.SOUTH || orientation == ForgeDirection.NORTH))) &&
		        getLock() == null && chest.getLock() == null);
	}
	
	// TileEntity synchronization
	
	@Override
	public Packet getDescriptionPacket() {
		Packet132TileEntityData packet =
				(Packet132TileEntityData)super.getDescriptionPacket();
		NBTTagCompound compound = packet.customParam1;
		if (lock != null) {
			NBTTagCompound lockCompound = new NBTTagCompound();
			lock.writeToNBT(lockCompound);
			compound.setCompoundTag("lock", lockCompound);
		}
        return packet;
	}
	
	@Override
	public void onDataPacket(INetworkManager net, Packet132TileEntityData packet) {
		super.onDataPacket(net, packet);
		NBTTagCompound compound = packet.customParam1;
		if (!compound.hasKey("lock")) lock = null;
		else lock = ItemStack.loadItemStackFromNBT(compound.getCompoundTag("lock"));
	}
	
	// Reading from / writing to NBT
	
	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		NBTTagList items = compound.getTagList("Items");
		contents = NbtUtils.readItems(items, contents.length);
		wrapper = new InventoryWrapper(contents, this);
		if (compound.hasKey("lock"))
			lock = ItemStack.loadItemStackFromNBT(compound.getCompoundTag("lock"));
	}
	
	@Override
	public void writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		compound.setTag("Items", NbtUtils.writeItems(contents));
		if (lock != null)
			compound.setCompoundTag("lock", lock.writeToNBT(new NBTTagCompound("")));
	}
	
	// IInventory stuff
	
	public int getNumColumns() { return (Config.normalSizedChests ? 9 : 13); }
	public int getNumRows() { return 3; }
	
	@Override
	public String getInvName() { return "container.reinforcedChest"; }
	@Override
	public int getInventoryStackLimit() { return 64; }
	
	@Override
	public int getSizeInventory() { return ((getLock() == null) ? wrapper.getSizeInventory() : 0); }
	
	@Override
	public ItemStack getStackInSlot(int slot) { return ((getLock() == null) ? wrapper.getStackInSlot(slot) : null); }
	@Override
	public void setInventorySlotContents(int slot, ItemStack stack) {
		if (getLock() == null) wrapper.setInventorySlotContents(slot, stack);
	}
	@Override
	public ItemStack decrStackSize(int slot, int amount) {
		return ((getLock() == null) ? wrapper.decrStackSize(slot, amount) : null);
	}
	@Override
	public ItemStack getStackInSlotOnClosing(int slot) {
		return ((getLock() == null) ? wrapper.getStackInSlotOnClosing(slot) : null);
	}
	
	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		return WorldUtils.isTileEntityUsableByPlayer(this, player);
	}
	@Override
	public boolean isInvNameLocalized() { return false; }
	@Override
	public boolean isStackValidForSlot(int i, ItemStack stack) { return true; }
	
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
	public boolean receiveClientEvent(int eventId, int val) {
		numUsingPlayers = val;
		return true;
	}
	
	@Override
	public void updateEntity() {
		numUsingPlayers = WorldUtils.syncPlayersUsing(this, ++ticksSinceSync, numUsingPlayers, wrapper);
		
		prevLidAngle = lidAngle;
		float lidSpeed = 0.1F;
		double x = xCoord + 0.5;
		double y = yCoord + 0.5;
		double z = zCoord + 0.5;
		if (isConnected()) {
			TileEntityConnectable connectable = getConnected();
			if (connectable != null) {
				x = (x + connectable.xCoord + 0.5) / 2;
				z = (z + connectable.zCoord + 0.5) / 2;
			}
		}
		
		// Play sound when opening
		if (numUsingPlayers > 0 && lidAngle == 0.0F && isMain())
			worldObj.playSoundEffect(x, y, z, "random.chestopen", 0.5F,
			                         worldObj.rand.nextFloat() * 0.1F + 0.9F);
		
		if ((numUsingPlayers == 0 && lidAngle > 0.0F) ||
		    (numUsingPlayers >  0 && lidAngle < 1.0F)) {
			
			if (numUsingPlayers > 0) lidAngle = Math.min(1.0F, lidAngle + lidSpeed);
			else lidAngle = Math.max(0.0F, lidAngle - lidSpeed);
			
			// Play sound when closing
			if (lidAngle < 0.5F && prevLidAngle >= 0.5F && isMain())
				worldObj.playSoundEffect(x, y, z, "random.chestclosed", 0.5F,
                        worldObj.rand.nextFloat() * 0.1F + 0.9F);
		}
	}
	
	// Dropping stuff
	
	public void dropContents() {
		for (ItemStack stack : contents)
			WorldUtils.dropStackFromBlock(worldObj, xCoord, yCoord, zCoord, stack);
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
	public boolean isLockValid(ItemStack lock) { return (lock.getItem() instanceof ILock); }
	
	@Override
	public void setLock(ItemStack lock) {
		if (lock != null && !isLockValid(lock))
			throw new InvalidParameterException("Can't set lock to " + lock + ".");
		TileEntityReinforcedChest chest = getMainChest();
		chest.lock = lock;
		// Mark the block for an update, sends description packet to players.
		worldObj.markBlockForUpdate(chest.xCoord, chest.yCoord, chest.zCoord);
	}
	
	@Override
	public boolean canUse(EntityPlayer player) { return (numUsingPlayers > 0); }
	
	@Override
	public void applyTrigger() { setPowered(true); }
	
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
		if (isConnected() && connected == ForgeDirection.EAST) {
			worldObj.notifyBlocksOfNeighborChange(xCoord + 2, yCoord, zCoord, id);
			worldObj.notifyBlocksOfNeighborChange(xCoord + 1, yCoord + 1, zCoord, id);
			worldObj.notifyBlocksOfNeighborChange(xCoord + 1, yCoord - 1, zCoord, id);
			worldObj.notifyBlocksOfNeighborChange(xCoord + 1, yCoord, zCoord + 1, id);
			worldObj.notifyBlocksOfNeighborChange(xCoord + 1, yCoord, zCoord - 1, id);
		} else worldObj.notifyBlocksOfNeighborChange(xCoord + 1, yCoord, zCoord, id);
		if (isConnected() && connected == ForgeDirection.SOUTH) {
			worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord, zCoord + 2, id);
			worldObj.notifyBlocksOfNeighborChange(xCoord + 1, yCoord, zCoord + 1, id);
			worldObj.notifyBlocksOfNeighborChange(xCoord - 1, yCoord, zCoord + 1, id);
			worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord + 1, zCoord + 1, id);
			worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord - 1, zCoord + 1, id);
		} else worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord, zCoord + 1, id);
		
	}
	
}
