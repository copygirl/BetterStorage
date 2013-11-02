package net.mcft.copy.betterstorage.inventory;

import java.util.List;

import net.mcft.copy.betterstorage.BetterStorage;
import net.mcft.copy.betterstorage.Config;
import net.mcft.copy.betterstorage.block.crate.CratePileData;
import net.mcft.copy.betterstorage.misc.Constants;
import net.mcft.copy.betterstorage.utils.StackUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

/** An inventory interface built for machines accessing crate piles. */
public class InventoryCrateBlockView extends InventoryBetterStorage {
	
	private static final int numStacksStored = 4;
	
	private final CratePileData data;
	
	private final ItemStack[] originalStacks = new ItemStack[numStacksStored];
	private final ItemStack[] exposedStacks = new ItemStack[numStacksStored];
	
	public InventoryCrateBlockView(CratePileData data) {
		super(Constants.containerCrate);
		this.data = data;
	}
	
	@Override
	public int getSizeInventory() { return (numStacksStored + 1); }
	
	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack) {
		return ((slot > 0) || (data.spaceForItem(stack) >= stack.stackSize));
	}
	
	@Override
	public ItemStack getStackInSlot(int slot) {
		if ((slot <= 0) || (slot >= getSizeInventory())) return null;
		return exposedStacks[slot - 1];
	}
	
	@Override
	public void setInventorySlotContents(int slot, ItemStack stack) {
		if ((slot < 0) || (slot >= getSizeInventory())) return;
		ItemStack oldStack = null;
		if (slot > 0) {
			oldStack = originalStacks[slot - 1];
			originalStacks[slot - 1] = exposedStacks[slot - 1] = stack;
		}
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
		originalStacks[slot - 1].stackSize = exposedStacks[slot - 1].stackSize -= amount;
		return data.removeItems(stack, amount);
	}
	
	@Override
	public void onInventoryChanged() {
		for (int i = 0; i < numStacksStored; i++)
			if (!ItemStack.areItemStacksEqual(originalStacks[i], exposedStacks[i]))
				setInventorySlotContents(i + 1, exposedStacks[i]);
	}
	
	@Override
	public boolean isUseableByPlayer(EntityPlayer player) { return true; }
	
	@Override
	public void openChest() { }
	@Override
	public void closeChest() { }
	
	public void onUpdate() {
		if (!Config.enableCrateInventoryInterface) return;
		
		for (int i = 0; i < numStacksStored; i++)
			if (!ItemStack.areItemStacksEqual(originalStacks[i], exposedStacks[i])) {
				BetterStorage.log.warning("A crate inventory was modified without onInventoryChanged() being called afterwards.");
				BetterStorage.log.warning("The crate Inventory interface will be disabled until the next restart, to minimize chances of issues.");
				BetterStorage.log.warning("A stack trace will be printed to make it easier to find which mod what caused this.");
				Thread.dumpStack();
				Config.enableCrateInventoryInterface = true;
				return;
			}
		
		List<ItemStack> picked = data.pickItemStacks(numStacksStored);
		for (int i = 0; i < numStacksStored; i++)
			originalStacks[i] = exposedStacks[i] = ((i < picked.size()) ? picked.get(i) : null);
	}
	
}
