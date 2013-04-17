package net.mcft.copy.betterstorage.misc.handlers;

import net.mcft.copy.betterstorage.BetterStorage;
import net.mcft.copy.betterstorage.block.TileEntityContainer;
import net.mcft.copy.betterstorage.client.gui.GuiBetterStorage;
import net.mcft.copy.betterstorage.container.ContainerBetterStorage;
import net.mcft.copy.betterstorage.container.ContainerKeyring;
import net.mcft.copy.betterstorage.misc.Constants;
import net.mcft.copy.betterstorage.utils.WorldUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.IGuiHandler;

public class GuiHandler implements IGuiHandler {
	
	@Override
	public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
		ContainerBetterStorage container = null;
		if (id < 100) {
			TileEntityContainer tileEntity = WorldUtils.get(world, x, y, z, TileEntityContainer.class);
			if (tileEntity != null) {
				if (!tileEntity.canPlayerUseContainer(player)) return null;
				container = tileEntity.createContainer(player, id);
				if (container == null)
					BetterStorage.log.warning(String.format("Couldn't create server GUI element for player %s from ID %d for %s at %d,%d,%d.", player.username, id, tileEntity.getClass().getSimpleName(), x, y, z));
			} else BetterStorage.log.warning(String.format("Couldn't create server GUI element for player %s from ID %d: No TileEntityInventory at %d,%d,%d.", player.username, id, x, y, z));
		} else switch (id) {
			case Constants.keyringGuiId:
				container = new ContainerKeyring(player);
				break;
			default:
				BetterStorage.log.warning(String.format("Couldn't create server GUI element for player %s from ID %d.", player.username, id));
				break;
		}
		return container;
	}
	
	@Override
	public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
		GuiBetterStorage gui = null;
		if (id < 100) {
			TileEntityContainer tileEntity = WorldUtils.get(world, x, y, z, TileEntityContainer.class);
			if (tileEntity != null) {
				if (!tileEntity.canPlayerUseContainer(player)) return null;
				gui = tileEntity.createGui(player, id);
				if (gui == null)
					BetterStorage.log.warning(String.format("Couldn't create client GUI element from ID %d for %s at %d,%d,%d.", id, tileEntity.getClass().getSimpleName(), x, y, z));
			} else BetterStorage.log.warning(String.format("Couldn't create client GUI element from ID %d: No TileEntityInventory at %d,%d,%d.", id, x, y, z));
		} else switch (id) {
			case Constants.keyringGuiId:
				gui = new GuiBetterStorage(new ContainerKeyring(player));
				break;
			default:
				BetterStorage.log.warning(String.format("Couldn't create client GUI element from ID %d.", id));
				break;
		}
		return gui;
	}
	
}
