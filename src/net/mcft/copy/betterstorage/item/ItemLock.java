package net.mcft.copy.betterstorage.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.mcft.copy.betterstorage.api.BetterStorageEnchantment;
import net.mcft.copy.betterstorage.api.ILock;
import net.mcft.copy.betterstorage.api.ILockable;
import net.mcft.copy.betterstorage.utils.WorldUtils;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public class ItemLock extends ItemBetterStorage implements ILock {
	
	public ItemLock(int id) {
		super(id);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister iconRegister) {
		itemIcon = iconRegister.registerIcon("betterstorage:lock");
	}
	
	@Override
	public boolean isItemTool(ItemStack stack) { return true; }
	@Override
	public int getItemEnchantability() { return 20; }
	
	@Override
	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world,
	                         int x, int y, int z, int side, float subX, float subY, float subZ) {
		if (world.isRemote) return false;
		
		ILockable lockable = WorldUtils.get(world, x, y, z, ILockable.class);
		// If there is no lockable container, it is already locked,
		// or the lock can't be applied, return false;
		if (lockable == null || lockable.getLock() != null ||
		    !lockable.isLockValid(stack)) return false;
		
		lockable.setLock(stack);
		// Remove the lock from the player's inventory.
		player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
		
		return true;
	}
	
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
