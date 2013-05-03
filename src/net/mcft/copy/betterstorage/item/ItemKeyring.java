package net.mcft.copy.betterstorage.item;

import net.mcft.copy.betterstorage.api.IKey;
import net.mcft.copy.betterstorage.container.ContainerKeyring;
import net.mcft.copy.betterstorage.utils.PlayerUtils;
import net.mcft.copy.betterstorage.utils.StackUtils;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemKeyring extends ItemBetterStorage implements IKey {
	
	private Icon[] icons = new Icon[4];
	
	public ItemKeyring(int id) {
		super(id);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister iconRegister) {
		for (int i = 0; i < icons.length; i++)
			icons[i] = iconRegister.registerIcon("betterstorage:keyring_" + i);
		itemIcon = icons[0];
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public Icon getIconFromDamage(int damage) {
		return icons[Math.min(damage, icons.length - 1)];
	}
	
	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
		if (world.isRemote || !player.isSneaking()) return stack;
		String title = (stack.hasDisplayName() ? stack.getDisplayName() : "");
		Container container = new ContainerKeyring(player, title);
		int index = player.inventory.currentItem; // protected slot
		PlayerUtils.openGui(player, "container.keyring", index, 1, title, container);
		return stack;
	}
	
	// IKey implementation
	
	@Override
	public boolean isNormalKey() { return false; }
	
	@Override
	public boolean unlock(ItemStack keyring, ItemStack lock, boolean useAbility) {
		
		// Goes through all the keys in the keyring,
		// returns if any of the keys fit in the lock.
		
		ItemStack[] items = StackUtils.getStackContents(keyring, 9);
		for (ItemStack key : items) {
			if (key == null) continue;
			IKey keyType = (IKey)key.getItem();
			if (keyType.unlock(key, lock, false))
				return true;
		}
		
		return false;
		
	}
	
	@Override
	public boolean canApplyEnchantment(ItemStack key, Enchantment enchantment) { return false; }
	
}
