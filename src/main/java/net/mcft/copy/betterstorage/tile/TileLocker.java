package net.mcft.copy.betterstorage.tile;

import net.mcft.copy.betterstorage.proxy.ClientProxy;
import net.mcft.copy.betterstorage.tile.entity.TileEntityLocker;
import net.mcft.copy.betterstorage.utils.WorldUtils;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileLocker extends TileContainerBetterStorage {
	
	public TileLocker() {
		super(Material.wood);
		
		setHardness(2.5f);
		setStepSound(soundTypeWood);
		setBlockBounds(1 / 16.0F, 1 / 16.0F, 1 / 16.0F, 15 / 16.0F, 15 / 16.0F, 15 / 16.0F);
		
		setHarvestLevel("axe", 0);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister iconRegister) {
		blockIcon = iconRegister.registerIcon("planks_oak");
	}
	
	@Override
	public boolean isOpaqueCube() { return false; }
	@Override
	public boolean renderAsNormalBlock() { return false; }
	
	@Override
	@SideOnly(Side.CLIENT)
	public int getRenderType() { return ClientProxy.lockerRenderId; }
	
	@Override
	public boolean isSideSolid(IBlockAccess world, int x, int y, int z, ForgeDirection side) {
		TileEntityLocker locker = WorldUtils.get(world, x, y, z, TileEntityLocker.class);
		return ((locker == null) || (locker.getOrientation() != side));
	}
	
	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
		float minX = 0;
		float minY = 0;
		float minZ = 0;
		float maxX = 1; 
		float maxY = 1;
		float maxZ = 1;
		switch (WorldUtils.get(world, x, y, z, TileEntityLocker.class).getOrientation()) {
			case EAST: maxX -= 1.0F / 16; break;
			case WEST: minX += 1.0F / 16; break;
			case SOUTH: maxZ -= 1.0F / 16; break;
			case NORTH: minZ += 1.0F / 16; break;
			default: break;
		}
		setBlockBounds(minX, minY, minZ, maxX, maxY, maxZ);
	}
	
	@Override
	public TileEntity createTileEntity(World world, int metadata) {
		return new TileEntityLocker();
	}
	
	@Override
	public boolean hasComparatorInputOverride() { return true; }
	
}
