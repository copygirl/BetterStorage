package net.mcft.copy.betterstorage.item.cardboard;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.mcft.copy.betterstorage.api.crafting.ContainerInfo;
import net.mcft.copy.betterstorage.api.crafting.IRecipeInput;
import net.mcft.copy.betterstorage.content.BetterStorageItems;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemEnchantedBook;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class RecipeInputEnchantedBook implements IRecipeInput {
	
	private static Set<Enchantment> validEnchantments = null;
	
	public static final RecipeInputEnchantedBook instance = new RecipeInputEnchantedBook();
	
	private RecipeInputEnchantedBook() {  }
	
	@Override
	public int getAmount() { return 1; }
	
	@Override
	public boolean matches(ItemStack stack) {
		if (validEnchantments == null) {
			validEnchantments = new HashSet<Enchantment>();
			ItemStack[] items = {
				new ItemStack(BetterStorageItems.cardboardHelmet),
				new ItemStack(BetterStorageItems.cardboardChestplate),
				new ItemStack(BetterStorageItems.cardboardLeggings),
				new ItemStack(BetterStorageItems.cardboardBoots),
				new ItemStack(BetterStorageItems.cardboardSword),
				new ItemStack(BetterStorageItems.cardboardPickaxe),
				new ItemStack(BetterStorageItems.cardboardShovel),
				new ItemStack(BetterStorageItems.cardboardAxe),
				new ItemStack(BetterStorageItems.cardboardHoe)
			};
			for (Enchantment ench : Enchantment.enchantmentsList) {
				if ((ench == null) || !ench.isAllowedOnBooks()) continue;
				for (ItemStack item : items)
					if (item.getItem() != null && ench.canApply(item)) {
						validEnchantments.add(ench);
						break;
					}
			}
		}
		if (stack.getItem() instanceof ItemEnchantedBook) {
			Map<Integer, Integer> enchants = EnchantmentHelper.getEnchantments(stack);
			for (Enchantment ench : validEnchantments)
				if (enchants.containsKey(ench.effectId))
					return true;
		}
		return false;
	}
	
	@Override
	public void craft(ItemStack input, ContainerInfo containerInfo) {  }
	
	@Override
	@SideOnly(Side.CLIENT)
	public List<ItemStack> getPossibleMatches() { return null; }
	
}