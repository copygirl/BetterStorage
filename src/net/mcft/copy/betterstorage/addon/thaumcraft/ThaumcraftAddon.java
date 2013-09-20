package net.mcft.copy.betterstorage.addon.thaumcraft;

import net.mcft.copy.betterstorage.addon.Addon;
import net.mcft.copy.betterstorage.block.BlockBackpack;
import net.mcft.copy.betterstorage.client.renderer.ItemRendererBackpack;
import net.mcft.copy.betterstorage.client.renderer.TileEntityReinforcedChestRenderer;
import net.mcft.copy.betterstorage.content.Blocks;
import net.mcft.copy.betterstorage.content.Items;
import net.mcft.copy.betterstorage.misc.Constants;
import net.mcft.copy.betterstorage.proxy.ClientProxy;
import net.mcft.copy.betterstorage.utils.MiscUtils;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.Configuration;
import thaumcraft.api.EnumTag;
import thaumcraft.api.ItemApi;
import thaumcraft.api.ObjectTags;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.research.ResearchItem;
import thaumcraft.api.research.ResearchList;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ThaumcraftAddon extends Addon {
	
	public static int thaumcraftBackpackId = 2880;
	public static int thaumiumChestId = 2881;
	
	public static BlockBackpack thaumcraftBackpack;
	public static BlockThaumiumChest thaumiumChest;
	
	public static int thaumiumChestRenderId;
	
	public static ItemStack thaumium;
	public static ItemStack fabric;
	public static ItemStack arcaneWood;
	
	public ThaumcraftAddon() {
		super("Thaumcraft");
	}
	
	@Override
	public void loadConfig(Configuration config) {
		thaumcraftBackpackId = config.getBlock("thaumcraftBackpack", thaumcraftBackpackId).getInt();
		thaumiumChestId = config.getBlock("thaumiumChest", thaumiumChestId).getInt();
	}
	
	@Override
	public void initializeBlocks() {
		thaumcraftBackpack = MiscUtils.conditionalNew(BlockThaumcraftBackpack.class, thaumcraftBackpackId);
		thaumiumChest = MiscUtils.conditionalNew(BlockThaumiumChest.class, thaumiumChestId);
	}
	
	@Override
	public void addRecipes() {
		
		thaumium   = ItemApi.getItem("itemResource", 2);
		fabric     = ItemApi.getItem("itemResource", 7);
		arcaneWood = ItemApi.getItem("blockWooden", 0);
		
		// Thaumaturge's backpack recipe
		if (MiscUtils.isEnabled(thaumcraftBackpack, Blocks.backpack)) {
			ObjectTags thaumcraftBackpackAspects =
					(new ObjectTags()).add(EnumTag.VOID, 16)
					                  .add(EnumTag.EXCHANGE, 12)
					                  .add(EnumTag.MAGIC, 10);
			ThaumcraftApi.addInfusionCraftingRecipe("MAGICSTORAGE", "BACKPACK",
					60, thaumcraftBackpackAspects, new ItemStack(thaumcraftBackpack),
					"#i#",
					"#O#",
					"###", '#', fabric,
					       'O', Blocks.backpack,
					       'i', thaumium);
		}
		
		// Thaumium chest recipe
		if (MiscUtils.isEnabled(thaumiumChest, Blocks.reinforcedChest)) {
			ObjectTags thaumiumChestAspects =
					(new ObjectTags()).add(EnumTag.METAL, 64)
					                  .add(EnumTag.VOID, 20)
					                  .add(EnumTag.MAGIC, 16);
			ThaumcraftApi.addInfusionCraftingRecipe("MAGICSTORAGE", "THAUMCHEST",
					55, thaumiumChestAspects, new ItemStack(thaumiumChest),
					"o#o",
					"#C#",
					"oOo", 'C', Blocks.reinforcedChest,
					       '#', arcaneWood,
					       'o', thaumium,
					       'O', Block.blockIron);
		}
		
		addAspects();
		
		ThaumcraftApi.registerResearchXML("/net/mcft/copy/betterstorage/addon/thaumcraft/research.xml");
		
	}
	
	private void addAspects() {
		
		addAspectsFor(Blocks.reinforcedChest, -1, EnumTag.VOID, 10, EnumTag.ARMOR, 14);
		addAspectsFor(Blocks.crate, -1, EnumTag.VOID, 4, EnumTag.WOOD, 2);
		addAspectsFor(Blocks.locker, -1, EnumTag.VOID, 4, EnumTag.WOOD, 2);
		addAspectsFor(Blocks.armorStand, -1, EnumTag.METAL, 14);
		addAspectsFor(Blocks.backpack, -1, EnumTag.VOID, 8, EnumTag.BEAST, 8, EnumTag.CLOTH, 8, EnumTag.ARMOR, 6);
		addAspectsFor(Blocks.enderBackpack, -1, EnumTag.MAGIC, 12, EnumTag.ELDRITCH, 14, EnumTag.DARK, 10, EnumTag.ARMOR, 8);
		addAspectsFor(Blocks.cardboardBox, -1, EnumTag.VOID, 3, EnumTag.TOOL, 4);
		
		addAspectsFor(Items.key, -1, EnumTag.METAL, 14, EnumTag.VALUABLE, 10, EnumTag.CONTROL, 2);
		addAspectsFor(Items.lock, -1, EnumTag.METAL, 20, EnumTag.VALUABLE, 12, EnumTag.ARMOR, 6, EnumTag.MECHANISM, 4);
		addAspectsFor(Items.keyring, -1, EnumTag.METAL, 6, EnumTag.VALUABLE, 2);
		addAspectsFor(Items.cardboardSheet, -1, EnumTag.TOOL, 1);
		
		addAspectsFor(thaumcraftBackpack, -1, EnumTag.VOID, 18, EnumTag.CLOTH, 16, EnumTag.ARMOR, 6, EnumTag.MAGIC, 18, EnumTag.EXCHANGE, 8);
		
	}
	private static void addAspectsFor(Block block, int meta, Object... stuff) {
		if (MiscUtils.isEnabled(block))
			addAspectsFor(block.blockID, meta, stuff);
	}
	private static void addAspectsFor(Item item, int meta, Object... stuff) {
		if (MiscUtils.isEnabled(item))
			addAspectsFor(item.itemID, meta, stuff);
	}
	private static void addAspectsFor(int id, int meta, Object... stuff) {
		ObjectTags aspects = new ObjectTags();
		for (int i = 0; i < stuff.length; i += 2)
			aspects.add((EnumTag)stuff[i], (Integer)stuff[i + 1]);
		ThaumcraftApi.registerObjectTag(id, meta, aspects);
	}
	
	@Override
	public void registerTileEntities() {
		GameRegistry.registerTileEntity(TileEntityThaumcraftBackpack.class, Constants.containerThaumcraftBackpack);
		GameRegistry.registerTileEntity(TileEntityThaumiumChest.class, Constants.containerThaumiumChest);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerRenderers() {
		MinecraftForgeClient.registerItemRenderer(thaumcraftBackpackId, ItemRendererBackpack.instance);
		thaumiumChestRenderId = ClientProxy.registerTileEntityRenderer(TileEntityThaumiumChest.class, new TileEntityReinforcedChestRenderer());
	}
	
	@Override
	public void postInitialize() {
		
		ObjectTags researchAspects = (new ObjectTags()).add(EnumTag.VOID, 20)
		                                               .add(EnumTag.MAGIC, 12)
		                                               .add(EnumTag.EXCHANGE, 6)
		                                               .add(EnumTag.KNOWLEDGE, 6);
		ResearchItem research = new ResearchItem("MAGICSTORAGE", researchAspects, -5, 4, thaumcraftBackpack);
		research.setParents(ResearchList.getResearch("UTFT"));
		research.longText = "Studying the Vacuos element and how it interacts with other elements, " +
		                    "you think you can apply this knowledge to enchant various containers so " +
		                    "they gain more abilities, such as being able to store more items.";
		research.registerResearchItem();
		
	}
	
}
