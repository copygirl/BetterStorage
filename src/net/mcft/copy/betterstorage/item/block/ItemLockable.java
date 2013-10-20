package net.mcft.copy.betterstorage.item.block;

import net.mcft.copy.betterstorage.block.BlockLockable;
import net.mcft.copy.betterstorage.block.ContainerMaterial;
import net.mcft.copy.betterstorage.misc.Constants;
import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

public class ItemLockable extends ItemBlock {
	
	public ItemLockable(int id) {
		super(id);
		setHasSubtypes(true);
		setMaxDamage(0);
	}
	
	@Override
	public int getMetadata(int damage) { return damage; }
	
	@Override
	public String getItemDisplayName(ItemStack stack) {
		if (!((BlockLockable)Block.blocksList[itemID]).hasMaterial())
			return super.getItemDisplayName(stack);
		
		ContainerMaterial material = ContainerMaterial.getMaterial(stack, ContainerMaterial.iron);
		
		String name = StatCollector.translateToLocal(getUnlocalizedName(stack) + ".name.full");
		String materialName = StatCollector.translateToLocal("material." + Constants.modId + "." + material.name);
		return name.replace("%MATERIAL%", materialName);
	}
	
}
