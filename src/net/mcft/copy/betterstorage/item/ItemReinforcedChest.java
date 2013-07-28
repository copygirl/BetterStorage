package net.mcft.copy.betterstorage.item;

import net.mcft.copy.betterstorage.block.ChestMaterial;
import net.mcft.copy.betterstorage.misc.Constants;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

public class ItemReinforcedChest extends ItemBlock {
	
	public ItemReinforcedChest(int id) {
		super(id);
		setHasSubtypes(true);
		setMaxDamage(0);
	}
	
	@Override
	public int getMetadata(int damage) { return damage; }
	
	@Override
	public String getItemDisplayName(ItemStack stack) {
		String name = StatCollector.translateToLocal(getLocalizedName(stack) + ".name.full");
		String material = "material." + Constants.modId + "." + ChestMaterial.get(stack.getItemDamage()).name;
		String materialName = StatCollector.translateToLocal(material);
		return name.replace("%MATERIAL%", materialName);
	}
	
}
