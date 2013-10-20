package net.mcft.copy.betterstorage.block.tileentity;

import net.mcft.copy.betterstorage.Config;
import net.mcft.copy.betterstorage.misc.Constants;
import net.minecraft.util.ResourceLocation;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileEntityReinforcedLocker extends TileEntityLocker {
	
	@Override
	@SideOnly(Side.CLIENT)
	public ResourceLocation getResource() {
		return getMaterial().getLockerResource(isConnected());
	}

	@Override
	public boolean canHaveLock() { return true; }
	@Override
	public boolean canHaveMaterial() { return true; }
	@Override
	public void setAttachmentPosition() {
		lockAttachment.setBox(8, 6.5, 0.5, 7, 7, 1);
	}
	
	@Override
	public int getColumns() { return Config.reinforcedColumns; }
	@Override
	protected String getConnectableName() { return Constants.containerReinforcedLocker; }
}
