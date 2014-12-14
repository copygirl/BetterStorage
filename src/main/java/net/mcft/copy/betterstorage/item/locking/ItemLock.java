package net.mcft.copy.betterstorage.item.locking;

import net.mcft.copy.betterstorage.api.BetterStorageEnchantment;
import net.mcft.copy.betterstorage.api.lock.EnumLockInteraction;
import net.mcft.copy.betterstorage.api.lock.ILock;
import net.mcft.copy.betterstorage.api.lock.ILockable;
import net.mcft.copy.betterstorage.item.ItemBetterStorage;
import net.mcft.copy.betterstorage.utils.RandomUtils;
import net.mcft.copy.betterstorage.utils.StackUtils;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemLock extends ItemBetterStorage implements ILock {
	
	//private IIcon iconColor, iconFullColor;
	
	public ItemLock() {
		setMaxDamage(64);
	}
	
	/*
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister iconRegister) {
		super.registerIcons(iconRegister);
		iconColor = iconRegister.registerIcon(Constants.modId + ":lock_color");
		iconFullColor = iconRegister.registerIcon(Constants.modId + ":lock_fullColor");
	}
	*/
	
	@Override
	public boolean isRepairable() { return true; }
	@Override
	public boolean getIsRepairable(ItemStack stack, ItemStack material) {
		return (material.getItem() == Items.gold_ingot);
	}
	
	@Override
	public boolean isDamageable() { return true; }
	@Override
	public int getItemEnchantability() { return 20; }
	
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
	*/
	
	/** Gives the lock a random ID if it doesn't have one. <br>
	 *  This is usually only when the lock is taken out of creative. */
	public static void ensureHasID(ItemStack stack) {
		if (!StackUtils.has(stack, TAG_KEYLOCK_ID))
			setID(stack, RandomUtils.getInt(1, 32000));
	}
	
	// ILock implementation
	
	@Override
	public String getLockType() { return "normal"; }
	
	@Override
	public void onUnlock(ItemStack lock, ItemStack key, ILockable lockable,
	                     EntityPlayer player, boolean success) {
		if (success) return;
		// Power is 2 when a key was used to open the lock, 1 otherwise.
		EnumLockInteraction interaction = ((key != null) ? EnumLockInteraction.PICK : EnumLockInteraction.OPEN);
		applyEffects(lock, lockable, player, interaction);
	}
	
	@Override
	public void applyEffects(ItemStack lock, ILockable lockable,
	                         EntityPlayer player, EnumLockInteraction interaction) {
		
		int shock   = BetterStorageEnchantment.getLevel(lock, "shock");
		int trigger = BetterStorageEnchantment.getLevel(lock, "trigger");
		
		if (shock > 0) {
			boolean open = (interaction == EnumLockInteraction.OPEN);
			boolean pick = (interaction == EnumLockInteraction.PICK);
			int damage = shock;
			if (pick) damage *= 3;
			player.attackEntityFrom(DamageSource.magic, damage);
			if ((shock >= 3) && !open) player.setFire(3);
		}
		
		if (trigger > 0) lockable.applyTrigger();
		
	}
	
	@Override
	public boolean canApplyEnchantment(ItemStack key, Enchantment enchantment) { return true; }
	
}
