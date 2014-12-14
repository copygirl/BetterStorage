package net.mcft.copy.betterstorage.container;

import net.mcft.copy.betterstorage.item.ItemBackpack;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;


public class SlotArmorBackpack extends Slot {
	
	private static final int armorType = 1;
	
	public SlotArmorBackpack(IInventory inventory, int slot, int x, int y) {
		super(inventory, slot, x, y);
	}
	

	//TODO (1.8): getBackgroundSprite looks promising. Actually, that WAS the only IIcon implementation I knew about.
	/*@Override
	@SideOnly(Side.CLIENT)
	public IIcon getBackgroundIconIndex() {
		return ItemArmor.func_94602_b(armorType);
	}*/

	
	@Override
	public int getSlotStackLimit() { return 1; }

	@Override
	public boolean isItemValid(ItemStack stack) {
		if (stack == null) return false;
		EntityPlayer player = ((InventoryPlayer)inventory).player;
		Item item = stack.getItem();
		if ((item instanceof ItemBackpack) &&
		    !ItemBackpack.canEquipBackpack(player)) return false;
		return item.isValidArmor(stack, armorType, player);
	}
	
	@Override
	public boolean canTakeStack(EntityPlayer player) { 
		ItemStack backpack = getStack();
		return ((backpack == null) || !(backpack.getItem() instanceof ItemBackpack));
	}
	
}
