package net.mcft.copy.betterstorage.block;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import net.mcft.copy.betterstorage.BetterStorage;
import net.mcft.copy.betterstorage.utils.WorldUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.ForgeDirection;

public class TileEntityCrate extends TileEntity implements IInventory {
	
	private static final ForgeDirection[] sideDirections = {
		ForgeDirection.NORTH, ForgeDirection.SOUTH, ForgeDirection.WEST, ForgeDirection.EAST
	};
	
	public static final int slotsPerCrate = 18;
	
	private CratePileData data;
	/** Crate pile id of this crate, used only for saving/loading. */
	private int id = -1;
	
	/** Get the pile data for this tile entity. */
	public CratePileData getPileData() {
		if (worldObj.isRemote) return null;
		if (data == null) {
			CratePileCollection collection = CratePileCollection.getCollection(worldObj);
			if (id == -1)
				setPileData(collection.createCratePile(), true);
			else setPileData(collection.getCratePile(id), false);
		}
		return data;
	}
	/** Sets the pile data and adds the crate to it if desired. <br>
	 *  Removes the crate from the old pile data if it had one. */
	private void setPileData(CratePileData data, boolean addCrate) {
		if (this.data != null)
			this.data.removeCrate(this);
		this.data = data;
		if (data != null) {
			id = data.id;
			if (addCrate) data.addCrate(this);
		} else id = -1;
	}
	/** Destroys all crates above, and makes sure when piles split,
	 *  each pile gets their own CratePileData object. */
	private void checkPileConnections(CratePileData data) {
		int x = xCoord;
		int y = yCoord;
		int z = zCoord;
		// Destroy all crates above.
		TileEntityCrate crateAbove = WorldUtils.getCrate(worldObj, x, y + 1, z);
		if (crateAbove != null && crateAbove.data == data)
			crateAbove.destroyCrate();
		// If there's still some crates left and this is a
		// base crate, see which crates are still connected.
		if (data.getNumCrates() > 0 && !WorldUtils.is(worldObj, x, y - 1, z, BetterStorage.crate)) {
			List<HashSet<TileEntityCrate>> crateSets =
					new ArrayList<HashSet<TileEntityCrate>>();
			int checkedChecks = 0;
			neighborLoop: // Suck it :P
			for (ForgeDirection dir : sideDirections) {
				int nx = x + dir.offsetX;
				int nz = z + dir.offsetZ;
				// Continue if this neighbor block is not a crate.
				TileEntityCrate neighborCrate = WorldUtils.getCrate(worldObj, nx, y, nz);
				if (neighborCrate == null) continue;
				// See if the neighbor crate is already in a crate set,
				// in that case continue with the next neighbor block.
				for (HashSet<TileEntityCrate> set : crateSets)
					if (set.contains(neighborCrate)) continue neighborLoop;
				// Create a new set of crates and fill it with all connecting crates.
				HashSet<TileEntityCrate> set = new HashSet<TileEntityCrate>();
				set.add(neighborCrate);
				for (ForgeDirection ndir : sideDirections)
					checkConnections(nx + ndir.offsetX, y, nz + ndir.offsetZ, set);
				crateSets.add(set);
				// If we checked all crates, stop the loop.
				checkedChecks += set.size();
				if (checkedChecks == data.getNumCrates()) break;
			}
			// If there's more than one crate set, they need to split.
			if (crateSets.size() > 0) {
				// The first crate set will keep the original pile data.
				// All other sets will get new pile data objects.
				for (int i = 1; i < crateSets.size(); i++) {
					HashSet<TileEntityCrate> set = crateSets.get(i);
					CratePileData newPileData = data.collection.createCratePile();
					int numCrates = set.size();
					// Add the base crates from the set.
					for (TileEntityCrate newPileCrate : set) {
						newPileCrate.setPileData(newPileData, true);
						// Add all crates above the base crate.
						while (true) {
							newPileCrate = WorldUtils.getCrate(worldObj, newPileCrate.xCoord, newPileCrate.yCoord + 1, newPileCrate.zCoord);
							if (newPileCrate == null) break;
							newPileCrate.setPileData(newPileData, true);
							numCrates++;
						}
					}
					// Move some of the items over to the new crate pile.
					int count = numCrates * data.getOccupiedSlots() / (data.getNumCrates() + numCrates);
					List<ItemStack> stacks = data.pickAndRemoveItemStacks(count);
					stacks = newPileData.addItems(stacks);
				}
				// Trim the original map to the size it actually is.
				// This is needed because the crates may not be removed in
				// order, from outside to inside.
				data.trimMap();
			}
		}
	}
	private void checkConnections(int x, int y, int z, HashSet<TileEntityCrate> set) {
		TileEntityCrate crate = WorldUtils.getCrate(worldObj, x, y, z);
		if (crate == null || set.contains(crate)) return;
		set.add(crate);
		for (ForgeDirection ndir : sideDirections)
			checkConnections(x + ndir.offsetX, y, z + ndir.offsetZ, set);
	}
	
	@Override
	public void updateEntity() {
		if (worldObj.isRemote || data != null ||
		    (id == -1 && findNeighborCratePile())) return;
		if (!isInvalid()) getPileData();
	}
	
	@Override
	public void invalidate() {
		super.invalidate();
		if (worldObj.isRemote) return;
		CratePileData data = getPileData();
		setPileData(null, false);
		dropOverflowContents(data);
		checkPileConnections(data);
	}
	
	private boolean findNeighborCratePile() {
		CratePileData pileData = null;
		boolean crateBelow = false;
		for (ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
			int x = xCoord + dir.offsetX;
			int y = yCoord + dir.offsetY;
			int z = zCoord + dir.offsetZ;
			CratePileData checkedPileData = WorldUtils.getCratePileData(worldObj, x, y, z);
			if (checkedPileData == null) continue;
			if ((pileData != null && checkedPileData.id != pileData.id) ||
			    (dir != ForgeDirection.DOWN && !crateBelow &&
			     WorldUtils.is(worldObj, x, y - 1, z, BetterStorage.crate)) ||
			    dir == ForgeDirection.UP || !checkedPileData.canAdd(this)) {
				destroyCrate();
				return false;
			}
			if (dir == ForgeDirection.DOWN) crateBelow = true;
			pileData = checkedPileData;
		}
		if (pileData != null)
			setPileData(pileData, true);
		return (pileData != null);
	}
	
	/** Destroys the crate when for example the crate's placement is invalid. */
	private void destroyCrate() {
		worldObj.setBlockWithNotify(xCoord, yCoord, zCoord, 0);
		dropItem(new ItemStack(BetterStorage.crate));
	}
	
	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		id = compound.getInteger("crateId");
	}
	@Override
	public void writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		compound.setInteger("crateId", id);
	}
	
	/** Drops a single item from the (destroyed) crate. */
	private void dropItem(ItemStack stack) {
		WorldUtils.dropStackFromBlock(worldObj, xCoord, yCoord, zCoord, stack);
	}
	/** Drops multiple item from the (destroyed) crate. */
	private void dropItems(List<ItemStack> stacks) {
		for (ItemStack stack : stacks)
			dropItem(stack);
	}
	/** Drops contents that don't fit into the
	 *  crate pile after a crate got destroyed. */
	private void dropOverflowContents(CratePileData data) {
		int amount = -data.getFreeSlots();
		dropItems(data.pickAndRemoveItemStacks(amount));
	}
	
	// IInventory implementation

	@Override
	public String getInvName() { return "container.crate"; }
	@Override
	public int getInventoryStackLimit() { return 64; }
	
	@Override
	public int getSizeInventory() {
		if (worldObj.isRemote) return 1;
		return getPileData().blockView.getSizeInventory();
	}
	
	@Override
	public ItemStack getStackInSlot(int slot) {
		return getPileData().blockView.getStackInSlot(slot);
	}
	@Override
	public void setInventorySlotContents(int slot, ItemStack stack) {
		getPileData().blockView.setInventorySlotContents(slot, stack);
	}
	@Override
	public ItemStack getStackInSlotOnClosing(int slot) {
		return getPileData().blockView.getStackInSlotOnClosing(slot);
	}
	@Override
	public ItemStack decrStackSize(int slot, int amount) {
		return getPileData().blockView.decrStackSize(slot, amount);
	}
	
	@Override
	public boolean isUseableByPlayer(EntityPlayer player) { return false; }
	
	@Override
	public void openChest() { getPileData().blockView.openChest(); }
	@Override
	public void closeChest() { getPileData().blockView.closeChest(); }
	
}
