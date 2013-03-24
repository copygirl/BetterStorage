package net.mcft.copy.betterstorage.block;

import java.util.List;
import java.util.Random;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.mcft.copy.betterstorage.BetterStorage;
import net.mcft.copy.betterstorage.client.ClientProxy;
import net.mcft.copy.betterstorage.enchantments.EnchantmentBetterStorage;
import net.mcft.copy.betterstorage.item.ItemLock;
import net.mcft.copy.betterstorage.misc.Constants;
import net.mcft.copy.betterstorage.utils.DirectionUtils;
import net.mcft.copy.betterstorage.utils.WorldUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
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
		setBlockBounds(0.0625F, 0.0F, 0.0625F, 0.9375F, 0.875F, 0.9375F);
		
		MinecraftForge.setBlockHarvestLevel(this, "axe", 2);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister iconRegister) {
		blockIcon = iconRegister.registerIcon("tree_side");
	}
	
	@Override
	public boolean isOpaqueCube() { return false; }
	@Override
	public boolean renderAsNormalBlock() { return false; }
	
	@Override
	@SideOnly(Side.CLIENT)
	public int getRenderType() { return ClientProxy.reinforcedChestRenderId; }
	
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
			int persistance = EnchantmentHelper.getEnchantmentLevel(EnchantmentBetterStorage.persistance.effectId, chest.getLock());
			if (persistance > 0) hardness *= persistance + 2;
		}
		return hardness;
	}
	
	@Override
	public float getExplosionResistance(Entity entity, World world, int x, int y, int z, double explosionX, double explosionY, double explosionZ) {
		float modifier = 1.0F;
		TileEntityReinforcedChest chest = WorldUtils.getChest(world, x, y, z);
		if (chest != null) {
			int persistance = EnchantmentHelper.getEnchantmentLevel(EnchantmentBetterStorage.persistance.effectId, chest.getLock());
			if (persistance > 0) modifier += Math.pow(2, persistance);
		}
		return super.getExplosionResistance(entity) * modifier;
	}
	
	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
		TileEntityReinforcedChest chest = WorldUtils.getChest(world, x, y, z);
		if (chest.isConnected()) {
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
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLiving player, ItemStack stack) {
		TileEntityReinforcedChest chest = WorldUtils.getChest(world, x, y, z);
		chest.orientation = DirectionUtils.getOrientation(player).getOpposite();
		chest.checkForConnections();
	}
	
	@Override
	public void breakBlock(World world, int x, int y, int z, int id, int meta) {
		TileEntityReinforcedChest chest = WorldUtils.getChest(world, x, y, z);
		super.breakBlock(world, x, y, z, id, meta);
		if (chest != null) {
			chest.dropContents();
			chest.dropLock();
			chest.disconnect();
		}
	}
	
	@Override
	public TileEntity createNewTileEntity(World world) {
		return new TileEntityReinforcedChest();
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
		
		int guiID = (chest.isConnected() ? Constants.chestLargeGuiId : Constants.chestSmallGuiId);
		guiID += chest.getNumColumns() - 9;
		player.openGui(BetterStorage.instance, guiID, world, x, y, z);
		return true;
	}
	
	public void applyTrigger(World world, int x, int y, int z, ItemStack lock) {
		if (!ItemLock.isLock(lock)) return;
		int trigger = EnchantmentHelper.getEnchantmentLevel(EnchantmentBetterStorage.trigger.effectId, lock);
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
	public int isProvidingWeakPower(IBlockAccess world, int x, int y, int z, int side) {
		return (WorldUtils.getChest(world, x, y, z).isPowered() ? 15 : 0);
	}
	
	@Override
	public int isProvidingStrongPower(IBlockAccess world, int x, int y, int z, int side) {
		return isProvidingWeakPower(world, x, y, z, side);
	}
	
	@Override
	public void updateTick(World world, int x, int y, int z, Random random) {
		WorldUtils.getChest(world, x, y, z).setPowered(false);
	}
	
}
