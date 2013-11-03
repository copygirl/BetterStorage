package net.mcft.copy.betterstorage.inventory;

import java.util.List;

import net.mcft.copy.betterstorage.BetterStorage;
import net.mcft.copy.betterstorage.Config;
import net.mcft.copy.betterstorage.api.ICrateWatcher;
import net.mcft.copy.betterstorage.block.crate.CratePileData;
import net.mcft.copy.betterstorage.misc.Constants;
import net.mcft.copy.betterstorage.utils.StackUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

/** An inventory interface built for machines accessing crate piles. */
public class InventoryCrateBlockView extends InventoryBetterStorage implements ICrateWatcher {
	
	private static final int numStacksStored = 4;
	
	private final CratePileData data;
	
	private final ItemStack[] originalStacks = new ItemStack[numStacksStored];
	private final ItemStack[] exposedStacks = new ItemStack[numStacksStored];
	
	/** If the crate contents were changed outside of this block view. */
	private boolean changed = true;
	/** If the block view is currently modifying items, so changed will not be set. */
	private boolean isModifying = false;
	
	/** If the crate was accessed by something. Resets every update. */
	private boolean accessed = false;
	/** Used to find mods which cause problems by not calling
	 *  onInventoryChanged after done manipulating stacks. */
	private Throwable offending = null;
	
	public InventoryCrateBlockView(CratePileData data) {
		super(Constants.containerCrate);
		this.data = data;
		data.addWatcher(this);
	}
	
	@Override
	public int getSizeInventory() { return (numStacksStored + 1); }
	
	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack) {
		return ((slot > 0) || (data.spaceForItem(stack) >= stack.stackSize));
	}
	
	@Override
	public ItemStack getStackInSlot(int slot) {
		access();
		if ((slot <= 0) || (slot >= getSizeInventory())) return null;
		return exposedStacks[slot - 1];
	}
	
	@Override
	public void setInventorySlotContents(int slot, ItemStack stack) {
		access();
		if ((slot < 0) || (slot >= getSizeInventory())) return;
		ItemStack oldStack = null;
		if (slot > 0) {
			oldStack = originalStacks[slot - 1];
			exposedStacks[slot - 1] = stack;
			originalStacks[slot - 1] = ItemStack.copyItemStack(stack);
		}
		isModifying = true;
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
				isModifying = false;
				return;
			}
			data.removeItems(oldStack);
		}
		data.addItems(stack);
		isModifying = false;
	}
	
	@Override
	public ItemStack decrStackSize(int slot, int amount) {
		ItemStack stack = getStackInSlot(slot);
		if (stack == null) return null;
		amount = Math.min(amount, stack.stackSize);
		originalStacks[slot - 1].stackSize = exposedStacks[slot - 1].stackSize -= amount;
		isModifying = true;
		ItemStack result = data.removeItems(stack, amount);
		isModifying = false;
		return result;
	}
	
	@Override
	public void onInventoryChanged() {
		for (int i = 0; i < numStacksStored; i++)
			if (!ItemStack.areItemStacksEqual(originalStacks[i], exposedStacks[i]))
				setInventorySlotContents(i + 1, exposedStacks[i]);
		offending = null;
	}
	
	@Override
	public boolean isUseableByPlayer(EntityPlayer player) { return true; }
	
	@Override
	public void openChest() { }
	@Override
	public void closeChest() { }
	
	
	public void onUpdate() {
		
		if (!Config.enableCrateInventoryInterface || !accessed) return;
		accessed = false;
		
		// Check for modifications done to the inventory
		// without onInventoryChanged being called.
		if (offending != null)
			for (int i = 0; i < numStacksStored; i++)
				if (!ItemStack.areItemStacksEqual(originalStacks[i], exposedStacks[i])) {
					BetterStorage.log.warning("A crate inventory was modified without onInventoryChanged() being called afterwards.");
					BetterStorage.log.warning("The crate Inventory interface will be disabled until the next restart, to minimize chances of issues.");
					BetterStorage.log.warning("A stack trace will be printed to make it easier to find which mod may have caused this.");
					offending.printStackTrace();
					Config.enableCrateInventoryInterface = false;
					return;
				}
		
		// Cause new stacks to be picked the
		// next time something is accessed.
		changed = true;
		
	}
	
	private void access() {
		accessed = true;
		if (offending == null)
			offending = new Throwable().fillInStackTrace();
		
		if (changed) {
			// Pick a new set of random stacks from the crate.
			List<ItemStack> picked = data.pickItemStacks(numStacksStored);
			for (int i = 0; i < numStacksStored; i++) {
				exposedStacks[i] = ((i < picked.size()) ? picked.get(i) : null);
				originalStacks[i] = ItemStack.copyItemStack(exposedStacks[i]);
			}
			changed = false;
		}
	}
	
	@Override
	public void onCrateItemsModified(ItemStack stack) {
		if (!isModifying) changed = true;
	}
	
}
