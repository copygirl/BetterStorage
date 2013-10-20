package net.mcft.copy.betterstorage.block;

import net.mcft.copy.betterstorage.block.tileentity.TileEntityLocker;
import net.mcft.copy.betterstorage.block.tileentity.TileEntityReinforcedLocker;
import net.mcft.copy.betterstorage.item.block.ItemLockable;
import net.mcft.copy.betterstorage.proxy.ClientProxy;
import net.mcft.copy.betterstorage.utils.WorldUtils;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockReinforcedLocker extends BlockLockable {
	
	public BlockReinforcedLocker(int id, Material material) {
		super(id, material);
		
		setHardness(8.0F);
		setResistance(20.0F);
		setStepSound(soundWoodFootstep);
		
		MinecraftForge.setBlockHarvestLevel(this, "axe", 2);
	}
	public BlockReinforcedLocker(int id) {
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
	public boolean isBlockSolidOnSide(World world, int x, int y, int z, ForgeDirection side) {
		TileEntityLocker locker = WorldUtils.get(world, x, y, z, TileEntityLocker.class);
		return (locker.getOrientation() != side);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public int getRenderType() { return ClientProxy.reinforcedLockerRenderId; }
	
	@Override
	public TileEntity createNewTileEntity(World world) {
		return new TileEntityReinforcedLocker();
	}
	
}
