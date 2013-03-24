package net.mcft.copy.betterstorage.item;

import net.mcft.copy.betterstorage.block.ChestMaterial;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class ItemReinforcedChest extends ItemBlock {
	
	public ItemReinforcedChest(int id) {
		super(id);
		setHasSubtypes(true);
		setMaxDamage(0);
	}
	
	@Override
	public int getMetadata(int damage) { return damage; }
	
	@Override
	public String getUnlocalizedName(ItemStack stack) {
		return getUnlocalizedName() + "." + ChestMaterial.get(stack.getItemDamage()).name;
	}
	
}
