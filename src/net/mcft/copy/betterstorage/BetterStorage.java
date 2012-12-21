package net.mcft.copy.betterstorage;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import net.mcft.copy.betterstorage.blocks.BlockCrate;
import net.mcft.copy.betterstorage.blocks.BlockReinforcedChest;
import net.mcft.copy.betterstorage.blocks.CratePileCollection;
import net.mcft.copy.betterstorage.blocks.InventoryBlockCrafting;
import net.mcft.copy.betterstorage.items.ItemKey;
import net.mcft.copy.betterstorage.items.ItemLock;
import net.minecraft.block.Block;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.world.WorldEvent.Save;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.Mod.PostInit;
import cpw.mods.fml.common.Mod.PreInit;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;


@Mod(modid="BetterStorage", name="BetterStorage", version="0.3.3")
@NetworkMod(clientSideRequired=true, serverSideRequired=false)
public class BetterStorage {
	
	public final static Random random = new Random();
	
	public static BlockCrate crate;
	
	// Vanilla material chests
	public static BlockReinforcedChest reinforcedIronChest;
	public static BlockReinforcedChest reinforcedGoldChest;
	public static BlockReinforcedChest reinforcedDiamondChest;
	public static BlockReinforcedChest reinforcedEmeraldChest;
	
	// Mod material chests
	public static BlockReinforcedChest reinforcedCopperChest;
	public static BlockReinforcedChest reinforcedTinChest;
	public static BlockReinforcedChest reinforcedSilverChest;
	
	public static ItemKey key;
	public static ItemLock lock;

	private final static String[] materials = { "copper", "tin", "silver" };
	
	public final static Map<String, BlockReinforcedChest> chestsByMaterial = new HashMap();
	
	
	@Instance("BetterStorage")
	public static BetterStorage instance;
	
	@SidedProxy(clientSide="net.mcft.copy.betterstorage.client.ClientProxy",
	            serverSide="net.mcft.copy.betterstorage.CommonProxy")
	public static CommonProxy proxy;
	
	@PreInit
	public void preInit(FMLPreInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(instance);
		Config.load(event.getSuggestedConfigurationFile());
		Config.save();
	}
	
	@Init
	public void load(FMLInitializationEvent event) {
		initializeItems();
		addRecipes();
		addLocalizations();
		GameRegistry.registerCraftingHandler(new CraftingHandler());
		NetworkRegistry.instance().registerGuiHandler(this, new GuiHandler());
		proxy.init();
	}
	
	@PostInit
	public void postInit(FMLPostInitializationEvent event) {
		Set<String> oreNames = new HashSet(Arrays.asList(OreDictionary.getOreNames()));
		for (String material : materials)
			tryAddModdedMaterialChestRecipe(material, oreNames);
	}
	
	private void initializeItems() {
		crate = new BlockCrate(Config.crateId);
		
		reinforcedIronChest    = new BlockReinforcedChest(getChestId(0), "iron");
		reinforcedGoldChest    = new BlockReinforcedChest(getChestId(1), "gold");
		reinforcedDiamondChest = new BlockReinforcedChest(getChestId(2), "diamond");
		reinforcedEmeraldChest = new BlockReinforcedChest(getChestId(3), "emerald");
		
		reinforcedCopperChest  = new BlockReinforcedChest(getChestId(4), "copper");
		reinforcedTinChest     = new BlockReinforcedChest(getChestId(5), "tin");
		reinforcedSilverChest  = new BlockReinforcedChest(getChestId(6), "silver");
		
		key = new ItemKey(Config.keyId);
		lock = new ItemLock(Config.lockId);
	}
	private int getChestId(int offset) {
		return Config.chestBaseId + offset;
	}
	
	private void addRecipes() {
		GameRegistry.addRecipe(new ItemStack(crate),
		        "o/o",
		        "/ /",
		        "o/o", 'o', Block.planks,
		               '/', Item.stick);
		
		addReinforcedChestRecipe(reinforcedIronChest, Item.ingotIron, Block.blockSteel);
		addReinforcedChestRecipe(reinforcedGoldChest, Item.ingotGold, Block.blockGold);
		addReinforcedChestRecipe(reinforcedDiamondChest, Item.diamond, Block.blockDiamond);
		addReinforcedChestRecipe(reinforcedEmeraldChest, Item.emerald, Block.blockEmerald);
		
		// Key recipe
		GameRegistry.addRecipe(new ItemStack(key),
		        ".o",
		        ".o",
		        " o", 'o', Item.ingotGold,
		              '.', Item.goldNugget);
		// Key duplicate recipe
		GameRegistry.addRecipe(new ItemStack(key),
		        ".o",
		        ".o",
		        "ko", 'o', Item.ingotGold,
		              '.', Item.goldNugget,
		              'k', new ItemStack(key, 1, -1));
		// Lock recipe
		GameRegistry.addRecipe(new ItemStack(lock),
		        " o ",
		        "oko",
		        "ooo", 'o', Item.ingotGold,
		               'k', new ItemStack(key, 1, -1));
	}
	
	private void addReinforcedChestRecipe(Block result, Object ingot, Object block) {
		ShapedOreRecipe recipe = new ShapedOreRecipe(result,
		        "o#o",
		        "#C#",
		        "oOo", 'C', Block.chest,
		               '#', "logWood",
		               'o', ingot,
		               'O', block);
		CraftingManager.getInstance().getRecipeList().add(recipe);
	}
	
	private void tryAddModdedMaterialChestRecipe(String material, Set<String> oreNames) {
		// Put together the ore dictionary name of an ingot made from this material.
		String ingotName = "ingot" + material.substring(0, 1).toUpperCase() + material.substring(1);
		// See if the ore dictionary knows about this,
		// then get all items associated with the name.
		if (!oreNames.contains(ingotName)) return;
		List<ItemStack> ingots = OreDictionary.getOres(ingotName);
		// This set makes sure we don't add a recipe for the same block twice.
		Set<Integer> blocks = new HashSet();
		// Go over all items and try to craft a block,
		// then add a recipe for that block.
		for (ItemStack ingot : ingots) {
			InventoryCrafting crafting = new InventoryBlockCrafting(ingot);
			ItemStack block = CraftingManager.getInstance().findMatchingRecipe(crafting, null);
			// See if we found a block recipe
			// and check if we haven't already made a recipe for this block.
			if (block == null || blocks.contains(block.itemID)) continue;
			blocks.add(block.itemID);
			BlockReinforcedChest chest = chestsByMaterial.get(material);
			addReinforcedChestRecipe(chest, "ingot" + chest.name, block);
		}
	}
	
	private void addLocalizations() {
		LanguageRegistry.instance().addStringLocalization("container.reinforcedChest", "Reinforced Chest");
		LanguageRegistry.instance().addStringLocalization("container.reinforcedChestLarge", "Large Reinforced Chest");
		LanguageRegistry.instance().addStringLocalization("container.crate", "Storage Crate");
	}
	
	@ForgeSubscribe
	public void onWorldSave(Save event) {
		CratePileCollection.saveAll(event.world);
	}
	
}
