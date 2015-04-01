package net.mcft.copy.betterstorage.container;

import invtweaks.api.container.ContainerSection;
import invtweaks.api.container.ContainerSectionCallback;
import invtweaks.api.container.InventoryContainer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.mcft.copy.betterstorage.inventory.InventoryCraftingStation;
import net.mcft.copy.betterstorage.inventory.InventoryTileEntity;
import net.mcft.copy.betterstorage.utils.StackUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@InventoryContainer
public class ContainerCraftingStation extends ContainerBetterStorage {
	
	public InventoryCraftingStation inv;
	
	private int lastOutputIsReal = 0;
	private int lastProgress = 0;
	
	public ContainerCraftingStation(EntityPlayer player, IInventory inventory) {
		super(player, inventory, 9, 2);
	}
	
	@ContainerSectionCallback
	@SideOnly(Side.CLIENT)
	public Map<ContainerSection, List<Slot>> getContainerSections() {
		HashMap<ContainerSection, List<Slot>> map = new HashMap<ContainerSection, List<Slot>>();
		map.put(ContainerSection.CHEST, inventorySlots.subList(17, 17 + getRows() * getColumns()));
		return map;
	}
	
	@Override
	public int getHeight() { return 209; }
	
	@Override
	protected void setupInventoryContainer() {
		inv = ((inventory instanceof InventoryCraftingStation)
				? (InventoryCraftingStation)inventory
				: ((InventoryCraftingStation)((InventoryTileEntity)inventory).inventory));
		
		// Crafting
		for (int y = 0; y < 3; y++)
			for (int x = 0; x < 3; x++)
				addSlotToContainer(new SlotBetterStorage(
						this, inventory, x + y * 3,
						17 + x * 18, 17 + y * 18));
		// Output
		for (int y = 0; y < 3; y++)
			for (int x = 0; x < 3; x++)
				addSlotToContainer(new SlotStationOutput(
						this, inventory, 9 + x + y * 3,
						107 + x * 18, 17 + y * 18));
		// Inventory
		for (int y = 0; y < getRows(); y++)
			for (int x = 0; x < getColumns(); x++)
				addSlotToContainer(new SlotBetterStorage(
						this, inventory, 18 + x + y * getColumns(),
						8 + x * 18, 76 + y * 18));
	}
	
	@Override
	protected boolean inInventory(int slot) { return (super.inInventory(slot) && (slot >= 9)); }
	@Override
	protected int transferStart(int slot) { return (!inInventory(slot) ? 18 : super.transferStart(slot)); }
	
	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		
		int outputIsReal = (inv.outputIsReal ? 1 : 0);
		sendUpdateIfChanged(0, outputIsReal, lastOutputIsReal);
		lastOutputIsReal = outputIsReal;
		
		int progress = ((inv.currentCrafting != null) ? Math.min(inv.progress, inv.currentCrafting.getCraftingTime()) : 0);
		sendUpdateIfChanged(1, progress, lastProgress);
		lastProgress = progress;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void updateProgressBar(int id, int val) {
		if (id == 0) inv.outputIsReal = (val != 0);
		else if (id == 1) inv.progress = val;
	}
	
	@Override
	public ItemStack slotClick(int slotId, int button, int special, EntityPlayer player) {
		if (!inv.outputIsReal && (inv.currentCrafting != null) && (slotId >= 9) && (slotId < 18) &&
		    (inv.output[slotId - 9] != null) && inv.canTake(player) && (special != 3)) {
			ItemStack craftingStack = inv.output[slotId - 9];
			int amount = craftingStack.stackSize;
			// Shift-click: Craft up to one stack of items at once.
			if (special == 1) {
				ItemStack stack;
				int count = 0;
				do {
					count += amount;
					inv.craft(player);
					stack = super.slotClick(slotId, button, special, player);
				} while (!inv.outputIsReal && (inv.currentCrafting != null) &&
				         (inv.output[slotId - 9] != null) && inv.canTake(player) &&
				         ((stack == null) || (StackUtils.matches(stack, inv.output[slotId - 9]))) &&
				         inv.hasItemRequirements() && (count + amount <= craftingStack.getMaxStackSize()));
				return stack;
			// Regular clicking: Craft once. 
			} else if (special < 2) {
				ItemStack holding = player.inventory.getItemStack();
				if ((holding == null) || (StackUtils.matches(holding, craftingStack) &&
				                          (holding.stackSize <= holding.getMaxStackSize() - amount)))
					inv.craft(player);
			// No fancy inventory mechanics in the crafting slots.
			} else return craftingStack;
		}
		return super.slotClick(slotId, button, special, player);
	}
	
	@Override
	public void onSlotChanged(int slot) {
		if (slot < 9) inv.inputChanged();
	}
	
	@Override
	public boolean func_94530_a(ItemStack stack, Slot slot) {
		// This controls if double-clicking has an effect on the slot.
		return (super.func_94530_a(stack, slot) && ((slot.slotNumber < 9) || (slot.slotNumber >= 18)));
	}
	
}
