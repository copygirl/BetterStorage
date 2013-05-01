package net.mcft.copy.betterstorage.container;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.mcft.copy.betterstorage.item.ItemBackpack;
import net.mcft.copy.betterstorage.utils.StackUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Icon;

public class SlotArmorBackpack extends Slot {
	
	private static final int armorType = 1;
	
	public SlotArmorBackpack(IInventory inventory, int slot, int x, int y) {
		super(inventory, slot, x, y);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public Icon getBackgroundIconIndex() {
		return ItemArmor.func_94602_b(1);
	}
	
	@Override
	public int getSlotStackLimit() { return 1; }
	
	@Override
	public boolean isItemValid(ItemStack stack) {
		if (stack == null) return false;
		return stack.getItem().isValidArmor(stack, armorType);
	}
	
	@Override
	public boolean canTakeStack(EntityPlayer player) {
		if (ItemBackpack.getBackpack(player) == null) return true;
		// For compatibility with previous versions:
		// Do not allow backpacks that still have the items tag to be taken from the slot.
		return (!StackUtils.has(getStack(), "Items") &&
				(StackUtils.get(getStack(), (byte)0, "hasItems") != 1));
	}
	
}
