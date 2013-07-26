package net.mcft.copy.betterstorage.block;

import java.util.Random;

import net.mcft.copy.betterstorage.block.tileentity.TileEntityCardboardBox;
import net.mcft.copy.betterstorage.item.ItemCardboardBox;
import net.mcft.copy.betterstorage.utils.StackUtils;
import net.mcft.copy.betterstorage.utils.WorldUtils;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockCardboardBox extends BlockContainerBetterStorage {
	
	private Icon sideIcon;
	
	public BlockCardboardBox(int id) {
		super(id, Material.wood);
		
		setHardness(0.8f);
		setStepSound(soundWoodFootstep);
		setBlockBounds(0.0625F, 0.0F, 0.0625F, 0.9375F, 0.875F, 0.9375F);
	}
	
	@Override
	public Class<? extends Item> getItemClass() { return ItemCardboardBox.class; }
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister iconRegister) {
		blockIcon = iconRegister.registerIcon("betterstorage:cardboardBox");
		sideIcon = iconRegister.registerIcon("betterstorage:cardboardBox_side");
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public Icon getIcon(int side, int meta) {
		return ((side < 2) ? blockIcon : sideIcon);
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
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity, ItemStack stack) {
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
		if (!cardboardBox.moved) {
			ItemStack stack = new ItemStack(this);
			if (!StackUtils.isEmpty(cardboardBox.contents))
				StackUtils.setStackContents(stack, cardboardBox.contents);
			// Don't drop an empty cardboard box in creative.
			else if (cardboardBox.brokenInCreative) return;
			WorldUtils.dropStackFromBlock(world, x, y, z, stack);
		} else cardboardBox.dropContents();
		super.breakBlock(world, x, y, z, id, meta);
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
