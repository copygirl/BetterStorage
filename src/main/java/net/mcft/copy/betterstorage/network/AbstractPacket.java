package net.mcft.copy.betterstorage.network;

import io.netty.channel.ChannelHandlerContext;

import java.io.IOException;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;

public abstract class AbstractPacket {
	
	public abstract void encode(ChannelHandlerContext context, PacketBuffer buffer) throws IOException;
	
	public abstract void decode(ChannelHandlerContext context, PacketBuffer buffer) throws IOException;
	
	public void handleClientSide(EntityPlayer player) {
		throw new UnsupportedOperationException(
				"Received " + getClass().getSimpleName() + " on invalid side client");
	}
	
	public void handleServerSide(EntityPlayer player) {
		throw new UnsupportedOperationException(
				"Received " + getClass().getSimpleName() + " on invalid side server");
	}
	
}
