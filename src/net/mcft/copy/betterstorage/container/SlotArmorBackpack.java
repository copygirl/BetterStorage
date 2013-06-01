package net.mcft.copy.betterstorage.container;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.mcft.copy.betterstorage.item.ItemBackpack;
import net.mcft.copy.betterstorage.misc.PropertiesBackpack;
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
		ItemStack backpack = getStack();
		if ((backpack == null) || !(backpack.getItem() instanceof ItemBackpack)) return true;
		ItemBackpack backpackType = (ItemBackpack)backpack.getItem();
		
		PropertiesBackpack data = ItemBackpack.getBackpackData(player);
		return (!ItemBackpack.isBackpackOpen(player) &&
				backpackType.canTake(data, backpack) &&
				!backpackType.containsItems(data, backpack));
	}
	
}
