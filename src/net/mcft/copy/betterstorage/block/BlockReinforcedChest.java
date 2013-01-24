package net.mcft.copy.betterstorage.block;

import java.util.List;
import java.util.Random;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.mcft.copy.betterstorage.BetterStorage;
import net.mcft.copy.betterstorage.Constants;
import net.mcft.copy.betterstorage.client.ReinforcedChestRenderingHandler;
import net.mcft.copy.betterstorage.enchantments.EnchantmentBetterStorage;
import net.mcft.copy.betterstorage.item.ItemLock;
import net.mcft.copy.betterstorage.utils.DirectionUtils;
import net.mcft.copy.betterstorage.utils.WorldUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.common.MinecraftForge;

public class BlockReinforcedChest extends BlockContainer {
	
	public BlockReinforcedChest(int id) {
		super(id, Material.wood);
		
		setHardness(8.0f);
		setResistance(20.0f);
		setStepSound(Block.soundWoodFootstep);
		setRequiresSelfNotify();
		setBlockBounds(0.0625F, 0.0F, 0.0625F, 0.9375F, 0.875F, 0.9375F);

		setCreativeTab(CreativeTabs.tabDecorations);
		GameRegistry.registerBlock(this, "reinforcedChest");
		MinecraftForge.setBlockHarvestLevel(this, "axe", 2);
	}
	
	@Override
	public boolean isOpaqueCube() { return false; }
	@Override
	public boolean renderAsNormalBlock() { return false; }
	
	@Override
	public int getBlockTextureFromSide(int side) { return 20; }
	
	@SideOnly(Side.CLIENT)
	@Override
	public int getRenderType() {
		return ReinforcedChestRenderingHandler.renderId;
	}
	
	@Override
	public int damageDropped(int metadata) { return metadata; }
	
	@Override
	public void getSubBlocks(int id, CreativeTabs tab, List list) {
		for (ChestMaterial material : ChestMaterial.materials)
			list.add(new ItemStack(id, 1, material.id));
	}
	
	@Override
	public float getBlockHardness(World world, int x, int y, int z) {
		TileEntityReinforcedChest chest = WorldUtils.getChest(world, x, y, z);
		float hardness = blockHardness;
		if (chest != null && chest.isLocked()) {
			hardness *= 15.0F;
			int persistance = EnchantmentHelper.getLevel(EnchantmentBetterStorage.persistance.effectId, chest.getLock());
			if (persistance > 0) hardness *= persistance + 2;
		}
		return hardness;
	}
	
	@Override
	public float getExplosionResistance(Entity entity, World world, int x, int y, int z, double explosionX, double explosionY, double explosionZ) {
		float modifier = 1.0F;
		TileEntityReinforcedChest chest = WorldUtils.getChest(world, x, y, z);
		if (chest != null) {
			int persistance = EnchantmentHelper.getLevel(EnchantmentBetterStorage.persistance.effectId, chest.getLock());
			if (persistance > 0) modifier += Math.pow(2, persistance);
		}
		return super.getExplosionResistance(entity) * modifier;
	}
	
	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
		TileEntityReinforcedChest chest = WorldUtils.getChest(world, x, y, z);
		if (chest.isLarge()) {
			if (chest.connected == ForgeDirection.NORTH)
				setBlockBounds(0.0625F, 0.0F, 0.0F, 0.9375F, 0.875F, 0.9375F);
			else if (chest.connected == ForgeDirection.SOUTH)
				setBlockBounds(0.0625F, 0.0F, 0.0625F, 0.9375F, 0.875F, 1.0F);
			else if (chest.connected == ForgeDirection.WEST)
				setBlockBounds(0.0F, 0.0F, 0.0625F, 0.9375F, 0.875F, 0.9375F);
			else if (chest.connected == ForgeDirection.EAST)
				setBlockBounds(0.0625F, 0.0F, 0.0625F, 1.0F, 0.875F, 0.9375F);
		} else setBlockBounds(0.0625F, 0.0F, 0.0625F, 0.9375F, 0.875F, 0.9375F);
	}
	
	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLiving player) {
		TileEntityReinforcedChest chest = WorldUtils.getChest(world, x, y, z);
		chest.orientation = DirectionUtils.getOrientation(player).getOpposite();
		chest.checkForAdjacentChests();
	}
	
	/*
	@Override
	public void onBlockAdded(World world, int x, int y, int z) {
		TileEntityReinforcedChest chest = (TileEntityReinforcedChest)createTileEntity(world, world.getBlockMetadata(x, y, z));
		chest.material = ItemReinforcedChest.newChestMaterial;
		world.setBlockTileEntity(x, y, z, chest);
		unifyAdjacentChests(world, x, y, z);
		unifyChest(world, x, y, z - 1, chest);
		unifyChest(world, x, y, z + 1, chest);
		unifyChest(world, x - 1, y, z, chest);
		unifyChest(world, x + 1, y, z, chest);
	}
	private void unifyChest(World world, int x, int y, int z, TileEntityReinforcedChest chest) {
		if (chest.canConnectTo(WorldUtils.getChest(world, x, y, z)))
			unifyAdjacentChests(world, x, y, z);
	}
	
	private static byte[] metadataLookup = { 2, 5, 3, 4 };
	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLiving player) {
		TileEntityReinforcedChest chest = WorldUtils.getChest(world, x, y, z);
		boolean canConnectToNegZ = chest.canConnectTo(WorldUtils.getChest(world, x, y, z - 1));
		boolean canConnectToPosZ = chest.canConnectTo(WorldUtils.getChest(world, x, y, z + 1));
		boolean canConnectToNegX = chest.canConnectTo(WorldUtils.getChest(world, x - 1, y, z));
		boolean canConnectToPosX = chest.canConnectTo(WorldUtils.getChest(world, x + 1, y, z));
		int dir = MathHelper.floor_double(player.rotationYaw * 4.0 / 360.0 + 0.5) & 3;s
		byte metadata = metadataLookup[dir];
		if (canConnectToNegZ || canConnectToPosZ || canConnectToNegX || canConnectToPosX) {
			if ((canConnectToNegZ || canConnectToPosZ) && (metadata == 4 || metadata == 5))
				world.setBlockMetadataWithNotify(x, y, z + ((canConnectToNegZ) ? -1 : 1), metadata);
			else if ((canConnectToNegX || canConnectToPosX) && (metadata == 2 || metadata == 3))
				world.setBlockMetadataWithNotify(x + ((canConnectToNegX) ? -1 : 1), y, z, metadata);
			else return;
		}
		world.setBlockMetadataWithNotify(x, y, z, metadata);
	}
	
	@Override
	public void unifyAdjacentChests(World world, int x, int y, int z) {
		if (world.isRemote) return;
		
		TileEntityReinforcedChest chest = WorldUtils.getChest(world, x, y, z);
		boolean canConnectToNegZ = chest.canConnectTo(WorldUtils.getChest(world, x, y, z - 1));
		boolean canConnectToPosZ = chest.canConnectTo(WorldUtils.getChest(world, x, y, z + 1));
		boolean canConnectToNegX = chest.canConnectTo(WorldUtils.getChest(world, x - 1, y, z));
		boolean canConnectToPosX = chest.canConnectTo(WorldUtils.getChest(world, x + 1, y, z));
		boolean blockOpaqueNegZ = isOpaque(world, x, y, z - 1);
		boolean blockOpaquePosZ = isOpaque(world, x, y, z + 1);
		boolean blockOpaqueNegX = isOpaque(world, x - 1, y, z);
		boolean blockOpaquePosX = isOpaque(world, x + 1, y, z);
		byte metadata;
		
		if (canConnectToNegZ || canConnectToPosZ) {
			
			int zz = z + (canConnectToNegZ ? -1 : 1);
			boolean blockOpaqueCorner1 = isOpaque(world, x - 1, y, zz);
			boolean blockOpaqueCorner2 = isOpaque(world, x + 1, y, zz);
			metadata = 5;
			if (world.getBlockMetadata(x, y, zz) == 4) metadata = 4;
			if ((blockOpaqueNegX ||  blockOpaqueCorner1) &&
			    !blockOpaquePosX && !blockOpaqueCorner2) metadata = 5;
			if ((blockOpaquePosX ||  blockOpaqueCorner2) &&
			    !blockOpaqueNegX && !blockOpaqueCorner1) metadata = 4;
			
		} else if (canConnectToNegX || canConnectToPosX) {
			
			int xx = x + (canConnectToNegX ? -1 : 1);
			boolean blockOpaqueCorner1 = isOpaque(world, xx, y, z - 1);
			boolean blockOpaqueCorner2 = isOpaque(world, xx, y, z + 1);
			metadata = 3;
			if (world.getBlockMetadata(xx, y, z) == 2) metadata = 2;
			if ((blockOpaqueNegZ ||  blockOpaqueCorner1) &&
			    !blockOpaquePosZ && !blockOpaqueCorner2) metadata = 3;
			if ((blockOpaquePosZ ||  blockOpaqueCorner2) &&
			    !blockOpaqueNegZ && !blockOpaqueCorner1) metadata = 2;
			
		} else {
			
			metadata = 3;
			// if (blockOpaqueNegZ && !blockOpaquePosZ) metadata = 3;
			if (blockOpaquePosZ && !blockOpaqueNegZ) metadata = 2;
			if (blockOpaqueNegX && !blockOpaquePosX) metadata = 5;
			if (blockOpaquePosX && !blockOpaqueNegX) metadata = 4;
			
		}
		world.setBlockMetadataWithNotify(x, y, z, metadata);
	}
	private boolean isOpaque(World world, int x, int y, int z) {
		return Block.opaqueCubeLookup[world.getBlockId(x, y, z)];
	}
	
	/** Returns if a chest of that material can be placed at that position. /
	public boolean canPlaceChestAt(World world, int x, int y, int z, int material) {
		AtomicBoolean chestFound = new AtomicBoolean(false);
		if (!checkChestPosition(world, x + 1, y, z, material, chestFound)) return false;
		if (!checkChestPosition(world, x - 1, y, z, material, chestFound)) return false;
		if (!checkChestPosition(world, x, y, z + 1, material, chestFound)) return false;
		if (!checkChestPosition(world, x, y, z - 1, material, chestFound)) return false;
		return true;
	}
	/** Returns if a chest can be placed next to another chest, if there is one. /
	private boolean checkChestPosition(World world, int x, int y, int z, int material, AtomicBoolean chestFound) {
		TileEntityReinforcedChest chest = WorldUtils.getChest(world, x, y, z);
		if (!canConnectChests(material, chest)) return true;
		// If there is a chest and
		// - there already was another chest or
		// - it is locked or
		// - it has a neighbor chest
		// return that the chest can't be placed.
		if (chestFound.get() || chest.isLocked() ||
		    isThereANeighborChest(world, x, y, z, material))
			return false;
		chestFound.set(true);
		return true;
	}
	
	private boolean isThereANeighborChest(World world, int x, int y, int z, int material) {
		return (canConnectChests(material, world, x, y, z - 1) ||
				canConnectChests(material, world, x, y, z + 1) ||
				canConnectChests(material, world, x - 1, y, z) ||
				canConnectChests(material, world, x + 1, y, z));
	}
	*/
	
	@Override
	public void breakBlock(World world, int x, int y, int z, int id, int meta) {
		TileEntityReinforcedChest chest = WorldUtils.getChest(world, x, y, z);
		super.breakBlock(world, x, y, z, id, meta);
		if (chest != null) {
			chest.dropContents();
			chest.dropLock();
			chest.disconnectChests();
		}
	}
	
	@Override
	public TileEntity createNewTileEntity(World world) {
		return new TileEntityReinforcedChest(this);
	}
	
	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int par6, float par7, float par8, float par9) {
		if (world.isRemote) return true;
		
		TileEntityReinforcedChest chest = WorldUtils.getChest(world, x, y, z);
		ItemStack holding = player.getHeldItem();
		
		// Use item if it's a lock and the chest is not locked.
		if (ItemLock.isLock(holding) && !chest.isLocked())
			return false;
		
		// Only continue if the chest can be opened.
		ItemStack lock = chest.getLock();
		if (!chest.canUse(player) &&
		    !ItemLock.lockTryOpen(lock, player, holding)) {
			applyTrigger(world, x, y, z, lock);
			return true;
		}
		
		int guiID = (chest.isLarge() ? Constants.chestGuiIdLarge : Constants.chestGuiIdSmall);
		guiID += chest.getNumColumns() - 9;
		player.openGui(BetterStorage.instance, guiID, world, x, y, z);
		return true;
	}
	
	public void applyTrigger(World world, int x, int y, int z, ItemStack lock) {
		if (!ItemLock.isLock(lock)) return;
		int trigger = EnchantmentHelper.getLevel(EnchantmentBetterStorage.trigger.effectId, lock);
		if (trigger > 0) WorldUtils.getChest(world, x, y, z).setPowered(true);
	}
	
	@Override
	public void onBlockClicked(World world, int x, int y, int z, EntityPlayer player) {
		if (world.isRemote) return;
		TileEntityReinforcedChest chest = WorldUtils.getChest(world, x, y, z);
		ItemStack lock = chest.getLock();
		ItemLock.lockApplyEffects(lock, player, 2);
		applyTrigger(world, x, y, z, lock);
	}
	
	// Trigger enchantment related
	
	@Override
	public boolean canProvidePower() { return true; }
	@Override
	public boolean isProvidingWeakPower(IBlockAccess world, int x, int y, int z, int side) {
		return WorldUtils.getChest(world, x, y, z).isPowered();
	}
	@Override
	public boolean isProvidingStrongPower(IBlockAccess world, int x, int y, int z, int side) {
		return isProvidingWeakPower(world, x, y, z, side);
	}
	@Override
	public void updateTick(World world, int x, int y, int z, Random random) {
		WorldUtils.getChest(world, x, y, z).setPowered(false);
	}
	
}
