package net.mcft.copy.betterstorage.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.util.EnumMap;
import java.util.List;

import net.mcft.copy.betterstorage.BetterStorage;
import net.mcft.copy.betterstorage.network.packet.PacketBackpackTeleport;
import net.mcft.copy.betterstorage.network.packet.PacketOpenGui;
import net.mcft.copy.betterstorage.utils.PlayerUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetHandler;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.World;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.FMLEmbeddedChannel;
import cpw.mods.fml.common.network.FMLIndexedMessageToMessageCodec;
import cpw.mods.fml.common.network.FMLOutboundHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.relauncher.Side;

@ChannelHandler.Sharable
public class ChannelHandler extends FMLIndexedMessageToMessageCodec<AbstractPacket> {
	
	private EnumMap<Side, FMLEmbeddedChannel> channels;
	
	public ChannelHandler() {
		addDiscriminator(0, PacketOpenGui.class);
		addDiscriminator(1, PacketBackpackTeleport.class);
	}
	
	@Override
	public void encodeInto(ChannelHandlerContext context, AbstractPacket packet, ByteBuf target) throws Exception {
		packet.encode(context, new PacketBuffer(target));
	}
	
	@Override
	public void decodeInto(ChannelHandlerContext context, ByteBuf source, AbstractPacket packet) {
		try {
			packet.decode(context, new PacketBuffer(source));
		} catch (Exception e) {
			BetterStorage.log.warn("Error decoding packet: %s", e);
			return;
		}
		if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT) {
			INetHandler netHandler = context.channel().attr(NetworkRegistry.NET_HANDLER).get();
			EntityPlayer player = ((NetHandlerPlayServer)netHandler).playerEntity;
			packet.handleServerSide(player);
		} else packet.handleClientSide(PlayerUtils.getLocalPlayer());
	}
	
	// Sending packets
	
	/** Sends a packet to the server. */
	public void sendToServer(AbstractPacket packet) {
		FMLEmbeddedChannel channel = channels.get(Side.CLIENT);
		channel.attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.TOSERVER);
		channel.writeAndFlush(channel);
	}
	
	/** Sends a packet to a player. */
	public void sendToPlayer(EntityPlayer player, AbstractPacket packet) {
		FMLEmbeddedChannel channel = channels.get(Side.SERVER);
		channel.attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.PLAYER);
		channel.attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(player);
		channel.writeAndFlush(packet);
	}
	
	/** Sends a packet to everyone near a certain position in the world. */
	public void sendToEveryoneNear(World world, double x, double y, double z,
	                               double distance, AbstractPacket packet) {
		FMLEmbeddedChannel channel = channels.get(Side.SERVER);
		channel.attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.ALLAROUNDPOINT);
		channel.attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(new NetworkRegistry.TargetPoint(world.provider.dimensionId, x, y, z, distance));
		channel.writeAndFlush(packet);
	}
	
	/** Sends a packet to everyone near this entity. */
	public void sendToEveryoneNear(Entity entity, AbstractPacket packet) {
		// TODO: In the best case, there should be a sendToEveryoneTracking.
		// At the moment this is only possible using vanilla packets or through private fields.
		sendToEveryoneNear(entity.worldObj, entity.posX, entity.posY, entity.posZ, 128, packet);
	}
	
	/** Sends a packet to everyone near a certain position in the world except for one player. */
	public void sendToEveryoneNear(World world, double x, double y, double z, double distance,
	                               EntityPlayer except, AbstractPacket packet) {
		for (EntityPlayer player : (List<EntityPlayer>)world.playerEntities) {
			double dx = x - player.posX;
			double dy = y - player.posY;
			double dz = z - player.posZ;
            if ((dx * dx + dy * dy + dz * dz) < (distance * distance))
            	sendToPlayer(player, packet);
            	
		}
	}
	
}
