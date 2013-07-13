package net.mcft.copy.betterstorage.utils;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet250CustomPayload;

public class PacketUtils {
	
	public static void sendPacket(EntityPlayer player, Packet packet) {
		((EntityPlayerMP)player).playerNetServerHandler.sendPacketToPlayer(packet);
	}
	public static void sendPacket(EntityPlayer player, Object... args) {
		sendPacket(player, makePacket(args));
	}
	
	public static Packet makePacket(Object... args) {
		ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
		DataOutputStream dataStream = new DataOutputStream(byteStream);
		for (Object obj : args) writeObject(dataStream, obj);
		return new Packet250CustomPayload("BetterStorage", byteStream.toByteArray());
	}
	
	private static void writeObject(DataOutputStream stream, Object object) {
		try {
			if (object instanceof Byte) stream.writeByte((Byte)object);
			else if (object instanceof Short) stream.writeShort((Short)object);
			else if (object instanceof Integer) stream.writeInt((Integer)object);
			else if (object instanceof Float) stream.writeFloat((Float)object);
			else if (object instanceof Double) stream.writeDouble((Double)object);
			else if (object instanceof String) stream.writeUTF((String)object);
			else if (object instanceof Boolean) stream.writeBoolean((Boolean)object);
			else if (object instanceof byte[]) stream.write((byte[])object);
		} catch (Exception e) { throw new RuntimeException(e);}
	}
	
}
