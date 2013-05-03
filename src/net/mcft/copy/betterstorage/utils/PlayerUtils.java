package net.mcft.copy.betterstorage.utils;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

import net.mcft.copy.betterstorage.client.gui.GuiBetterStorage;
import net.mcft.copy.betterstorage.client.gui.GuiCrate;
import net.mcft.copy.betterstorage.container.ContainerKeyring;
import net.mcft.copy.betterstorage.misc.handlers.PacketHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet250CustomPayload;

public class PlayerUtils {
	
	public static void openGui(EntityPlayer pl, String name, int columns, int rows, String title, Container container) {
		
		EntityPlayerMP player = (EntityPlayerMP)pl;
		
		player.closeInventory();
		player.incrementWindowID();
		
		ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
		DataOutputStream dataStream = new DataOutputStream(byteStream);
		
		try {
			dataStream.writeByte(PacketHandler.openGui);
			dataStream.writeInt(player.currentWindowId);
			dataStream.writeUTF(name);
			dataStream.writeByte(columns);
			dataStream.writeByte(rows);
			dataStream.writeUTF(title);
		} catch (Exception e) { throw new RuntimeException(e); }
		
		Packet packet = new Packet250CustomPayload("BetterStorage", byteStream.toByteArray());
		player.playerNetServerHandler.sendPacketToPlayer(packet);
		
		player.openContainer = container;
		player.openContainer.windowId = player.currentWindowId;
		player.openContainer.addCraftingToCrafters(player);
		
	}
	
	public static void openGui(EntityPlayer player, String name, int columns, int rows, String title) {
		
		GuiScreen gui = createGuiFromName(player, name, columns, rows, title);
		Minecraft.getMinecraft().displayGuiScreen(gui);
		
	}
	
	private static GuiScreen createGuiFromName(EntityPlayer player, String name, int columns, int rows, String title) {
		
		boolean localized = !title.isEmpty();
		if (!localized) title = name;
		
		if (name.equals("container.crate"))
			return new GuiCrate(player, rows, title, localized);
		if (name.equals("container.keyring")) {
			ContainerKeyring.setProtectedIndex(columns);
			return new GuiBetterStorage(new ContainerKeyring(player, title));
		} else return new GuiBetterStorage(player, columns, rows, title, localized);
		
	}
	
}
