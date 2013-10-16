package net.mcft.copy.betterstorage.block;

import java.util.Random;

import net.mcft.copy.betterstorage.block.tileentity.TileEntityBackpack;
import net.mcft.copy.betterstorage.item.block.ItemBackpack;
import net.mcft.copy.betterstorage.proxy.ClientProxy;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockBackpack extends BlockContainerBetterStorage {
	
	public BlockBackpack(int id) {
		super(id, Material.cloth);
		
		setHardness(0.7f);
		setStepSound(soundClothFootstep);
		setBlockBounds(3 / 16.0F, 0.0F, 3 / 16.0F, 13 / 16.0F, 13 / 16.0F, 13 / 16.0F);
	}
	
	@Override
	public Class<? extends Item> getItemClass() { return ItemBackpack.class; }
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister iconRegister) {
		blockIcon = iconRegister.registerIcon("wool_colored_brown");
	}
	
	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
		ForgeDirection orientation = ForgeDirection.getOrientation(world.getBlockMetadata(x, y, z));
		if ((orientation == ForgeDirection.NORTH) || (orientation == ForgeDirection.SOUTH))
			setBlockBounds(2 / 16.0F, 0.0F, 3 / 16.0F, 14 / 16.0F, 13 / 16.0F, 13 / 16.0F);
		else if ((orientation == ForgeDirection.WEST) || (orientation == ForgeDirection.EAST))
			setBlockBounds(3 / 16.0F, 0.0F, 2 / 16.0F, 13 / 16.0F, 13 / 16.0F, 14 / 16.0F);
		else setBlockBounds(3 / 16.0F, 0.0F, 3 / 16.0F, 13 / 16.0F, 13 / 16.0F, 13 / 16.0F);
	}
	
	@Override
	public boolean isOpaqueCube() { return false; }
	@Override
	public boolean renderAsNormalBlock() { return false; }
	
	@Override
	@SideOnly(Side.CLIENT)
	public int getRenderType() { return ClientProxy.backpackRenderId; }
	
	@Override
	public int quantityDropped(int meta, int fortune, Random random) { return 0; }
	
	@Override
	public TileEntity createNewTileEntity(World world) {
		return new TileEntityBackpack();
	}
	
}
