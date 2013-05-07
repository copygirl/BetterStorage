package net.mcft.copy.betterstorage.block;

import net.mcft.copy.betterstorage.container.ContainerBetterStorage;
import net.mcft.copy.betterstorage.inventory.InventoryWrapper;
import net.mcft.copy.betterstorage.utils.PlayerUtils;
import net.mcft.copy.betterstorage.utils.WorldUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockEnderBackpack extends BlockBackpack {
	
	public BlockEnderBackpack(int id) {
		super(id);
		setHardness(1.5f);
	}
	
	@Override
	public TileEntity createNewTileEntity(World world) {
		return new TileEntityBackpack();
	}
	
	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player,
	                                int side, float hitX, float hitY, float hitZ) {
		if (!world.isRemote) {
			TileEntityBackpack backpack = WorldUtils.get(world, x, y, z, TileEntityBackpack.class);
			IInventory inventory = new InventoryEnderBackpack(player, backpack);
			Container container = new ContainerBetterStorage(player, inventory, 9, 3);
			PlayerUtils.openGui(player, "container.enderBackpack", 9, 3, backpack.getCustomTitle(), container);
		}
		return true;
	}
	
	static class InventoryEnderBackpack extends InventoryWrapper {
		
		public final TileEntityBackpack backpack;
		
		public InventoryEnderBackpack(EntityPlayer player, TileEntityBackpack backpack) {
			super(player.getInventoryEnderChest());
			this.backpack = backpack;
		}
		@Override
		public boolean isUseableByPlayer(EntityPlayer player) {
			return WorldUtils.isTileEntityUsableByPlayer(backpack, player);
		}
		@Override
		public void openChest() { backpack.onContainerOpened(); }
		@Override
		public void closeChest() { backpack.onContainerClosed(); }
		
	}
	
}
