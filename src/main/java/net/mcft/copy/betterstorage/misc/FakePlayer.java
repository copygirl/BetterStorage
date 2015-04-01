package net.mcft.copy.betterstorage.misc;

import net.mcft.copy.betterstorage.utils.WorldUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.World;

import com.mojang.authlib.GameProfile;

public class FakePlayer extends EntityPlayer {
	
	private static final GameProfile profile = new GameProfile(null, "[BETTERSTORAGE]");
	private static FakePlayer player;
	
	
	private FakePlayer(World world) {
		super(world, profile);
	}
	
	@Override
	public void addChatMessage(IChatComponent message) {  }
	
	@Override
	public ChunkCoordinates getPlayerCoordinates() { return null; }
	
	@Override
	public boolean canCommandSenderUseCommand(int permissionLevel, String commandName) { return false; }
	
	
	public static FakePlayer get(World world) {
		if (player == null)
			player = new FakePlayer(world);
		else player.setWorld(world);
		return player;
	}
	public static FakePlayer get(TileEntity entity) {
		return get((entity != null) ? entity.getWorldObj() : WorldUtils.getLocalWorld());
	}
	
	public static void unset() {
		player.setWorld(null);
	}
	
}
