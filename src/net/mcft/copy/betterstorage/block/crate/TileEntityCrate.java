package net.mcft.copy.betterstorage.block.crate;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import net.mcft.copy.betterstorage.Config;
import net.mcft.copy.betterstorage.api.ICrateStorage;
import net.mcft.copy.betterstorage.api.ICrateWatcher;
import net.mcft.copy.betterstorage.block.tileentity.TileEntityContainer;
import net.mcft.copy.betterstorage.container.ContainerBetterStorage;
import net.mcft.copy.betterstorage.container.ContainerCrate;
import net.mcft.copy.betterstorage.content.Blocks;
import net.mcft.copy.betterstorage.inventory.InventoryCratePlayerView;
import net.mcft.copy.betterstorage.misc.Constants;
import net.mcft.copy.betterstorage.utils.PlayerUtils;
import net.mcft.copy.betterstorage.utils.WorldUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet132TileEntityData;
import net.minecraftforge.common.ForgeDirection;

public class TileEntityCrate extends TileEntityContainer implements IInventory, ICrateStorage {
	
	private static final ForgeDirection[] sideDirections = {
		ForgeDirection.NORTH, ForgeDirection.SOUTH, ForgeDirection.WEST, ForgeDirection.EAST
	};
	
	public static final int slotsPerCrate = 18;
	
	private CratePileData data;
	/** Crate pile id of this crate, used only for saving/loading. */
	private int id = -1;
	
	public int getID() { return id; }
	
	/** Get the pile data for this tile entity. */
	public CratePileData getPileData() {
		if (worldObj.isRemote)
			throw new IllegalStateException("Can't be called client-side.");
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
			markForUpdate();
			if (addCrate) data.addCrate(this);
		} else id = -1;
	}
	/** Destroys all crates above, and makes sure when piles split,
	 *  each pile gets their own CratePileData object. */
	private void checkPileConnections(CratePileData data) {
		int x = xCoord, y = yCoord, z = zCoord;
		// Destroy all crates above.
		TileEntityCrate crateAbove = WorldUtils.get(worldObj, x, y + 1, z, TileEntityCrate.class);
		if ((crateAbove != null) && (crateAbove.data == data)) {
			worldObj.setBlockToAir(x, y + 1, z);
			crateAbove.dropItem(new ItemStack(Blocks.crate));
		}
		// If there's still some crates left and this is a
		// base crate, see which crates are still connected.
		if ((data.getNumCrates() > 0) && !WorldUtils.is(worldObj, x, y - 1, z, Blocks.crate)) {
			List<HashSet<TileEntityCrate>> crateSets =
					new ArrayList<HashSet<TileEntityCrate>>();
			int checkedChecks = 0;
			neighborLoop: // Suck it :P
			for (ForgeDirection dir : sideDirections) {
				int nx = x + dir.offsetX;
				int nz = z + dir.offsetZ;
				// Continue if this neighbor block is not a crate.
				TileEntityCrate neighborCrate = WorldUtils.get(worldObj, nx, y, nz, TileEntityCrate.class);
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
							newPileCrate = WorldUtils.get(worldObj, newPileCrate.xCoord, newPileCrate.yCoord + 1, newPileCrate.zCoord, TileEntityCrate.class);
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
		TileEntityCrate crate = WorldUtils.get(worldObj, x, y, z, TileEntityCrate.class);
		if ((crate == null) || set.contains(crate)) return;
		set.add(crate);
		for (ForgeDirection ndir : sideDirections)
			checkConnections(x + ndir.offsetX, y, z + ndir.offsetZ, set);
	}
	
	@Override
	public void updateEntity() {
		if (worldObj.isRemote || (data != null)) return;
		if (!isInvalid()) getPileData();
	}
	
	public void attemptConnect(ForgeDirection side) {
		if (worldObj.isRemote || (side == ForgeDirection.UP)) return;
		int x = xCoord + side.offsetX;
		int y = yCoord + side.offsetY;
		int z = zCoord + side.offsetZ;
		TileEntityCrate crateClicked = WorldUtils.get(worldObj, x, y, z, TileEntityCrate.class);
		if (crateClicked == null) return;
		CratePileData pileData = crateClicked.getPileData();
		if (pileData.canAdd(this))
			setPileData(pileData, true);
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
	
	// TileEntityContainer stuff
	
	@Override
	protected int getSizeContents() { return 0; }
	@Override
	public String getName() { return Constants.containerCrate; }
	
	@Override
	public void openGui(EntityPlayer player) {
		if (!canPlayerUseContainer(player)) return;
		PlayerUtils.openGui(player, getName(), getColumns(), 2 * Math.min(data.getNumCrates(), 3),
		                    getContainerTitle(), createContainer(player));
	}
	
	@Override
	public ContainerBetterStorage createContainer(EntityPlayer player) {
		return new ContainerCrate(player, new InventoryCratePlayerView(this));
	}
	
	// IInventory implementation

	@Override
	public String getInvName() { return getName(); }
	@Override
	public int getInventoryStackLimit() { return 64; }
	
	@Override
	public int getSizeInventory() {
		if (!Config.enableCrateInventoryInterface) return 0;
		if (worldObj.isRemote) return 1;
		return getPileData().blockView.getSizeInventory();
	}
	
	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack) {
		if (!Config.enableCrateInventoryInterface) return false;
		return getPileData().blockView.isItemValidForSlot(slot, stack);
	}
	
	@Override
	public ItemStack getStackInSlot(int slot) {
		if (!Config.enableCrateInventoryInterface) return null;
		return getPileData().blockView.getStackInSlot(slot);
	}
	@Override
	public void setInventorySlotContents(int slot, ItemStack stack) {
		if (Config.enableCrateInventoryInterface)
			getPileData().blockView.setInventorySlotContents(slot, stack);
	}
	@Override
	public ItemStack getStackInSlotOnClosing(int slot) {
		if (!Config.enableCrateInventoryInterface) return null;
			return getPileData().blockView.getStackInSlotOnClosing(slot);
	}
	@Override
	public ItemStack decrStackSize(int slot, int amount) {
		if (!Config.enableCrateInventoryInterface) return null;
		return getPileData().blockView.decrStackSize(slot, amount);
	}
	@Override
	public void onInventoryChanged() {
		if (Config.enableCrateInventoryInterface)
			getPileData().blockView.onInventoryChanged();
	}
	
	@Override
	public boolean isUseableByPlayer(EntityPlayer player) { return false; }
	@Override
	public boolean isInvNameLocalized() { return false; }
	
	@Override
	public void openChest() { getPileData().blockView.openChest(); }
	@Override
	public void closeChest() { getPileData().blockView.closeChest(); }
	
	// ICrateStorage implementation
	
	@Override
	public Object getInventoryIdentifier(ForgeDirection side) { return getPileData(); }
	@Override
	public List<ItemStack> getContents(ForgeDirection side) { return getPileData().getContents(); }
	@Override
	public int getItemCount(ForgeDirection side, ItemStack identifier) { return getPileData().getItemCount(identifier); }
	@Override
	public int spaceForItem(ForgeDirection side, ItemStack identifier) { return getPileData().spaceForItem(identifier); }
	@Override
	public ItemStack insertItems(ForgeDirection side, ItemStack stack) { return getPileData().addItems(stack); }
	@Override
	public ItemStack extractItems(ForgeDirection side, ItemStack stack) { return getPileData().removeItems(stack); }
	@Override
	public void registerCrateWatcher(ICrateWatcher watcher) { getPileData().addWatcher(watcher); }
	@Override
	public void unregisterCrateWatcher(ICrateWatcher watcher) { getPileData().removeWatcher(watcher); }
	
	// TileEntity synchronization
	
	@Override
	public Packet getDescriptionPacket() {
		NBTTagCompound compound = new NBTTagCompound();
		compound.setInteger("crateId", id);
        return new Packet132TileEntityData(xCoord, yCoord, zCoord, 0, compound);
	}
	@Override
	public void onDataPacket(INetworkManager net, Packet132TileEntityData packet) {
		id = packet.data.getInteger("crateId");
		worldObj.markBlockForRenderUpdate(xCoord, yCoord, zCoord);
	}
	
	// Reading from / writing to NBT
	
	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		id = compound.getInteger("crateId");
	}
	@Override
	public void writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		compound.setInteger("crateId", id);
		
		// Save the pile data on chunk unload if
		// dirty and pops it from the dirty list.
		CratePileData data = getPileData();
		CratePileCollection collection = CratePileCollection.getCollection(worldObj);
		if (collection.getDirtyPiles().remove(data))
			collection.save(data);
	}
	
}
