package net.mcft.copy.betterstorage.inventory;

import net.mcft.copy.betterstorage.block.tileentity.TileEntityCraftingStation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class InventoryCraftingStation extends InventoryBetterStorage {
	
	private TileEntityCraftingStation entity;
	
	private final ItemStack[] crafting;
	private final ItemStack[] output;
	private final ItemStack[] contents;
	
	public InventoryCraftingStation(TileEntityCraftingStation entity) {
		this("", entity.crafting, entity.output, entity.contents);
		this.entity = entity;
	}
	public InventoryCraftingStation(String name) {
		this(name, new ItemStack[9], new ItemStack[9], new ItemStack[18]);
	}
	private InventoryCraftingStation(String name, ItemStack[] crafting, ItemStack[] output, ItemStack[] contents) {
		super(name);
		this.crafting = crafting;
		this.output = output;
		this.contents = contents;
	}
	
	@Override
	public int getSizeInventory() { return (crafting.length + output.length + contents.length); }
	
	@Override
	public ItemStack getStackInSlot(int slot) {
		if (slot < crafting.length) return crafting[slot];
		else if (slot < crafting.length + output.length)
			return output[slot - crafting.length];
		else return contents[slot - (crafting.length + output.length)];
	}
	@Override
	public void setInventorySlotContents(int slot, ItemStack stack) {
		if (slot < crafting.length) crafting[slot] = stack;
		else if (slot < crafting.length + output.length)
			output[slot - crafting.length] = stack;
		else contents[slot - (crafting.length + output.length)] = stack;
	}
	
	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack) {
		return ((slot < crafting.length) || (slot >= crafting.length + output.length));
	}
	
	@Override
	public boolean isUseableByPlayer(EntityPlayer player) { return true; }
	
	@Override
	public void openChest() {  }
	@Override
	public void closeChest() {  }
	@Override
	public void onInventoryChanged() {  }
	
}
