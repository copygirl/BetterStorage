package net.mcft.copy.betterstorage.misc;

import net.mcft.copy.betterstorage.addon.Addon;
import net.mcft.copy.betterstorage.api.crafting.BetterStorageCrafting;
import net.mcft.copy.betterstorage.content.BetterStorageItems;
import net.mcft.copy.betterstorage.content.BetterStorageTiles;
import net.mcft.copy.betterstorage.item.cardboard.CardboardEnchantmentRecipe;
import net.mcft.copy.betterstorage.item.cardboard.CardboardRepairRecipe;
import net.mcft.copy.betterstorage.item.recipe.DrinkingHelmetRecipe;
import net.mcft.copy.betterstorage.item.recipe.DyeRecipe;
import net.mcft.copy.betterstorage.item.recipe.KeyRecipe;
import net.mcft.copy.betterstorage.item.recipe.LockColorRecipe;
import net.mcft.copy.betterstorage.item.recipe.LockRecipe;
import net.mcft.copy.betterstorage.tile.ContainerMaterial;
import net.mcft.copy.betterstorage.utils.MiscUtils;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.RecipeSorter;
import net.minecraftforge.oredict.RecipeSorter.Category;
import net.minecraftforge.oredict.ShapedOreRecipe;
import cpw.mods.fml.common.registry.GameRegistry;

public final class Recipes {
	
	private Recipes() {  }
	
	public static void add() {
		
		registerRecipeSorter();
		
		addTileRecipes();
		addItemRecipes();
		addCardboardRecipes();
		
		GameRegistry.addRecipe(new DyeRecipe());
		Addon.addRecipesAll();
		
	}
	
	private static void registerRecipeSorter() {
		
		RecipeSorter.register("betterstorage:drinkinghelmetrecipe", DrinkingHelmetRecipe.class, Category.SHAPED,    "");
		RecipeSorter.register("betterstorage:keyrecipe",            KeyRecipe.class,            Category.SHAPED,    "");
		RecipeSorter.register("betterstorage:lockrecipe",           LockRecipe.class,           Category.SHAPED,    "");
		
		RecipeSorter.register("betterstorage:dyerecipe",       DyeRecipe.class,       Category.SHAPELESS, "");
		RecipeSorter.register("betterstorage:lockcolorrecipe", LockColorRecipe.class, Category.SHAPELESS, "");
		
	}
	
	private static void addTileRecipes() {
		
		// Crate recipe
		if (MiscUtils.isEnabled(BetterStorageTiles.crate))
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(BetterStorageTiles.crate),
					"o/o",
					"/ /",
					"o/o", 'o', "plankWood",
					       '/', "stickWood"));
		
		// Reinforced chest recipes
		if (MiscUtils.isEnabled(BetterStorageTiles.reinforcedChest))
			for (ContainerMaterial material : ContainerMaterial.getMaterials()) {
				IRecipe recipe = material.getReinforcedRecipe(Blocks.chest, BetterStorageTiles.reinforcedChest);
				if (recipe != null) GameRegistry.addRecipe(recipe);
			}
		
		// Locker recipe
		if (MiscUtils.isEnabled(BetterStorageTiles.locker)) {
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(BetterStorageTiles.locker),
					"ooo",
					"o |",
					"ooo", 'o', "plankWood",
					       '|', Blocks.trapdoor));
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(BetterStorageTiles.locker),
					"ooo",
					"| o",
					"ooo", 'o', "plankWood",
					       '|', Blocks.trapdoor));
			
			// Reinforced locker recipes
			if (MiscUtils.isEnabled(BetterStorageTiles.reinforcedLocker))
				for (ContainerMaterial material : ContainerMaterial.getMaterials()) {
					IRecipe recipe = material.getReinforcedRecipe(BetterStorageTiles.locker, BetterStorageTiles.reinforcedLocker);
					if (recipe != null) GameRegistry.addRecipe(recipe);
				}
		}
		
		// Armor stand recipe
		if (MiscUtils.isEnabled(BetterStorageTiles.armorStand))
			GameRegistry.addShapedRecipe(new ItemStack(BetterStorageTiles.armorStand),
					" i ",
					"/i/",
					" s ", 's', new ItemStack(Blocks.stone_slab, 1, 0),
					       'i', Items.iron_ingot,
					       '/', Items.stick);
		
		// Backpack recipe
		if (MiscUtils.isEnabled(BetterStorageTiles.backpack))
			GameRegistry.addShapedRecipe(new ItemStack(BetterStorageTiles.backpack),
					"#i#",
					"#O#",
					"###", '#', Items.leather,
					       'O', Blocks.wool,
					       'i', Items.gold_ingot);
		
		// Cardboard box recipe
		if (MiscUtils.isEnabled(BetterStorageTiles.cardboardBox, BetterStorageItems.cardboardSheet))
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(BetterStorageTiles.cardboardBox),
					"ooo",
					"o o",
					"ooo", 'o', "sheetCardboard"));
		
		// Crafting Station recipe
		if (MiscUtils.isEnabled(BetterStorageTiles.craftingStation))
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(BetterStorageTiles.craftingStation),
					"B-B",
					"PTP",
					"WCW", 'B', Blocks.stonebrick,
					       '-', Blocks.light_weighted_pressure_plate,
					       'P', Blocks.piston,
					       'T', Blocks.crafting_table,
					       'W', "plankWood",
					       'C', (MiscUtils.isEnabled(BetterStorageTiles.crate) ? BetterStorageTiles.crate : Blocks.chest)));
		
		// Flint Block recipe
		if (MiscUtils.isEnabled(BetterStorageTiles.flintBlock)) {
			GameRegistry.addShapedRecipe(new ItemStack(BetterStorageTiles.flintBlock),
					"ooo",
					"ooo",
					"ooo", 'o', Items.flint);
			GameRegistry.addShapelessRecipe(new ItemStack(Items.flint, 9), BetterStorageTiles.flintBlock);
		}
		
	}
	
	private static void addItemRecipes() {
		
		if (MiscUtils.isEnabled(BetterStorageItems.key)) {
			// Key recipe
			GameRegistry.addRecipe(KeyRecipe.createKeyRecipe(
					".o",
					".o",
					" o", 'o', Items.gold_ingot,
					      '.', Items.gold_nugget));
			// Key modify recipe
			GameRegistry.addRecipe(KeyRecipe.createKeyRecipe(
					"k", 'k', new ItemStack(BetterStorageItems.key, 1, OreDictionary.WILDCARD_VALUE)));
		}
		
		if (MiscUtils.isEnabled(BetterStorageItems.lock)) {
			// Lock recipe
			if (MiscUtils.isEnabled(BetterStorageItems.key))
				GameRegistry.addRecipe(LockRecipe.createLockRecipe());
			// Lock color recipe
			GameRegistry.addRecipe(LockColorRecipe.createLockColorRecipe());
		}
		
		// Keyring recipe
		if (MiscUtils.isEnabled(BetterStorageItems.keyring))
			GameRegistry.addShapedRecipe(new ItemStack(BetterStorageItems.keyring),
					"...",
					". .",
					"...", '.', Items.gold_nugget);

		// Drinking helmet recipe
		if (MiscUtils.isEnabled(BetterStorageItems.drinkingHelmet))
			GameRegistry.addRecipe(new DrinkingHelmetRecipe(BetterStorageItems.drinkingHelmet));
		
	}
	
	private static void addCardboardRecipes() {
		
		// Cardboard sheet recipe
		if (MiscUtils.isEnabled(BetterStorageItems.cardboardSheet)) {
			GameRegistry.addShapelessRecipe(new ItemStack(BetterStorageItems.cardboardSheet, 4),
					Items.paper, Items.paper, Items.paper,
					Items.paper, Items.paper, Items.paper,
					Items.paper, Items.paper, Items.slime_ball);
		}
		
		// Cardboard helmet recipe
		if (MiscUtils.isEnabled(BetterStorageItems.cardboardHelmet))
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(BetterStorageItems.cardboardHelmet),
					"ooo",
					"o o", 'o', "sheetCardboard"));
		// Cardboard chestplate recipe
		if (MiscUtils.isEnabled(BetterStorageItems.cardboardChestplate))
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(BetterStorageItems.cardboardChestplate),
					"o o",
					"ooo",
					"ooo", 'o', "sheetCardboard"));
		// Cardboard leggings recipe
		if (MiscUtils.isEnabled(BetterStorageItems.cardboardLeggings))
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(BetterStorageItems.cardboardLeggings),
					"ooo",
					"o o",
					"o o", 'o', "sheetCardboard"));
		// Cardboard boots recipe
		if (MiscUtils.isEnabled(BetterStorageItems.cardboardBoots))
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(BetterStorageItems.cardboardBoots),
					"o o",
					"o o", 'o', "sheetCardboard"));
		
		// Cardboard sword recipe
		if (MiscUtils.isEnabled(BetterStorageItems.cardboardSword))
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(BetterStorageItems.cardboardSword),
					"o",
					"o",
					"/", 'o', "sheetCardboard",
					     '/', Items.stick));
		// Cardboard pickaxe recipe
		if (MiscUtils.isEnabled(BetterStorageItems.cardboardPickaxe))
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(BetterStorageItems.cardboardPickaxe),
					"ooo",
					" / ",
					" / ", 'o', "sheetCardboard",
					       '/', Items.stick));
		// Cardboard shovel recipe
		if (MiscUtils.isEnabled(BetterStorageItems.cardboardShovel))
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(BetterStorageItems.cardboardShovel),
					"o",
					"/",
					"/", 'o', "sheetCardboard",
					     '/', Items.stick));
		
		// Cardboard axe recipe
		if (MiscUtils.isEnabled(BetterStorageItems.cardboardAxe)) {
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(BetterStorageItems.cardboardAxe),
					"oo",
					"o/",
					" /", 'o', "sheetCardboard",
					      '/', Items.stick));
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(BetterStorageItems.cardboardAxe),
					"oo",
					"/o",
					"/ ", 'o', "sheetCardboard",
					      '/', Items.stick));
		}
		
		// Cardboard hoe recipe
		if (MiscUtils.isEnabled(BetterStorageItems.cardboardHoe)) {
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(BetterStorageItems.cardboardHoe),
					"oo",
					" /",
					" /", 'o', "sheetCardboard",
					      '/', Items.stick));
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(BetterStorageItems.cardboardHoe),
					"oo",
					"/ ",
					"/ ", 'o', "sheetCardboard",
					      '/', Items.stick));
		}
		
		// Crafting Station: Add cardboard enchantment recipe
		BetterStorageCrafting.addStationRecipe(new CardboardEnchantmentRecipe());
		
		// Crafting Station: Add cardboard repair recipe
		if (MiscUtils.isEnabled(BetterStorageItems.cardboardSheet))
			BetterStorageCrafting.addStationRecipe(new CardboardRepairRecipe());
		
	}
	
}
