package net.mcft.copy.betterstorage.item;

import net.mcft.copy.betterstorage.api.BetterStorageEnchantment;
import net.mcft.copy.betterstorage.api.ILock;
import net.mcft.copy.betterstorage.api.ILockable;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;

public class ItemLock extends ItemBetterStorage implements ILock {
	
	public ItemLock(int id) {
		super(id);
	}
	
	@Override
	public boolean isItemTool(ItemStack stack) { return true; }
	@Override
	public int getItemEnchantability() { return 20; }
	
	// ILock implementation
	
	@Override
	public String getLockType() { return "normal"; }
	
	@Override
	public void onUnlock(ItemStack lock, ItemStack key, ILockable lockable,
	                     EntityPlayer player, boolean success) {
		if (success) return;
		// Power is 2 when a key was used to open the lock, 1 otherwise.
		int power = ((key != null) ? 2 : 1);
		applyEffects(lock, lockable, player, power);
	}
	
	@Override
	public void applyEffects(ItemStack lock, ILockable lockable, EntityPlayer player, int power) {
		
		int shock   = BetterStorageEnchantment.getLevel(lock, "shock");
		int trigger = BetterStorageEnchantment.getLevel(lock, "trigger");
		
		if (shock > 0) {
			// Damage the player, and sets em on fire if shock is level 3.
			player.attackEntityFrom(DamageSource.magic, shock * Math.min(power, 2) * 5 / 2);
			if (shock >= 3) player.setFire(shock * 2 * power);
		}
		
		if (trigger > 0) lockable.applyTrigger();
		
	}
	
	@Override
	public boolean canApplyEnchantment(ItemStack key, Enchantment enchantment) { return true; }
	
}
