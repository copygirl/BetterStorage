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
import net.mcft.copy.betterstorage.item.recipe.PresentRecipe;
import net.mcft.copy.betterstorage.item.recipe.PresentRemoveNametagRecipe;
import net.mcft.copy.betterstorage.tile.ContainerMaterial;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.RecipeSorter;
import net.minecraftforge.oredict.RecipeSorter.Category;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;
import cpw.mods.fml.common.registry.GameRegistry;

public final class Recipes {
	
	private Recipes() {  }
	
	public static void add() {
		
		registerOreDictionary();
		registerRecipeSorter();
		
		addTileRecipes();
		addItemRecipes();
		addCardboardRecipes();
		
		GameRegistry.addRecipe(new DyeRecipe());
		Addon.addRecipesAll();
		
	}
	
	private static void registerRecipeSorter() {
		
		RecipeSorter.register("betterstorage:drinkinghelmetrecipe", DrinkingHelmetRecipe.class, RecipeSorter.Category.SHAPED, "after:minecraft:shaped");
		RecipeSorter.register("betterstorage:keyrecipe", KeyRecipe.class, RecipeSorter.Category.SHAPED, "after:minecraft:shaped");
		RecipeSorter.register("betterstorage:lockrecipe", LockRecipe.class, RecipeSorter.Category.SHAPED, "after:minecraft:shaped");
		RecipeSorter.register("betterstorage:presentrecipe", PresentRecipe.class, RecipeSorter.Category.SHAPED, "after:minecraft:shaped");
		
		RecipeSorter.register("betterstorage:dyerecipe", DyeRecipe.class, RecipeSorter.Category.SHAPELESS, "after:minecraft:shapeless");
		RecipeSorter.register("betterstorage:lockcolorrecipe", LockColorRecipe.class, RecipeSorter.Category.SHAPELESS, "after:minecraft:shapeless");
		
	}
	
	private static void registerOreDictionary() {
		
		OreDictionary.registerOre("chestWood", Blocks.chest);
		OreDictionary.registerOre("craftingTableWood", Blocks.crafting_table);
		
	}
	
	private static void addTileRecipes() {
		
		// Crate recipe
		if (BetterStorageTiles.crate != null)
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(BetterStorageTiles.crate),
					"o/o",
					"/ /",
					"o/o", 'o', "plankWood",
					       '/', "stickWood"));
		
		// Reinforced chest recipes
		if (BetterStorageTiles.reinforcedChest != null)
			for (ContainerMaterial material : ContainerMaterial.getMaterials()) {
				IRecipe recipe = material.getReinforcedRecipe(Blocks.chest, BetterStorageTiles.reinforcedChest);
				if (recipe != null) GameRegistry.addRecipe(recipe);
			}
		
		// Locker recipe
		if (BetterStorageTiles.locker != null) {
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
			if (BetterStorageTiles.reinforcedLocker != null)
				for (ContainerMaterial material : ContainerMaterial.getMaterials()) {
					IRecipe recipe = material.getReinforcedRecipe(BetterStorageTiles.locker, BetterStorageTiles.reinforcedLocker);
					if (recipe != null) GameRegistry.addRecipe(recipe);
				}
		}
		
		// Armor stand recipe
		if (BetterStorageTiles.armorStand != null)
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(BetterStorageTiles.armorStand),
					" i ",
					"/i/",
					" s ", 's', new ItemStack(Blocks.stone_slab, 1, 0),
					       'i', "ingotIron",
					       '/', "stickWood"));
		
		// Backpack recipe
		if (BetterStorageTiles.backpack != null)
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(BetterStorageItems.itemBackpack),
					"#i#",
					"#O#",
					"###", '#', Items.leather,
					       'O', Blocks.wool,
					       'i', "ingotGold"));
		
		// Cardboard box recipe
		if ((BetterStorageTiles.cardboardBox != null) &&
		    (BetterStorageItems.cardboardSheet != null))
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(BetterStorageTiles.cardboardBox),
					"ooo",
					"o o",
					"ooo", 'o', "sheetCardboard"));
		
		// Crafting Station recipe
		if (BetterStorageTiles.craftingStation != null)
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(BetterStorageTiles.craftingStation),
					"B-B",
					"PTP",
					"WCW", 'B', Blocks.stonebrick,
					       '-', Blocks.light_weighted_pressure_plate,
					       'P', Blocks.piston,
					       'T', "craftingTableWood",
					       'W', "plankWood",
					       'C', ((BetterStorageTiles.crate != null) ? BetterStorageTiles.crate : Blocks.chest)));
		
		// Present recipe
		if ((BetterStorageTiles.present != null) &&
		    (BetterStorageTiles.cardboardBox != null)) {
			GameRegistry.addRecipe(new PresentRecipe());
			BetterStorageCrafting.addStationRecipe(new PresentRemoveNametagRecipe());
		}
		
		// Flint Block recipe
		if (BetterStorageTiles.flintBlock != null) {
			GameRegistry.addShapedRecipe(new ItemStack(BetterStorageTiles.flintBlock),
					"ooo",
					"ooo",
					"ooo", 'o', Items.flint);
			GameRegistry.addShapelessRecipe(new ItemStack(Items.flint, 9), BetterStorageTiles.flintBlock);
		}
		
	}
	
	private static void addItemRecipes() {
		
		if (BetterStorageItems.key != null) {
			// Key recipe
			// TODO: Add support for ore dictionary gold ingots / nuggets.
			GameRegistry.addRecipe(KeyRecipe.createKeyRecipe(
					".o",
					".o",
					" o", 'o', Items.gold_ingot,
					      '.', Items.gold_nugget));
			// Key modify recipe
			GameRegistry.addRecipe(KeyRecipe.createKeyRecipe(
					"k", 'k', new ItemStack(BetterStorageItems.key)));
		}
		
		if (BetterStorageItems.lock != null) {
			// Lock recipe
			if (BetterStorageItems.key != null)
				GameRegistry.addRecipe(LockRecipe.createLockRecipe());
			// Lock color recipe
			GameRegistry.addRecipe(LockColorRecipe.createLockColorRecipe());
		}
		
		// Keyring recipe
		if (BetterStorageItems.keyring != null)
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(BetterStorageItems.keyring),
					"...",
					". .",
					"...", '.', "nuggetGold"));

		// Drinking helmet recipe
		if (BetterStorageItems.drinkingHelmet != null)
			GameRegistry.addRecipe(new DrinkingHelmetRecipe(BetterStorageItems.drinkingHelmet));
		
	}
	
	private static void addCardboardRecipes() {
		
		// Cardboard sheet recipe
		if (BetterStorageItems.cardboardSheet != null) {
			GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(BetterStorageItems.cardboardSheet, 4),
					Items.paper, Items.paper, Items.paper,
					Items.paper, Items.paper, Items.paper,
					Items.paper, Items.paper, "slimeball"));
		}
		
		// Cardboard helmet recipe
		if (BetterStorageItems.cardboardHelmet != null)
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(BetterStorageItems.cardboardHelmet),
					"ooo",
					"o o", 'o', "sheetCardboard"));
		// Cardboard chestplate recipe
		if (BetterStorageItems.cardboardChestplate != null)
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(BetterStorageItems.cardboardChestplate),
					"o o",
					"ooo",
					"ooo", 'o', "sheetCardboard"));
		// Cardboard leggings recipe
		if (BetterStorageItems.cardboardLeggings != null)
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(BetterStorageItems.cardboardLeggings),
					"ooo",
					"o o",
					"o o", 'o', "sheetCardboard"));
		// Cardboard boots recipe
		if (BetterStorageItems.cardboardBoots != null)
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(BetterStorageItems.cardboardBoots),
					"o o",
					"o o", 'o', "sheetCardboard"));
		
		// Cardboard sword recipe
		if (BetterStorageItems.cardboardSword != null)
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(BetterStorageItems.cardboardSword),
					"o",
					"o",
					"/", 'o', "sheetCardboard",
					     '/', "stickWood"));
		// Cardboard pickaxe recipe
		if (BetterStorageItems.cardboardPickaxe != null)
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(BetterStorageItems.cardboardPickaxe),
					"ooo",
					" / ",
					" / ", 'o', "sheetCardboard",
					       '/', "stickWood"));
		// Cardboard shovel recipe
		if (BetterStorageItems.cardboardShovel != null)
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(BetterStorageItems.cardboardShovel),
					"o",
					"/",
					"/", 'o', "sheetCardboard",
					     '/', "stickWood"));
		
		// Cardboard axe recipe
		if (BetterStorageItems.cardboardAxe != null) {
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(BetterStorageItems.cardboardAxe),
					"oo",
					"o/",
					" /", 'o', "sheetCardboard",
					      '/', "stickWood"));
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(BetterStorageItems.cardboardAxe),
					"oo",
					"/o",
					"/ ", 'o', "sheetCardboard",
					      '/', "stickWood"));
		}
		
		// Cardboard hoe recipe
		if (BetterStorageItems.cardboardHoe != null) {
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(BetterStorageItems.cardboardHoe),
					"oo",
					" /",
					" /", 'o', "sheetCardboard",
					      '/', "stickWood"));
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(BetterStorageItems.cardboardHoe),
					"oo",
					"/ ",
					"/ ", 'o', "sheetCardboard",
					      '/', "stickWood"));
		}
		
		if (BetterStorageItems.anyCardboardItemsEnabled) {
			// Crafting Station: Add cardboard enchantment recipe
			BetterStorageCrafting.addStationRecipe(new CardboardEnchantmentRecipe());
			
			// Crafting Station: Add cardboard repair recipe
			if (BetterStorageItems.cardboardSheet != null)
				BetterStorageCrafting.addStationRecipe(new CardboardRepairRecipe());
		}
		
	}
	
}
