package net.mcft.copy.betterstorage.block;

import net.mcft.copy.betterstorage.block.tileentity.TileEntityReinforcedChest;
import net.mcft.copy.betterstorage.item.block.ItemLockable;
import net.mcft.copy.betterstorage.proxy.ClientProxy;
import net.mcft.copy.betterstorage.utils.WorldUtils;
import net.minecraft.block.Block;
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

public class BlockReinforcedChest extends BlockLockable {
	
	public BlockReinforcedChest(int id, Material material) {
		super(id, material);
		
		setHardness(8.0F);
		setResistance(20.0F);
		setStepSound(Block.soundWoodFootstep);
		setBlockBounds(0.0625F, 0.0F, 0.0625F, 0.9375F, 0.875F, 0.9375F);
		
		MinecraftForge.setBlockHarvestLevel(this, "axe", 2);
	}
	public BlockReinforcedChest(int id) {
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
	public int getRenderType() { return ClientProxy.reinforcedChestRenderId; }
	
	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
		TileEntityReinforcedChest chest = WorldUtils.get(world, x, y, z, TileEntityReinforcedChest.class);
		if (chest.isConnected()) {
			ForgeDirection connected = chest.getConnected();
			if (connected == ForgeDirection.NORTH)
				setBlockBounds(0.0625F, 0.0F, 0.0F, 0.9375F, 0.875F, 0.9375F);
			else if (connected == ForgeDirection.SOUTH)
				setBlockBounds(0.0625F, 0.0F, 0.0625F, 0.9375F, 0.875F, 1.0F);
			else if (connected == ForgeDirection.WEST)
				setBlockBounds(0.0F, 0.0F, 0.0625F, 0.9375F, 0.875F, 0.9375F);
			else if (connected == ForgeDirection.EAST)
				setBlockBounds(0.0625F, 0.0F, 0.0625F, 1.0F, 0.875F, 0.9375F);
		} else setBlockBounds(0.0625F, 0.0F, 0.0625F, 0.9375F, 0.875F, 0.9375F);
	}
	
	@Override
	public TileEntity createNewTileEntity(World world) {
		return new TileEntityReinforcedChest();
	}
	
}
