package net.mcft.copy.betterstorage.misc;

import net.mcft.copy.betterstorage.addon.Addon;
import net.mcft.copy.betterstorage.block.ChestMaterial;
import net.mcft.copy.betterstorage.content.Blocks;
import net.mcft.copy.betterstorage.content.Items;
import net.mcft.copy.betterstorage.item.recipe.KeyRecipe;
import net.mcft.copy.betterstorage.item.recipe.LockRecipe;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.ShapedOreRecipe;
import cpw.mods.fml.common.registry.GameRegistry;

public final class Recipes {
	
	private Recipes() {  }
	
	public static void add() {
		
		// Crate recipe
		if (Blocks.crate != null)
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(Blocks.crate),
					"o/o",
					"/ /",
					"o/o", 'o', "plankWood",
					       '/', "stickWood"));
		
		// Reinforced chest recipes
		if (Blocks.reinforcedChest != null)
			for (ChestMaterial material : ChestMaterial.materials)
				GameRegistry.addRecipe(material.getRecipe(Blocks.reinforcedChest));
		
		// Locker recipe
		if (Blocks.locker != null) {
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
		if (Blocks.armorStand != null)
			GameRegistry.addRecipe(new ItemStack(Blocks.armorStand),
					" i ",
					"/i/",
					" s ", 's', new ItemStack(Block.stoneSingleSlab, 1, 0),
					       'i', Item.ingotIron,
					       '/', Item.stick);
		
		// Backpack recipe
		if (Blocks.backpack != null)
			GameRegistry.addRecipe(new ItemStack(Blocks.backpack),
					"#i#",
					"#O#",
					"###", '#', Item.leather,
					       'O', Block.cloth,
					       'i', Item.ingotGold);
		
		// Cardboard box recipe
		if ((Blocks.cardboardBox != null) && (Items.cardboardSheet != null))
			GameRegistry.addRecipe(new ItemStack(Blocks.cardboardBox),
					"ooo",
					"o o",
					"ooo", 'o', Items.cardboardSheet);
		
		// Key recipe
		if (Items.key != null) {
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
		if ((Items.lock != null) && (Items.key != null))
			GameRegistry.addRecipe(LockRecipe.createLockRecipe());
		
		// Keyring recipe
		if ((Items.keyring != null))
			GameRegistry.addRecipe(new ItemStack(Items.keyring),
					"...",
					". .",
					"...", '.', Item.goldNugget);
		
		// Cardboard sheet recipe
		if (Items.cardboardSheet != null)
			GameRegistry.addRecipe(new ItemStack(Items.cardboardSheet),
					"ooo",
					"ooo",
					"ooo", 'o', Item.paper);
		
		Addon.addRecipesAll();
		
	}
	
}
