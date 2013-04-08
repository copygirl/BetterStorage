package net.mcft.copy.betterstorage;

import java.util.Random;
import java.util.logging.Logger;

import net.mcft.copy.betterstorage.block.BlockArmorStand;
import net.mcft.copy.betterstorage.block.BlockLocker;
import net.mcft.copy.betterstorage.block.BlockReinforcedChest;
import net.mcft.copy.betterstorage.block.ChestMaterial;
import net.mcft.copy.betterstorage.block.crate.BlockCrate;
import net.mcft.copy.betterstorage.enchantment.EnchantmentBetterStorage;
import net.mcft.copy.betterstorage.item.ItemKey;
import net.mcft.copy.betterstorage.item.ItemKeyring;
import net.mcft.copy.betterstorage.item.ItemLock;
import net.mcft.copy.betterstorage.item.KeyRecipe;
import net.mcft.copy.betterstorage.item.LockRecipe;
import net.mcft.copy.betterstorage.misc.Constants;
import net.mcft.copy.betterstorage.misc.CreativeTabBetterStorage;
import net.mcft.copy.betterstorage.misc.Registry;
import net.mcft.copy.betterstorage.misc.handlers.CraftingHandler;
import net.mcft.copy.betterstorage.misc.handlers.GuiHandler;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
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


@Mod(modid="BetterStorage", version="@VERSION@", useMetadata=true)
@NetworkMod(clientSideRequired=true, serverSideRequired=false)
public class BetterStorage {
	
	public final static Random random = new Random();
	
	@Instance("BetterStorage")
	public static BetterStorage instance;
	
	@SidedProxy(clientSide="net.mcft.copy.betterstorage.client.ClientProxy",
	            serverSide="net.mcft.copy.betterstorage.CommonProxy")
	public static CommonProxy proxy;
	
	public static Logger log;
	
	
	// Blocks
	public static BlockCrate crate;
	public static BlockReinforcedChest reinforcedChest;
	public static BlockLocker locker;
	public static BlockArmorStand armorStand;
	
	// Items
	public static ItemKey key;
	public static ItemLock lock;
	public static ItemKeyring keyring;
	
	
	public static CreativeTabs creativeTab;
	
	
	@PreInit
	public void preInit(FMLPreInitializationEvent event) {
		
		log = event.getModLog();
		
		Config.load(event.getSuggestedConfigurationFile());
		Config.save();
		
	}
	
	@Init
	public void load(FMLInitializationEvent event) {
		
		creativeTab = new CreativeTabBetterStorage();
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
		locker = new BlockLocker(Config.lockerId);
		armorStand = new BlockArmorStand(Config.armorStandId);
		
		key = new ItemKey(Config.keyId);
		lock = new ItemLock(Config.lockId);
		keyring = new ItemKeyring(Config.keyringId);
		
		Registry.doYourThing();
		
	}
	
	private void addRecipes() {
		
		// Crate recipe
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(crate),
				"o/o",
				"/ /",
				"o/o", 'o', "plankWood",
				       '/', Item.stick));
		
		// Reinforced chest recipes
		for (ChestMaterial material : ChestMaterial.materials)
			GameRegistry.addRecipe(material.getRecipe(reinforcedChest));
		
		// Locker recipe
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
		
		// Armor stand recipe
		GameRegistry.addRecipe(new ItemStack(armorStand),
				" i ",
				"/i/",
				" s ", 's', new ItemStack(Block.stoneSingleSlab, 1, 0),
				       'i', Item.ingotIron,
				       '/', Item.stick);
		
		// Key recipe
		GameRegistry.addRecipe(KeyRecipe.createKeyRecipe(
				".o",
				".o",
				" o", 'o', Item.ingotGold,
				      '.', Item.goldNugget));
		// Key modify recipe
		GameRegistry.addRecipe(KeyRecipe.createKeyRecipe(
				"k", 'k', new ItemStack(key, 1, Constants.anyDamage)));
		// Lock recipe
		GameRegistry.addRecipe(LockRecipe.createLockRecipe());
		
	}
	
	private void addLocalizations() {
		
		LanguageRegistry lang = LanguageRegistry.instance();
		
		lang.addStringLocalization("itemGroup.betterstorage", "BetterStorage");
		
		lang.addStringLocalization("container.crate", "Storage Crate");
		lang.addStringLocalization("container.reinforcedChest", "Reinforced Chest");
		lang.addStringLocalization("container.reinforcedChestLarge", "Large Reinforced Chest");
		lang.addStringLocalization("container.locker", "Locker");
		lang.addStringLocalization("container.lockerLarge", "Large Locker");
		lang.addStringLocalization("container.keyring", "Keyring");
		
		lang.addStringLocalization("enchantment.key.unlocking", "Unlocking");
		lang.addStringLocalization("enchantment.key.lockpicking", "Lockpicking");
		lang.addStringLocalization("enchantment.key.morphing", "Morphing");
		
		lang.addStringLocalization("enchantment.lock.persistance", "Persistance");
		lang.addStringLocalization("enchantment.lock.security", "Security");
		lang.addStringLocalization("enchantment.lock.shock", "Shock");
		lang.addStringLocalization("enchantment.lock.trigger", "Trigger");
		
		for (ChestMaterial material : ChestMaterial.materials)
			lang.addStringLocalization("tile.reinforcedChest." + material.name + ".name", "Reinforced " + material.nameCapitalized + " Chest");
		
	}
	
}
