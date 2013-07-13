package net.mcft.copy.betterstorage.utils;

import net.mcft.copy.betterstorage.addon.thaumcraft.GuiThaumiumChest;
import net.mcft.copy.betterstorage.client.gui.GuiBetterStorage;
import net.mcft.copy.betterstorage.client.gui.GuiCrate;
import net.mcft.copy.betterstorage.container.ContainerKeyring;
import net.mcft.copy.betterstorage.inventory.InventoryCardboardBox;
import net.mcft.copy.betterstorage.inventory.InventoryWrapper;
import net.mcft.copy.betterstorage.misc.handlers.PacketHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.DataWatcher;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class PlayerUtils {
	
	public static void openGui(EntityPlayer pl, String name, int columns, int rows, String title, Container container) {
		
		EntityPlayerMP player = (EntityPlayerMP)pl;
		if (title == null) title = "";
		
		player.closeContainer();
		player.incrementWindowID();
		
		PacketUtils.sendPacket(player,
				PacketHandler.openGui, player.currentWindowId,
				name, (byte)columns, (byte)rows, title);
		
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
		
		if (name.equals("container.crate"))
			return new GuiCrate(player, rows, title, localized);
		if (name.equals("container.keyring")) {
			ContainerKeyring.setProtectedIndex(columns);
			return new GuiBetterStorage(new ContainerKeyring(player, title));
		}
		if (name.startsWith("container.thaumiumChest"))
			return new GuiThaumiumChest(player, columns, rows, title, localized);

		if (name.equals("container.cardboardBox"))
			return new GuiBetterStorage(player, columns, rows, new InventoryWrapper(new InventoryCardboardBox(new ItemStack[9]), title, localized));
		return new GuiBetterStorage(player, columns, rows, title, localized);
		
	}
	
	public static void setHideCape(EntityPlayer player, boolean hidden) {
		DataWatcher dataWatcher = player.getDataWatcher();
		int index = 16, shift = 1;
		int value = dataWatcher.getWatchableObjectByte(index);
		if (hidden) dataWatcher.updateObject(index, (byte)(value | 1 << shift));
		else dataWatcher.updateObject(index, (byte)(value & ~(1 << shift)));
	}
	
}
