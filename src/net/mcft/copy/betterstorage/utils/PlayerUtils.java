package net.mcft.copy.betterstorage.utils;

import net.mcft.copy.betterstorage.addon.thaumcraft.GuiThaumiumChest;
import net.mcft.copy.betterstorage.client.gui.GuiBetterStorage;
import net.mcft.copy.betterstorage.client.gui.GuiCraftingStation;
import net.mcft.copy.betterstorage.client.gui.GuiCrate;
import net.mcft.copy.betterstorage.container.ContainerKeyring;
import net.mcft.copy.betterstorage.inventory.InventoryCardboardBox;
import net.mcft.copy.betterstorage.inventory.InventoryWrapper;
import net.mcft.copy.betterstorage.misc.Constants;
import net.mcft.copy.betterstorage.misc.handlers.PacketHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.Packet;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public final class PlayerUtils {
	
	private PlayerUtils() {  }
	
	public static void openGui(EntityPlayer pl, String name, int columns, int rows, String title, Container container) {
		
		EntityPlayerMP player = (EntityPlayerMP)pl;
		if (title == null) title = "";
		
		player.closeContainer();
		player.incrementWindowID();
		
		Packet packet = PacketHandler.makePacket(
				PacketHandler.openGui, player.currentWindowId,
				name, (byte)columns, (byte)rows, title);
		PacketDispatcher.sendPacketToPlayer(packet, (Player)player);
		
		player.openContainer = container;
		player.openContainer.windowId = player.currentWindowId;
		player.openContainer.addCraftingToCrafters(player);
		
	}
	
	@SideOnly(Side.CLIENT)
	public static void openGui(EntityPlayer player, String name, int columns, int rows, String title) {
		GuiScreen gui = createGuiFromName(player, name, columns, rows, title);
		Minecraft.getMinecraft().displayGuiScreen(gui);
	}
	@SideOnly(Side.CLIENT)
	private static GuiScreen createGuiFromName(EntityPlayer player, String name, int columns, int rows, String title) {
		
		boolean localized = !title.isEmpty();
		if (!localized) title = name;
		
		if (name.equals(Constants.containerCrate)) {
			return new GuiCrate(player, rows, title, localized);
		} else if (name.equals(Constants.containerKeyring)) {
			ContainerKeyring.setProtectedIndex(columns);
			return new GuiBetterStorage(new ContainerKeyring(player, title));
		} else if (name.startsWith(Constants.containerThaumiumChest)) {
			return new GuiThaumiumChest(player, columns, rows, title, localized);
		} else if (name.equals(Constants.containerCardboardBox)) {
			IInventory inventory = new InventoryWrapper(new InventoryCardboardBox(new ItemStack[9]), title, localized);
			return new GuiBetterStorage(player, columns, rows, inventory);
		} else if (name.equals(Constants.containerCraftingStation)) {
			return new GuiCraftingStation(player, title, localized);
		} else return new GuiBetterStorage(player, columns, rows, title, localized);
		
	}
	
}
