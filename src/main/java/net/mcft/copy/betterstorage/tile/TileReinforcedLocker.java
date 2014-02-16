package net.mcft.copy.betterstorage.tile;

import net.mcft.copy.betterstorage.item.tile.ItemLockable;
import net.mcft.copy.betterstorage.proxy.ClientProxy;
import net.mcft.copy.betterstorage.tile.entity.TileEntityLocker;
import net.mcft.copy.betterstorage.tile.entity.TileEntityReinforcedLocker;
import net.mcft.copy.betterstorage.utils.WorldUtils;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileReinforcedLocker extends TileLockable {
	
	public TileReinforcedLocker(int id, Material material) {
		super(id, material);
		
		setHardness(8.0F);
		setResistance(20.0F);
		setStepSound(soundWoodFootstep);
		setBlockBounds(1 / 16.0F, 1 / 16.0F, 1 / 16.0F, 15 / 16.0F, 15 / 16.0F, 15 / 16.0F);
		
		MinecraftForge.setBlockHarvestLevel(this, "axe", 2);
	}
	public TileReinforcedLocker(int id) {
		this(id, Material.wood);
	}
	
	@Override
	public Class<? extends Item> getItemClass() { return ItemLockable.class; }
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister iconRegister) {
		blockIcon = iconRegister.registerIcon("log_oak");
	}
	
	@Override
	public boolean isOpaqueCube() { return false; }
	@Override
	public boolean renderAsNormalBlock() { return false; }
	
	@Override
	@SideOnly(Side.CLIENT)
	public int getRenderType() { return ClientProxy.reinforcedLockerRenderId; }
	
	@Override
	public boolean isBlockSolidOnSide(World world, int x, int y, int z, ForgeDirection side) {
		TileEntityLocker locker = WorldUtils.get(world, x, y, z, TileEntityLocker.class);
		return ((locker == null) || (locker.getOrientation() != side));
	}
	
	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
		float minX = 0, minY = 0, minZ = 0;
		float maxX = 1, maxY = 1, maxZ = 1;
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
	public TileEntity createNewTileEntity(World world) {
		return new TileEntityReinforcedLocker();
	}
	
}
