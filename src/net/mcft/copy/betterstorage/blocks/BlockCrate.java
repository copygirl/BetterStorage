package net.mcft.copy.betterstorage.blocks;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import net.mcft.copy.betterstorage.BetterStorage;
import net.mcft.copy.betterstorage.Constants;
import net.minecraft.src.Block;
import net.minecraft.src.BlockContainer;
import net.minecraft.src.CreativeTabs;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.Material;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;
import net.minecraftforge.common.MinecraftForge;

public class BlockCrate extends BlockContainer {
	
	public BlockCrate(int id) {
		super(id, Material.wood);
		setHardness(2.0f);
		setStepSound(Block.soundWoodFootstep);
		setCreativeTab(CreativeTabs.tabDecorations);
		blockIndexInTexture = 0;
		
		GameRegistry.registerBlock(this);
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
