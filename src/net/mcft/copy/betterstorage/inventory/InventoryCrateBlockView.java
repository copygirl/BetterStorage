package net.mcft.copy.betterstorage.inventory;

import net.mcft.copy.betterstorage.block.crate.CratePileData;
import net.mcft.copy.betterstorage.misc.Constants;
import net.mcft.copy.betterstorage.utils.StackUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

/** An inventory interface built for machines accessing crate piles. */
public class InventoryCrateBlockView extends InventoryBetterStorage {
	
	private CratePileData data;
	
	public InventoryCrateBlockView(CratePileData data) {
		super(Constants.containerCrate);
		this.data = data;
	}
	
	@Override
	public int getSizeInventory() { return data.getNumItems() + 1; }
	
	@Override
	public ItemStack getStackInSlot(int slot) {
		if ((slot <= 0) || (slot >= getSizeInventory())) return null;
		ItemStack stack = data.getItemStack(slot - 1).copy();
		stack.stackSize = Math.min(stack.stackSize, stack.getMaxStackSize());
		return stack;
	}
	
	@Override
	public void setInventorySlotContents(int slot, ItemStack stack) {
		if ((slot < 0) || (slot >= getSizeInventory())) return;
		ItemStack oldStack = getStackInSlot(slot);
		if (oldStack != null) {
			// If the two stacks match, just add/remove the difference.
			if (StackUtils.matches(oldStack, stack)) {
				int count = stack.stackSize - oldStack.stackSize;
				if (count > 0) {
					ItemStack add = stack.copy();
					add.stackSize = count;
					data.addItems(add);
				} else if (count < 0) {
					ItemStack remove = stack.copy();
					remove.stackSize = -count;
					data.removeItems(remove);
				}
				return;
			}
			data.removeItems(oldStack);
		}
		data.addItems(stack);
	}
	
	@Override
	public ItemStack decrStackSize(int slot, int amount) {
		ItemStack stack = getStackInSlot(slot);
		if (stack == null) return null;
		amount = Math.min(amount, stack.stackSize);
		return data.removeItems(stack, amount);
	}
	
	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack) {
		return ((slot != 0) || (data.getFreeSlots() > 0));
	}
	
	@Override
	public boolean isUseableByPlayer(EntityPlayer player) { return false; }
	
	@Override
	public void onInventoryChanged() { }
	@Override
	public void openChest() { }
	@Override
	public void closeChest() { }
	
}
