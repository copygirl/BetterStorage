package net.mcft.copy.betterstorage.items;

import cpw.mods.fml.common.registry.LanguageRegistry;
import net.mcft.copy.betterstorage.Constants;
import net.minecraft.src.CreativeTabs;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;

public class ItemKey extends Item {
	
	public ItemKey(int id) {
		super(id);
		setMaxStackSize(1);
		setIconCoord(0, 0);
		
		setItemName("key");
		LanguageRegistry.addName(this, "Key");
		
		setCreativeTab(CreativeTabs.tabMisc);
		// This is needed to make sure the item stays in
		// the crafting matrix when used to craft a lock.
		this.setContainerItem(this);
	}
	
	@Override
	public String getTextureFile() { return Constants.items; }
	
	@Override
	public boolean doesContainerItemLeaveCraftingGrid(ItemStack stack) { return false; }
	@Override
	public ItemStack getContainerItemStack(ItemStack stack) { return stack; }
	
}
