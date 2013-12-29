package net.mcft.copy.betterstorage.api.crafting;

import java.util.Random;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public interface IStationRecipe {
	
	/** Returns if this input matches the items needed for this recipe. */
	boolean matches(ItemStack[] input);
	
	/** Fills the input array with sample items (used for NEI). */
	void getSampleInput(ItemStack[] input, Random rnd);
	
	/** Fills the requirements for crafting this recipe again.
	 *  Called after {@link #canCraft(ItemStack[], EntityPlayer) canCraft()} and
	 *  before {@link #craft(ItemStack[], ItemStack[], EntityPlayer) craft()}. <br>
	 *  If the recipe can't be crafted again, a machine will not be able to craft
	 *  this, so it doesn't empty the crafting grid. Players don't have this
	 *  limitation. After the recipe is crafted, the crafting grid is filled
	 *  with items from the internal storage that match the requirements.
	 * @param currentInput The current input items.
	 * @param requiredInput The items needed to craft this recipe again. */
	void getCraftRequirements(ItemStack[] currentInput, IRecipeInput[] requiredInput);
	
	/** Returns the amount of experience displayed for this recipe. <br>
	 *  Needs to be checked in {@link #canCraft(ItemStack[], EntityPlayer) canCraft()}
	 *  and decreased in {@link #craft(ItemStack[], ItemStack[], EntityPlayer) craft()}. */
	int getExperienceDisplay(ItemStack[] input);
	
	/** Returns the time (in game ticks) needed to craft this recipe. <br>
	 *  Return 0 for the recipe to be instantly available. Note: When a machine crafts,
	 *  there might be minimum delay, which can be changed in the config file. Keep
	 *  this within 0 to 32000 ticks. */
	int getCraftingTime(ItemStack[] input);
	
	/** Returns the output items for this recipe. */
	ItemStack[] getOutput(ItemStack[] input);
	
	/** Returns if the recipe can be crafted.
	 * @param source contains information of where this recipe is crafted and who crafts it. */
	boolean canCraft(ItemStack[] input, ICraftingSource source);
	
	/** Called after the recipe is crafted.
	 * @param input has to be modified to decrease/damage items used in the recipe. <br>
	 *              Empty stacks (stack size = 0) and broken items (damage > max damage)
	 *              will be removed from the input array automatically.
	 * @param source contains information of where this recipe was crafted and who crafted it. */
	void craft(ItemStack[] input, ICraftingSource source);
	
}
