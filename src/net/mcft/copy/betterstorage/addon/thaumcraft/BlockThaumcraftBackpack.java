package net.mcft.copy.betterstorage.addon.thaumcraft;

import net.mcft.copy.betterstorage.block.BlockBackpack;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockThaumcraftBackpack extends BlockBackpack {
	
	public BlockThaumcraftBackpack(int id) {
		super(id);
	}
	
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
