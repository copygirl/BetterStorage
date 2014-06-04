package net.mcft.copy.betterstorage.api;

import net.mcft.copy.betterstorage.utils.StackUtils;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public final class BetterStorageUtils {
	
	private BetterStorageUtils() {  }
	
	/** Returns if the stack matches the match stack. Stack size is ignored. <br>
	 *  If the match stack damage is WILDCARD_VALUE, it will match any damage. <br>
	 *  If the match stack doesn't have an NBT compound, it will match any NBT data. <br>
	 *  (If the match stack has an empty NBT compound it'll only match stacks without NBT data.) */
	public static boolean wildcardMatch(ItemStack match, ItemStack stack) {
		return ((match == null) ? (stack == null) :
		        ((stack != null) && (Item.getIdFromItem(match.getItem()) == Item.getIdFromItem(stack.getItem())) &&
		         ((StackUtils.getRealItemDamage(match) == OreDictionary.WILDCARD_VALUE) ||
		          (StackUtils.getRealItemDamage(match) == StackUtils.getRealItemDamage(stack))) &&
		         (!match.hasTagCompound() ||
		          (match.getTagCompound().hasNoTags() && !stack.hasTagCompound()) ||
		          (match.getTagCompound().equals(stack.getTagCompound())))));
	}
	
}
