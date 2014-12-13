package net.mcft.copy.betterstorage.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.inventory.SlotCrafting;
import net.minecraft.item.ItemStack;

public class CustomSlotCrafting extends SlotCrafting {

	public CustomSlotCrafting(EntityPlayer par1EntityPlayer,
			InventoryCrafting par2iInventory, IInventory par3iInventory, int par4,
			int par5, int par6) {
		super(par1EntityPlayer, par2iInventory, par3iInventory, par4, par5, par6);
	}

	@Override
	public void onCrafting(ItemStack par1ItemStack, int par2) {
		super.onCrafting(par1ItemStack, par2);
	}

	@Override
	public void onCrafting(ItemStack par1ItemStack) {
		super.onCrafting(par1ItemStack);
	}	
}
