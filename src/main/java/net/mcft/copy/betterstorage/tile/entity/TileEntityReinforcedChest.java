package net.mcft.copy.betterstorage.tile.entity;

import net.mcft.copy.betterstorage.BetterStorage;
import net.mcft.copy.betterstorage.config.GlobalConfig;
import net.mcft.copy.betterstorage.misc.Constants;
import net.mcft.copy.betterstorage.utils.WorldUtils;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TileEntityReinforcedChest extends TileEntityLockable {
	
	@Override
	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getRenderBoundingBox() {
		return WorldUtils.getAABB(this, 0, 0, 0, 1, 1, 1);
	}
	
	@SideOnly(Side.CLIENT)
	public ResourceLocation getResource() {
		return getMaterial().getChestResource(isConnected());
	}
	
	@Override
	public void setAttachmentPosition() {
		double x = (!isConnected() ? 8 :
		            (((getOrientation() == EnumFacing.WEST) ||
		              (getOrientation() == EnumFacing.SOUTH)) ? 0 : 16));
		lockAttachment.setBox(x, 6.5, 0.5, 7, 7, 1);
	}
	
	// TileEntityContainer stuff
	
	@Override
	public int getColumns() { return BetterStorage.globalConfig.getInteger(GlobalConfig.reinforcedColumns); }
	
	// TileEntityConnactable stuff
	
	private static EnumFacing[] neighbors = { EnumFacing.EAST, EnumFacing.NORTH,
		EnumFacing.WEST, EnumFacing.SOUTH };
	
	@Override
	protected String getConnectableName() { return Constants.containerReinforcedChest; }
	
	@Override
	public EnumFacing[] getPossibleNeighbors() { return neighbors; }
	
	@Override
	public boolean canConnect(TileEntityConnectable connectable) {
		return ((connectable instanceof TileEntityReinforcedChest) && super.canConnect(connectable) &&
		        (((getPos().getX() != connectable.getPos().getX()) && ((getOrientation() == EnumFacing.SOUTH) ||
		                                             (getOrientation() == EnumFacing.NORTH))) ||
		         ((getPos().getZ() != connectable.getPos().getZ()) && ((getOrientation() == EnumFacing.EAST) ||
		                                             (getOrientation() == EnumFacing.WEST)))));
	}
	
}
