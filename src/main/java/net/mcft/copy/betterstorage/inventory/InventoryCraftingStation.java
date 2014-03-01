package net.mcft.copy.betterstorage.inventory;

import java.util.Arrays;

import net.mcft.copy.betterstorage.api.crafting.BetterStorageCrafting;
import net.mcft.copy.betterstorage.api.crafting.ContainerInfo;
import net.mcft.copy.betterstorage.api.crafting.CraftingSourceTileEntity;
import net.mcft.copy.betterstorage.api.crafting.ICraftingSource;
import net.mcft.copy.betterstorage.api.crafting.IRecipeInput;
import net.mcft.copy.betterstorage.api.crafting.StationCrafting;
import net.mcft.copy.betterstorage.config.GlobalConfig;
import net.mcft.copy.betterstorage.item.recipe.VanillaStationCrafting;
import net.mcft.copy.betterstorage.tile.entity.TileEntityCraftingStation;
import net.mcft.copy.betterstorage.utils.InventoryUtils;
import net.mcft.copy.betterstorage.utils.StackUtils;
import net.mcft.copy.betterstorage.utils.WorldUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerDestroyItemEvent;

public class InventoryCraftingStation extends InventoryBetterStorage {
	
	public TileEntityCraftingStation entity = null;
	
	public final ItemStack[] crafting;
	public final ItemStack[] output;
	public final ItemStack[] contents;
	
	public final ItemStack[] lastOutput;
	
	public StationCrafting currentCrafting = null;
	public boolean outputIsReal = false;
	public int progress = 0;
	
	private boolean hasRequirements = false;
	private boolean checkHasRequirements = true;
	
	public InventoryCraftingStation(TileEntityCraftingStation entity) {
		this("", entity.crafting, entity.output, entity.contents);
		this.entity = entity;
	}
	public InventoryCraftingStation(String name) {
		this(name, new ItemStack[9], new ItemStack[9], new ItemStack[18]);
	}
	private InventoryCraftingStation(String name, ItemStack[] crafting, ItemStack[] output, ItemStack[] contents) {
		super(name);
		this.crafting = crafting;
		this.output = output;
		this.contents = contents;
		
		lastOutput = new ItemStack[output.length];
		updateLastOutput();
		
		onInventoryChanged();
	}
	
	public void update() {
		if (!outputIsReal && (currentCrafting != null) &&
		    (((progress < currentCrafting.getCraftingTime()) ||
		      (progress < GlobalConfig.stationAutocraftDelaySetting.getValue())))) progress++; 
	}
	
	/** Called whenever the crafting input changes. */
	public void inputChanged() {
		currentCrafting = BetterStorageCrafting.findMatchingStationCrafting(crafting);
		if (currentCrafting == null)
			currentCrafting = VanillaStationCrafting.findVanillaRecipe(this);
		if (!outputIsReal) {
			if (currentCrafting != null) {
				ItemStack[] craftingOutput = currentCrafting.getOutput();
				for (int i = 0; i < output.length; i++)
					output[i] = ((i < craftingOutput.length) ? ItemStack.copyItemStack(craftingOutput[i]) : null);
			} else Arrays.fill(output, null);
		}
		updateLastOutput();
	}
	
	/** Called when an item is removed from the output
	 *  slot while it doesn't store any real items. */
	public void craft(EntityPlayer player) {
		ICraftingSource source = new CraftingSourceTileEntity(entity, player);
		currentCrafting.craft(source);
		IRecipeInput[] requiredInput = currentCrafting.getCraftRequirements();
		for (int i = 0; i < crafting.length; i++)
			if (crafting[i] != null)
				crafting[i] = craftSlot(crafting[i], requiredInput[i], player, false);
		int requiredExperience = currentCrafting.getRequiredExperience();
		if ((requiredExperience != 0) && (player != null) && !player.capabilities.isCreativeMode)
			player.experienceLevel -= requiredExperience;
		pullRequired(requiredInput, false);
		outputIsReal = !outputEmpty();
		progress = 0;
		inputChanged();
		checkHasRequirements = true;
	}
	
	private ItemStack craftSlot(ItemStack stack, IRecipeInput requiredInput, EntityPlayer player, boolean simulate) {
		if (simulate) stack = stack.copy();
		ContainerInfo containerInfo = new ContainerInfo();
		requiredInput.craft(stack, containerInfo);
		ItemStack containerItem = ItemStack.copyItemStack(containerInfo.getContainerItem());
		
		boolean removeStack = false;
		if (stack.stackSize <= 0) {
			// Item stack is depleted.
			removeStack = true;
		} else if (stack.getItem().isDamageable() && (stack.getItemDamage() > stack.getMaxDamage())) {
			// Item stack is destroyed.
			removeStack = true;
			if (player != null)
				MinecraftForge.EVENT_BUS.post(new PlayerDestroyItemEvent(player, stack));
		}
		
		// If the stack has been depleted, set it
		// to either null, or the container item.
		if (removeStack) {
			if (!containerInfo.doesLeaveCrafting()) {
				stack = containerItem;
				containerItem = null;
			} else stack = null;
		}
		
		if ((containerItem != null) && !simulate) {
			// Try to add the container item to the internal storage, or spawn the item in the world.
			if (!InventoryUtils.tryAddItemToInventory(containerItem, new InventoryStacks(contents), true) && (entity != null))
				WorldUtils.spawnItem(entity.worldObj, entity.xCoord + 0.5, entity.yCoord + 0.5, entity.zCoord + 0.5, containerItem);
		}
		return stack;
	}
	
	/** Pull items required for the recipe from the internal inventory. Returns if successful. */
	public boolean pullRequired(IRecipeInput[] requiredInput, boolean simulate) {
		ItemStack[] contents = (simulate ? this.contents.clone() : this.contents);
		ItemStack[] crafting = (simulate ? this.crafting.clone() : this.crafting);
		boolean success = true;
		craftingLoop:
		for (int i = 0; i < crafting.length; i++) {
			ItemStack stack = crafting[i];
			IRecipeInput required = requiredInput[i];
			if (required != null) {
				int currentAmount = 0;
				if ((stack != null) && simulate)
					stack = craftSlot(stack, required, null, true);
				if (stack != null) {
					if (!required.matches(stack)) return false;
					currentAmount = stack.stackSize;
				}
				int requiredAmount = (required.getAmount() - currentAmount);
				if (requiredAmount <= 0) continue;
				for (int j = 0; j < contents.length; j++) {
					ItemStack contentsStack = contents[j];
					if (contentsStack == null) continue;
					if ((stack == null) ? required.matches(contentsStack)
					                    : StackUtils.matches(stack, contentsStack)) {
						int amount = Math.min(contentsStack.stackSize, requiredAmount);
						crafting[i] = stack = StackUtils.copyStack(contentsStack, (currentAmount += amount));
						contents[j] =         StackUtils.copyStack(contentsStack, contentsStack.stackSize - amount);
						if ((requiredAmount -= amount) <= 0)
							continue craftingLoop;
					}
				}
			} else if (stack == null)
				continue;
			success = false;
			if (!simulate) break;
		}
		return success;
	}
	
	/** Returns if items can be taken out of the output slots. */
	public boolean canTake(EntityPlayer player) {
		return (outputIsReal || ((currentCrafting != null) &&
		                         (currentCrafting.canCraft(new CraftingSourceTileEntity(entity, player))) &&
		                         (progress >= currentCrafting.getCraftingTime()) && hasRequiredExperience(player) &&
		                          ((player != null) || ((progress >= GlobalConfig.stationAutocraftDelaySetting.getValue()) &&
		                                                hasRequirements()))));
	}
	
	private boolean hasRequiredExperience(EntityPlayer player) {
		int requiredExperience = currentCrafting.getRequiredExperience();
		return ((requiredExperience == 0) ||
		        ((player != null) && (player.capabilities.isCreativeMode ||
		                              (player.experienceLevel >= requiredExperience))));
	}
	
	/** Returns if the crafting station has the items
	 *  required in its inventory to craft the recipe again. */
	private boolean hasRequirements() {
		if (checkHasRequirements) {
			hasRequirements = pullRequired(currentCrafting.getCraftRequirements(), true);
			checkHasRequirements = false;
		}
		return hasRequirements;
	}
	
	// IInventory implementation
	
	@Override
	public int getSizeInventory() { return (crafting.length + output.length + contents.length); }
	
	@Override
	public ItemStack getStackInSlot(int slot) {
		if (slot < crafting.length) return crafting[slot];
		else if (slot < crafting.length + output.length)
			return output[slot - crafting.length];
		else return contents[slot - (crafting.length + output.length)];
	}
	@Override
	public void setInventorySlotContents(int slot, ItemStack stack) {
		if (slot < crafting.length) crafting[slot] = stack;
		else if (slot < crafting.length + output.length)
			output[slot - crafting.length] = stack;
		else contents[slot - (crafting.length + output.length)] = stack;
	}
	
	@Override
	public boolean isUseableByPlayer(EntityPlayer player) { return true; }
	
	@Override
	public void openChest() {  }
	@Override
	public void closeChest() {  }
	
	@Override
	public void onInventoryChanged() {
		
		boolean updateLastOutput = false;
		
		// See if items were taken out from the output.
		if (outputChanged()) {
			// If the output is ghost items, there is a recipe and we're
			// on the server, craft the items and decrement the input.
			if (!outputIsReal && (currentCrafting != null) && (entity != null))
				craft(null);
			updateLastOutput = true;
		}
		
		if (outputEmpty()) {
			// Otherwise set the output to not be real.
			outputIsReal = false;
			if (currentCrafting != null) {
				// Fill it with ghost output from the recipe.
				ItemStack[] recipeOutput = currentCrafting.getOutput();
				for (int i = 0; i < recipeOutput.length; i++)
					output[i] = ItemStack.copyItemStack(recipeOutput[i]);
			}
			updateLastOutput = true;
		}
		
		checkHasRequirements = true;
		if (updateLastOutput)
			updateLastOutput();
		
	}
	
	// Utility functions

	private void updateLastOutput() {
		for (int i = 0; i < output.length; i++)
			lastOutput[i] = ItemStack.copyItemStack(output[i]);
	}
	private boolean outputChanged() {
		for (int i = 0; i < output.length; i++)
			if (!ItemStack.areItemStacksEqual(output[i], lastOutput[i]))
				return true;
		return false;
	}
	private boolean outputEmpty() {
		for (int i = 0; i < output.length; i++)
			if (output[i] != null) return false;
		return true;
	}
	
}
