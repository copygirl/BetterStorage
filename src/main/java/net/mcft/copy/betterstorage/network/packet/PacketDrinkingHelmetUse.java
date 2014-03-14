package net.mcft.copy.betterstorage.network.packet;

import io.netty.channel.ChannelHandlerContext;

import java.io.IOException;

import net.mcft.copy.betterstorage.item.ItemDrinkingHelmet;
import net.mcft.copy.betterstorage.network.AbstractPacket;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;

/** Sent when the player presses the button to use eir drinking helmet. */
public class PacketDrinkingHelmetUse extends AbstractPacket {
	
	public PacketDrinkingHelmetUse() {  }
	
	@Override
	public void encode(ChannelHandlerContext context, PacketBuffer buffer) throws IOException {
		// No additional data.
	}
	
	@Override
	public void decode(ChannelHandlerContext context, PacketBuffer buffer) throws IOException {
		// No additional data.
	}
	
	@Override
	public void handleServerSide(EntityPlayer player) {
		ItemDrinkingHelmet.use(player);
	}
	
}
