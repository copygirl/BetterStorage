package net.mcft.copy.betterstorage.item;

import net.mcft.copy.betterstorage.BetterStorage;
import net.mcft.copy.betterstorage.misc.Constants;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemKeyring extends ItemBetterStorage {
	
	private Icon[] icons = new Icon[4];
	
	public ItemKeyring(int id) {
		super(id);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void updateIcons(IconRegister iconRegister) {
		for (int i = 0; i < icons.length; i++)
			icons[i] = iconRegister.registerIcon("betterstorage:keyring_" + i);
		iconIndex = icons[0];
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public Icon getIconFromDamage(int damage) {
		return icons[Math.min(damage, icons.length - 1)];
	}
	
	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
		if (world.isRemote || !player.isSneaking()) return stack;
		player.openGui(BetterStorage.instance, Constants.keyringGuiId,
		               player.worldObj, (int)player.posX, (int)player.posY, (int)player.posZ);
		return stack;
	}
	
}
