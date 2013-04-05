package net.mcft.copy.betterstorage.block;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.mcft.copy.betterstorage.BetterStorage;
import net.mcft.copy.betterstorage.client.ClientProxy;
import net.mcft.copy.betterstorage.misc.Constants;
import net.mcft.copy.betterstorage.utils.DirectionUtils;
import net.mcft.copy.betterstorage.utils.WorldUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.common.MinecraftForge;

public class BlockLocker extends BlockContainer {
	
	public BlockLocker(int id) {
		super(id, Material.wood);
		
		setHardness(2.5f);
		setStepSound(Block.soundWoodFootstep);
		
		MinecraftForge.setBlockHarvestLevel(this, "axe", 0);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister iconRegister) {
		blockIcon = iconRegister.registerIcon("wood");
	}

	@Override
	public boolean isOpaqueCube() { return false; }
	@Override
	public boolean renderAsNormalBlock() { return false; }
	
	@Override
	@SideOnly(Side.CLIENT)
	public int getRenderType() { return ClientProxy.lockerRenderId; }
	
	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLiving player, ItemStack stack) {
		TileEntityLocker locker = WorldUtils.getLocker(world, x, y, z);
		locker.orientation = DirectionUtils.getOrientation(player).getOpposite();
		double angle = DirectionUtils.getRotation(locker.orientation.getOpposite());
		double yaw = ((player.rotationYaw % 360) + 360) % 360;
		locker.mirror = (DirectionUtils.angleDifference(angle, yaw) > 0);
		locker.checkForConnections();
	}
	
	@Override
	public void breakBlock(World world, int x, int y, int z, int id, int meta) {
		TileEntityLocker locker = WorldUtils.getLocker(world, x, y, z);
		super.breakBlock(world, x, y, z, id, meta);
		if (locker != null) {
			locker.dropContents();
			locker.disconnect();
		}
	}
	
	@Override
	public TileEntity createNewTileEntity(World world) {
		return new TileEntityLocker();
	}
	
	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
		if (world.isRemote) return true;
		
		TileEntityLocker locker = WorldUtils.getLocker(world, x, y, z);
		ForgeDirection sideDirection = DirectionUtils.getDirectionFromSide(side);
		if (world.isRemote || locker == null || locker.orientation != sideDirection) return true;
		
		int guiID = (locker.isConnected() ? Constants.lockerLargeGuiId : Constants.lockerSmallGuiId);
		player.openGui(BetterStorage.instance, guiID, world, x, y, z);
		return true;
	}
	
}
