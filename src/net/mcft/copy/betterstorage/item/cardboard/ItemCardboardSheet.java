package net.mcft.copy.betterstorage.item.cardboard;

import net.mcft.copy.betterstorage.item.ItemBetterStorage;
import net.minecraft.item.EnumArmorMaterial;
import net.minecraft.item.EnumToolMaterial;
import net.minecraftforge.common.EnumHelper;

public class ItemCardboardSheet extends ItemBetterStorage {

	public static final EnumToolMaterial toolMaterial =
			EnumHelper.addToolMaterial("cardboard", 0, 64, 2.5F, 0.0F, 18);
	public static final EnumArmorMaterial armorMaterial =
			EnumHelper.addArmorMaterial("cardboard", 5, new int[]{ 1, 2, 2, 1 }, 20);
	
	public ItemCardboardSheet(int id) {
		super(id);
		setMaxStackSize(8);
		
		toolMaterial.customCraftingMaterial = this;
		armorMaterial.customCraftingMaterial = this;
	}
	
}
