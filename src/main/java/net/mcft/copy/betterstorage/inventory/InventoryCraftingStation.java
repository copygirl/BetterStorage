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
import net.mcft.copy.betterstorage.misc.FakePlayer;
import net.mcft.copy.betterstorage.tile.entity.TileEntityCraftingStation;
import net.mcft.copy.betterstorage.utils.InventoryUtils;
import net.mcft.copy.betterstorage.utils.StackUtils;
import net.mcft.copy.betterstorage.utils.WorldUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.SlotCrafting;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerDestroyItemEvent;
import cpw.mods.fml.common.FMLCommonHandler;

public class InventoryCraftingStation extends InventoryBetterStorage {
	
	public TileEntityCraftingStation entity = null;
	
	public final ItemStack[] crafting;
	public final ItemStack[] output;
	public final ItemStack[] contents;
	
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
	}
	
	public void update() {
		if (!outputIsReal && (currentCrafting != null)) {
			if (progress >= Math.max(currentCrafting.getCraftingTime(), GlobalConfig.stationAutocraftDelaySetting.getValue())) {
				if ((entity != null) && entity.isRedstonePowered() &&
				    (currentCrafting.getRequiredExperience() == 0) && hasItemRequirements())
					craft(null);
			} else progress++;
		}
	}
	
	/** Called whenever the crafting input changes. */
	public void inputChanged() {
		progress = 0;
		currentCrafting = BetterStorageCrafting.findMatchingStationCrafting(crafting);
		if ((currentCrafting == null) && GlobalConfig.enableStationVanillaCraftingSetting.getValue())
			currentCrafting = VanillaStationCrafting.findVanillaRecipe(this);
		updateGhostOutput();
	}
	
	public void updateGhostOutput() {
		if (outputIsReal) return;
		if (currentCrafting != null) {
			ItemStack[] craftingOutput = currentCrafting.getOutput();
			for (int i = 0; i < output.length; i++)
				output[i] = ((i < craftingOutput.length) ? ItemStack.copyItemStack(craftingOutput[i]) : null);
		} else Arrays.fill(output, null);
	}
	
	/** Called when an item is removed from the output slot while it doesn't
	 *  store any real items. Returns if the recipe can be crafted again.*/
	private boolean craft(EntityPlayer player, boolean simulate) {
		ItemStack[] contents = (simulate ? this.contents.clone() : this.contents);
		ItemStack[] crafting = (simulate ? this.crafting.clone() : this.crafting);
		if (simulate)
			for (int i = 0; i < crafting.length; i++)
				crafting[i] = ItemStack.copyItemStack(crafting[i]);
		
		if (currentCrafting instanceof VanillaStationCrafting) {
			boolean unset = false;
			if (player == null) {
				player = FakePlayer.get(entity);
				unset = true;
			}
			
			ItemStack craftOutput = (simulate ? output[4].copy() : output[4]);
			IInventory craftMatrix = new InventoryStacks(crafting);
			FMLCommonHandler.instance().firePlayerCraftingEvent(player, craftOutput, craftMatrix);
			new CustomSlotCrafting(player, craftOutput);
			
			if (unset) {
				FakePlayer.unset();
				player = null;
			}
		}
		
		ICraftingSource source = new CraftingSourceTileEntity(entity, player);
		currentCrafting.craft(source);
		
		IRecipeInput[] requiredInput = currentCrafting.getCraftRequirements();
		for (int i = 0; i < crafting.length; i++)
			if (crafting[i] != null)
				crafting[i] = craftSlot(crafting[i], requiredInput[i], player, simulate);
		
		boolean pulled = pullRequired(contents, crafting, requiredInput);
		
		if (!simulate) {
			int requiredExperience = currentCrafting.getRequiredExperience();
			if ((requiredExperience != 0) && (player != null) && !player.capabilities.isCreativeMode)
				player.experienceLevel -= requiredExperience;
			
			outputIsReal = !outputEmpty();
			progress = 0;
			inputChanged();
			checkHasRequirements = true;
		}
		
		return pulled;
	}
	public void craft(EntityPlayer player) {
		craft(player, false);
	}
	public boolean simulateCraft() {
		boolean canCraftAgain = craft(FakePlayer.get(entity), true);
		FakePlayer.unset();
		return canCraftAgain;
	}
	private static class CustomSlotCrafting extends SlotCrafting {
		public CustomSlotCrafting(EntityPlayer player, ItemStack stack) {
			super(player, null, null, 0, 0, 0);
			onCrafting(stack);
		}
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
				WorldUtils.spawnItem(entity.getWorldObj(), entity.xCoord + 0.5, entity.yCoord + 0.5, entity.zCoord + 0.5, containerItem);
		}
		return stack;
	}
	
	/** Pull items required for the recipe from the internal inventory. Returns if successful. */
	public boolean pullRequired(ItemStack[] contents, ItemStack[] crafting, IRecipeInput[] requiredInput) {
		boolean success = true;
		craftingLoop:
		for (int i = 0; i < crafting.length; i++) {
			ItemStack stack = crafting[i];
			IRecipeInput required = requiredInput[i];
			if (required != null) {
				int currentAmount = 0;
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
		}
		return success;
	}
	
	/** Returns if items can be taken out of the output slots. */
	public boolean canTake(EntityPlayer player) {
		return (outputIsReal || ((player != null) && (currentCrafting != null) &&
		                         (currentCrafting.canCraft(new CraftingSourceTileEntity(entity, player))) &&
		                         (progress >= currentCrafting.getCraftingTime()) && hasRequiredExperience(player)));
	}
	
	private boolean hasRequiredExperience(EntityPlayer player) {
		int requiredExperience = currentCrafting.getRequiredExperience();
		return ((requiredExperience == 0) ||
		        ((player != null) && (player.capabilities.isCreativeMode ||
		                              (player.experienceLevel >= requiredExperience))));
	}
	
	/** Returns if the crafting station has the items
	 *  required in its inventory to craft the recipe again. */
	public boolean hasItemRequirements() {
		if (checkHasRequirements) {
			hasRequirements = simulateCraft();
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
		markDirty();
	}
	
	@Override
	public boolean isUseableByPlayer(EntityPlayer player) { return true; }
	
	@Override
	public void openInventory() {  }
	@Override
	public void closeInventory() {  }
	
	@Override
	public void markDirty() {
		if (entity != null)
			entity.markDirtySuper();
		if (outputEmpty()) {
			outputIsReal = false;
			updateGhostOutput();
		}
		checkHasRequirements = true;
	}
	
	// Utility functions
	
	private boolean outputEmpty() {
		for (int i = 0; i < output.length; i++)
			if (output[i] != null) return false;
		return true;
	}
	
}
