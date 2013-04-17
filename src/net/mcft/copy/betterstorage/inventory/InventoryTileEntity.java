package net.mcft.copy.betterstorage.inventory;

import net.mcft.copy.betterstorage.block.TileEntityContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

/** An inventory that is connected to a TileEntityInventoy. */
public class InventoryTileEntity extends InventoryBetterStorage {
	
	public final TileEntityContainer tileEntity;
	public final IInventory inventory;
	public final int columns, rows;
	
	public InventoryTileEntity(TileEntityContainer tileEntity, IInventory inventory) {
		this.tileEntity = tileEntity;
		this.inventory = inventory;

		columns = tileEntity.getColumns();
		rows = inventory.getSizeInventory() / columns;
	}
	public InventoryTileEntity(TileEntityContainer tileEntity, ItemStack[]... allContents) {
		this(tileEntity, new InventoryWrapper(allContents));
	}
	
	@Override
	public String getInvName() { return tileEntity.getContainerTitle(); }
	@Override
	public boolean isInvNameLocalized() { return !tileEntity.shouldLocalizeTitle(); }
	
	@Override
	public int getSizeInventory() { return inventory.getSizeInventory(); }
	@Override
	public ItemStack getStackInSlot(int slot) { return inventory.getStackInSlot(slot); }
	@Override
	public void setInventorySlotContents(int slot, ItemStack stack) {
		inventory.setInventorySlotContents(slot, stack);
	}
	@Override
	public void onInventoryChanged() {  }
	
	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		return tileEntity.canPlayerUseContainer(player);
	}
	@Override
	public void openChest() {
		tileEntity.onContainerOpened();
	}
	@Override
	public void closeChest() {
		tileEntity.onContainerClosed();
	}
	
}
