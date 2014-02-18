package net.mcft.copy.betterstorage.container;

import net.mcft.copy.betterstorage.inventory.InventoryCraftingStation;
import net.mcft.copy.betterstorage.inventory.InventoryTileEntity;
import net.mcft.copy.betterstorage.item.recipe.VanillaStationRecipe;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.SlotCrafting;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ContainerCraftingStation extends ContainerBetterStorage {
	
	public InventoryCraftingStation inv;
	
	private InventoryCrafting craftMatrix;
	private SlotCrafting slotCrafting;
	
	private int lastOutputIsReal = 0;
	private int lastProgress = 0;
	
	public ContainerCraftingStation(EntityPlayer player, IInventory inventory) {
		super(player, inventory, 9, 2);
	}
	
	@Override
	public int getHeight() { return 209; }
	
	@Override
	protected void setupInventoryContainer() {
		inv = ((inventory instanceof InventoryCraftingStation)
				? (InventoryCraftingStation)inventory
				: ((InventoryCraftingStation)((InventoryTileEntity)inventory).inventory));
		craftMatrix = new InventoryCrafting(this, 3, 3);
		craftMatrix.stackList = inv.crafting;
		slotCrafting = new SlotCrafting(player, null, null, 0, 0, 0);
		
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
	protected boolean inInventory(int slot) { return (super.inInventory(slot) && (slot >= 18)); }
	@Override
	protected int transferStart(int slot) { return (!inInventory(slot) ? 18 : super.transferStart(slot)); }
	
	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		
		int outputIsReal = (inv.outputIsReal ? 1 : 0);
		sendUpdateIfChanged(0, outputIsReal, lastOutputIsReal);
		lastOutputIsReal = outputIsReal;
		
		int progress = Math.min(inv.progress, inv.craftingTime);
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
		if (!inv.outputIsReal && (inv.currentRecipe != null) && (slotId >= 9) && (slotId < 18) &&
		    (inv.output[slotId - 9] != null) && inv.canTake(player)) {
			if (inv.currentRecipe instanceof VanillaStationRecipe) {
				GameRegistry.onItemCrafted(player, inv.output[slotId - 9], craftMatrix);
				slotCrafting.onCrafting(inv.output[slotId - 9]);
			}
			inv.craft(player);
		}
		return super.slotClick(slotId, button, special, player);
	}
	
	@Override
	public void onSlotChanged(int slot) {
		if (slot < 9) inv.checkRecipe();
	}
	
	@Override
	public boolean func_94530_a(ItemStack stack, Slot slot) {
		// This controls if double-clicking has an effect on the slot.
		return (super.func_94530_a(stack, slot) && ((slot.slotNumber < 9) || (slot.slotNumber >= 18)));
	}
	
}
