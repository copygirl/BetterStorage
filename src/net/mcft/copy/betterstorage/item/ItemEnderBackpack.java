package net.mcft.copy.betterstorage.item;

import java.util.List;

import net.mcft.copy.betterstorage.Config;
import net.mcft.copy.betterstorage.inventory.InventoryWrapper;
import net.mcft.copy.betterstorage.misc.Constants;
import net.mcft.copy.betterstorage.misc.PropertiesBackpack;
import net.mcft.copy.betterstorage.misc.Resources;
import net.mcft.copy.betterstorage.misc.handlers.KeyBindingHandler;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemEnderBackpack extends ItemBackpack {
	
	public ItemEnderBackpack(int id) {
		super(id, material);
		setMaxDamage(380);
	}
	
	@Override
	public String getName() { return Constants.containerEnderBackpack; }
	
	@Override
	protected IInventory getBackpackItemsInternal(EntityLivingBase carrier, EntityPlayer player) {
		return new InventoryEnderBackpackEquipped(player.getInventoryEnderChest());
	}
	
	@Override
	public boolean canTake(PropertiesBackpack backpackData, ItemStack backpack) { return false; }
	@Override
	public boolean containsItems(PropertiesBackpack backpackData) { return false; }
	
	// Item stuff
	
	@Override
	public String getArmorTexture(ItemStack stack, Entity entity, int slot, String type) {
		return Resources.enderBackpackTexture.toString();
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean advancedTooltips) {
		if (getBackpack(player) == stack) {
			PropertiesBackpack backpackData = getBackpackData(player);
			if (ItemBackpack.isBackpackOpen(player)) {
				list.add("Bound backpack.");
				list.add("Currently being used by a player.");
			} else {
				list.add("Bound backpack. Sneak and right click");
				list.add("ground with empty hand to unequip.");
			}
			if (KeyBindingHandler.serverBackpackKeyEnabled) {
				GameSettings settings = Minecraft.getMinecraft().gameSettings;
				String str = GameSettings.getKeyDisplayString(Config.backpackOpenKey);
				list.add("Press " + str + " to open while equipped.");
			}
		} else {
			list.add("Place down and break");
			list.add("while sneaking to equip.");
		}
	}
	
	@Override
	public boolean getIsRepairable(ItemStack stack, ItemStack repairMaterial) {
		return (repairMaterial.itemID == Block.obsidian.blockID);
	}
	
	class InventoryEnderBackpackEquipped extends InventoryWrapper {
		public InventoryEnderBackpackEquipped(IInventory base) { super(base); }
		@Override public String getInvName() { return getName(); }
	}
	
	
}
