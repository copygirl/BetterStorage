package net.mcft.copy.betterstorage.block;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import net.mcft.copy.betterstorage.BetterStorage;
import net.mcft.copy.betterstorage.Constants;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

public class BlockCrate extends BlockContainer {
	
	public BlockCrate(int id) {
		super(id, Material.wood);
		setHardness(2.0f);
		setStepSound(Block.soundWoodFootstep);
		setCreativeTab(CreativeTabs.tabDecorations);
		blockIndexInTexture = 0;
		
		GameRegistry.registerBlock(this, "crate");
		MinecraftForge.setBlockHarvestLevel(this, "axe", 0);
		setBlockName("crate");
		LanguageRegistry.addName(this, "Storage Crate");
	}
	
	@Override
	public String getTextureFile() { return Constants.terrain; }
	
	@Override
	public TileEntity createNewTileEntity(World world) {
        return new TileEntityCrate();
    }
	
	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int par6, float par7, float par8, float par9) {
		if (player.isSneaking()) return false;
		if (world.isRemote) return true;
		TileEntityCrate crate = (TileEntityCrate)world.getBlockTileEntity(x, y, z);
		int crates = crate.getPileData().getNumCrates();
		int id = Constants.crateGuiIdSmall + Math.min(crates, 3) - 1;
		player.openGui(BetterStorage.instance, id, world, x, y, z);
		return true;
	}
	
}
