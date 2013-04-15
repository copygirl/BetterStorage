package net.mcft.copy.betterstorage.block;

import java.util.Random;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.mcft.copy.betterstorage.proxy.ClientProxy;
import net.mcft.copy.betterstorage.utils.StackUtils;
import net.mcft.copy.betterstorage.utils.WorldUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;

public class BlockBackpack extends BlockContainer {
	
	public BlockBackpack(int id) {
		super(id, Material.cloth);
		
		setHardness(0.7f);
		setStepSound(Block.soundClothFootstep);
		setBlockBounds(3 / 16.0F, 0.0F, 3 / 16.0F, 13 / 16.0F, 13 / 16.0F, 13 / 16.0F);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister iconRegister) {
		blockIcon = iconRegister.registerIcon("cloth_12");
	}
	
	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
		ForgeDirection orientation = ForgeDirection.getOrientation(world.getBlockMetadata(x, y, z));
		if (orientation == ForgeDirection.NORTH || orientation == ForgeDirection.SOUTH)
			setBlockBounds(3 / 16.0F, 0.0F, 2 / 16.0F, 13 / 16.0F, 13 / 16.0F, 14 / 16.0F);
		else if (orientation == ForgeDirection.WEST || orientation == ForgeDirection.EAST)
			setBlockBounds(2 / 16.0F, 0.0F, 3 / 16.0F, 14 / 16.0F, 13 / 16.0F, 13 / 16.0F);
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
	
	@Override
	public void breakBlock(World world, int x, int y, int z, int id, int meta) {
		TileEntityBackpack backpack = WorldUtils.getBackpack(world, x, y, z);
		super.breakBlock(world, x, y, z, id, meta);
		if (backpack != null && !backpack.equipped) {
			ItemStack stack = new ItemStack(this, 1, backpack.damage);
			WorldUtils.dropStackFromBlock(world, x, y, z, stack);
			backpack.dropContents();
		}
	}
	
	@Override
	public boolean removeBlockByPlayer(World world, EntityPlayer player, int x, int y, int z) {
		if (world.isRemote || !player.isSneaking() ||
		    player.inventory.armorInventory[2] != null)
			return world.setBlockToAir(x, y, z);
		
		TileEntityBackpack backpack = WorldUtils.getBackpack(world, x, y, z);
		backpack.equipped = true;
		
		if (!world.setBlockToAir(x, y, z)) return false;
		
		ItemStack backpackItem = new ItemStack(this, 1, backpack.damage);
		StackUtils.setStackContents(backpackItem, backpack.getContents());
		player.inventory.armorInventory[2] = backpackItem;
		
		return true;
	}
	
	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
		if (world.isRemote) return true;
		TileEntityBackpack backpack = WorldUtils.getBackpack(world, x, y, z);
		player.displayGUIChest(backpack.getWrapper());
		return true;
	}
	
}
