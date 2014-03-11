package net.mcft.copy.betterstorage.tile;

import net.mcft.copy.betterstorage.attachment.IHasAttachments;
import net.mcft.copy.betterstorage.tile.entity.TileEntityContainer;
import net.mcft.copy.betterstorage.utils.WorldUtils;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public abstract class TileContainerBetterStorage extends TileBetterStorage {
	
	protected TileContainerBetterStorage(Material material) {
		super(material);
		isBlockContainer = true;
	}
	
	@Override
	public boolean onBlockEventReceived(World world, int x, int y, int z, int eventId, int eventPar) {
        TileEntity te = world.getTileEntity(x, y, z);
        return ((te != null) ? te.receiveClientEvent(eventId, eventPar) : false);
	}
	
	// Pass actions to TileEntityContainer
	
	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase player, ItemStack stack) {
		getContainer(world, x, y, z).onBlockPlaced(player, stack);
	}
	
	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player,
	                                int side, float hitX, float hitY, float hitZ) {
		return getContainer(world, x, y, z).onBlockActivated(player, side, hitX, hitY, hitZ);
	}
	
	@Override
	public boolean removedByPlayer(World world, EntityPlayer player, int x, int y, int z) {
		if (!getContainer(world, x, y, z).onBlockBreak(player)) return false;
		return super.removedByPlayer(world, player, x, y, z);
	}
	
	@Override
	public void onBlockPreDestroy(World world, int x, int y, int z, int meta) {
		TileEntityContainer container = getContainer(world, x, y, z);
		if (container != null) container.onBlockDestroyed();
	}
	
	@Override
	public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z) {
		TileEntityContainer container = getContainer(world, x, y, z);
		if (container instanceof IHasAttachments) {
			ItemStack pick = ((IHasAttachments)container).getAttachments().pick(target);
			if (pick != null) return pick;
		}
		ItemStack pick = super.getPickBlock(target, world, x, y, z);
		return container.onPickBlock(pick, target);
	}
	
	@Override
	public int getComparatorInputOverride(World world, int x, int y, int z, int direction) {
		return TileEntityContainer.getContainerComparatorSignalStrength(world, x, y, z);
	}
	
	private TileEntityContainer getContainer(World world, int x, int y, int z) {
		return WorldUtils.get(world, x, y, z, TileEntityContainer.class);
	}
	
}
