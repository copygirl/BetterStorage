package net.mcft.copy.betterstorage;

import java.util.logging.Logger;

import net.mcft.copy.betterstorage.addon.Addon;
import net.mcft.copy.betterstorage.block.BlockArmorStand;
import net.mcft.copy.betterstorage.block.BlockBackpack;
import net.mcft.copy.betterstorage.block.BlockCardboardBox;
import net.mcft.copy.betterstorage.block.BlockEnderBackpack;
import net.mcft.copy.betterstorage.block.BlockLocker;
import net.mcft.copy.betterstorage.block.BlockReinforcedChest;
import net.mcft.copy.betterstorage.block.ChestMaterial;
import net.mcft.copy.betterstorage.block.crate.BlockCrate;
import net.mcft.copy.betterstorage.item.EnchantmentBetterStorage;
import net.mcft.copy.betterstorage.item.ItemCardboardSheet;
import net.mcft.copy.betterstorage.item.ItemKey;
import net.mcft.copy.betterstorage.item.ItemKeyring;
import net.mcft.copy.betterstorage.item.ItemLock;
import net.mcft.copy.betterstorage.item.ItemMasterKey;
import net.mcft.copy.betterstorage.item.recipe.KeyRecipe;
import net.mcft.copy.betterstorage.item.recipe.LockRecipe;
import net.mcft.copy.betterstorage.misc.Constants;
import net.mcft.copy.betterstorage.misc.CreativeTabBetterStorage;
import net.mcft.copy.betterstorage.misc.Registry;
import net.mcft.copy.betterstorage.misc.handlers.CraftingHandler;
import net.mcft.copy.betterstorage.misc.handlers.PacketHandler;
import net.mcft.copy.betterstorage.proxy.CommonProxy;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.ShapedOreRecipe;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;

@Mod(modid = Constants.modId,
     version = "@VERSION@",
     useMetadata = true,
     dependencies = "after:Thaumcraft")
@NetworkMod(clientSideRequired = true,
            serverSideRequired = false,
            channels = { Constants.modId },
            packetHandler = PacketHandler.class)
public class BetterStorage {
	
	@Instance(Constants.modId)
	public static BetterStorage instance;
	
	@SidedProxy(serverSide = Constants.commonProxy,
	            clientSide = Constants.clientProxy)
	public static CommonProxy proxy;
	
	public static Logger log;
	
	
	// Blocks
	public static BlockCrate crate;
	public static BlockReinforcedChest reinforcedChest;
	public static BlockLocker locker;
	public static BlockArmorStand armorStand;
	public static BlockBackpack backpack;
	public static BlockEnderBackpack enderBackpack;
	public static BlockCardboardBox cardboardBox;
	
	// Items
	public static ItemKey key;
	public static ItemLock lock;
	public static ItemKeyring keyring;
	public static ItemCardboardSheet cardboardSheet;
	public static ItemMasterKey masterKey;
	
	
	public static CreativeTabs creativeTab;
	
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		
		log = event.getModLog();
		creativeTab = new CreativeTabBetterStorage();
		
		Addon.initAll();
		
		Config.load(event.getSuggestedConfigurationFile());
		
		initializeItems();
		EnchantmentBetterStorage.init();
		
		GameRegistry.registerCraftingHandler(new CraftingHandler());
		
		LanguageRegistry.instance().loadLocalization("/assets/" + Constants.modName + "/lang/en_US.xml", "en_US", true);
		
	}
	
	@EventHandler
	public void load(FMLInitializationEvent event) {
		
		addRecipes();
		proxy.init();
		
	}
	
	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		Addon.postInitAll();
	}
	
	private void initializeItems() {
		
		crate           = conditionalNew(BlockCrate.class, Config.crateId);
		reinforcedChest = conditionalNew(BlockReinforcedChest.class, Config.chestId);
		locker          = conditionalNew(BlockLocker.class, Config.lockerId);
		armorStand      = conditionalNew(BlockArmorStand.class, Config.armorStandId);
		backpack        = conditionalNew(BlockBackpack.class, Config.backpackId);
		enderBackpack   = conditionalNew(BlockEnderBackpack.class, Config.enderBackpackId);
		cardboardBox    = conditionalNew(BlockCardboardBox.class, Config.cardboardBoxId);
		
		key            = conditionalNew(ItemKey.class, Config.keyId);
		lock           = conditionalNew(ItemLock.class, Config.lockId);
		keyring        = conditionalNew(ItemKeyring.class, Config.keyringId);
		cardboardSheet = conditionalNew(ItemCardboardSheet.class, Config.cardboardSheetId);
		masterKey      = conditionalNew(ItemMasterKey.class, Config.masterKeyId);
		
		Addon.initializeAllItems();
		
		Registry.doYourThing();
		
	}
	
	public static <T> T conditionalNew(Class<T> theClass, int id, Object... requirements) {
		if (id <= 0) return null;
		for (Object obj : requirements)
			if ((obj == null) || ((obj instanceof Integer) && ((Integer)obj <= 0))) return null;
		try { return theClass.getConstructor(int.class).newInstance(id); }
		catch (Exception e) { throw new RuntimeException(e); }
	}
	
	private void addRecipes() {
		
		// Crate recipe
		if (crate != null)
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(crate),
					"o/o",
					"/ /",
					"o/o", 'o', "plankWood",
					       '/', Item.stick));
		
		// Reinforced chest recipes
		if (reinforcedChest != null)
			for (ChestMaterial material : ChestMaterial.materials)
				GameRegistry.addRecipe(material.getRecipe(reinforcedChest));
		
		// Locker recipe
		if (locker != null) {
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(locker),
					"ooo",
					"o |",
					"ooo", 'o', "plankWood",
					       '|', Block.trapdoor));
			GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(locker),
					"ooo",
					"| o",
					"ooo", 'o', "plankWood",
					       '|', Block.trapdoor));
		}
		
		// Armor stand recipe
		if (armorStand != null)
			GameRegistry.addRecipe(new ItemStack(armorStand),
					" i ",
					"/i/",
					" s ", 's', new ItemStack(Block.stoneSingleSlab, 1, 0),
					       'i', Item.ingotIron,
					       '/', Item.stick);
		
		// Backpack recipe
		if (backpack != null)
			GameRegistry.addRecipe(new ItemStack(backpack),
					"#i#",
					"#O#",
					"###", '#', Item.leather,
					       'O', Block.cloth,
					       'i', Item.ingotGold);
		
		// Cardboard box recipe
		if ((cardboardBox != null) && (cardboardSheet != null))
			GameRegistry.addRecipe(new ItemStack(cardboardBox),
					"ooo",
					"o o",
					"ooo", 'o', cardboardSheet);
		
		// Key recipe
		if (key != null) {
			GameRegistry.addRecipe(KeyRecipe.createKeyRecipe(
					".o",
					".o",
					" o", 'o', Item.ingotGold,
					      '.', Item.goldNugget));
		// Key modify recipe
			GameRegistry.addRecipe(KeyRecipe.createKeyRecipe(
					"k", 'k', new ItemStack(key, 1, Constants.anyDamage)));
		}
		
		// Lock recipe
		if ((lock != null) && (key != null))
			GameRegistry.addRecipe(LockRecipe.createLockRecipe());
		
		// Keyring recipe
		if ((keyring != null))
			GameRegistry.addRecipe(new ItemStack(keyring),
					"...",
					". .",
					"...", '.', Item.goldNugget);
		
		// Cardboard sheet recipe
		if (cardboardSheet != null)
			GameRegistry.addRecipe(new ItemStack(cardboardSheet),
					"ooo",
					"ooo",
					"ooo", 'o', Item.paper);
		
		Addon.addAllRecipes();
		
	}
	
}
