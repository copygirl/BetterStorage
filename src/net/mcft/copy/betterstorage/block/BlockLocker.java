package net.mcft.copy.betterstorage.block;

import net.mcft.copy.betterstorage.block.tileentity.TileEntityLocker;
import net.mcft.copy.betterstorage.proxy.ClientProxy;
import net.mcft.copy.betterstorage.utils.WorldUtils;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockLocker extends BlockContainerBetterStorage {
	
	public BlockLocker(int id) {
		super(id, Material.wood);
		
		setHardness(2.5f);
		setStepSound(soundWoodFootstep);
		
		MinecraftForge.setBlockHarvestLevel(this, "axe", 0);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister iconRegister) {
		blockIcon = iconRegister.registerIcon("planks_oak");
	}

	@Override
	public boolean isOpaqueCube() { return false; }
	@Override
	public boolean renderAsNormalBlock() { return false; }
	
	@Override
	public boolean isBlockSolidOnSide(World world, int x, int y, int z, ForgeDirection side) {
		TileEntityLocker locker = WorldUtils.get(world, x, y, z, TileEntityLocker.class);
		return (locker.getOrientation() != side);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public int getRenderType() { return ClientProxy.lockerRenderId; }
	
	@Override
	public TileEntity createNewTileEntity(World world) {
		return new TileEntityLocker();
	}
	
}
