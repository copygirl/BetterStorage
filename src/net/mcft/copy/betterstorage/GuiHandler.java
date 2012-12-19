package net.mcft.copy.betterstorage;

import static net.minecraftforge.common.ForgeDirection.DOWN;
import net.mcft.copy.betterstorage.blocks.ContainerCrate;
import net.mcft.copy.betterstorage.blocks.ContainerReinforcedChest;
import net.mcft.copy.betterstorage.blocks.InventoryCratePlayerView;
import net.mcft.copy.betterstorage.blocks.InventoryWrapper;
import net.mcft.copy.betterstorage.blocks.TileEntityCrate;
import net.mcft.copy.betterstorage.blocks.TileEntityReinforcedChest;
import net.mcft.copy.betterstorage.client.GuiReinforcedChest;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.GuiChest;
import net.minecraft.src.IInventory;
import net.minecraft.src.InventoryBasic;
import net.minecraft.src.InventoryLargeChest;
import net.minecraft.src.TileEntity;
import net.minecraft.src.World;
import cpw.mods.fml.common.network.IGuiHandler;

public class GuiHandler implements IGuiHandler {
	
	private IInventory getChestInventory(World world, int x, int y, int z) {
		TileEntity tileEntity = world.getBlockTileEntity(x, y, z);
		if (!(tileEntity instanceof TileEntityReinforcedChest)) return null;
		if (world.isBlockSolidOnSide(x, y + 1, z, DOWN)) return null;
		
		TileEntityReinforcedChest chest = (TileEntityReinforcedChest)tileEntity;
		IInventory inventory = new InventoryWrapper(chest);
		int id = chest.getBlockType().blockID;
		if (world.getBlockId(x, y, z - 1) == id)
			inventory = new InventoryLargeChest("container.reinforcedChestLarge", getChestWrapper(world, x, y, z - 1), inventory);
		else if (world.getBlockId(x, y, z + 1) == id)
			inventory = new InventoryLargeChest("container.reinforcedChestLarge", inventory, getChestWrapper(world, x, y, z + 1));
		else if (world.getBlockId(x - 1, y, z) == id)
			inventory = new InventoryLargeChest("container.reinforcedChestLarge", getChestWrapper(world, x - 1, y, z), inventory);
		else if (world.getBlockId(x + 1, y, z) == id)
			inventory = new InventoryLargeChest("container.reinforcedChestLarge", inventory, getChestWrapper(world, x + 1, y, z));
		return inventory;
	}
	private IInventory getChestWrapper(World world, int x, int y, int z) {
		TileEntityReinforcedChest chest = TileEntityReinforcedChest.getChestAt(world, x, y, z);
		return new InventoryWrapper(chest);
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
