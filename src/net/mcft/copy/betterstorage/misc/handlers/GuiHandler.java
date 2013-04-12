package net.mcft.copy.betterstorage.misc.handlers;

import net.mcft.copy.betterstorage.BetterStorage;
import net.mcft.copy.betterstorage.block.TileEntityLocker;
import net.mcft.copy.betterstorage.block.TileEntityReinforcedChest;
import net.mcft.copy.betterstorage.block.crate.TileEntityCrate;
import net.mcft.copy.betterstorage.client.gui.GuiBetterStorage;
import net.mcft.copy.betterstorage.client.gui.GuiCrate;
import net.mcft.copy.betterstorage.client.gui.GuiReinforcedChest;
import net.mcft.copy.betterstorage.container.ContainerBetterStorage;
import net.mcft.copy.betterstorage.container.ContainerCrate;
import net.mcft.copy.betterstorage.container.ContainerKeyring;
import net.mcft.copy.betterstorage.inventory.InventoryCombined;
import net.mcft.copy.betterstorage.inventory.InventoryCratePlayerView;
import net.mcft.copy.betterstorage.misc.Constants;
import net.mcft.copy.betterstorage.utils.WorldUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import cpw.mods.fml.common.network.IGuiHandler;

public class GuiHandler implements IGuiHandler {
	
	private IInventory getChestInventory(World world, int x, int y, int z) {
		TileEntity tileEntity = world.getBlockTileEntity(x, y, z);
		if (!(tileEntity instanceof TileEntityReinforcedChest)) return null;
		if (world.isBlockSolidOnSide(x, y + 1, z, ForgeDirection.DOWN)) return null;
		TileEntityReinforcedChest chest = (TileEntityReinforcedChest)tileEntity;
		if (chest.isConnected()) {
			TileEntityReinforcedChest connectedChest = chest.getConnectedChest();
			if (connectedChest == null) {
				BetterStorage.log.info("Warning: getConnectedChest returned null.");
				return null;
			}
			IInventory mainInventory = (chest.isMain() ? chest.getWrapper() : connectedChest.getWrapper());
			IInventory sideInventory = (chest.isMain() ? connectedChest.getWrapper() : chest.getWrapper());
			return new InventoryCombined("container.reinforcedChestLarge", mainInventory, sideInventory);
		} else return chest.getWrapper();
	}
	
	@Override
	public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
		IInventory inventory;
		switch (id) {
			case Constants.crateSmallGuiId:
			case Constants.crateMediumGuiId:
			case Constants.crateLargeGuiId:
				TileEntityCrate crate = WorldUtils.getCrate(world, x, y, z);
				if (crate == null) return null;
				return new ContainerCrate(player, new InventoryCratePlayerView(crate));
			case Constants.lockerSmallGuiId:
				inventory = WorldUtils.getLocker(world, x, y, z);
				return new ContainerBetterStorage(player, inventory, 9, 3);
			case Constants.lockerLargeGuiId:
				TileEntityLocker locker = WorldUtils.getLocker(world, x, y, z);
				IInventory mainInventory = (locker.isMain() ? locker : locker.getConnectedLocker());
				IInventory sideInventory = (locker.isMain() ? locker.getConnectedLocker() : locker);
				inventory = new InventoryCombined("container.lockerLarge", mainInventory, sideInventory);
				return new ContainerBetterStorage(player, inventory, 9, 6);
			case Constants.keyringGuiId:
				return new ContainerKeyring(player);
			default:
				if (id >= Constants.chestSmallGuiId &&
				    id <  Constants.chestLargeGuiId + 10) {
					inventory = getChestInventory(world, x, y, z);
					if (inventory == null) return null;
					boolean large = (id >= Constants.chestLargeGuiId);
					int columns = 9 + (id % 10);
					int rows    = (large ? 6 : 3);
					return new ContainerBetterStorage(player, inventory, columns, rows);
				} else return null;
		}
	}
	
	@Override
	public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
		int rows;
		int columns = 9;
		switch (id) {
			case Constants.crateSmallGuiId:
			case Constants.crateMediumGuiId:
			case Constants.crateLargeGuiId:
				rows = 2;
				if (id == Constants.crateMediumGuiId) rows = 4;
				else if (id == Constants.crateLargeGuiId) rows = 6;
				return new GuiCrate(player, rows);
			case Constants.lockerSmallGuiId:
				return new GuiBetterStorage(player, 9, 3, "container.locker");
			case Constants.lockerLargeGuiId:
				return new GuiBetterStorage(player, 9, 6, "container.lockerLarge");
			case Constants.keyringGuiId:
				return new GuiBetterStorage(new ContainerKeyring(player));
			default:
				if (id >= Constants.chestSmallGuiId &&
				    id <  Constants.chestLargeGuiId + 10) {
					boolean large = (id >= Constants.chestLargeGuiId);
					String name = "container.reinforcedChest" + (large ? "Large" : "");
					columns = 9 + (id % 10);
					rows    = (large ? 6 : 3);
					return new GuiReinforcedChest(player, columns, rows, new InventoryBasic(name, false, columns * rows));
				} else return null;
		}
	}
	
}
