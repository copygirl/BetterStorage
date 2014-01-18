package net.mcft.copy.betterstorage.addon.thaumcraft;

import net.mcft.copy.betterstorage.tile.TileReinforcedChest;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileThaumiumChest extends TileReinforcedChest {
	
	public TileThaumiumChest(int id) {
		super(id, Material.iron);
		
		setHardness(12.0f);
		setResistance(35.0f);
		setStepSound(soundMetalFootstep);
		
		MinecraftForge.setBlockHarvestLevel(this, "pickaxe", 2);
	}
	
	@Override
	public boolean hasMaterial() { return false; }
	
	@Override
	@SideOnly(Side.CLIENT)
	public int getRenderType() { return ThaumcraftAddon.thaumiumChestRenderId; }
	
	@Override
	public TileEntity createNewTileEntity(World world) {
		return new TileEntityThaumiumChest();
	}
	
}
