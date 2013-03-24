package net.mcft.copy.betterstorage.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.mcft.copy.betterstorage.api.ILockable;
import net.mcft.copy.betterstorage.enchantment.EnchantmentBetterStorage;
import net.mcft.copy.betterstorage.utils.StackUtils;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;
import net.minecraft.world.World;

public class ItemKey extends ItemBetterStorage {
	
	private Icon iconGold, iconIron, iconColor;
	
	public ItemKey(int id) {
		super(id);
		
		// This is needed to make sure the item stays in the crafting
		// matrix when used to craft a lock or duplicate a key.
		setContainerItem(this);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void updateIcons(IconRegister iconRegister) {
		iconGold = iconIndex = iconRegister.registerIcon("betterstorage:key_gold");
		iconIron = iconRegister.registerIcon("betterstorage:key_iron");
		iconColor = iconRegister.registerIcon("betterstorage:key_color");
	}
	
	@Override
	public boolean isItemTool(ItemStack stack) { return true; }
	@Override
	public int getItemEnchantability() { return 20; }
	
	@Override
	public boolean doesContainerItemLeaveCraftingGrid(ItemStack stack) { return false; }
	@Override
	public ItemStack getContainerItemStack(ItemStack stack) { return stack; }
	
	@SideOnly(Side.CLIENT)
	@Override
	public boolean requiresMultipleRenderPasses() { return true; }
	
	@SideOnly(Side.CLIENT)
	@Override
	public int getColorFromItemStack(ItemStack stack, int renderPass) {
		if (renderPass == 0) return 0xFFFFFF;
		return StackUtils.get(stack, 0xFFFFFF, "display", "color");
	}
	
	@Override
	public Icon getIcon(ItemStack stack, int renderPass) {
		boolean colored = StackUtils.has(stack, "display", "color");
		boolean ironPlated = (StackUtils.get(stack, (byte)0, "display", "ironPlated") == 1);
		return ((renderPass > 0 && colored) ? iconColor : (ironPlated ? iconIron : iconGold));
	}
	
	@Override
	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world,
	                         int x, int y, int z, int side, float subX, float subY, float subZ) {
		if (world.isRemote) return false;
		
		TileEntity entity = world.getBlockTileEntity(x, y, z);
		// Return if there is no lockable container.
		if (entity == null || !(entity instanceof ILockable)) return false;
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
	
	public static boolean tryKeyOpenLock(ItemStack lock, ItemStack key) {
		if (!ItemKey.isKey(key)) return false;
		
		int lockId = lock.getItemDamage();
		int keyId  = key.getItemDamage();
		
		int lockSecurity = EnchantmentHelper.getEnchantmentLevel(EnchantmentBetterStorage.security.effectId, lock);
		int unlocking    = EnchantmentHelper.getEnchantmentLevel(EnchantmentBetterStorage.unlocking.effectId, key);
		int lockpicking  = EnchantmentHelper.getEnchantmentLevel(EnchantmentBetterStorage.lockpicking.effectId, key);
		int morphing     = EnchantmentHelper.getEnchantmentLevel(EnchantmentBetterStorage.morphing.effectId, key);
		
		int effectiveUnlocking   = Math.max(0, unlocking - lockSecurity);
		int effectiveLockpicking = Math.max(0, lockpicking - lockSecurity);
		int effectiveMorphing    = Math.max(0, morphing - lockSecurity);
		
		if (lockId == keyId) return true;
		
		if (effectiveUnlocking > 0) {
			int div = (int)Math.pow(2, 9 + effectiveUnlocking * 1.5);
			if (lockId / div == keyId / div) return true;
		}
		if (effectiveLockpicking > 0) {
			NBTTagList list = key.getEnchantmentTagList();
			for (int i = 0; i < list.tagCount(); i++) {
				NBTTagCompound compound = (NBTTagCompound)list.tagAt(i);
				if (compound.getShort("id") != EnchantmentBetterStorage.lockpicking.effectId) continue;
				int level = compound.getShort("lvl") - 1;
				if (level == 0) {
					list.removeTag(i);
					if (list.tagCount() == 0)
						key.getTagCompound().removeTag("ench");
				} else compound.setShort("lvl", (short)level);
				break;
			}
			return true;
		}
		if (effectiveMorphing > 0) {
			key.setItemDamage(lockId);
			NBTTagList list = key.getEnchantmentTagList();
			for (int i = 0; i < list.tagCount(); i++) {
				NBTTagCompound compound = (NBTTagCompound)list.tagAt(i);
				if (compound.getShort("id") != EnchantmentBetterStorage.morphing.effectId) continue;
				list.removeTag(i);
				// Morphed keys keep their enchanted look, it looks sweet.
				// if (list.tagCount() == 0)
				//	key.getTagCompound().removeTag("ench");
			}
			return true;
		}
		
		return false;
	}
	
}
