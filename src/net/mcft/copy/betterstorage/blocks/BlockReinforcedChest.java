package net.mcft.copy.betterstorage.blocks;

import java.util.concurrent.atomic.AtomicBoolean;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.mcft.copy.betterstorage.BetterStorage;
import net.mcft.copy.betterstorage.Constants;
import net.mcft.copy.betterstorage.client.ReinforcedChestRenderingHandler;
import net.mcft.copy.betterstorage.items.ItemLock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

public class BlockReinforcedChest extends BlockContainer {
	
	// Code from this class is mainly stolen from the BlockChest class.
	// A little prettified here and there, but basically the same.
	
	private final static byte[] rotationMetadataLookup = { 2, 5, 3, 4 };
	
	public String material;
	// Name is just material capitalized.
	public String name;
	
	public BlockReinforcedChest(int id, String material) {
		super(id, Material.wood);
		BetterStorage.chestsByMaterial.put(material, this);
		
		this.material = material;
		name = material.substring(0, 1).toUpperCase() + material.substring(1);
		setHardness(8.0f);
		setResistance(20.0f);
		setStepSound(Block.soundWoodFootstep);
		setRequiresSelfNotify();
		setBlockBounds(1.0f / 16, 0, 1.0f / 16, 15.0f / 16, 14.0f / 16, 15.0f / 16);
		
		GameRegistry.registerBlock(this);
		MinecraftForge.setBlockHarvestLevel(this, "axe", 2);
		
		setBlockName("reinforced" + name + "Chest");
		LanguageRegistry.addName(this, "Reinforced " + name + " Chest");
		
		setCreativeTab(CreativeTabs.tabDecorations);
	}
	
	@Override
	public float getBlockHardness(World world, int x, int y, int z) {
		TileEntityReinforcedChest chest = TileEntityReinforcedChest.getChestAt(world, x, y, z);
		return blockHardness * ((chest == null || !chest.isLocked()) ? 1.0F : 12.0F);
	}
	
	@Override
	public boolean isOpaqueCube() { return false; }
	@Override
	public boolean renderAsNormalBlock() { return false; }
	
	@Override
	@SideOnly(Side.CLIENT)
	public int getRenderType() {
		return ReinforcedChestRenderingHandler.renderId;
	}
	
	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
		if (world.getBlockId(x, y, z - 1) == blockID)
			setBlockBounds(1.0f / 16, 0, 0, 15.0f / 16, 14.0f / 16, 15.0f / 16);
		else if (world.getBlockId(x, y, z + 1) == blockID)
			setBlockBounds(1.0f / 16, 0, 1.0f / 16, 15.0f / 16, 14.0f / 16, 1);
		else if (world.getBlockId(x - 1, y, z) == blockID)
			setBlockBounds(0, 0, 1.0f / 16, 15.0f / 16, 14.0f / 16, 15.0f / 16);
		else if (world.getBlockId(x + 1, y, z) == blockID)
			setBlockBounds(1.0f / 16, 0, 1.0f / 16, 1, 14.0f / 16, 15.0f / 16);
		else setBlockBounds(1.0f / 16, 0, 1.0f / 16, 15.0f / 16, 14.0f / 16, 15.0f / 16);
	}
	
	@Override
	public void onBlockAdded(World world, int x, int y, int z) {
		super.onBlockAdded(world, x, y, z);
		unifyAdjacentChests(world, x, y, z);
		if (world.getBlockId(x, y, z - 1) == blockID)
			unifyAdjacentChests(world, x, y, z - 1);
		if (world.getBlockId(x, y, z + 1) == blockID)
			unifyAdjacentChests(world, x, y, z + 1);
		if (world.getBlockId(x - 1, y, z) == blockID)
			unifyAdjacentChests(world, x - 1, y, z);
		if (world.getBlockId(x + 1, y, z) == blockID)
			unifyAdjacentChests(world, x + 1, y, z);
	}
	
	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLiving entity) {
		int idNegZ = world.getBlockId(x, y, z - 1);
		int idPosZ = world.getBlockId(x, y, z + 1);
		int idNegX = world.getBlockId(x - 1, y, z);
		int idPosX = world.getBlockId(x + 1, y, z);
		int rotation = MathHelper.floor_double((entity.rotationYaw * 4.0D / 360.0D) + 0.5D) & 3;
		byte metadata = rotationMetadataLookup[rotation];
		if (idNegZ == blockID || idPosZ == blockID || idNegX == blockID || idPosX == blockID) {
			if ((idNegZ == blockID || idPosZ == blockID) &&
			    (metadata == 4 || metadata == 5)) {
				if (idNegZ == blockID) world.setBlockMetadataWithNotify(x, y, z - 1, metadata);
				else world.setBlockMetadataWithNotify(x, y, z + 1, metadata);
				world.setBlockMetadataWithNotify(x, y, z, metadata);
			}
			if ((idNegX == blockID || idPosX == blockID) &&
			    (metadata == 2 || metadata == 3)) {
				if (idNegX == blockID) world.setBlockMetadataWithNotify(x - 1, y, z, metadata);
				else world.setBlockMetadataWithNotify(x + 1, y, z, metadata);
				world.setBlockMetadataWithNotify(x, y, z, metadata);
			}
		} else world.setBlockMetadataWithNotify(x, y, z, metadata);
	}
	
	public void unifyAdjacentChests(World world, int x, int y, int z) {
		if (world.isRemote) return;
		int idNegZ = world.getBlockId(x, y, z - 1);
		int idPosZ = world.getBlockId(x, y, z + 1);
		int idNegX = world.getBlockId(x - 1, y, z);
		int idPosX = world.getBlockId(x + 1, y, z);
		int idCorner1, idCorner2;
		int metadata, otherMetadata;
		if (idNegZ == blockID || idPosZ == blockID) {
			idCorner1 = world.getBlockId(x - 1, y, z + ((idNegZ == blockID) ? -1 : 1));
			idCorner2 = world.getBlockId(x + 1, y, z + ((idNegZ == blockID) ? -1 : 1));
			metadata = 5;
			otherMetadata = world.getBlockMetadata(x, y, z + ((idNegZ == blockID) ? -1 : 1));
			if (otherMetadata == 4) metadata = 4;
			if ((Block.opaqueCubeLookup[idNegX] ||  Block.opaqueCubeLookup[idCorner1]) &&
			    !Block.opaqueCubeLookup[idPosX] && !Block.opaqueCubeLookup[idCorner2]) metadata = 5;
			if ((Block.opaqueCubeLookup[idPosX] ||  Block.opaqueCubeLookup[idCorner2]) &&
			    !Block.opaqueCubeLookup[idNegX] && !Block.opaqueCubeLookup[idCorner1]) metadata = 4;
		} else if (idNegX == blockID || idPosX == blockID) {
			idCorner1 = world.getBlockId(x + ((idNegX == blockID) ? -1 : 1), y, z - 1);
			idCorner2 = world.getBlockId(x + ((idNegX == blockID) ? -1 : 1), y, z + 1);
			metadata = 3;
			otherMetadata = world.getBlockMetadata(x + ((idNegX == blockID) ? -1 : 1), y, z);
			if (otherMetadata == 2) metadata = 2;
			if ((Block.opaqueCubeLookup[idNegZ] ||  Block.opaqueCubeLookup[idCorner1]) &&
			    !Block.opaqueCubeLookup[idPosZ] && !Block.opaqueCubeLookup[idCorner2]) metadata = 3;
			if ((Block.opaqueCubeLookup[idPosZ] ||  Block.opaqueCubeLookup[idCorner2]) &&
			    !Block.opaqueCubeLookup[idNegZ] && !Block.opaqueCubeLookup[idCorner1]) metadata = 2;
		} else {
			metadata = 3;
			if (Block.opaqueCubeLookup[idNegZ] && !Block.opaqueCubeLookup[idPosZ]) metadata = 3;
			if (Block.opaqueCubeLookup[idPosZ] && !Block.opaqueCubeLookup[idNegZ]) metadata = 2;
			if (Block.opaqueCubeLookup[idNegX] && !Block.opaqueCubeLookup[idPosX]) metadata = 5;
			if (Block.opaqueCubeLookup[idPosX] && !Block.opaqueCubeLookup[idNegX]) metadata = 4;
		}
		world.setBlockMetadataWithNotify(x, y, z, metadata);
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
	public void onNeighborBlockChange(World world, int x, int y, int z, int id) {
		super.onNeighborBlockChange(world, x, y, z, id);
		TileEntityReinforcedChest chest = TileEntityReinforcedChest.getChestAt(world, x, y, z);
		if (chest != null) chest.updateContainingBlockInfo();
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
		
		// Use item when it is a lock and chest is not locked.
		ItemStack holding = player.getHeldItem();
		if (holding != null && (holding.getItem() instanceof ItemLock) && !chest.isLocked()) return false;
		
		// Only continue if the chest can be opened.
		if (!chest.canOpenChest(player)) return true;
		
		int guiID = Constants.chestGuiIdSmall + (chest.getColumns() - 9);
		if (world.getBlockId(x, y, z - 1) == blockID || world.getBlockId(x, y, z + 1) == blockID ||
		    world.getBlockId(x - 1, y, z) == blockID || world.getBlockId(x + 1, y, z) == blockID)
			guiID = Constants.chestGuiIdLarge + (chest.getColumns() - 9);
		player.openGui(BetterStorage.instance, guiID, world, x, y, z);
		return true;
	}
	
	@Override
	public TileEntity createNewTileEntity(World world) {
		return new TileEntityReinforcedChest(this);
	}
	
}
