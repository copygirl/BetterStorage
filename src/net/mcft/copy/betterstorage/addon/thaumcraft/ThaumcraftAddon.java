package net.mcft.copy.betterstorage.addon.thaumcraft;

import net.mcft.copy.betterstorage.BetterStorage;
import net.mcft.copy.betterstorage.addon.Addon;
import net.mcft.copy.betterstorage.block.BlockBackpack;
import net.mcft.copy.betterstorage.block.ChestMaterial;
import net.mcft.copy.betterstorage.client.renderer.ItemRendererBackpack;
import net.mcft.copy.betterstorage.client.renderer.TileEntityReinforcedChestRenderer;
import net.mcft.copy.betterstorage.misc.Registry;
import net.mcft.copy.betterstorage.proxy.ClientProxy;
import net.minecraft.block.Block;
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
import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ThaumcraftAddon extends Addon {
	
	public static int thaumcraftBackpackId = 2880;
	public static int thaumiumChestId = 2881;
	
	public static BlockBackpack thaumcraftBackpack;
	public static BlockThaumiumChest thaumiumChest;
	
	public static int thaumiumChestRenderId;
	
	public ThaumcraftAddon() {
		super("Thaumcraft");
	}
	
	@Override
	public void loadConfig(Configuration config) {
		thaumcraftBackpackId = config.getBlock("thaumcraftBackpack", thaumcraftBackpackId).getInt();
		thaumiumChestId = config.getBlock("thaumiumChest", thaumiumChestId).getInt();
	}
	
	@Override
	public void initializeItems() {
		thaumcraftBackpack = new BlockThaumcraftBackpack(thaumcraftBackpackId);
		thaumiumChest = new BlockThaumiumChest(thaumiumChestId);
	}
	
	@Override
	public void registerItems() {
		Registry.registerHacky(thaumcraftBackpack, "thaumcraftBackpack", "Thaumaturge's Backpack", ItemThaumcraftBackpack.class);
		Registry.register(thaumiumChest, "thaumiumChest", "Thaumium Chest");
	}
	
	@Override
	public void addRecipes() {
		
		ItemStack thaumium = ItemApi.getItem("itemResource", 2);
		ItemStack fabric = ItemApi.getItem("itemResource", 7);
		ItemStack arcaneWood = ItemApi.getItem("blockWooden", 0);
		
		ObjectTags thaumcraftBackpackAspects =
				(new ObjectTags()).add(EnumTag.VOID, 16)
				                  .add(EnumTag.EXCHANGE, 12)
				                  .add(EnumTag.MAGIC, 10);
		ThaumcraftApi.addInfusionCraftingRecipe("MAGICSTORAGE", "BACKPACK",
				60, thaumcraftBackpackAspects, new ItemStack(thaumcraftBackpack),
				"#i#",
				"#O#",
				"###", '#', fabric,
				       'O', BetterStorage.backpack,
				       'i', thaumium);
		
		ObjectTags thaumiumChestAspects =
				(new ObjectTags()).add(EnumTag.METAL, 64)
				                  .add(EnumTag.VOID, 20)
				                  .add(EnumTag.MAGIC, 16);
		ThaumcraftApi.addInfusionCraftingRecipe("MAGICSTORAGE", "THAUMCHEST",
				55, thaumiumChestAspects, new ItemStack(thaumiumChest),
				"o#o",
				"#C#",
				"oOo", 'C', BetterStorage.reinforcedChest,
				       '#', arcaneWood,
				       'o', thaumium,
				       'O', Block.blockIron);
		
		addAspects();
		
		ThaumcraftApi.registerResearchXML("/net/mcft/copy/betterstorage/addon/thaumcraft/research.xml");
		
	}
	
	private void addAspects() {
		
		// Vanilla materials reinforced chests
		addAspectsFor(BetterStorage.reinforcedChest.blockID, ChestMaterial.iron.id,
		              EnumTag.VOID, 6, EnumTag.ARMOR, 12, EnumTag.METAL, 64);
		addAspectsFor(BetterStorage.reinforcedChest.blockID, ChestMaterial.gold.id,
		              EnumTag.VOID, 6, EnumTag.ARMOR, 12, EnumTag.METAL, 64, EnumTag.VALUABLE, 30);
		addAspectsFor(BetterStorage.reinforcedChest.blockID, ChestMaterial.diamond.id,
		              EnumTag.VOID, 6, EnumTag.ARMOR, 12, EnumTag.CRYSTAL, 96, EnumTag.VALUABLE, 30, EnumTag.PURE, 30);
		addAspectsFor(BetterStorage.reinforcedChest.blockID, ChestMaterial.emerald.id,
		              EnumTag.VOID, 6, EnumTag.ARMOR, 12, EnumTag.CRYSTAL, 80, EnumTag.VALUABLE, 30, EnumTag.EXCHANGE, 30);
		
		// Mod materials reinforced chests
		addAspectsFor(BetterStorage.reinforcedChest.blockID, ChestMaterial.copper.id,
		              EnumTag.VOID, 6, EnumTag.ARMOR, 12, EnumTag.METAL, 48, EnumTag.LIFE, 16);
		addAspectsFor(BetterStorage.reinforcedChest.blockID, ChestMaterial.tin.id,
		              EnumTag.VOID, 6, EnumTag.ARMOR, 12, EnumTag.METAL, 48, EnumTag.CONTROL, 16);
		addAspectsFor(BetterStorage.reinforcedChest.blockID, ChestMaterial.silver.id,
		              EnumTag.VOID, 6, EnumTag.ARMOR, 12, EnumTag.METAL, 48, EnumTag.EXCHANGE, 16);
		addAspectsFor(BetterStorage.reinforcedChest.blockID, ChestMaterial.zinc.id,
		              EnumTag.VOID, 6, EnumTag.ARMOR, 12, EnumTag.METAL, 64);
		
		addAspectsFor(BetterStorage.crate.blockID, -1, EnumTag.VOID, 4, EnumTag.WOOD, 2);
		addAspectsFor(BetterStorage.locker.blockID, -1, EnumTag.VOID, 4, EnumTag.WOOD, 2);
		addAspectsFor(BetterStorage.armorStand.blockID, -1, EnumTag.METAL, 14);
		addAspectsFor(BetterStorage.backpack.blockID, -1, EnumTag.VOID, 8, EnumTag.BEAST, 8, EnumTag.CLOTH, 8, EnumTag.ARMOR, 6);
		addAspectsFor(BetterStorage.enderBackpack.blockID, -1, EnumTag.MAGIC, 12, EnumTag.ELDRITCH, 14, EnumTag.DARK, 10, EnumTag.ARMOR, 8);
		
		addAspectsFor(BetterStorage.key.itemID, -1, EnumTag.METAL, 14, EnumTag.VALUABLE, 10, EnumTag.CONTROL, 2);
		addAspectsFor(BetterStorage.lock.itemID, -1, EnumTag.METAL, 20, EnumTag.VALUABLE, 12, EnumTag.ARMOR, 6, EnumTag.MECHANISM, 4);
		addAspectsFor(BetterStorage.keyring.itemID, -1, EnumTag.METAL, 6, EnumTag.VALUABLE, 2);
		
		addAspectsFor(thaumcraftBackpack.blockID, -1, EnumTag.VOID, 18, EnumTag.CLOTH, 16, EnumTag.ARMOR, 6, EnumTag.MAGIC, 18, EnumTag.EXCHANGE, 8);
		
	}
	private static void addAspectsFor(int id, int meta, Object... stuff) {
		ObjectTags aspects = new ObjectTags();
		for (int i = 0; i < stuff.length; i += 2)
			aspects.add((EnumTag)stuff[i], (Integer)stuff[i + 1]);
		ThaumcraftApi.registerObjectTag(id, meta, aspects);
	}
	
	@Override
	public void addLocalizations(LanguageRegistry lang) {
		lang.addStringLocalization("container.thaumcraftBackpack", "Thaumaturge's Backpack");
		lang.addStringLocalization("container.thaumiumChest", "Thaumium Chest");
		lang.addStringLocalization("container.thaumiumChestLarge", "Large Thaumium Chest");
	}
	
	@Override
	public void registerTileEntites() {
		GameRegistry.registerTileEntity(TileEntityThaumcraftBackpack.class, "container.thaumcraftBackpack");
		GameRegistry.registerTileEntity(TileEntityThaumiumChest.class, "container.thaumiumChest");
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public void postClientInit() {
		MinecraftForgeClient.registerItemRenderer(thaumcraftBackpackId, ItemRendererBackpack.instance);
		thaumiumChestRenderId = ClientProxy.registerTileEntityRenderer(TileEntityThaumiumChest.class, new TileEntityReinforcedChestRenderer());
	}
	
	@Override
	public void postInit() {
		
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
