package net.mcft.copy.betterstorage.blocks;

import java.util.concurrent.atomic.AtomicBoolean;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import net.mcft.copy.betterstorage.BetterStorage;
import net.mcft.copy.betterstorage.Constants;
import net.mcft.copy.betterstorage.client.ReinforcedChestRenderingHandler;
import net.mcft.copy.betterstorage.items.ItemLock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

public class BlockReinforcedChest extends BlockChest {
	
	// Code from this class is mainly stolen from the BlockChest class.
	// A little prettified here and there, but basically the same.
	
	private final static byte[] rotationMetadataLookup = { 2, 5, 3, 4 };
	
	public String material;
	// Name is just material capitalized.
	public String name;
	
	public BlockReinforcedChest(int id, String material) {
		super(id);
		BetterStorage.chestsByMaterial.put(material, this);
		
		this.material = material;
		name = material.substring(0, 1).toUpperCase() + material.substring(1);
		setHardness(8.0f);
		setResistance(20.0f);
		setStepSound(Block.soundWoodFootstep);
		setRequiresSelfNotify();
		
		GameRegistry.registerBlock(this, "reinforced" + name + "Chest");
		MinecraftForge.setBlockHarvestLevel(this, "axe", 2);
		
		setBlockName("reinforced" + name + "Chest");
		LanguageRegistry.addName(this, "Reinforced " + name + " Chest");
	}
	
	@Override
	public float getBlockHardness(World world, int x, int y, int z) {
		TileEntityReinforcedChest chest = TileEntityReinforcedChest.getChestAt(world, x, y, z);
		return blockHardness * ((chest == null || !chest.isLocked()) ? 1.0F : 12.0F);
	}
	
	@Override
	public int getRenderType() {
		return ReinforcedChestRenderingHandler.renderId;
	}
	
	@Override
	public int getBlockTexture(IBlockAccess blocks, int x, int y, int z, int meta) { return 20; }
	@Override
	public int getBlockTextureFromSide(int side) { return 20; }
	
	@Override
	public boolean canPlaceBlockAt(World world, int x, int y, int z) {
		AtomicBoolean chestFound = new AtomicBoolean(false);
		if (!checkChestPosition(world, x + 1, y, z, chestFound)) return false;
		if (!checkChestPosition(world, x - 1, y, z, chestFound)) return false;
		if (!checkChestPosition(world, x, y, z + 1, chestFound)) return false;
		if (!checkChestPosition(world, x, y, z - 1, chestFound)) return false;
		return true;
	}
	/** Checks to see if a chest can be placed next to another chest, if there is one. */
	private boolean checkChestPosition(World world, int x, int y, int z, AtomicBoolean chestFound) {
		// If there is no chest, we're fine.
		if (world.getBlockId(x, y, z) != blockID) return true;
		// If there is a chest and
		// - there already was another chest,
		// - it has a neighbor chest or
		// - it is locked
		// return that the chest can't be placed.
		if (chestFound.get() || isThereANeighborChest(world, x, y, z) ||
		    TileEntityReinforcedChest.getChestAt(world, x, y, z).isLocked()) return false;
		chestFound.set(true);
		return true;
	}
	private boolean isThereANeighborChest(World world, int x, int y, int z) {
		return (world.getBlockId(x, y, z - 1) == blockID || world.getBlockId(x, y, z + 1) == blockID ||
		        world.getBlockId(x - 1, y, z) == blockID || world.getBlockId(x + 1, y, z) == blockID);
	}
	
	@Override
	public void breakBlock(World world, int x, int y, int z, int id, int meta) {
		TileEntityReinforcedChest chest = TileEntityReinforcedChest.getChestAt(world, x, y, z);
		if (chest != null) {
			chest.dropContents();
			chest.dropLock();
		}
		super.breakBlock(world, x, y, z, id, meta);
	}
	
	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int par6, float par7, float par8, float par9) {
		if (world.isRemote) return true;
		
		TileEntityReinforcedChest chest = TileEntityReinforcedChest.getChestAt(world, x, y, z);
		ItemStack holding = player.getHeldItem();
		
		// Use item if it's a lock and the chest is not locked.
		if (ItemLock.isLock(holding) && !chest.isLocked())
			return false;
		
		// Only continue if the chest can be opened.
		if (!chest.canUse(player) &&
		    !ItemLock.lockTryOpen(chest.getLock(), player, holding))
			return true;
		
		int guiID = Constants.chestGuiIdSmall + (chest.getNumColumns() - 9);
		if (world.getBlockId(x, y, z - 1) == blockID || world.getBlockId(x, y, z + 1) == blockID ||
		    world.getBlockId(x - 1, y, z) == blockID || world.getBlockId(x + 1, y, z) == blockID)
			guiID = Constants.chestGuiIdLarge + (chest.getNumColumns() - 9);
		player.openGui(BetterStorage.instance, guiID, world, x, y, z);
		return true;
	}
	
	@Override
	public TileEntity createNewTileEntity(World world) {
		return new TileEntityReinforcedChest(this);
	}
	
}
