package net.mcft.copy.betterstorage.block.tileentity;

import net.mcft.copy.betterstorage.inventory.InventoryCardboardBox;
import net.mcft.copy.betterstorage.inventory.InventoryTileEntity;
import net.mcft.copy.betterstorage.misc.Constants;

public class TileEntityCardboardBox extends TileEntityContainer {
	
	/** Whether this cardboard box was picked up and placed down again. <br>
	 *  If so, the box won't drop any more, as it's only usable once. */
	public boolean moved = false;
	
	public boolean brokenInCreative = false;
	
	// TileEntityContainer stuff
	
	@Override
	public String getName() { return Constants.containerCardboardBox; }
	
	@Override
	public int getRows() { return 1; }
	
	@Override
	public InventoryTileEntity makePlayerInventory() {
		return new InventoryTileEntity(this, new InventoryCardboardBox(contents));
	}
	
}
