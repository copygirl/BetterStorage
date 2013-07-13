package net.mcft.copy.betterstorage.block;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.oredict.ShapedOreRecipe;

public class ChestMaterial {

	private static ChestMaterial[] materialLookup = new ChestMaterial[16];
	public static List<ChestMaterial> materials = new ArrayList<ChestMaterial>();
	
	// Vanilla materials
	public static ChestMaterial iron    = new ChestMaterial(0, "iron", Item.ingotIron, Block.blockIron);
	public static ChestMaterial gold    = new ChestMaterial(1, "gold", Item.ingotGold, Block.blockGold);
	public static ChestMaterial diamond = new ChestMaterial(2, "diamond", Item.diamond, Block.blockDiamond);
	public static ChestMaterial emerald = new ChestMaterial(3, "emerald", Item.emerald, Block.blockEmerald);
	
	// Mod materials
	public static ChestMaterial copper = new ChestMaterial(5, "copper", "ingotCopper", "blockCopper");
	public static ChestMaterial tin    = new ChestMaterial(6, "tin", "ingotTin", "blockTin");
	public static ChestMaterial silver = new ChestMaterial(7, "silver", "ingotSilver", "blockSilver");
	public static ChestMaterial zinc   = new ChestMaterial(8, "zinc", "ingotZinc", "blockZinc");
	
	public static ChestMaterial get(int id) { return materialLookup[id]; }
	
	public int id;
	public String name;
	public String nameCapitalized;
	
	private Object ingot, block;
	
	private ChestMaterial(int id, String name, Object ingot, Object block) {
		this.id = id;
		this.name = name;
		this.ingot = ingot;
		this.block = block;
		nameCapitalized = name.substring(0, 1).toUpperCase() + name.substring(1);
		materialLookup[id] = this;
		materials.add(this);
	}
	
	public ShapedOreRecipe getRecipe(Block result) {
		return new ShapedOreRecipe(
				new ItemStack(result, 1, id),
				"o#o",
				"#C#",
				"oOo", 'C', Block.chest,
				       '#', "logWood",
				       'o', ingot,
				       'O', block);
	}
	
	public ResourceLocation getResource(boolean large) {
		return new ResourceLocation("betterstorage", "textures/models/chest" + (large ? "_large/" : "/") + name + ".png");
	}
	
}
