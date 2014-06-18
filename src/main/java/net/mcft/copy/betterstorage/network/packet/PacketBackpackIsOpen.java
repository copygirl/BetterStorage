package net.mcft.copy.betterstorage.network.packet;

import java.io.IOException;

import net.mcft.copy.betterstorage.item.ItemBackpack;
import net.mcft.copy.betterstorage.network.AbstractPacket;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;

/** Updates players about the open status of equipped backpacks. */
public class PacketBackpackIsOpen extends AbstractPacket<PacketBackpackIsOpen> {
	
	public int entityID;
	public boolean isOpen;
	
	public PacketBackpackIsOpen() {  }
	public PacketBackpackIsOpen(int entityID, boolean isOpen) {
		this.entityID = entityID;
		this.isOpen = isOpen;
	}
	
	@Override
	public void encode(PacketBuffer buffer) throws IOException {
		buffer.writeInt(entityID);
		buffer.writeBoolean(isOpen);
	}
	
	@Override
	public void decode( PacketBuffer buffer) throws IOException {
		entityID = buffer.readInt();
		isOpen = buffer.readBoolean();
	}
	
	@Override
	public void handle(EntityPlayer player) {
		Entity entity = player.worldObj.getEntityByID(entityID);
		if ((entity != null) && (entity instanceof EntityLivingBase))
			ItemBackpack.getBackpackData((EntityLivingBase)entity).playersUsing = ((isOpen) ? 1 : 0);
	}
	
}
