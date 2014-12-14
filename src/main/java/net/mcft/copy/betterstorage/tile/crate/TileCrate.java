package net.mcft.copy.betterstorage.tile.crate;

import net.mcft.copy.betterstorage.item.tile.ItemTileBetterStorage;
import net.mcft.copy.betterstorage.tile.TileContainerBetterStorage;
import net.mcft.copy.betterstorage.utils.WorldUtils;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

//TODO (1.8): Botania
//@Interface(modid = "Botania", iface = "vazkii.botania.api.mana.ILaputaImmobile", striprefs = true)
public class TileCrate extends TileContainerBetterStorage /*implements ILaputaImmobile*/ {
	
	//private ConnectedTexture texture = new ConnectedTextureCrate();
	
	public TileCrate() {
		super(Material.wood);
		
		setHardness(2.0f);
		setStepSound(soundTypeWood);
		
		setHarvestLevel("axe", 0);
	}
	
	@Override
	public Class<? extends ItemBlock> getItemClass() { return ItemTileBetterStorage.class; }
	
	/*
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
	*/
	
	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileEntityCrate();
    }
	
	public void onBlockPlacedExtended(World world, BlockPos pos,
	                                  EnumFacing side, float hitX, float hitY, float hitZ,
	                                  EntityLivingBase entity, ItemStack stack) {
		TileEntityCrate crate = WorldUtils.get(world, pos, TileEntityCrate.class);
		if (stack.hasDisplayName())
			crate.setCustomTitle(stack.getDisplayName());
		crate.attemptConnect(side.getOpposite());
	}
	
	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumFacing side, float hitX, float hitY, float hitZ){
		if (world.isRemote) return true;
		WorldUtils.get(world, pos, TileEntityCrate.class).openGui(player);
		return true;
	}
	
	@Override
	public boolean hasComparatorInputOverride() { return true; }
	
	//TODO (1.8): IIcons? What's that?
	/*private class ConnectedTextureCrate extends ConnectedTexture {
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
	*/

	/*@Override
	public boolean canMove(World world, int x, int y, int z) {
		return false;
	}*/
}
