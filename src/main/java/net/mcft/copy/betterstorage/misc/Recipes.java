package net.mcft.copy.betterstorage.misc;

import net.mcft.copy.betterstorage.addon.Addon;
import net.mcft.copy.betterstorage.content.Items;
import net.mcft.copy.betterstorage.content.Tiles;
import net.mcft.copy.betterstorage.item.recipe.DrinkingHelmetRecipe;
import net.mcft.copy.betterstorage.item.recipe.DyeRecipe;
import net.mcft.copy.betterstorage.item.recipe.KeyRecipe;
import net.mcft.copy.betterstorage.item.recipe.LockColorRecipe;
import net.mcft.copy.betterstorage.item.recipe.LockRecipe;
import net.mcft.copy.betterstorage.tile.ContainerMaterial;
import net.mcft.copy.betterstorage.utils.MiscUtils;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
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
		if (MiscUtils.isEnabled(Tiles.crate))
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Tiles.crate),
					"o/o",
					"/ /",
					"o/o", 'o', "plankWood",
					       '/', "stickWood"));
		
		// Reinforced chest recipes
		if (MiscUtils.isEnabled(Tiles.reinforcedChest))
			for (ContainerMaterial material : ContainerMaterial.getMaterials()) {
				IRecipe recipe = material.getReinforcedRecipe(Block.chest, Tiles.reinforcedChest);
				if (recipe != null) GameRegistry.addRecipe(recipe);
			}
		
		// Locker recipe
		if (MiscUtils.isEnabled(Tiles.locker)) {
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Tiles.locker),
					"ooo",
					"o |",
					"ooo", 'o', "plankWood",
					       '|', Block.trapdoor));
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Tiles.locker),
					"ooo",
					"| o",
					"ooo", 'o', "plankWood",
					       '|', Block.trapdoor));
			
			// Reinforced locker recipes
			if (MiscUtils.isEnabled(Tiles.reinforcedLocker))
				for (ContainerMaterial material : ContainerMaterial.getMaterials()) {
					IRecipe recipe = material.getReinforcedRecipe(Tiles.locker, Tiles.reinforcedLocker);
					if (recipe != null) GameRegistry.addRecipe(recipe);
				}
		}
		
		// Armor stand recipe
		if (MiscUtils.isEnabled(Tiles.armorStand))
			GameRegistry.addShapedRecipe(new ItemStack(Tiles.armorStand),
					" i ",
					"/i/",
					" s ", 's', new ItemStack(Block.stoneSingleSlab, 1, 0),
					       'i', Item.ingotIron,
					       '/', Item.stick);
		
		// Backpack recipe
		if (MiscUtils.isEnabled(Tiles.backpack))
			GameRegistry.addShapedRecipe(new ItemStack(Tiles.backpack),
					"#i#",
					"#O#",
					"###", '#', Item.leather,
					       'O', Block.cloth,
					       'i', Item.ingotGold);
		
		// Cardboard box recipe
		if (MiscUtils.isEnabled(Tiles.cardboardBox, Items.cardboardSheet))
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Tiles.cardboardBox),
					"ooo",
					"o o",
					"ooo", 'o', "sheetCardboard"));
		
		// Crafting Station recipe
		if (MiscUtils.isEnabled(Tiles.craftingStation))
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Tiles.craftingStation),
					"B-B",
					"PTP",
					"WCW", 'B', Block.stoneBrick,
					       '-', Block.pressurePlateGold,
					       'P', Block.pistonBase,
					       'T', Block.workbench,
					       'W', "plankWood",
					       'C', (MiscUtils.isEnabled(Tiles.crate) ? Tiles.crate : Block.chest)));
		
		// Flint Block recipe
		if (MiscUtils.isEnabled(Tiles.flintBlock)) {
			GameRegistry.addShapedRecipe(new ItemStack(Tiles.flintBlock),
					"ooo",
					"ooo",
					"ooo", 'o', Item.flint);
			GameRegistry.addShapelessRecipe(new ItemStack(Item.flint, 9), Tiles.flintBlock);
		}
		
	}
	
	private static void addItemRecipes() {
		
		if (MiscUtils.isEnabled(Items.key)) {
			// Key recipe
			GameRegistry.addRecipe(KeyRecipe.createKeyRecipe(
					".o",
					".o",
					" o", 'o', Item.ingotGold,
					      '.', Item.goldNugget));
			// Key modify recipe
			GameRegistry.addRecipe(KeyRecipe.createKeyRecipe(
					"k", 'k', new ItemStack(Items.key, 1, OreDictionary.WILDCARD_VALUE)));
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
			GameRegistry.addShapedRecipe(new ItemStack(Items.keyring),
					"...",
					". .",
					"...", '.', Item.goldNugget);

		// Drinking helmet recipe
		if (MiscUtils.isEnabled(Items.drinkingHelmet))
			GameRegistry.addRecipe(new DrinkingHelmetRecipe(Items.drinkingHelmet));
		
	}
	
	private static void addCardboardRecipes() {
		
		// Cardboard sheet recipe
		if (MiscUtils.isEnabled(Items.cardboardSheet)) {
			GameRegistry.addShapelessRecipe(new ItemStack(Items.cardboardSheet, 4),
					Item.paper, Item.paper, Item.paper,
					Item.paper, Item.paper, Item.paper,
					Item.paper, Item.paper, Item.slimeBall);
			
			// Cardboard helmet recipe
			if (MiscUtils.isEnabled(Items.cardboardHelmet))
				GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Items.cardboardHelmet),
						"ooo",
						"o o", 'o', "sheetCardboard"));
			// Cardboard chestplate recipe
			if (MiscUtils.isEnabled(Items.cardboardChestplate))
				GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Items.cardboardChestplate),
						"o o",
						"ooo",
						"ooo", 'o', "sheetCardboard"));
			// Cardboard leggings recipe
			if (MiscUtils.isEnabled(Items.cardboardLeggings))
				GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Items.cardboardLeggings),
						"ooo",
						"o o",
						"o o", 'o', "sheetCardboard"));
			// Cardboard boots recipe
			if (MiscUtils.isEnabled(Items.cardboardBoots))
				GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Items.cardboardBoots),
						"o o",
						"o o", 'o', "sheetCardboard"));
			
			// Cardboard sword recipe
			if (MiscUtils.isEnabled(Items.cardboardSword))
				GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Items.cardboardSword),
						"o",
						"o",
						"/", 'o', "sheetCardboard",
						     '/', Item.stick));
			// Cardboard pickaxe recipe
			if (MiscUtils.isEnabled(Items.cardboardPickaxe))
				GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Items.cardboardPickaxe),
						"ooo",
						" / ",
						" / ", 'o', "sheetCardboard",
						       '/', Item.stick));
			// Cardboard shovel recipe
			if (MiscUtils.isEnabled(Items.cardboardShovel))
				GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Items.cardboardShovel),
						"o",
						"/",
						"/", 'o', "sheetCardboard",
						     '/', Item.stick));
			
			// Cardboard axe recipe
			if (MiscUtils.isEnabled(Items.cardboardAxe)) {
				GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Items.cardboardAxe),
						"oo",
						"o/",
						" /", 'o', "sheetCardboard",
						      '/', Item.stick));
				GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Items.cardboardAxe),
						"oo",
						"/o",
						"/ ", 'o', "sheetCardboard",
						      '/', Item.stick));
			}
			
			// Cardboard hoe recipe
			if (MiscUtils.isEnabled(Items.cardboardHoe)) {
				GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Items.cardboardHoe),
						"oo",
						" /",
						" /", 'o', "sheetCardboard",
						      '/', Item.stick));
				GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Items.cardboardHoe),
						"oo",
						"/ ",
						"/ ", 'o', "sheetCardboard",
						      '/', Item.stick));
			}
		}
		
	}
	
}
