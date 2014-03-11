package net.mcft.copy.betterstorage.tile.crate;

import net.mcft.copy.betterstorage.item.tile.ItemTileBetterStorage;
import net.mcft.copy.betterstorage.misc.ConnectedTexture;
import net.mcft.copy.betterstorage.misc.Constants;
import net.mcft.copy.betterstorage.tile.TileContainerBetterStorage;
import net.mcft.copy.betterstorage.utils.WorldUtils;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileCrate extends TileContainerBetterStorage {
	
	private ConnectedTexture texture = new ConnectedTextureCrate();
	
	public TileCrate() {
		super(Material.wood);
		
		setHardness(2.0f);
		setStepSound(soundTypeWood);
		
		setHarvestLevel("axe", 0);
	}
	
	@Override
	public Class<? extends Item> getItemClass() { return ItemTileBetterStorage.class; }
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister iconRegister) {
		texture.registerIcons(iconRegister, Constants.modId + ":crate/%s");
	}
	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta) { return texture.getIcon("all"); }
	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side) {
		return texture.getConnectedIcon(world, x, y, z, ForgeDirection.getOrientation(side));
	}
	
	@Override
	public TileEntity createTileEntity(World world, int metadata) {
        return new TileEntityCrate();
    }
	
	public void onBlockPlacedExtended(World world, int x, int y, int z,
	                                  int side, float hitX, float hitY, float hitZ,
	                                  EntityLivingBase entity, ItemStack stack) {
		TileEntityCrate crate = WorldUtils.get(world, x, y, z, TileEntityCrate.class);
		if (stack.hasDisplayName())
			crate.setCustomTitle(stack.getDisplayName());
		crate.attemptConnect(ForgeDirection.getOrientation(side).getOpposite());
	}
	
	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player,
	                                int side, float hitX, float hitY, float hitZ) {
		if (world.isRemote) return true;
		WorldUtils.get(world, x, y, z, TileEntityCrate.class).openGui(player);
		return true;
	}
	
	@Override
	public boolean hasComparatorInputOverride() { return true; }
	
	private class ConnectedTextureCrate extends ConnectedTexture {
		@Override
		public boolean canConnect(IBlockAccess world, int x, int y, int z, ForgeDirection side, ForgeDirection connected) {
			if (world.getBlock(x, y, z) != TileCrate.this) return false;
			int offX = x + connected.offsetX;
			int offY = y + connected.offsetY;
			int offZ = z + connected.offsetZ;
			TileEntityCrate connectedCrate = WorldUtils.get(world, offX, offY, offZ, TileEntityCrate.class);
			if (connectedCrate == null) return false;
			TileEntityCrate crate = WorldUtils.get(world, x, y, z, TileEntityCrate.class);
			return (crate.getID() == connectedCrate.getID());
		}
	}
	
}
