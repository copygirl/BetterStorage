package net.mcft.copy.betterstorage.api.stand;

import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.mojang.authlib.GameProfile;

@SideOnly(Side.CLIENT)
public class ClientArmorStandPlayer extends AbstractClientPlayer {	
	
	public ClientArmorStandPlayer(World world) {
		super(world, new GameProfile(null, "[ARMOR STAND]"));
		setInvisible(true);
	}
}
