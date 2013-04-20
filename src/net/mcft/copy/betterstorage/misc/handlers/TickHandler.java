package net.mcft.copy.betterstorage.misc.handlers;

import java.util.EnumSet;
import java.util.List;

import net.mcft.copy.betterstorage.item.ItemBackpack;
import net.mcft.copy.betterstorage.misc.PropertiesHiddenCloak;
import net.mcft.copy.betterstorage.utils.PlayerUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;

public class TickHandler implements ITickHandler {
	
	private final EnumSet<TickType> ticks = EnumSet.of(TickType.RENDER);
	
	@Override
	public String getLabel() { return "BetterStorage"; }
	
	@Override
	public EnumSet<TickType> ticks() { return ticks; }
	
	@Override
	public void tickStart(EnumSet<TickType> type, Object... tickData) {
		World world = Minecraft.getMinecraft().theWorld;
		if (world == null) return;
		for (EntityPlayer player : (List<EntityPlayer>)world.playerEntities) {
			ItemStack chestArmor = player.getCurrentArmor(2);
			boolean hasBackpack = ((chestArmor != null) && (chestArmor.getItem() instanceof ItemBackpack));
			// If player has a cloak and a backpack, hide
			// the cloak, and save it to restore later.
			if (hasBackpack && (player.cloakUrl != null)) {
				PropertiesHiddenCloak hiddenCloak = PlayerUtils.getOrCreateProperties(player, PropertiesHiddenCloak.class);
				hiddenCloak.cloakUrl = player.cloakUrl;
				player.cloakUrl = null;
			// If the player has no backpack and no cloak,
			// see if there's one saved away and restore it.
			} else if (!hasBackpack && (player.cloakUrl == null)) {
				PropertiesHiddenCloak hiddenCloak = PlayerUtils.getProperties(player, PropertiesHiddenCloak.class);
				if ((hiddenCloak != null) && (hiddenCloak.cloakUrl != null)) {
					player.cloakUrl = hiddenCloak.cloakUrl;
					hiddenCloak.cloakUrl = null;
				}
			}
		}
	}
	
	@Override
	public void tickEnd(EnumSet<TickType> type, Object... tickData) {  }
	
}
