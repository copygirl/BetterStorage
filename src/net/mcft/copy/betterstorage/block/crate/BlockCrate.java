package net.mcft.copy.betterstorage.block.crate;

import net.mcft.copy.betterstorage.block.BlockContainerBetterStorage;
import net.mcft.copy.betterstorage.misc.Constants;
import net.mcft.copy.betterstorage.utils.WorldUtils;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockCrate extends BlockContainerBetterStorage {
	
	public BlockCrate(int id) {
		super(id, Material.wood);
		
		setHardness(2.0f);
		setStepSound(Block.soundWoodFootstep);
		
		MinecraftForge.setBlockHarvestLevel(this, "axe", 0);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister iconRegister) {
		blockIcon = iconRegister.registerIcon(Constants.modId + ":crate");
	}
	
	@Override
	public TileEntity createNewTileEntity(World world) {
        return new TileEntityCrate();
    }
	
	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity, ItemStack stack) {
		TileEntityCrate crate = WorldUtils.get(world, x, y, z, TileEntityCrate.class);
		if (stack.hasDisplayName())
			crate.setCustomTitle(stack.getDisplayName());
		// This will result in the crate ID being initialized.
		crate.updateEntity();
	}
	
	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int par6, float par7, float par8, float par9) {
		if (world.isRemote) return true;
		WorldUtils.get(world, x, y, z, TileEntityCrate.class).openGui(player);
		return true;
	}
	
}
