package net.mcft.copy.betterstorage.tile.entity;

import net.mcft.copy.betterstorage.content.BetterStorageTiles;
import net.mcft.copy.betterstorage.inventory.InventoryCardboardBox;
import net.mcft.copy.betterstorage.inventory.InventoryTileEntity;
import net.mcft.copy.betterstorage.misc.Constants;
import net.mcft.copy.betterstorage.utils.StackUtils;
import net.mcft.copy.betterstorage.utils.WorldUtils;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;

public class TileEntityCardboardBox extends TileEntityContainer {
	
	/** Whether this cardboard box was picked up and placed down again. <br>
	 *  If so, the box won't drop any more, as it's only usable once. */
	public boolean moved = false;
	
	// TileEntityContainer stuff
	
	@Override
	public String getName() { return Constants.containerCardboardBox; }
	
	@Override
	public int getRows() { return 1; }
	
	@Override
	public InventoryTileEntity makePlayerInventory() {
		return new InventoryTileEntity(this, new InventoryCardboardBox(contents));
	}
	
	@Override
	public void onBlockPlaced(EntityLivingBase player, ItemStack stack) {
		super.onBlockPlaced(player, stack);
		// If the cardboard box item has items, set the container contents to them.
		if (!StackUtils.has(stack, "Items")) return;
		ItemStack[] itemContents = StackUtils.getStackContents(stack, contents.length);
		System.arraycopy(itemContents, 0, contents, 0, itemContents.length);
		moved = true;
	}
	
	@Override
	public void onBlockDestroyed() {
		if (!moved) {
			boolean empty = StackUtils.isEmpty(contents);
			ItemStack stack = new ItemStack(BetterStorageTiles.cardboardBox);
			if (!empty) StackUtils.setStackContents(stack, contents);
			// Don't drop an empty cardboard box in creative.
			if (!empty || !brokenInCreative)
				WorldUtils.dropStackFromBlock(this, stack);
		}
		super.onBlockDestroyed();
	}
	
	@Override
	public void dropContents() {
		if (moved) super.dropContents();
	}
	
}
