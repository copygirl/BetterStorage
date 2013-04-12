package net.mcft.copy.betterstorage.client.gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.mcft.copy.betterstorage.misc.Constants;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;

@SideOnly(Side.CLIENT)
public class GuiReinforcedChest extends GuiBetterStorage {
	
	public GuiReinforcedChest(EntityPlayer player, int columns, int rows, IInventory inventory) {
		super(player, columns, rows, inventory);
	}
	
	@Override
	protected String getTexture() {
		if (columns <= 9) return "/gui/container.png";
		else return Constants.reinforcedChestContainer;
	}
	
}
