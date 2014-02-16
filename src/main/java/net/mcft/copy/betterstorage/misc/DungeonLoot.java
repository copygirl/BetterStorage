package net.mcft.copy.betterstorage.misc;

import java.util.Random;

import net.mcft.copy.betterstorage.content.Items;
import net.mcft.copy.betterstorage.content.Tiles;
import net.mcft.copy.betterstorage.item.ItemDrinkingHelmet;
import net.mcft.copy.betterstorage.utils.NbtUtils;
import net.mcft.copy.betterstorage.utils.StackUtils;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraftforge.common.ChestGenHooks;

public final class DungeonLoot {
	
	private DungeonLoot() {  }
	
	public static void add() {
		
		if (Items.drinkingHelmet != null) {
			ItemStack drinkingHelmet = new ItemStack(Items.drinkingHelmet);
			addMultiple(drinkingHelmet, ChestGenHooks.DUNGEON_CHEST, 1.0,
			                            ChestGenHooks.PYRAMID_DESERT_CHEST, 2.0,
			                            ChestGenHooks.PYRAMID_JUNGLE_CHEST, 2.0,
			                            ChestGenHooks.STRONGHOLD_CORRIDOR, 1.0,
			                            ChestGenHooks.STRONGHOLD_CROSSING, 1.0);
			
			ItemStack drinkingHelmetSpecial = new ItemStack(Items.drinkingHelmet);
			drinkingHelmetSpecial.setItemName("Nishmet");
			ItemStack fireResPotion = new ItemStack(Item.potion, 1, 8259);
			ItemStack healPotion = new ItemStack(Item.potion, 1, 8229);
			ItemDrinkingHelmet.setPotions(drinkingHelmetSpecial, new ItemStack[]{ fireResPotion, healPotion });
			drinkingHelmetSpecial.addEnchantment(Enchantment.fireProtection, 5);
			drinkingHelmetSpecial.addEnchantment(Enchantment.fireAspect, 3);
			drinkingHelmetSpecial.addEnchantment(Enchantment.unbreaking, 2);
			NBTTagList drinkingHelmetLore = NbtUtils.createList(null, "\"i fail at names for items\" -Nishtown");
			StackUtils.set(drinkingHelmetSpecial, drinkingHelmetLore, "display", "Lore");
			addMultiple(drinkingHelmetSpecial, ChestGenHooks.PYRAMID_DESERT_CHEST, 0.2);
		}
		
		if (Tiles.backpack != null) {
			ItemStack backpackSpecial1 = new ItemStack(Tiles.backpack);
			backpackSpecial1.setItemName("Everlasting Backpack");
			backpackSpecial1.addEnchantment(Enchantment.unbreaking, 4);
			StackUtils.set(backpackSpecial1, 0x006622, "display", "color");
			addMultiple(backpackSpecial1, ChestGenHooks.PYRAMID_JUNGLE_CHEST, 0.5);
			
			ItemStack backpackSpecial2 = new ItemStack(Tiles.backpack);
			backpackSpecial2.setItemName("Shielding Backpack");
			backpackSpecial2.addEnchantment(Enchantment.protection, 5);
			StackUtils.set(backpackSpecial2, 0x0000BB, "display", "color");
			addMultiple(backpackSpecial2, ChestGenHooks.PYRAMID_DESERT_CHEST, 0.4);
		}
		
	}
	
	private static void addMultiple(ItemStack stack, Object... args) {
		for (int i = 0; i < args.length; i += 2)
			ChestGenHooks.addItem((String)args[i], makeItem(stack, (Double)args[i + 1]));
	}
	
	private static WeightedRandomChestContent makeItem(ItemStack stack, double chance) {
		if (chance < 1.0) return new RareChestContent(stack, chance);
		return new WeightedRandomChestContent(stack, 1, 1, (int)chance);
	}
	
	
	private static class RareChestContent extends WeightedRandomChestContent {
		private final double chance;
		public RareChestContent(ItemStack stack, double chance) {
			super(stack, 1, 1, 1);
			this.chance = chance;
		}
		@Override
		protected ItemStack[] generateChestContent(Random random, IInventory newInventory) {
			if (random.nextDouble() >= chance) return new ItemStack[0];
			else return super.generateChestContent(random, newInventory);
		}
	}
	
}
