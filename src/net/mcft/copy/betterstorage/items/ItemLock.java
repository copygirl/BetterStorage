package net.mcft.copy.betterstorage.items;

import cpw.mods.fml.common.registry.LanguageRegistry;
import net.mcft.copy.betterstorage.Constants;
import net.minecraft.src.CreativeTabs;
import net.minecraft.src.Item;

public class ItemLock extends Item {
	
	public ItemLock(int id) {
		super(id);
		setMaxStackSize(1);
		setIconCoord(1, 0);
		
		setItemName("lock");
		LanguageRegistry.addName(this, "Lock");
		
		setCreativeTab(CreativeTabs.tabMisc);
	}
	
	@Override
	public String getTextureFile() { return Constants.items; }
	
}
