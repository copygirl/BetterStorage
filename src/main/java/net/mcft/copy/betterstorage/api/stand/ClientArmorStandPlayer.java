package net.mcft.copy.betterstorage.api.stand;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.World;

import com.mojang.authlib.GameProfile;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ClientArmorStandPlayer extends AbstractClientPlayer {
	
	public ClientArmorStandPlayer(World world) {
		super(world, new GameProfile(null, "[ARMOR STAND]"));
		setInvisible(true);
	}
	
	@Override
	public void addChatMessage(IChatComponent message) {  }
	
	@Override
	public ChunkCoordinates getPlayerCoordinates() { return null; }
	
	@Override
	public boolean canCommandSenderUseCommand(int permissionLevel, String commandName) { return false; }
	
}
