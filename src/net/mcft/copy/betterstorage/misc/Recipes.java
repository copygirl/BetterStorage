package net.mcft.copy.betterstorage.misc;

import net.mcft.copy.betterstorage.addon.Addon;
import net.mcft.copy.betterstorage.block.ContainerMaterial;
import net.mcft.copy.betterstorage.content.Blocks;
import net.mcft.copy.betterstorage.content.Items;
import net.mcft.copy.betterstorage.item.recipe.CardboardEnchantRecipe;
import net.mcft.copy.betterstorage.item.recipe.DrinkingHelmetRecipe;
import net.mcft.copy.betterstorage.item.recipe.DyeRecipe;
import net.mcft.copy.betterstorage.item.recipe.KeyRecipe;
import net.mcft.copy.betterstorage.item.recipe.LockColorRecipe;
import net.mcft.copy.betterstorage.item.recipe.LockRecipe;
import net.mcft.copy.betterstorage.utils.MiscUtils;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.oredict.ShapedOreRecipe;
import cpw.mods.fml.common.registry.GameRegistry;

public final class Recipes {
	
	private Recipes() {  }
	
	public static void add() {
		
		// Crate recipe
		if (MiscUtils.isEnabled(Blocks.crate))
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Blocks.crate),
					"o/o",
					"/ /",
					"o/o", 'o', "plankWood",
					       '/', "stickWood"));
		
		// Reinforced chest recipes
		if (MiscUtils.isEnabled(Blocks.reinforcedChest))
			for (ContainerMaterial material : ContainerMaterial.getMaterials()) {
				IRecipe recipe = material.getReinforcedRecipe(Block.chest, Blocks.reinforcedChest);
				if (recipe != null) GameRegistry.addRecipe(recipe);
			}
		
		// Locker recipe
		if (MiscUtils.isEnabled(Blocks.locker)) {
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Blocks.locker),
					"ooo",
					"o |",
					"ooo", 'o', "plankWood",
					       '|', Block.trapdoor));
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Blocks.locker),
					"ooo",
					"| o",
					"ooo", 'o', "plankWood",
					       '|', Block.trapdoor));
			
			// Reinforced locker recipes
			if (MiscUtils.isEnabled(Blocks.reinforcedLocker))
				for (ContainerMaterial material : ContainerMaterial.getMaterials()) {
					IRecipe recipe = material.getReinforcedRecipe(Blocks.locker, Blocks.reinforcedLocker);
					if (recipe != null) GameRegistry.addRecipe(recipe);
				}
		}
		
		// Armor stand recipe
		if (MiscUtils.isEnabled(Blocks.armorStand))
			GameRegistry.addRecipe(new ItemStack(Blocks.armorStand),
					" i ",
					"/i/",
					" s ", 's', new ItemStack(Block.stoneSingleSlab, 1, 0),
					       'i', Item.ingotIron,
					       '/', Item.stick);
		
		// Backpack recipe
		if (MiscUtils.isEnabled(Blocks.backpack))
			GameRegistry.addRecipe(new ItemStack(Blocks.backpack),
					"#i#",
					"#O#",
					"###", '#', Item.leather,
					       'O', Block.cloth,
					       'i', Item.ingotGold);
		
		// Cardboard box recipe
		if (MiscUtils.isEnabled(Blocks.cardboardBox, Items.cardboardSheet))
			GameRegistry.addRecipe(new ItemStack(Blocks.cardboardBox),
					"ooo",
					"o o",
					"ooo", 'o', Items.cardboardSheet);
		
		if (MiscUtils.isEnabled(Items.key)) {
			// Key recipe
			GameRegistry.addRecipe(KeyRecipe.createKeyRecipe(
					".o",
					".o",
					" o", 'o', Item.ingotGold,
					      '.', Item.goldNugget));
			// Key modify recipe
			GameRegistry.addRecipe(KeyRecipe.createKeyRecipe(
					"k", 'k', new ItemStack(Items.key, 1, Constants.anyDamage)));
		}
		
		if (MiscUtils.isEnabled(Items.lock)) {
			// Lock recipe
			if (MiscUtils.isEnabled(Items.key))
				GameRegistry.addRecipe(LockRecipe.createLockRecipe());
			// Lock color recipe
			GameRegistry.addRecipe(LockColorRecipe.createLockColorRecipe());
		}
		
		// Keyring recipe
		if (MiscUtils.isEnabled(Items.keyring))
			GameRegistry.addRecipe(new ItemStack(Items.keyring),
					"...",
					". .",
					"...", '.', Item.goldNugget);
		
		// Cardboard sheet recipe
		if (MiscUtils.isEnabled(Items.cardboardSheet)) {
			GameRegistry.addRecipe(new ItemStack(Items.cardboardSheet),
					"ooo",
					"ooo",
					"ooo", 'o', Item.paper);
			
			// Cardboard helmet recipe
			if (MiscUtils.isEnabled(Items.cardboardHelmet))
				GameRegistry.addRecipe(new ItemStack(Items.cardboardHelmet),
						"ooo",
						"o o", 'o', Items.cardboardSheet);
			// Cardboard chestplate recipe
			if (MiscUtils.isEnabled(Items.cardboardChestplate))
				GameRegistry.addRecipe(new ItemStack(Items.cardboardChestplate),
						"o o",
						"ooo",
						"ooo", 'o', Items.cardboardSheet);
			// Cardboard leggings recipe
			if (MiscUtils.isEnabled(Items.cardboardLeggings))
				GameRegistry.addRecipe(new ItemStack(Items.cardboardLeggings),
						"ooo",
						"o o",
						"o o", 'o', Items.cardboardSheet);
			// Cardboard boots recipe
			if (MiscUtils.isEnabled(Items.cardboardBoots))
				GameRegistry.addRecipe(new ItemStack(Items.cardboardBoots),
						"o o",
						"o o", 'o', Items.cardboardSheet);
			
			// Cardboard sword recipe
			if (MiscUtils.isEnabled(Items.cardboardSword))
				GameRegistry.addRecipe(new ItemStack(Items.cardboardSword),
						"o",
						"o",
						"/", 'o', Items.cardboardSheet,
						     '/', Item.stick);
			// Cardboard pickaxe recipe
			if (MiscUtils.isEnabled(Items.cardboardPickaxe))
				GameRegistry.addRecipe(new ItemStack(Items.cardboardPickaxe),
						"ooo",
						" / ",
						" / ", 'o', Items.cardboardSheet,
						       '/', Item.stick);
			// Cardboard shovel recipe
			if (MiscUtils.isEnabled(Items.cardboardShovel))
				GameRegistry.addRecipe(new ItemStack(Items.cardboardShovel),
						"o",
						"/",
						"/", 'o', Items.cardboardSheet,
						     '/', Item.stick);
			
			// Cardboard axe recipe
			if (MiscUtils.isEnabled(Items.cardboardAxe))
				GameRegistry.addRecipe(new ItemStack(Items.cardboardAxe),
						"oo",
						"o/",
						" /", 'o', Items.cardboardSheet,
						      '/', Item.stick);
			if (MiscUtils.isEnabled(Items.cardboardAxe))
				GameRegistry.addRecipe(new ItemStack(Items.cardboardAxe),
						"oo",
						"/o",
						"/ ", 'o', Items.cardboardSheet,
						      '/', Item.stick);
			
			// Cardboard hoe recipe
			if (MiscUtils.isEnabled(Items.cardboardHoe))
				GameRegistry.addRecipe(new ItemStack(Items.cardboardHoe),
						"oo",
						" /",
						" /", 'o', Items.cardboardSheet,
						      '/', Item.stick);
			if (MiscUtils.isEnabled(Items.cardboardHoe))
				GameRegistry.addRecipe(new ItemStack(Items.cardboardHoe),
						"oo",
						"/ ",
						"/ ", 'o', Items.cardboardSheet,
						      '/', Item.stick);
		}
		
		// Cardboard enchanting recipe
		GameRegistry.addRecipe(new CardboardEnchantRecipe());

		// Drinking helmet recipe
		if (MiscUtils.isEnabled(Items.drinkingHelmet))
			GameRegistry.addRecipe(new DrinkingHelmetRecipe(Items.drinkingHelmet));
		
		GameRegistry.addRecipe(new DyeRecipe());
		
		Addon.addRecipesAll();
		
	}
	
}
