package net.mcft.copy.betterstorage.network.packet;

import java.io.IOException;

import net.mcft.copy.betterstorage.item.ItemBackpack;
import net.mcft.copy.betterstorage.network.AbstractPacket;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;

/** Updates players about the stack used for "hidden" equipped backpacks.
 *  "Hidden" meaning it's not taking up the chestplate slot. */
public class PacketBackpackStack extends AbstractPacket<PacketBackpackStack> {
	
	public int entityID;
	public ItemStack stack;
	
	public PacketBackpackStack() {  }
	public PacketBackpackStack(int entityID, ItemStack stack) {
		this.entityID = entityID;
		this.stack = stack;
	}
	
	@Override
	public void encode(PacketBuffer buffer) throws IOException {
		buffer.writeInt(entityID);
		buffer.writeItemStackToBuffer(stack);
	}
	
	@Override
	public void decode(PacketBuffer buffer) throws IOException {
		entityID = buffer.readInt();
		stack = buffer.readItemStackFromBuffer();
	}
	
	@Override
	public void handle(EntityPlayer player) {
		Entity entity = player.worldObj.getEntityByID(entityID);
		if ((entity != null) && (entity instanceof EntityLivingBase))
			ItemBackpack.getBackpackData((EntityLivingBase)entity).backpack = stack;
	}
	
}
