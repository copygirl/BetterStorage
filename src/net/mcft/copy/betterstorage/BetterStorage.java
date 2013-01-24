package net.mcft.copy.betterstorage;

import java.util.Random;
import net.mcft.copy.betterstorage.block.BlockCrate;
import net.mcft.copy.betterstorage.block.BlockReinforcedChest;
import net.mcft.copy.betterstorage.block.ChestMaterial;
import net.mcft.copy.betterstorage.block.CratePileCollection;
import net.mcft.copy.betterstorage.enchantments.EnchantmentBetterStorage;
import net.mcft.copy.betterstorage.item.ItemKey;
import net.mcft.copy.betterstorage.item.ItemLock;
import net.mcft.copy.betterstorage.item.ItemReinforcedChest;
import net.mcft.copy.betterstorage.item.KeyRecipe;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.world.WorldEvent.Save;
import net.minecraftforge.oredict.ShapedOreRecipe;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.Mod.PreInit;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;


@Mod(modid="BetterStorage", name="BetterStorage", version="0.5.0-indev")
@NetworkMod(clientSideRequired=true, serverSideRequired=false)
public class BetterStorage {
	
	public final static Random random = new Random();
	
	public static BlockCrate crate;
	public static BlockReinforcedChest reinforcedChest;
	
	public static ItemKey key;
	public static ItemLock lock;
	
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
		EnchantmentBetterStorage.init();
		addRecipes();
		addLocalizations();
		GameRegistry.registerCraftingHandler(new CraftingHandler());
		NetworkRegistry.instance().registerGuiHandler(this, new GuiHandler());
		proxy.init();
	}
	
	private void initializeItems() {
		crate = new BlockCrate(Config.crateId);
		reinforcedChest = new BlockReinforcedChest(Config.chestId);
		new ItemReinforcedChest(Config.chestId);
		
		key  = new ItemKey(Config.keyId);
		lock = new ItemLock(Config.lockId);
	}
	
	private void addRecipes() {
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(crate),
				"o/o",
				"/ /",
				"o/o", 'o', "plankWood",
				       '/', Item.stick));
		
		for (ChestMaterial material : ChestMaterial.materials)
			GameRegistry.addRecipe(material.getRecipe(reinforcedChest));
		
		// Key recipe
		GameRegistry.addRecipe(KeyRecipe.createKeyRecipe(
				".o",
				".o",
				" o", 'o', Item.ingotGold,
				      '.', Item.goldNugget));
		// Key modify recipe
		GameRegistry.addRecipe(KeyRecipe.createKeyRecipe(
				"k", 'k', new ItemStack(key, 1, -1)));
		// Lock recipe
		GameRegistry.addRecipe(new ItemStack(lock),
				" o ",
				"oko",
				"oio", 'o', Item.ingotGold,
				       'k', new ItemStack(key, 1, -1),
				       'i', Item.ingotIron);
	}
	
	/*
	private void addReinforcedChestRecipe(Block result, Object ingot, Object block) {
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
	*/
	
	private void addLocalizations() {
		
		addLocal("container.reinforcedChest", "Reinforced Chest");
		addLocal("container.reinforcedChestLarge", "Large Reinforced Chest");
		addLocal("container.crate", "Storage Crate");
		
		addLocal("enchantment.key.unlocking", "Unlocking");
		addLocal("enchantment.key.lockpicking", "Lockpicking");
		addLocal("enchantment.key.morphing", "Morphing");
		
		addLocal("enchantment.lock.persistance", "Persistance");
		addLocal("enchantment.lock.security", "Security");
		addLocal("enchantment.lock.shock", "Shock");
		addLocal("enchantment.lock.trigger", "Trigger");
		
		for (ChestMaterial material : ChestMaterial.materials)
			addLocal("reinforcedChest." + material.name + ".name", "Reinforced " + material.nameCapitalized + " Chest");
		
	}
	private void addLocal(String key, String value) {
		LanguageRegistry.instance().addStringLocalization(key, value);
	}
	
	@ForgeSubscribe
	public void onWorldSave(Save event) {
		CratePileCollection.saveAll(event.world);
	}
	
	public static void log(String message) {
		System.out.println("[BetterStorage] " + message);
	}
	
}
