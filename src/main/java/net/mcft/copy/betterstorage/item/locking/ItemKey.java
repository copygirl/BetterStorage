package net.mcft.copy.betterstorage.item.locking;

import net.mcft.copy.betterstorage.api.BetterStorageEnchantment;
import net.mcft.copy.betterstorage.api.lock.IKey;
import net.mcft.copy.betterstorage.api.lock.ILock;
import net.mcft.copy.betterstorage.item.ItemBetterStorage;
import net.mcft.copy.betterstorage.utils.RandomUtils;
import net.mcft.copy.betterstorage.utils.StackUtils;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemKey extends ItemBetterStorage implements IKey {
	
	//private IIcon iconColor, iconFullColor;
	
	public ItemKey() {
		// This is needed to make sure the item stays in the crafting
		// matrix when used to craft a lock or duplicate a key.
		setContainerItem(this);
	}
	
	
	/*
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister iconRegister) {
		super.registerIcons(iconRegister);
		iconColor = iconRegister.registerIcon(Constants.modId + ":key_color");
		iconFullColor = iconRegister.registerIcon(Constants.modId + ":key_fullColor");
	}
	*/

	@Override
	public boolean isDamageable() { return true; }
	@Override
	public int getItemEnchantability() { return 20; }
	
	@Override
	public ItemStack getContainerItem(ItemStack stack) { return stack; }
	
	@Override
	public void onCreated(ItemStack stack, World world, EntityPlayer player) {
		if (!world.isRemote) ensureHasID(stack);
	}
	@Override
	public void onUpdate(ItemStack stack, World world, Entity entity, int slot, boolean isBeingHeld) {
		if (!world.isRemote) ensureHasID(stack);
	}
	
	/*
	@SideOnly(Side.CLIENT)
	@Override
	public boolean requiresMultipleRenderPasses() { return true; }
	*/
	
	@SideOnly(Side.CLIENT)
	@Override
	public int getColorFromItemStack(ItemStack stack, int renderPass) {
		int fullColor = getFullColor(stack);
		if (fullColor < 0) fullColor = 0xFFFFFF;
		if (renderPass > 0) {
			int color = getColor(stack);
			return ((color < 0) ? fullColor : color);
		} else return fullColor;
	}
	
	/*
	@Override
	public IIcon getIcon(ItemStack stack, int renderPass) {
		boolean hasFullColor = (getFullColor(stack) >= 0);
		if ((renderPass > 0) && (getColor(stack) >= 0)) return iconColor;
		return (hasFullColor ? iconFullColor : itemIcon);
	}
	
	/** Gives the key a random ID if it doesn't have one already. */
	public static void ensureHasID(ItemStack stack) {
		if (!StackUtils.has(stack, TAG_KEYLOCK_ID))
			setID(stack, RandomUtils.getInt(1, 32000));
	}
	
	// IKey implementation
	
	@Override
	public boolean isNormalKey() { return true; }
	
	@Override
	public boolean unlock(ItemStack key, ItemStack lock, boolean useAbility) {
		
		ILock lockType = (ILock)lock.getItem();
		// If the lock type isn't normal, the key can't unlock it.
		if (lockType.getLockType() != "normal")
			return false;
		
		int lockId = getID(lock);
		int keyId = getID(key);
		
		// If the lock and key IDs match, return true.
		if (lockId == keyId) return true;
		
		int lockSecurity = BetterStorageEnchantment.getLevel(lock, "security");
		int unlocking    = BetterStorageEnchantment.getLevel(key, "unlocking");
		int lockpicking  = BetterStorageEnchantment.getLevel(key, "lockpicking");
		int morphing     = BetterStorageEnchantment.getLevel(key, "morphing");
		
		int effectiveUnlocking   = Math.max(0, unlocking - lockSecurity);
		int effectiveLockpicking = Math.max(0, lockpicking - lockSecurity);
		int effectiveMorphing    = Math.max(0, morphing - lockSecurity);
		
		if (effectiveUnlocking > 0) {
			int div = (int)Math.pow(2, 10 + effectiveUnlocking * 1);
			if (lockId / div == keyId / div) return true;
		}
		if (useAbility && (effectiveLockpicking > 0)) {
			NBTTagList list = key.getEnchantmentTagList();
			for (int i = 0; i < list.tagCount(); i++) {
				NBTTagCompound compound = list.getCompoundTagAt(i);
				if (compound.getShort("id") != BetterStorageEnchantment.get("lockpicking").effectId) continue;
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
		if (useAbility && (effectiveMorphing > 0)) {
			key.setItemDamage(lockId);
			NBTTagList list = key.getEnchantmentTagList();
			for (int i = 0; i < list.tagCount(); i++) {
				NBTTagCompound compound = list.getCompoundTagAt(i);
				if (compound.getShort("id") != BetterStorageEnchantment.get("morphing").effectId) continue;
				list.removeTag(i);
				// Morphed keys keep their enchanted look, it looks sweet.
				// if (list.tagCount() == 0)
				//	key.getTagCompound().removeTag("ench");
			}
			return true;
		}
		
		return false;
		
	}
	
	@Override
	public boolean canApplyEnchantment(ItemStack key, Enchantment enchantment) { return true; }
	
}
