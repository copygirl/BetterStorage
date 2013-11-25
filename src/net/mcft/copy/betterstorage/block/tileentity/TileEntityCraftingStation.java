package net.mcft.copy.betterstorage.block.tileentity;

import net.mcft.copy.betterstorage.container.ContainerBetterStorage;
import net.mcft.copy.betterstorage.container.ContainerCraftingStation;
import net.mcft.copy.betterstorage.inventory.InventoryCraftingStation;
import net.mcft.copy.betterstorage.inventory.InventoryTileEntity;
import net.mcft.copy.betterstorage.misc.Constants;
import net.mcft.copy.betterstorage.utils.NbtUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class TileEntityCraftingStation extends TileEntityContainer
                                       implements IInventory, ISidedInventory {
	
	public ItemStack[] crafting;
	public ItemStack[] output;
	
	@Override
	protected int getSizeContents() { return 18; }
	
	@Override
	public String getName() { return Constants.containerCraftingStation; }
	
	@Override
	public InventoryTileEntity makePlayerInventory() {
		// Workaround because instance variables get initialized AFTER the
		// parent constructor. This gets called IN the parent constructor.
		crafting = new ItemStack[9];
		output = new ItemStack[9];
		
		return new InventoryTileEntity(this, new InventoryCraftingStation(this));
	}
	
	@Override
	public ContainerBetterStorage createContainer(EntityPlayer player) {
		return new ContainerCraftingStation(player, new InventoryCraftingStation(this));
	}
	
	// IInventory implementation
	
	@Override
	public String getInvName() { return getName(); }
	@Override
	public boolean isInvNameLocalized() { return !shouldLocalizeTitle(); }
	@Override
	public int getInventoryStackLimit() { return 64; }
	@Override
	public int getSizeInventory() { return (getPlayerInventory().getSizeInventory() - 9); }
	
	@Override
	public ItemStack getStackInSlot(int slot) {
		return getPlayerInventory().getStackInSlot(slot + 9); }
	@Override
	public void setInventorySlotContents(int slot, ItemStack stack) {
		getPlayerInventory().setInventorySlotContents(slot + 9, stack); }
	@Override
	public ItemStack decrStackSize(int slot, int amount) {
		return getPlayerInventory().decrStackSize(slot + 9, amount); }
	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack) {
		return getPlayerInventory().isItemValidForSlot(slot + 9, stack); }
	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		return getPlayerInventory().isUseableByPlayer(player); }
	
	@Override
	public ItemStack getStackInSlotOnClosing(int slot) { return null; }
	@Override
	public void openChest() {  }
	@Override
	public void closeChest() {  }
	@Override
	public void onInventoryChanged() {  }
	
	// ISidedInventory implementation
	
	private static int[] slotsAny = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17 };
	private static int[] slotsBottom = { 18, 19, 20, 21, 22, 23, 24, 25, 26 };
	
	@Override
	public int[] getAccessibleSlotsFromSide(int side) { return ((side == 0) ? slotsBottom : slotsAny); }
	@Override
	public boolean canInsertItem(int slot, ItemStack stack, int side) { return (side != 0); }
	@Override
	public boolean canExtractItem(int slot, ItemStack stack, int side) { return (side != 0); }
	
	// Reading from / writing to NBT
	
	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		NbtUtils.readItems(crafting, compound.getTagList("Crafting"));
		NbtUtils.readItems(output, compound.getTagList("Output"));
	}
	@Override
	public void writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		compound.setTag("Crafting", NbtUtils.writeItems(crafting));
		compound.setTag("Output", NbtUtils.writeItems(output));
	}
	
}
