package net.mcft.copy.betterstorage.blocks;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import buildcraft.api.inventory.ISpecialInventory;

import net.mcft.copy.betterstorage.BetterStorage;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;

public class TileEntityCrate extends TileEntity implements IInventory, ISpecialInventory {
	
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
		TileEntityCrate crateAbove = getCrateAt(worldObj, x, y + 1, z);
		if (crateAbove != null && crateAbove.data == data)
			crateAbove.destroyCrate();
		// If there's still some crates left and this is a
		// base crate, see which crates are still connected.
		if (data.getNumCrates() > 0 && !isCrate(worldObj, x, y - 1, z)) {
			List<HashSet<TileEntityCrate>> crateSets =
					new ArrayList<HashSet<TileEntityCrate>>();
			int checkedChecks = 0;
			neighborLoop: // Suck it :P
			for (ForgeDirection dir : sideDirections) {
				int nx = x + dir.offsetX;
				int nz = z + dir.offsetZ;
				// Continue if this neighbor block is not a crate.
				TileEntityCrate neighborCrate = getCrateAt(worldObj, nx, y, nz);
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
							newPileCrate = getCrateAt(worldObj, newPileCrate.xCoord, newPileCrate.yCoord + 1, newPileCrate.zCoord);
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
		TileEntityCrate crate = getCrateAt(worldObj, x, y, z);
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
			CratePileData checkedPileData = getCratePileData(worldObj, x, y, z);
			if (checkedPileData == null) continue;
			if ((pileData != null && checkedPileData.id != pileData.id) ||
			    (dir != ForgeDirection.DOWN && !crateBelow && isCrate(worldObj, x, y - 1, z)) ||
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
		dropItemStack(worldObj, new ItemStack(BetterStorage.crate), xCoord, yCoord, zCoord);
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
	
	private static void dropItemStack(World world, ItemStack stack, double x, double y, double z) {
		Random random = BetterStorage.random;
		float rx = random.nextFloat() * 0.8F + 0.1F;
		float ry = random.nextFloat() * 0.8F + 0.1F;
		float rz = random.nextFloat() * 0.8F + 0.1F;
		EntityItem item = new EntityItem(world, x + rx, y + ry, z + rz, stack);
		item.motionX = random.nextGaussian() * 0.05F;
		item.motionY = random.nextGaussian() * 0.05F + 0.2F;
		item.motionZ = random.nextGaussian() * 0.05F;
		world.spawnEntityInWorld(item);
	}
	private void dropItemStack(ItemStack stack) {
		dropItemStack(worldObj, stack, xCoord, yCoord, zCoord);
	}
	private void dropItemStacks(List<ItemStack> stacks) {
		for (ItemStack stack : stacks)
			dropItemStack(stack);
	}
	private void dropOverflowContents(CratePileData data) {
		int amount = -data.getFreeSlots();
		dropItemStacks(data.pickAndRemoveItemStacks(amount));
	}
	
	/** Returns the TileEntityCrate at a position in the world, null if there's none. */
	private static TileEntityCrate getCrateAt(World world, int x, int y, int z) {
		TileEntity tileEntity = world.getBlockTileEntity(x, y, z);
		if (!(tileEntity instanceof TileEntityCrate)) return null;
		return (TileEntityCrate)tileEntity;
	}
	/** Returns the pile data of a crate at a position in the world, null if there's none */
	private static CratePileData getCratePileData(World world, int x, int y, int z) {
		TileEntityCrate crate = getCrateAt(world, x, y, z);
		return ((crate != null) ? crate.getPileData() : null);
	}
	/** Returns whether there's a crate at a position in the world. */
	private static boolean isCrate(World world, int x, int y, int z) {
		return (getCrateAt(world, x, y, z) != null);
	}
	
	// IInventory methods
	@Override
	public int getSizeInventory() {
		// BuildCraft calls this client-side.
		if (worldObj.isRemote) return 1;
		return getPileData().blockView.getSizeInventory();
	}
	@Override
	public ItemStack getStackInSlot(int slot) {
		return getPileData().blockView.getStackInSlot(slot);
	}
	@Override
	public ItemStack decrStackSize(int slot, int amount) {
		return getPileData().blockView.decrStackSize(slot, amount);
	}
	@Override
	public ItemStack getStackInSlotOnClosing(int slot) {
		return getPileData().blockView.getStackInSlotOnClosing(slot);
	}
	@Override
	public void setInventorySlotContents(int slot, ItemStack stack) {
		getPileData().blockView.setInventorySlotContents(slot, stack);
	}
	@Override
	public String getInvName() { return "container.crate"; }
	@Override
	public int getInventoryStackLimit() { return 64; }
	@Override
	public boolean isUseableByPlayer(EntityPlayer player) { return false; }
	@Override
	public void openChest() {
		getPileData().blockView.openChest();
	}
	@Override
	public void closeChest() {
		getPileData().blockView.closeChest();
	}
	
	// ISpecialInventory methods
	@Override
	public int addItem(ItemStack stack, boolean doAdd, ForgeDirection from) {
		CratePileData pileData = getPileData();
		int amount = Math.min(pileData.spaceForItem(stack), stack.stackSize);
		if (doAdd && amount > 0) pileData.addItems(stack);
		return amount;
	}
	@Override
	public ItemStack[] extractItem(boolean doRemove, ForgeDirection from, int maxItemCount) {
		List<ItemStack> list = new ArrayList<ItemStack>();
		CratePileData pileData = getPileData();
		for (int i = 0; i < pileData.getNumItems() && maxItemCount > 0; i++) {
			ItemStack stack = pileData.getItemStack(i);
			int amount = Math.min(maxItemCount, stack.stackSize);
			stack = stack.copy();
			stack.stackSize = amount;
			maxItemCount -= amount;
			list.add(stack);
			if (doRemove)
				pileData.removeItems(stack);
		}
		return list.toArray(new ItemStack[list.size()]);
	}
	
}
