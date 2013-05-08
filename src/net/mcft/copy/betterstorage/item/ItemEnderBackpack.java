package net.mcft.copy.betterstorage.item;

import java.util.List;

import net.mcft.copy.betterstorage.inventory.InventoryWrapper;
import net.mcft.copy.betterstorage.misc.Constants;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemEnderBackpack extends ItemBackpack {
	
	public ItemEnderBackpack(int id) {
		super(id);
		setMaxDamage(280);
	}
	
	@Override
	public String getName() { return "container.enderBackpack"; }
	
	@Override
	protected IInventory getBackpackItemsInternal(EntityLiving carrier, EntityPlayer player) {
		return new InventoryEnderBackpackEquipped(player.getInventoryEnderChest());
	}
	
	@Override
	public boolean canUnequip(EntityPlayer player, ItemStack backpack) { return false; }
	
	// Item stuff
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister iconRegister) {
		itemIcon = iconRegister.registerIcon("betterstorage:enderBackpack");
	}
	
	@Override
	public String getArmorTexture(ItemStack stack, Entity entity, int slot, int layer) {
		return Constants.enderBackpackTexture;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean advancedTooltips) {
		if (getBackpack(player) != stack) return;
		list.add("Bound backpack. Hold shift and right click");
		list.add("ground with empty hand to unequip.");
	}
	
	class InventoryEnderBackpackEquipped extends InventoryWrapper {
		public InventoryEnderBackpackEquipped(IInventory base) { super(base); }
		@Override public String getInvName() { return getName(); }
	}
	
	
}
