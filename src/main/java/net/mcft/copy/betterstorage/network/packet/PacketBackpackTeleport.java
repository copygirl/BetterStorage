package net.mcft.copy.betterstorage.network.packet;

import java.io.IOException;

import net.mcft.copy.betterstorage.network.AbstractPacket;
import net.mcft.copy.betterstorage.utils.RandomUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.EnumParticleTypes;

/** Spawns the particles caused from ender backpacks being teleported. */
public class PacketBackpackTeleport extends AbstractPacket<PacketBackpackTeleport> {
	
	public double sourceX, sourceY, sourceZ;
	public int    targetX, targetY, targetZ;
	
	public PacketBackpackTeleport() {  }
	public PacketBackpackTeleport(double sourceX, double sourceY, double sourceZ,
	                              int    targetX, int    targetY, int    targetZ) {
		this.sourceX = sourceX;
		this.sourceY = sourceY;
		this.sourceZ = sourceZ;
		this.targetX = targetX;
		this.targetY = targetY;
		this.targetZ = targetZ;
	}
	
	@Override
	public void encode(PacketBuffer buffer) throws IOException {
		buffer.writeDouble(sourceX);
		buffer.writeDouble(sourceY);
		buffer.writeDouble(sourceZ);
		buffer.writeInt(targetX);
		buffer.writeInt(targetY);
		buffer.writeInt(targetZ);
	}
	
	@Override
	public void decode(PacketBuffer buffer) throws IOException {
		sourceX = buffer.readDouble();
		sourceY = buffer.readDouble();
		sourceZ = buffer.readDouble();
		targetX = buffer.readInt();
		targetY = buffer.readInt();
		targetZ = buffer.readInt();
	}
	
	@Override
	public void handle(EntityPlayer player) {
		int amount = 128;
		for (int i = 0; i < amount; i++) {
			double a = i / (double)(amount - 1);
			double vX = RandomUtils.getDouble(-0.3, 0.3);
			double vY = RandomUtils.getDouble(-0.3, 0.3);
			double vZ = RandomUtils.getDouble(-0.3, 0.3);
			double pX = sourceX + (targetX - sourceX) * a + RandomUtils.getDouble(0.3, 0.7);
			double pY = sourceY + (targetY - sourceY) * a + RandomUtils.getDouble(-0.5, 0.0) + a / 2;
			double pZ = sourceZ + (targetZ - sourceZ) * a + RandomUtils.getDouble(0.3, 0.7);
			player.worldObj.spawnParticle(EnumParticleTypes.PORTAL, pX, pY, pZ, vX, vY, vZ);
		}
	}
	
}
