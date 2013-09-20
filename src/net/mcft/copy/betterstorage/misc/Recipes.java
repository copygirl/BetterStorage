package net.mcft.copy.betterstorage.misc;

import net.mcft.copy.betterstorage.addon.Addon;
import net.mcft.copy.betterstorage.block.ContainerMaterial;
import net.mcft.copy.betterstorage.content.Blocks;
import net.mcft.copy.betterstorage.content.Items;
import net.mcft.copy.betterstorage.item.recipe.DrinkingHelmetRecipe;
import net.mcft.copy.betterstorage.item.recipe.KeyRecipe;
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
				IRecipe recipe = material.getChestRecipe(Blocks.reinforcedChest);
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
		
		// Lock recipe
		if (MiscUtils.isEnabled(Items.lock, Items.key))
			GameRegistry.addRecipe(LockRecipe.createLockRecipe());
		
		// Keyring recipe
		if (MiscUtils.isEnabled(Items.keyring))
			GameRegistry.addRecipe(new ItemStack(Items.keyring),
					"...",
					". .",
					"...", '.', Item.goldNugget);
		
		// Cardboard sheet recipe
		if (MiscUtils.isEnabled(Items.cardboardSheet))
			GameRegistry.addRecipe(new ItemStack(Items.cardboardSheet),
					"ooo",
					"ooo",
					"ooo", 'o', Item.paper);

		// Drinking helmet recipe
		if (MiscUtils.isEnabled(Items.drinkingHelmet))
			GameRegistry.addRecipe(new DrinkingHelmetRecipe(Items.drinkingHelmet));
		
		Addon.addRecipesAll();
		
	}
	
}
