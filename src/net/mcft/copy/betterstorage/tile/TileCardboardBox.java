package net.mcft.copy.betterstorage.tile;

import java.util.Random;

import net.mcft.copy.betterstorage.item.tile.ItemCardboardBox;
import net.mcft.copy.betterstorage.misc.Constants;
import net.mcft.copy.betterstorage.tile.entity.TileEntityCardboardBox;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileCardboardBox extends TileContainerBetterStorage {
	
	private Icon sideIcon;
	
	public TileCardboardBox(int id) {
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
		blockIcon = iconRegister.registerIcon(Constants.modId + ":cardboardBox");
		sideIcon = iconRegister.registerIcon(Constants.modId + ":cardboardBox_side");
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
	
}
