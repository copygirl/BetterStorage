package net.mcft.copy.betterstorage.addon.thaumcraft;

import net.mcft.copy.betterstorage.tile.TileBackpack;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileThaumcraftBackpack extends TileBackpack {
	
	public TileThaumcraftBackpack(int id) {
		super(id);
	}
	
	@Override
	public Class<? extends Item> getItemClass() { return ItemThaumcraftBackpack.class; }
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister iconRegister) {
		blockIcon = iconRegister.registerIcon("wool_colored_purple");
	}
	
	@Override
	public TileEntity createNewTileEntity(World world) {
		return new TileEntityThaumcraftBackpack();
	}
	
}
