package net.mcft.copy.betterstorage.network.packet;

import io.netty.channel.ChannelHandlerContext;

import java.io.IOException;

import net.mcft.copy.betterstorage.item.ItemBackpack;
import net.mcft.copy.betterstorage.network.AbstractPacket;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;

/** Sent by the client when an entity spawns for them.
 *  Causes backpack information to be sent to that client. */
public class PacketClientSpawn extends AbstractPacket {
	
	public int entityID;
	
	public PacketClientSpawn() {  }
	public PacketClientSpawn(int entityID) {
		this.entityID = entityID;
	}
	
	@Override
	public void encode(ChannelHandlerContext context, PacketBuffer buffer) throws IOException {
		buffer.writeInt(entityID);
	}
	
	@Override
	public void decode(ChannelHandlerContext context, PacketBuffer buffer) throws IOException {
		entityID = buffer.readInt();
	}
	
	@Override
	public void handleServerSide(EntityPlayer player) {
		Entity entity = player.worldObj.getEntityByID(entityID);
		if ((entity != null) && (entity instanceof EntityLivingBase))
			ItemBackpack.getBackpackData((EntityLivingBase)entity).sendDataToPlayer((EntityLivingBase)entity, player);
	}
	
}
