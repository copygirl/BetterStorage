package net.mcft.copy.betterstorage.network.packet;

import java.io.IOException;

import net.mcft.copy.betterstorage.network.AbstractPacket;
import net.mcft.copy.betterstorage.tile.entity.TileEntityPresent;
import net.mcft.copy.betterstorage.utils.WorldUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;

/** Spawns the particles caused from opening a present. */
public class PacketPresentOpen extends AbstractPacket<PacketPresentOpen> {
	
	public int x, y, z;
	public boolean destroy;
	
	public PacketPresentOpen() {  }
	public PacketPresentOpen(int x, int y, int z, boolean destroy) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.destroy = destroy;
	}
	
	@Override
	public void encode(PacketBuffer buffer) throws IOException {
		buffer.writeInt(x);
		buffer.writeInt(y);
		buffer.writeInt(z);
		buffer.writeBoolean(destroy);
	}
	
	@Override
	public void decode(PacketBuffer buffer) throws IOException {
		x = buffer.readInt();
		y = buffer.readInt();
		z = buffer.readInt();
		destroy = buffer.readBoolean();
	}
	
	@Override
	public void handle(EntityPlayer player) {
		TileEntityPresent present = WorldUtils.get(player.worldObj, x, y, z, TileEntityPresent.class);
		EffectRenderer effRender = Minecraft.getMinecraft().effectRenderer;
		if (present != null)
			for (int side = 0; side < 6; side++)
				for (int i = 0; i < (destroy ? 2 : 20); i++)
					if (destroy)
						effRender.addBlockDestroyEffects(x, y, z,
								present.getBlockType(), present.getBlockMetadata());
					else effRender.addBlockHitEffects(x, y, z, side);
	}
	
}
