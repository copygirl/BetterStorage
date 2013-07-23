package net.mcft.copy.betterstorage.item;

import net.mcft.copy.betterstorage.api.BetterStorageEnchantment;
import net.mcft.copy.betterstorage.api.IKey;
import net.mcft.copy.betterstorage.api.ILock;
import net.mcft.copy.betterstorage.misc.Constants;
import net.mcft.copy.betterstorage.utils.RandomUtils;
import net.mcft.copy.betterstorage.utils.StackUtils;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemKey extends ItemBetterStorage implements IKey {
	
	private Icon iconColor, iconFullColor;
	
	public ItemKey(int id) {
		super(id);
		
		// This is needed to make sure the item stays in the crafting
		// matrix when used to craft a lock or duplicate a key.
		setContainerItem(this);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister iconRegister) {
		super.registerIcons(iconRegister);
		iconColor = iconRegister.registerIcon(Constants.modName + ":key_color");
		iconFullColor = iconRegister.registerIcon(Constants.modName + ":key_fullColor");
	}
	
	@Override
	public boolean isItemTool(ItemStack stack) { return true; }
	@Override
	public int getItemEnchantability() { return 20; }
	
	@Override
	public boolean doesContainerItemLeaveCraftingGrid(ItemStack stack) { return false; }
	@Override
	public ItemStack getContainerItemStack(ItemStack stack) { return stack; }
	
	@Override
	public void onCreated(ItemStack stack, World world, EntityPlayer player) {
		if (!world.isRemote) ensureHasID(stack);
	}
	@Override
	public void onUpdate(ItemStack stack, World world, Entity entity, int slot, boolean isBeingHeld) {
		if (!world.isRemote) ensureHasID(stack);
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public boolean requiresMultipleRenderPasses() { return true; }
	
	@SideOnly(Side.CLIENT)
	@Override
	public int getColorFromItemStack(ItemStack stack, int renderPass) {
		boolean fullColor = (StackUtils.get(stack, (byte)0, "fullColor") == 1);
		return (((renderPass > 0) || fullColor) ? StackUtils.get(stack, 0xFFFFFF, "color") : 0xFFFFFF);
	}
	
	@Override
	public Icon getIcon(ItemStack stack, int renderPass) {
		boolean colored = StackUtils.has(stack, "color");
		boolean fullColor = (StackUtils.get(stack, (byte)0, "fullColor") == 1);
		return (((renderPass > 0) && colored) ? iconColor : (fullColor ? iconFullColor : itemIcon));
	}
	
	/** Gives the key a random ID if it doesn't have one already. */
	public static void ensureHasID(ItemStack stack) {
		if (!StackUtils.has(stack, "id"))
			StackUtils.set(stack, RandomUtils.getInt(1, 32000), "id");
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
		
		int lockId = StackUtils.get(lock, 0, "id");
		int keyId = StackUtils.get(key, 0, "id");
		
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
				NBTTagCompound compound = (NBTTagCompound)list.tagAt(i);
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
				NBTTagCompound compound = (NBTTagCompound)list.tagAt(i);
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
