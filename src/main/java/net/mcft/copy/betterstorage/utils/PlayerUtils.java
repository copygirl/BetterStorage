package net.mcft.copy.betterstorage.utils;

import net.mcft.copy.betterstorage.BetterStorage;
import net.mcft.copy.betterstorage.addon.thaumcraft.GuiThaumiumChest;
import net.mcft.copy.betterstorage.client.gui.GuiBetterStorage;
import net.mcft.copy.betterstorage.client.gui.GuiCraftingStation;
import net.mcft.copy.betterstorage.client.gui.GuiCrate;
import net.mcft.copy.betterstorage.container.ContainerKeyring;
import net.mcft.copy.betterstorage.inventory.InventoryCardboardBox;
import net.mcft.copy.betterstorage.inventory.InventoryWrapper;
import net.mcft.copy.betterstorage.misc.Constants;
import net.mcft.copy.betterstorage.network.packet.PacketOpenGui;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public final class PlayerUtils {
	
	private PlayerUtils() {  }
	
	@SideOnly(Side.CLIENT)
	public static EntityPlayer getLocalPlayer() {
		return Minecraft.getMinecraft().thePlayer;
	}
	
	public static void openGui(EntityPlayer pl, String name, int columns, int rows, String title, Container container) {
		
		EntityPlayerMP player = (EntityPlayerMP)pl;
		if (title == null) title = "";
		
		player.closeContainer();
		player.getNextWindowId();
		
		BetterStorage.networkChannel.sendTo(
				new PacketOpenGui(player.currentWindowId, name, columns, rows, title), player);
		
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
		
		if (name.equals(Constants.containerCrate))
			return new GuiCrate(player, rows, title, localized);
		else if (name.equals(Constants.containerKeyring))
			return new GuiBetterStorage(new ContainerKeyring(player, title, columns));
		else if (name.startsWith(Constants.containerThaumiumChest))
			return new GuiThaumiumChest(player, columns, rows, title, localized);
		else if (name.equals(Constants.containerCardboardBox))
			return new GuiBetterStorage(player, columns, rows, new InventoryWrapper(
					new InventoryCardboardBox(new ItemStack[columns * rows]), title, localized));
		else if (name.equals(Constants.containerCraftingStation))
			return new GuiCraftingStation(player, title, localized);
		else return new GuiBetterStorage(player, columns, rows, title, localized);
		
	}
	
}
