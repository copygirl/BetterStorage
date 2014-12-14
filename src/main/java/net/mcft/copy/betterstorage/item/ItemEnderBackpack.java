package net.mcft.copy.betterstorage.item;

import net.mcft.copy.betterstorage.content.BetterStorageTiles;
import net.mcft.copy.betterstorage.inventory.InventoryWrapper;
import net.mcft.copy.betterstorage.misc.Constants;
import net.mcft.copy.betterstorage.misc.PropertiesBackpack;
import net.mcft.copy.betterstorage.misc.Resources;
import net.mcft.copy.betterstorage.tile.TileBackpack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

public class ItemEnderBackpack extends ItemBackpack {
	
	public ItemEnderBackpack() {
		super(ItemBackpack.material);
		setMaxDamage(0);
	}
	
	@Override
	public String getBackpackName() { return Constants.containerEnderBackpack; }
	
	@Override
	public int getBackpackRows() { return 3; }
	
	@Override
	protected int getDefaultColor() { return -1; }
	
	@Override
	public TileBackpack getBlockType() { return BetterStorageTiles.enderBackpack; }
	
	@Override
	protected IInventory getBackpackItemsInternal(EntityLivingBase carrier, EntityPlayer player) {
		return new InventoryEnderBackpackEquipped(player.getInventoryEnderChest());
	}
	
	@Override
	public boolean containsItems(PropertiesBackpack backpackData) { return false; }
	@Override
	protected String getAdditionalInfo(ItemStack stack, EntityPlayer player) { return "backpack.bound"; }
	
	// Item stuff
	
	@Override
	public String getArmorTexture(ItemStack stack, Entity entity, int slot, String type) {
		return Resources.textureEnderBackpack.toString();
	}
	
	class InventoryEnderBackpackEquipped extends InventoryWrapper {
		public InventoryEnderBackpackEquipped(IInventory base) { super(base); }
		@Override public String getName() { return getBackpackName(); }
	}
	
}
