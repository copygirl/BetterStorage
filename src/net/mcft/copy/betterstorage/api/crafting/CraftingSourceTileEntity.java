package net.mcft.copy.betterstorage.api.crafting;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public abstract class CraftingSourceTileEntity implements ICraftingSource {
	
	public final TileEntity entity;
	public final EntityPlayer player;
	
	public CraftingSourceTileEntity(TileEntity entity, EntityPlayer player) {
		this.entity = entity;
		this.player = player;
	}
	
	@Override
	public EntityPlayer getPlayer() { return player; }
	
	@Override
	public abstract IInventory getInventory();
	
	@Override
	public World getWorld() { return ((entity != null) ? entity.worldObj : null); }
	
	@Override
	public double getX() { return ((entity != null) ? (entity.xCoord + 0.5) : 0); }
	@Override
	public double getY() { return ((entity != null) ? (entity.yCoord + 0.5) : 0); }
	@Override
	public double getZ() { return ((entity != null) ? (entity.zCoord + 0.5) : 0); }
	
}
