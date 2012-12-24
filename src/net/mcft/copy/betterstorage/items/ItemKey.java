package net.mcft.copy.betterstorage.items;

import cpw.mods.fml.common.registry.LanguageRegistry;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class ItemKey extends ItemBetterStorage {
	
	public ItemKey(int id) {
		super(id);
		setIconCoord(0, 0);
		
		setItemName("key");
		LanguageRegistry.addName(this, "Key");
		
		setCreativeTab(CreativeTabs.tabMisc);
		// This is needed to make sure the item stays in
		// the crafting matrix when used to craft a lock.
		this.setContainerItem(this);
	}
	
	@Override
	public boolean doesContainerItemLeaveCraftingGrid(ItemStack stack) { return false; }
	@Override
	public ItemStack getContainerItemStack(ItemStack stack) { return stack; }
	
	@Override
	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world,
	                         int x, int y, int z, int side, float subX, float subY, float subZ) {
		if (world.isRemote) return false;
		
		TileEntity entity = world.getBlockTileEntity(x, y, z);
		// Return if there is no lockable container.
		if (entity == null && !(entity instanceof ILockable)) return false;
		ILockable lockable = (ILockable)entity;
		// Return if the container isn't locked.
		if (!lockable.isLocked()) return false;
		ItemStack lock = lockable.getLock();
		// Try to unlock the container, return if unsuccessful.
		if (!ItemLock.lockTryUnlock(lock, player, player.getHeldItem())) return false;
		
		// Remove and drop the lock on the player.
		lockable.setLock(null);
		EntityItem item = player.dropPlayerItem(lock);
		item.delayBeforeCanPickup = 0;
		
		return true;
	}
	
	public static boolean isKey(ItemStack stack) {
		return (stack != null && stack.getItem() instanceof ItemKey);
	}
	
}
