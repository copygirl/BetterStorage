package net.mcft.copy.betterstorage;

import net.mcft.copy.betterstorage.block.ContainerCrate;
import net.mcft.copy.betterstorage.block.ContainerReinforcedChest;
import net.mcft.copy.betterstorage.block.TileEntityCrate;
import net.mcft.copy.betterstorage.block.TileEntityReinforcedChest;
import net.mcft.copy.betterstorage.client.GuiReinforcedChest;
import net.mcft.copy.betterstorage.inventory.InventoryCombined;
import net.mcft.copy.betterstorage.inventory.InventoryCratePlayerView;
import net.minecraft.client.gui.inventory.GuiChest;
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
		if (chest.isLarge()) {
			TileEntityReinforcedChest connectedChest = chest.getConnectedChest();
			if (connectedChest == null) {
				BetterStorage.log("Warning: getConnectedChest returned null.");
				return null;
			}
			IInventory mainInventory = (chest.isMainChest() ? chest.getWrapper() : connectedChest.getWrapper());
			IInventory sideInventory = (chest.isMainChest() ? connectedChest.getWrapper() : chest.getWrapper());
			return new InventoryCombined<IInventory>("container.reinforcedChestLarge", mainInventory, sideInventory);
		} else return chest.getWrapper();
	}
	
	@Override
	public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
		IInventory inventory;
		switch (id) {
			case Constants.crateGuiIdSmall:
			case Constants.crateGuiIdMedium:
			case Constants.crateGuiIdLarge:
				TileEntity tileEntity = world.getBlockTileEntity(x, y, z);
				if (!(tileEntity instanceof TileEntityCrate)) return null;
				TileEntityCrate crate = (TileEntityCrate)tileEntity;
				return new ContainerCrate(player, new InventoryCratePlayerView(crate));
			default:
				if (id >= Constants.chestGuiIdSmall &&
				    id <  Constants.chestGuiIdLarge + 10) {
					inventory = getChestInventory(world, x, y, z);
					if (inventory == null) return null;
					boolean large = (id >= Constants.chestGuiIdLarge);
					int columns = 9 + (id % 10);
					int rows    = (large ? 6 : 3);
					return new ContainerReinforcedChest(player, columns, rows, inventory);
				} else return null;
		}
	}
	
	@Override
	public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
		int rows;
		int columns = 9;
		switch (id) {
			case Constants.crateGuiIdSmall:
			case Constants.crateGuiIdMedium:
			case Constants.crateGuiIdLarge:
				rows = 2;
				if (id == Constants.crateGuiIdMedium) rows = 4;
				else if (id == Constants.crateGuiIdLarge) rows = 6;
				return new GuiChest(player.inventory, new InventoryBasic("container.crate", columns * rows));
			default:
				if (id >= Constants.chestGuiIdSmall &&
				    id <  Constants.chestGuiIdLarge + 10) {
					boolean large = (id >= Constants.chestGuiIdLarge);
					String name = "container.reinforcedChest" + (large ? "Large" : "");
					columns = 9 + (id % 10);
					rows    = (large ? 6 : 3);
					return new GuiReinforcedChest(player, columns, rows, new InventoryBasic(name, columns * rows));
				}
				return null;
		}
	}
	
}
