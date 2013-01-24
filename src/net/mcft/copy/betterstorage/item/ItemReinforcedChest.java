package net.mcft.copy.betterstorage.item;

import net.mcft.copy.betterstorage.block.ChestMaterial;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class ItemReinforcedChest extends ItemBlock {
	
	public ItemReinforcedChest(int id) {
		super(id - 256);
	}
	
	@Override
	public int getMetadata(int damage) { return damage; }

	@Override
	public String getItemName() { return "reinforcedChest"; }
	@Override
	public String getItemNameIS(ItemStack stack) {
		return getItemName() + "." + ChestMaterial.get(stack.getItemDamage()).name;
	}
	
}
