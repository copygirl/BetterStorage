package net.mcft.copy.betterstorage.addon.thaumcraft;

import net.mcft.copy.betterstorage.misc.BetterStorageResource;
import net.mcft.copy.betterstorage.misc.Constants;
import net.mcft.copy.betterstorage.tile.entity.TileEntityReinforcedChest;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TileEntityThaumiumChest extends TileEntityReinforcedChest {
	
	@Override
	protected String getConnectableName() { return Constants.containerThaumiumChest; }
	
	@Override
	@SideOnly(Side.CLIENT)
	public ResourceLocation getResource() {
		return new BetterStorageResource("textures/models/chest" + (isConnected() ? "_large/" : "/") + "thaumium.png");
	}
	
	@Override
	public int getColumns() { return 17; }
	
	/* In Thaumcraft 4, aura / nodes / flux changed.
	
	private void createFlux() {
		
		// Every 10 seconds.
		if (++ticksExisted % 200 != 0) return;
		
		// Count items over normal backpack capacity.
		IInventory inventory = getPlayerInventory();
		int count = -(getRows() * Config.reinforcedChestColumns);
		for (int i = 0; i < inventory.getSizeInventory(); i++)
			if (inventory.getStackInSlot(i) != null) count++;
		
		// If there's more items in there than a reinforced chest can hold,
		// randomly generate some flux. The more items, the more likely.
		if (RandomUtils.getInt((isConnected() ? 48 : 24)) > count) return;
		
		int auraId = ThaumcraftApi.getClosestAuraWithinRange(worldObj, xCoord + 0.5, yCoord + 0.5, zCoord + 0.5, 640);
		if (auraId == -1) return;
		ThaumcraftApi.queueNodeChanges(auraId, 0, 0, false, (new ObjectTags()).add(EnumTag.VOID, 2), 0, 0, 0);
		
	}
	
	private void fluxEffects() {
		
		boolean open = (getPlayersUsing() > 0);
		
		double x = xCoord + 0.5;
		double y = yCoord + 0.5;
		double z = zCoord + 0.5;
		TileEntityConnectable connectable = getConnectedTileEntity();
		if (connectable != null) {
			x = (x + connectable.xCoord + 0.5) / 2;
			z = (z + connectable.zCoord + 0.5) / 2;
		}
		
		// Every 5 seconds if container is being used, 25 otherwise.
		if (ticksExisted % ((open ? 5 : 25) * 20) != 0) return;
		
		// Get closest aura node.
		int auraId = ThaumcraftApi.getClosestAuraWithinRange(worldObj, x, y, z, 640);
		if (auraId == -1) return;
		AuraNode aura = ThaumcraftApi.getNodeCopy(auraId);
		
		// Get and count flux.
		EnumTag[] aspects = aura.flux.getAspectsSortedAmount();
		int total = 0;
		for (int i = 0; i < aspects.length; i++)
			total += aura.flux.tags.get(aspects[i]);
		
		// The higher the flux is, the bigger the chance.
		if (RandomUtils.getInt(32, 448) > total) return;
		
		// Rearrange some items.
		rearrangeItems();
		
		// Play sound effect.
		worldObj.playSoundEffect(x, y, z, "random.breath", 0.5F, 0.5F);
		
	}
	
	private void rearrangeItems() {
		
		IInventory inventory = getPlayerInventory();
		
		// Collect indices of all slots with items inside.
		List<Integer> indices = new ArrayList<Integer>();
		for (int i = 0; i < inventory.getSizeInventory(); i++)
			if (inventory.getStackInSlot(i) != null) indices.add(i);
		if (indices.size() <= 0) return;
		
		// Get a random item stack.
		int index = indices.get(RandomUtils.getInt(indices.size()));
		ItemStack item = inventory.getStackInSlot(index).copy();
		
		// Get the item stack of a random slot (may be null).
		int switchIndex;
		do { switchIndex = RandomUtils.getInt(inventory.getSizeInventory()); }
		while (switchIndex == index);
		ItemStack switchItem = ItemStack.copyItemStack(inventory.getStackInSlot(switchIndex));
		
		if ((switchItem == null) && (item.stackSize > 1) &&
		    RandomUtils.getBoolean(0.5)) {
			
			// Move some items to an empty stack.
			int count = RandomUtils.getInt(1, item.stackSize / 8);
			switchItem = item.splitStack(count);
			
		} else if (StackUtils.matches(item, switchItem) &&
		           (switchItem.stackSize < item.stackSize) &&
		           RandomUtils.getBoolean(0.5)) {
			
			// Move some items to a stack with the same item.
			int count = RandomUtils.getInt(1, item.stackSize - switchItem.stackSize - 1);
			item.stackSize -= count;
			switchItem.stackSize += count;
			
		} else {
			// Switch items.
			ItemStack temp = item;
			item = switchItem;
			switchItem = temp;
		}
		
		// Update slots.
		inventory.setInventorySlotContents(index, item);
		inventory.setInventorySlotContents(switchIndex, switchItem);
		
	}
	*/
	
}
