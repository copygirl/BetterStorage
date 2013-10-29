package net.mcft.copy.betterstorage.addon.thaumcraft;

import java.util.ArrayList;
import java.util.List;

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
import thaumcraft.api.ItemApi;
import thaumcraft.api.ThaumcraftApi;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.crafting.InfusionRecipe;
import thaumcraft.api.research.ResearchItem;
import thaumcraft.api.research.ResearchPage;
import cpw.mods.fml.common.Loader;
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
	public static ItemStack thaumiumBlock;
	public static ItemStack fabric;
	
	public static InfusionRecipe thaumcraftBackpackRecipe;
	public static InfusionRecipe thaumiumChestRecipe;
	
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
		
		thaumium      = ItemApi.getItem("itemResource", 2);
		thaumiumBlock = ItemApi.getBlock("blockCosmeticSolid", 4);
		fabric        = ItemApi.getItem("itemResource", 7);
		
		ItemStack oakWood = new ItemStack(Block.wood);
		
		// Thaumaturge's backpack recipe
		if (MiscUtils.isEnabled(thaumcraftBackpack, Blocks.backpack)) {
			thaumcraftBackpackRecipe = ThaumcraftApi.addInfusionCraftingRecipe("betterstorage.magicstorage",
					new ItemStack(thaumcraftBackpack), 1,
					createAspectList(Aspect.VOID, 16, Aspect.EXCHANGE, 12, Aspect.MAGIC, 10),
					new ItemStack(Blocks.backpack),
					new ItemStack[]{ thaumium, fabric, fabric, fabric });
		}
		
		// Thaumium chest recipe
		if (MiscUtils.isEnabled(thaumiumChest, Blocks.reinforcedChest)) {
			thaumiumChestRecipe = ThaumcraftApi.addInfusionCraftingRecipe("betterstorage.magicstorage",
					new ItemStack(thaumiumChest), 4,
					createAspectList(Aspect.METAL, 16, Aspect.VOID, 20, Aspect.MAGIC, 16),
					new ItemStack(Blocks.reinforcedChest),
					new ItemStack[]{ thaumiumBlock, thaumium, oakWood, thaumium, oakWood, thaumium });
		}
		
	}
	
	private void addAspects() {
		
		addAspectsFor(Blocks.crate, -1, true, Aspect.VOID, 3);
		addAspectsFor(Blocks.locker, -1, true, Aspect.VOID, 4);
		addAspectsFor(Blocks.reinforcedChest, -1, true, Aspect.VOID, 5, Aspect.METAL, 10, Aspect.ARMOR, 6);
		
		addAspectsFor(Blocks.backpack, -1, true, Aspect.VOID, 4, Aspect.EXCHANGE, 6);
		addAspectsFor(Blocks.enderBackpack, -1, true, Aspect.DARKNESS, 8, Aspect.VOID, 4, Aspect.EXCHANGE, 8,
		                                              Aspect.TRAVEL, 4, Aspect.ELDRITCH, 4, Aspect.MAGIC, 4);
		
		addAspectsFor(Items.cardboardSheet, -1, false, Aspect.CRAFT, 1);
		addAspectsFor(Blocks.cardboardBox, -1, true, Aspect.VOID, 2, Aspect.TRAVEL, 2);
		
		addAspectsFor(Items.key, -1, false, Aspect.GREED, 5, Aspect.METAL, 4, Aspect.TOOL, 2);
		addAspectsFor(Items.lock, -1, false, Aspect.GREED, 4, Aspect.METAL, 6, Aspect.MECHANISM, 6, Aspect.ARMOR, 8);
		addAspectsFor(Items.keyring, -1, false, Aspect.GREED, 1, Aspect.METAL, 2, Aspect.TOOL, 1);
		
		addAspectsFor(Items.drinkingHelmet, -1, true, Aspect.ARMOR, 2, Aspect.MECHANISM, 5, Aspect.ENERGY, 6);
		
	}
	private static void addAspectsFor(Block block, int meta, boolean add, Object... aspects) {
		if (MiscUtils.isEnabled(block))
			addAspectsFor(block.blockID, meta, add, aspects);
	}
	private static void addAspectsFor(Item item, int meta, boolean add, Object... aspects) {
		if (MiscUtils.isEnabled(item))
			addAspectsFor(item.itemID, meta, add, aspects);
	}
	private static void addAspectsFor(int id, int meta, boolean add, Object... aspects) {
		AspectList list = createAspectList(aspects);
		if (add) ThaumcraftApi.registerComplexObjectTag(id, meta, list);
		else ThaumcraftApi.registerObjectTag(id, meta, list);
	}
	
	public static AspectList createAspectList(Object... aspects) {
		AspectList list = new AspectList();
		for (int i = 0; i < aspects.length; i += 2)
			list.add((Aspect)aspects[i], (Integer)aspects[i + 1]);
		return list;
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
		
		addAspects();
		
		if (MiscUtils.isEnabled(thaumcraftBackpack, thaumiumChest)) {
			ResearchItem research = new ResearchItem(
					"betterstorage.magicstorage", "ARTIFICE",
					createAspectList(Aspect.VOID, 2, Aspect.MAGIC, 1, Aspect.EXCHANGE, 1),
					2, (Loader.isModLoaded("ThaumicTinkerer") ? 3 : 4), 3, new ItemStack(thaumcraftBackpack));
			research.setParents("ENCHFABRIC");
			research.setParentsHidden("INFUSION");
			research.setConcealed();
			List<ResearchPage> pages = new ArrayList<ResearchPage>();
			pages.add(new ResearchPage("tc.research_page.betterstorage.magicstorage.1"));
			if (MiscUtils.isEnabled(thaumcraftBackpack))
				pages.add(new ResearchPage(thaumcraftBackpackRecipe));
			if (MiscUtils.isEnabled(thaumiumChest))
				pages.add(new ResearchPage(thaumiumChestRecipe));
			research.setPages(pages.toArray(new ResearchPage[0]));
			research.registerResearchItem();
		}
		
	}
	
}
