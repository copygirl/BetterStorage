package net.mcft.copy.betterstorage.network;

import java.util.List;

import net.mcft.copy.betterstorage.misc.Constants;
import net.mcft.copy.betterstorage.network.packet.PacketBackpackHasItems;
import net.mcft.copy.betterstorage.network.packet.PacketBackpackIsOpen;
import net.mcft.copy.betterstorage.network.packet.PacketBackpackOpen;
import net.mcft.copy.betterstorage.network.packet.PacketBackpackStack;
import net.mcft.copy.betterstorage.network.packet.PacketBackpackTeleport;
import net.mcft.copy.betterstorage.network.packet.PacketClientSpawn;
import net.mcft.copy.betterstorage.network.packet.PacketDrinkingHelmetUse;
import net.mcft.copy.betterstorage.network.packet.PacketLockHit;
import net.mcft.copy.betterstorage.network.packet.PacketOpenGui;
import net.mcft.copy.betterstorage.network.packet.PacketSyncSetting;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;

public class ChannelHandler extends SimpleNetworkWrapper {
	
	public ChannelHandler() {
		super(Constants.modId);
		register(0, Side.CLIENT, PacketOpenGui.class);
		register(1, Side.CLIENT, PacketBackpackTeleport.class);
		register(2, Side.CLIENT, PacketBackpackHasItems.class);
		register(3, Side.CLIENT, PacketBackpackIsOpen.class);
		register(4, Side.SERVER, PacketBackpackOpen.class);
		register(5, Side.CLIENT, PacketBackpackStack.class);
		register(6, Side.SERVER, PacketClientSpawn.class);
		register(7, Side.SERVER, PacketDrinkingHelmetUse.class);
		register(8, Side.SERVER, PacketLockHit.class);
		register(9, Side.CLIENT, PacketSyncSetting.class);
	}
	
	public <T extends IMessage & IMessageHandler<T, IMessage>> void register(int id, Side receivingSide, Class<T> messageClass) {
		registerMessage(messageClass, messageClass, id, receivingSide);
	}
	
	// Sending packets
	
	public void sendTo(IMessage message, EntityPlayer player) {
		sendTo(message, (EntityPlayerMP)player);
	}
	
	public void sendToAllAround(IMessage message, World world, double x, double y, double z, double distance) {
		sendToAllAround(message, new NetworkRegistry.TargetPoint(world.provider.dimensionId, x, y, z, distance));
	}
	
	public void sendToAllAround(IMessage message, World world, double x, double y, double z,
	                            double distance, EntityPlayer except) {
		for (EntityPlayer player : (List<EntityPlayer>)world.playerEntities) {
			if (player == except) continue;
			double dx = x - player.posX;
			double dy = y - player.posY;
			double dz = z - player.posZ;
            if ((dx * dx + dy * dy + dz * dz) < (distance * distance))
            	sendTo(message, player);
		}
	}
	
	public void sendToAllTracking(IMessage message, Entity entity) {
		((WorldServer)entity.worldObj).getEntityTracker().func_151247_a(entity, getPacketFrom(message));
	}
	
}
