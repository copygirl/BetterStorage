package net.mcft.copy.betterstorage.api.crafting;

import net.mcft.copy.betterstorage.block.tileentity.TileEntityCraftingStation;
import net.mcft.copy.betterstorage.inventory.InventoryStacks;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;

public class CraftingSourceStation extends CraftingSourceTileEntity {
	
	public final TileEntityCraftingStation station;
	
	public CraftingSourceStation(TileEntityCraftingStation station, EntityPlayer player) {
		super(station, player);
		this.station = station;
	}
	
	@Override
	public IInventory getInventory() {
		return ((station != null) ? new InventoryStacks(station.contents) : null);
	}
	
}
