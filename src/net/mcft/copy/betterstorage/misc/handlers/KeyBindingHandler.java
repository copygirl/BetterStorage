package net.mcft.copy.betterstorage.misc.handlers;

import java.util.EnumSet;

import net.mcft.copy.betterstorage.Config;
import net.mcft.copy.betterstorage.item.ItemBackpack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import cpw.mods.fml.client.registry.KeyBindingRegistry.KeyHandler;
import cpw.mods.fml.common.TickType;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class KeyBindingHandler extends KeyHandler {
	
	@SideOnly(Side.CLIENT)
	public static boolean serverBackpackKeyEnabled = false;
	
	public KeyBindingHandler() {
		super(new KeyBinding[]{ new KeyBinding("key.backpackOpen", Config.backpackOpenKey) }, new boolean[1]);
	}
	
	@Override
	public EnumSet<TickType> ticks() { return EnumSet.of(TickType.CLIENT); }
	
	@Override
	public String getLabel() { return "betterstorage:KeyHandler"; }
	
	@Override
	public void keyDown(EnumSet<TickType> types, KeyBinding kb, boolean tickEnd, boolean isRepeat) {
		Minecraft mc = Minecraft.getMinecraft();
		if (!tickEnd || !mc.inGameHasFocus || !serverBackpackKeyEnabled) return;
		EntityPlayer player = mc.thePlayer;
		if ((player == null) || (ItemBackpack.getBackpack(player) == null)) return;
		PacketDispatcher.sendPacketToServer(PacketHandler.makePacket(PacketHandler.backpackOpen));
	}
	
	@Override
	public void keyUp(EnumSet<TickType> types, KeyBinding kb, boolean tickEnd) {  }
	
}
