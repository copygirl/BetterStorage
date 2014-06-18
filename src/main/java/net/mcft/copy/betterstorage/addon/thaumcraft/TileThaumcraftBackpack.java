package net.mcft.copy.betterstorage.addon.thaumcraft;

import net.mcft.copy.betterstorage.item.ItemBackpack;
import net.mcft.copy.betterstorage.tile.TileBackpack;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileThaumcraftBackpack extends TileBackpack {
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister iconRegister) {
		blockIcon = iconRegister.registerIcon("wool_colored_purple");
	}
	
	@Override
	public TileEntity createTileEntity(World world, int metadata) {
		return new TileEntityThaumcraftBackpack();
	}
	
	@Override
	public ItemBackpack getItemType() { return ThaumcraftAddon.itemThaumcraftBackpack; }
	
}
