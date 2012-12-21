package net.mcft.copy.betterstorage.items;

import cpw.mods.fml.common.registry.LanguageRegistry;
import net.mcft.copy.betterstorage.blocks.TileEntityReinforcedChest;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemKey extends ItemBetterStorage {
	
	public ItemKey(int id) {
		super(id);
		setIconCoord(0, 0);
		
		setItemName("key");
		LanguageRegistry.addName(this, "Key");
		
		setCreativeTab(CreativeTabs.tabMisc);
		// This is needed to make sure the item stays in
		// the crafting matrix when used to craft a lock.
		this.setContainerItem(this);
	}
	
	@Override
	public boolean doesContainerItemLeaveCraftingGrid(ItemStack stack) { return false; }
	@Override
	public ItemStack getContainerItemStack(ItemStack stack) { return stack; }
	
	@Override
	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world,
	                         int x, int y, int z, int side, float subX, float subY, float subZ) {
		if (world.isRemote) return false;
		TileEntityReinforcedChest chest = TileEntityReinforcedChest.getChestAt(world, x, y, z);
		if (chest != null && chest.isLocked() && player.isSneaking()) {
			chest.unlockChest(player);
			return true;
		} else return false;
	}
	
}
