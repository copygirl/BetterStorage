package net.mcft.copy.betterstorage.network.packet;

import io.netty.channel.ChannelHandlerContext;

import java.io.IOException;

import net.mcft.copy.betterstorage.BetterStorage;
import net.mcft.copy.betterstorage.config.GlobalConfig;
import net.mcft.copy.betterstorage.item.ItemBackpack;
import net.mcft.copy.betterstorage.network.AbstractPacket;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;

/** Sent when the player presses the button to open eir backpack. */
public class PacketBackpackOpen extends AbstractPacket {
	
	public PacketBackpackOpen() {  }
	
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
		if (BetterStorage.globalConfig.getBoolean(GlobalConfig.enableBackpackOpen))
			ItemBackpack.openBackpack(player, player);
	}
	
}
