package net.mcft.copy.betterstorage.misc.handlers;

import java.util.EnumSet;

import net.mcft.copy.betterstorage.Config;
import net.mcft.copy.betterstorage.item.block.ItemBackpack;
import net.mcft.copy.betterstorage.misc.Constants;
import net.mcft.copy.betterstorage.misc.CurrentItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import cpw.mods.fml.client.registry.KeyBindingRegistry.KeyHandler;
import cpw.mods.fml.common.TickType;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class KeyBindingHandler extends KeyHandler {
	
	private static final KeyBinding backpackOpen = new KeyBinding("key.backpackOpen", Config.backpackOpenKey);
	private static final KeyBinding drinkingHelmet = new KeyBinding("key.drinkingHelmet", Config.drinkingHelmetKey);
	
	private static final KeyBinding[] bindings = new KeyBinding[]{ backpackOpen, drinkingHelmet };
	
	public static boolean serverBackpackKeyEnabled = false;
	
	public KeyBindingHandler() { super(bindings, new boolean[bindings.length]); }
	
	@Override
	public EnumSet<TickType> ticks() { return EnumSet.of(TickType.CLIENT); }
	
	@Override
	public String getLabel() { return Constants.modName + ".KeyHandler"; }
	
	@Override
	public void keyDown(EnumSet<TickType> types, KeyBinding kb, boolean tickEnd, boolean isRepeat) {
		Minecraft mc = Minecraft.getMinecraft();
		EntityPlayer player = mc.thePlayer;
		if (!tickEnd || !mc.inGameHasFocus || (player == null)) return;
		if ((kb == backpackOpen) && serverBackpackKeyEnabled && (ItemBackpack.getBackpack(player) != null))
			PacketDispatcher.sendPacketToServer(PacketHandler.makePacket(PacketHandler.backpackOpen));
		else if ((kb == drinkingHelmet) && (player.getCurrentItemOrArmor(CurrentItem.HEAD) != null))
			PacketDispatcher.sendPacketToServer(PacketHandler.makePacket(PacketHandler.drinkingHelmet));
	}
	
	@Override
	public void keyUp(EnumSet<TickType> types, KeyBinding kb, boolean tickEnd) {  }
	
}
