package net.mcft.copy.betterstorage.inventory;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.mcft.copy.betterstorage.BetterStorage;
import net.mcft.copy.betterstorage.ItemIdentifier;
import net.mcft.copy.betterstorage.block.CratePileData;
import net.mcft.copy.betterstorage.block.TileEntityCrate;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

/** An inventory interface built for players accessing crate piles. */
public class InventoryCratePlayerView extends InventoryBetterStorage {
	
	private static final int inventoryMaxSize = 9 * 6;
	
	public final CratePileData data;
	public final TileEntityCrate crate;
	
	private ItemStack[] tempContents;
	private Map<ItemIdentifier, Integer> itemCounts = new HashMap<ItemIdentifier, Integer>();
	
	public InventoryCratePlayerView(TileEntityCrate crate) {
		super("container.crate");
		this.data = crate.getPileData();
		this.crate = crate;
		
		int count = Math.min(data.getCapacity(), inventoryMaxSize);
		tempContents = new ItemStack[count];
	}
	
	private int getItemCount(ItemIdentifier item) {
		if (item == null) return 0;
		if (itemCounts.containsKey(item))
			return itemCounts.get(item);
		else return 0;
	}
	private int getItemCount(ItemStack item) {
		if (item == null) return 0;
		return getItemCount(new ItemIdentifier(item));
	}
	
	private void setItemCount(ItemIdentifier item, int count) {
		if (item == null) return;
		if (count > 0) itemCounts.put(item, count);
		else itemCounts.remove(item);
	}
	private void setItemCount(ItemStack item, int count) {
		if (item == null) return;
		setItemCount(new ItemIdentifier(item), count);
	}
	
	private void setItemCountRelative(ItemIdentifier item, int count) {
		if (item == null) return;
		setItemCount(item, getItemCount(item) + count);
	}
	private void setItemCountRelative(ItemStack item, int count) {
		if (item == null) return;
		setItemCountRelative(new ItemIdentifier(item), count);
	}
	
	@Override
	public int getSizeInventory() { return tempContents.length; }
	
	@Override
	public ItemStack getStackInSlot(int slot) {
		if (slot < 0 || slot >= getSizeInventory()) return null;
		ItemStack stack = tempContents[slot];
		if (stack != null) {
			ItemStack oldStack = stack.copy();
			int difference = data.getItemCount(stack) - getItemCount(stack);
			difference = Math.min(difference, stack.getMaxStackSize() - stack.stackSize);
			difference = Math.max(difference, -stack.stackSize);
			if (difference != 0) {
				setItemCountRelative(stack, difference);
				stack.stackSize += difference;
				if (stack.stackSize <= 0) {
					tempContents[slot] = null;
					stack = null;
				}
			}
		} else {
			int totalStacks = 0;
			List<Entry<ItemIdentifier, Integer>> stacks =
					new ArrayList<Entry<ItemIdentifier, Integer>>();
			for (ItemStack contentsStack : data) {
				int count = contentsStack.stackSize - getItemCount(contentsStack);
				if (count <= 0) continue;
				ItemIdentifier item = new ItemIdentifier(contentsStack);
				int numStacks = item.calcNumStacks(count);
				stacks.add(new AbstractMap.SimpleEntry(item, numStacks));
				totalStacks += numStacks;
			}
			for (Entry<ItemIdentifier, Integer> entry : stacks) {
				ItemIdentifier item = entry.getKey();
				int numStacks = entry.getValue();
				if (BetterStorage.random.nextInt(totalStacks) < numStacks) {
					ItemStack contentsStack = data.getItemStack(item);
					int count = contentsStack.stackSize - getItemCount(contentsStack);
					stack = contentsStack.copy();
					stack.stackSize = Math.min(count, stack.getMaxStackSize());
					setItemCountRelative(item, stack.stackSize);
					tempContents[slot] = stack;
					break;
				}
				totalStacks -= numStacks;
			}
		}
		return stack;
	}
	
	@Override
	public void setInventorySlotContents(int slot, ItemStack stack) {
		if (slot < 0 || slot >= getSizeInventory()) return;
		ItemStack oldStack = getStackInSlot(slot);
		if (oldStack != null) {
			setItemCountRelative(oldStack, -oldStack.stackSize);
			data.removeItems(oldStack, oldStack.stackSize);
		}
		if (stack != null) {
			stack.stackSize = Math.min(stack.stackSize, Math.min(data.spaceForItem(stack),
			                                                     stack.getMaxStackSize()));
			if (stack.stackSize == 0) return;
			setItemCountRelative(stack, stack.stackSize);
			data.addItems(stack);
		}
		tempContents[slot] = ItemStack.copyItemStack(stack);
	}
	
	@Override
	public ItemStack decrStackSize(int slot, int amount) {
		ItemStack stack = getStackInSlot(slot);
		if (stack == null) return null;
		amount = Math.min(amount, stack.stackSize);
		stack.stackSize -= amount;
		if (stack.stackSize <= 0)
			tempContents[slot] = null;
		setItemCountRelative(stack, -amount);
		ItemStack result = data.removeItems(stack, amount);
		return result;
	}
	
	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		int x = crate.xCoord;
		int y = crate.yCoord;
		int z = crate.zCoord;
		return (player.worldObj.getBlockTileEntity(x, y, z) == crate &&
				player.getDistanceSq(x + 0.5, y + 0.5, z + 0.5) < 64.0 &&
				getSizeInventory() <= data.getCapacity());
	}
	
	@Override
	public void onInventoryChanged() { }
	@Override
	public void openChest() { }
	@Override
	public void closeChest() { }
	
}
