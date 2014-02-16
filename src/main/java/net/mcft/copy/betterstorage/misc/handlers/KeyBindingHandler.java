package net.mcft.copy.betterstorage.misc.handlers;

import java.util.EnumSet;

import net.mcft.copy.betterstorage.BetterStorage;
import net.mcft.copy.betterstorage.config.GlobalConfig;
import net.mcft.copy.betterstorage.item.ItemBackpack;
import net.mcft.copy.betterstorage.misc.Constants;
import net.mcft.copy.betterstorage.misc.CurrentItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;

import org.lwjgl.input.Keyboard;

import cpw.mods.fml.client.registry.KeyBindingRegistry.KeyHandler;
import cpw.mods.fml.common.TickType;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class KeyBindingHandler extends KeyHandler {
	
	public static final KeyBinding backpackOpen = new KeyBinding("key.betterstorage.backpackOpen", Keyboard.KEY_B);
	public static final KeyBinding drinkingHelmet = new KeyBinding("key.betterstorage.drinkingHelmet", Keyboard.KEY_F);
	
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
		if ((kb == backpackOpen) && (ItemBackpack.getBackpack(player) != null) &&
		    BetterStorage.globalConfig.getBoolean(GlobalConfig.enableBackpackOpen))
			PacketDispatcher.sendPacketToServer(PacketHandler.makePacket(PacketHandler.backpackOpen));
		else if ((kb == drinkingHelmet) && (player.getCurrentItemOrArmor(CurrentItem.HEAD) != null))
			PacketDispatcher.sendPacketToServer(PacketHandler.makePacket(PacketHandler.drinkingHelmet));
	}
	
	@Override
	public void keyUp(EnumSet<TickType> types, KeyBinding kb, boolean tickEnd) {  }
	
}
