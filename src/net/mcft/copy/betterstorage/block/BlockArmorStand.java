package net.mcft.copy.betterstorage.block;

import java.util.Random;

import net.mcft.copy.betterstorage.block.tileentity.TileEntityArmorStand;
import net.mcft.copy.betterstorage.item.block.ItemArmorStand;
import net.mcft.copy.betterstorage.misc.Constants;
import net.mcft.copy.betterstorage.misc.SetBlockFlag;
import net.mcft.copy.betterstorage.proxy.ClientProxy;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockArmorStand extends BlockContainerBetterStorage {
	
	public BlockArmorStand(int id) {
		super(id, Material.rock);
		
		setHardness(2.5f);
		setBlockBounds(2 / 16.0F, 0, 2 / 16.0F, 14 / 16.0F, 2, 14 / 16.0F);
		
		MinecraftForge.setBlockHarvestLevel(this, "pickaxe", 0);
	}
	
	@Override
	public Class<? extends Item> getItemClass() { return ItemArmorStand.class; }
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister iconRegister) {
		blockIcon = iconRegister.registerIcon("stone_slab_top");
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public String getItemIconName() { return Constants.modId + ":armorstand"; }
	
	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
		int metadata = world.getBlockMetadata(x, y, z);
		if (metadata == 0) setBlockBounds(2 / 16.0F, 0, 2 / 16.0F, 14 / 16.0F, 2, 14 / 16.0F);
		else setBlockBounds(2 / 16.0F, -1, 2 / 16.0F, 14 / 16.0F, 1, 14 / 16.0F);
	}
	
	@Override
	public boolean isOpaqueCube() { return false; }
	@Override
	public boolean renderAsNormalBlock() { return false; }
	
	@Override
	@SideOnly(Side.CLIENT)
	public int getRenderType() { return ClientProxy.armorStandRenderId; }
	
	@Override
	public int quantityDropped(int meta, int fortune, Random random) {
		return ((meta == 0) ? 1 : 0);
	}
	
	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase player, ItemStack stack) {
		super.onBlockPlacedBy(world, x, y, z, player, stack);
		// Set block above to armor stand with metadata 1.
		world.setBlock(x, y + 1, z, blockID, 1, SetBlockFlag.DEFAULT);
	}
	
	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player,
	                                int side, float hitX, float hitY, float hitZ) {
		if (world.isRemote) return true;
		if (world.getBlockMetadata(x, y, z) > 0) { y -= 1; hitY += 1; }
		return super.onBlockActivated(world, x, y, z, player, side, hitX, hitY, hitZ);
	}
	@Override
	public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z) {
		if (world.getBlockMetadata(x, y, z) > 0) { y -= 1; }
		return super.getPickBlock(target, world, x, y, z);
	}
	
	@Override
	public boolean removeBlockByPlayer(World world, EntityPlayer player, int x, int y, int z) {
		return world.setBlockToAir(x, y, z);
	}
	@Override
	public void breakBlock(World world, int x, int y, int z, int id, int meta) {
		if (meta > 0) return;
		super.breakBlock(world, x, y, z, id, meta);
	}
	
	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, int id) {
		int metadata = world.getBlockMetadata(x, y, z);
		int targetY = y + ((metadata == 0) ? 1 : -1);
		int targetMeta = ((metadata == 0) ? 1 : 0);
		if (world.getBlockId(x, targetY, z) == blockID &&
		    world.getBlockMetadata(x, targetY, z) == targetMeta) return;
		world.setBlock(x, y, z, 0);
		if (metadata == 0)
			dropBlockAsItem(world, x, y, z, metadata, 0);
	}
	
	@Override
	public TileEntity createNewTileEntity(World world) {
		return new TileEntityArmorStand();
	}
	@Override
	public TileEntity createTileEntity(World world, int metadata) {
		return ((metadata == 0) ? createNewTileEntity(world) : null);
	}
	
}
