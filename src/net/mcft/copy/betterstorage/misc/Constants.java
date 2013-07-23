package net.mcft.copy.betterstorage.misc;

import net.mcft.copy.betterstorage.block.crate.TileEntityCrate;
import net.mcft.copy.betterstorage.block.tileentity.TileEntityArmorStand;
import net.mcft.copy.betterstorage.block.tileentity.TileEntityBackpack;
import net.mcft.copy.betterstorage.block.tileentity.TileEntityCardboardBox;
import net.mcft.copy.betterstorage.block.tileentity.TileEntityLocker;
import net.mcft.copy.betterstorage.block.tileentity.TileEntityReinforcedChest;
import cpw.mods.fml.common.registry.GameRegistry;

public class Constants {
	
	public static final String modId = "BetterStorage";
	public static final String modName = "betterstorage";
	
	public static final String commonProxy = "net.mcft.copy.betterstorage.proxy.CommonProxy";
	public static final String clientProxy = "net.mcft.copy.betterstorage.proxy.ClientProxy";
	
	public static final int anyDamage = Short.MAX_VALUE;
	
	public static final String containerCrate = "container." + modName + ".crate";
	public static final String containerReinforcedChest = "container." + modName + ".reinforcedChest";
	public static final String containerLocker = "container." + modName + ".locker";
	public static final String containerArmorStand = "container." + modName + ".armorStand";
	public static final String containerBackpack = "container." + modName + ".backpack";
	public static final String containerEnderBackpack = "container." + modName + ".enderBackpack";
	public static final String containerCardboardBox = "container." + modName + ".cardboardBox";
	
	public static final String containerKeyring = "container." + modName + ".keyring";

	public static final String containerThaumcraftBackpack = "container." + modName + ".thaumcraftBackpack";
	public static final String containerThaumiumChest = "container." + modName + ".thaumiumChest";
	
}
