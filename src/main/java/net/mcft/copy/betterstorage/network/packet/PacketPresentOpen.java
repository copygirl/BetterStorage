package net.mcft.copy.betterstorage.network.packet;

import java.io.IOException;

import net.mcft.copy.betterstorage.network.AbstractPacket;
import net.mcft.copy.betterstorage.tile.entity.TileEntityPresent;
import net.mcft.copy.betterstorage.utils.WorldUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

/** Spawns the particles caused from opening a present. */
public class PacketPresentOpen extends AbstractPacket<PacketPresentOpen> {
	
	public BlockPos pos;
	public boolean destroy;
	
	public PacketPresentOpen() {  }
	public PacketPresentOpen(BlockPos pos, boolean destroy) {
		this.pos = pos;
		this.destroy = destroy;
	}
	
	@Override
	public void encode(PacketBuffer buffer) throws IOException {
		buffer.writeBlockPos(pos);
		buffer.writeBoolean(destroy);
	}
	
	@Override
	public void decode(PacketBuffer buffer) throws IOException {
		pos = buffer.readBlockPos();
		destroy = buffer.readBoolean();
	}
	
	@Override
	public void handle(EntityPlayer player) {
		TileEntityPresent present = WorldUtils.get(player.worldObj, pos, TileEntityPresent.class);
		EffectRenderer effRender = Minecraft.getMinecraft().effectRenderer;
		if (present != null)
			for (int side = 0; side < 6; side++)
				for (int i = 0; i < (destroy ? 2 : 20); i++)
					if (destroy)
						effRender.func_180533_a(pos, player.worldObj.getBlockState(pos)); //TODO (1.8): Not sure if addBlockDestroyEffects
					else effRender.func_180532_a(pos, EnumFacing.getFront(side)); //addBlockHitEffects
	}
	
}
