package net.mcft.copy.betterstorage.item.locking;

import net.mcft.copy.betterstorage.BetterStorage;
import net.mcft.copy.betterstorage.api.IContainerItem;
import net.mcft.copy.betterstorage.api.lock.IKey;
import net.mcft.copy.betterstorage.container.ContainerKeyring;
import net.mcft.copy.betterstorage.item.ItemBetterStorage;
import net.mcft.copy.betterstorage.misc.Constants;
import net.mcft.copy.betterstorage.utils.PlayerUtils;
import net.mcft.copy.betterstorage.utils.StackUtils;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemKeyring extends ItemBetterStorage implements IKey, IContainerItem {
	
	public ItemKeyring() {
		super();
		this.setHasSubtypes(true);
		
		//TODO (1.8): Investigate why this is not working.
		BetterStorage.proxy.registerItemRender(this, 1, "keyring_1");
		BetterStorage.proxy.registerItemRender(this, 2, "keyring_2");
		BetterStorage.proxy.registerItemRender(this, 3, "keyring_3");
	}
	
	/*private IIcon[] icons = new IIcon[4];
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister iconRegister) {
		for (int i = 0; i < icons.length; i++)
			icons[i] = iconRegister.registerIcon(Constants.modId + ":keyring_" + i);
		itemIcon = icons[0];
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIconFromDamage(int damage) {
		return icons[Math.min(damage, icons.length - 1)];
	}
	*/
	
	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
		if (world.isRemote || !player.isSneaking()) return stack;
		String title = StackUtils.get(stack, "", "display", "Name");
		int protectedSlot = player.inventory.currentItem;
		Container container = new ContainerKeyring(player, title, protectedSlot);
		PlayerUtils.openGui(player, Constants.containerKeyring, protectedSlot, 1, title, container);
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
	
	// IContainerItem implementation
	
	@Override
	public ItemStack[] getContainerItemContents(ItemStack container) {
		return StackUtils.getStackContents(container, 9);
	}
	
	@Override
	public boolean canBeStoredInContainerItem(ItemStack item) { return true; }

}
