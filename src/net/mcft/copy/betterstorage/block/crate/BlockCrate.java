package net.mcft.copy.betterstorage.block.crate;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.mcft.copy.betterstorage.block.TileEntityContainer;
import net.mcft.copy.betterstorage.utils.WorldUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

public class BlockCrate extends BlockContainer {
	
	public BlockCrate(int id) {
		super(id, Material.wood);
		
		setHardness(2.0f);
		setStepSound(Block.soundWoodFootstep);
		
		MinecraftForge.setBlockHarvestLevel(this, "axe", 0);
		setCreativeTab(CreativeTabs.tabDecorations);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister iconRegister) {
		blockIcon = iconRegister.registerIcon("betterstorage:crate");
	}
	
	@Override
	public TileEntity createNewTileEntity(World world) {
        return new TileEntityCrate();
    }
	
	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLiving entity, ItemStack stack) {
		if (stack.hasDisplayName())
			WorldUtils.get(world, x, y, z, TileEntityContainer.class).setCustomTitle(stack.getDisplayName());
	}
	
	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int par6, float par7, float par8, float par9) {
		if (world.isRemote) return true;
		WorldUtils.get(world, x, y, z, TileEntityCrate.class).openGui(player);
		return true;
	}
	
}
