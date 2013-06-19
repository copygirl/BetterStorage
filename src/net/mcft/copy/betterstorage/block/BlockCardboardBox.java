package net.mcft.copy.betterstorage.block;

import java.util.Random;

import net.mcft.copy.betterstorage.block.tileentity.TileEntityCardboardBox;
import net.mcft.copy.betterstorage.utils.StackUtils;
import net.mcft.copy.betterstorage.utils.WorldUtils;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockCardboardBox extends BlockContainer {
	
	public BlockCardboardBox(int id) {
		super(id, Material.wood);
		
		setHardness(0.8f);
		setBlockBounds(0.0625F, 0.0F, 0.0625F, 0.9375F, 0.875F, 0.9375F);
	}
	
	@Override
	public boolean isOpaqueCube() { return false; }
	@Override
	public boolean renderAsNormalBlock() { return false; }
	
	@Override
	public int quantityDropped(Random rand) { return 0; }
	
	@Override
	public TileEntity createNewTileEntity(World world) {
		return new TileEntityCardboardBox();
	}
	
	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLiving entity, ItemStack stack) {
		TileEntityCardboardBox cardboardBox = WorldUtils.get(world, x, y, z, TileEntityCardboardBox.class);
		if (stack.hasDisplayName())
			cardboardBox.setCustomTitle(stack.getDisplayName());
		// If the cardboard box item has items, set the container contents to them.
		if (StackUtils.has(stack, "Items")) {
			ItemStack[] contents = StackUtils.getStackContents(stack, cardboardBox.contents.length);
			System.arraycopy(contents, 0, cardboardBox.contents, 0, contents.length);
			cardboardBox.moved = true;
		}
	}
	
	@Override
	public boolean removeBlockByPlayer(World world, EntityPlayer player, int x, int y, int z) {
		TileEntityCardboardBox cardboardBox = WorldUtils.get(world, x, y, z, TileEntityCardboardBox.class);
		if (player.capabilities.isCreativeMode)
			cardboardBox.brokenInCreative = true;
		return super.removeBlockByPlayer(world, player, x, y, z);
	}
	
	@Override
	public void breakBlock(World world, int x, int y, int z, int id, int meta) {
		TileEntityCardboardBox cardboardBox = WorldUtils.get(world, x, y, z, TileEntityCardboardBox.class);
		super.breakBlock(world, x, y, z, id, meta);
		if (!cardboardBox.moved) {
			ItemStack stack = new ItemStack(this);
			if (!StackUtils.isEmpty(cardboardBox.contents))
				StackUtils.setStackContents(stack, cardboardBox.contents);
			// Don't drop an empty cardboard box in creative.
			else if (cardboardBox.brokenInCreative) return;
			WorldUtils.dropStackFromBlock(world, x, y, z, stack);
		} else cardboardBox.dropContents();
	}
	
	@Override
	public boolean onBlockActivated(World world, int x, int y, int z,
	                                EntityPlayer player, int side,
	                                float hitX, float hitY, float hitZ) {
		if (!world.isRemote)
			WorldUtils.get(world, x, y, z, TileEntityCardboardBox.class).openGui(player);
		return true;
	}
	
}
