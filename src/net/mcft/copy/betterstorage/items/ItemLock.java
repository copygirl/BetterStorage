package net.mcft.copy.betterstorage.items;

import cpw.mods.fml.common.registry.LanguageRegistry;
import net.mcft.copy.betterstorage.blocks.TileEntityReinforcedChest;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemLock extends ItemBetterStorage {
	
	public ItemLock(int id) {
		super(id);
		setIconCoord(1, 0);
		
		setItemName("lock");
		LanguageRegistry.addName(this, "Lock");
		
		setCreativeTab(CreativeTabs.tabMisc);
	}

	@Override
	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world,
	                         int x, int y, int z, int side, float subX, float subY, float subZ) {
		if (world.isRemote) return false;
		TileEntityReinforcedChest chest = TileEntityReinforcedChest.getChestAt(world, x, y, z);
		if (chest != null && !chest.isLocked()) {
			chest.lockChest(player);
			return true;
		} else return false;
	}
	
}
