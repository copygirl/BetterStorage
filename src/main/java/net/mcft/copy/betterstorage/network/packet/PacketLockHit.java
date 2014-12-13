package net.mcft.copy.betterstorage.network.packet;

import java.io.IOException;

import net.mcft.copy.betterstorage.BetterStorage;
import net.mcft.copy.betterstorage.config.GlobalConfig;
import net.mcft.copy.betterstorage.network.AbstractPacket;
import net.mcft.copy.betterstorage.tile.entity.TileEntityLockable;
import net.mcft.copy.betterstorage.utils.WorldUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.BlockPos;

/** Lets other clients know about a lock being hit. */
public class PacketLockHit extends AbstractPacket<PacketLockHit> {
	
	public BlockPos pos;
	public boolean damage;
	
	public PacketLockHit() {  }
	public PacketLockHit(BlockPos pos, boolean damage) {
		this.pos = pos;
		this.damage = damage;
	}
	
	@Override
	public void encode(PacketBuffer buffer) throws IOException {
		buffer.writeBlockPos(pos);
		buffer.writeBoolean(damage);
	}
	
	@Override
	public void decode(PacketBuffer buffer) throws IOException {
		pos = buffer.readBlockPos();
		damage = buffer.readBoolean();
	}
	
	@Override
	public void handle(EntityPlayer player) {
		TileEntityLockable lockable = WorldUtils.get(player.worldObj, pos, TileEntityLockable.class);
		damage &= BetterStorage.globalConfig.getBoolean(GlobalConfig.lockBreakable);
		if (lockable != null)
			lockable.lockAttachment.hit(damage);
	}
	
}
