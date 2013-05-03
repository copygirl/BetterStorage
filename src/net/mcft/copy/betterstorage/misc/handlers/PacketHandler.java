package net.mcft.copy.betterstorage.misc.handlers;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;

import net.mcft.copy.betterstorage.utils.PlayerUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.relauncher.Side;

public class PacketHandler implements IPacketHandler {
	
	public static final int openGui = 0;
	
	@Override
	public void onPacketData(INetworkManager manager, Packet250CustomPayload packet, Player pl) {
		EntityPlayer player = (EntityPlayer)pl;
		Side side = FMLCommonHandler.instance().getEffectiveSide();
		DataInputStream stream = new DataInputStream(new ByteArrayInputStream(packet.data));
		try {
			int id = stream.readByte();
			switch (id) {
				case openGui:
					if (side == Side.SERVER)
						throw new Exception("Received BetterStorage packet for ID " + id + " on invalid side " + side + ".");
					int windowId = stream.readInt();
					String name = stream.readUTF();
					int columns = stream.readByte();
					int rows = stream.readByte();
					String title = stream.readUTF();
					PlayerUtils.openGui(player, name, columns, rows, title);
					player.openContainer.windowId = windowId;
					break;
				default:
					throw new Exception("Received BetterStorage packet for unhandled ID " + id + " on side " + side + ".");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
