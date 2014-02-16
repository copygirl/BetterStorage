package net.mcft.copy.betterstorage.item.tile;

import net.mcft.copy.betterstorage.misc.Constants;
import net.mcft.copy.betterstorage.tile.ContainerMaterial;
import net.mcft.copy.betterstorage.tile.TileLockable;
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
		if (!((TileLockable)Block.blocksList[itemID]).hasMaterial())
			return super.getItemDisplayName(stack);
		
		ContainerMaterial material = ContainerMaterial.getMaterial(stack, ContainerMaterial.iron);
		
		String name = StatCollector.translateToLocal(getUnlocalizedName(stack) + ".name.full");
		String materialName = StatCollector.translateToLocal("material." + Constants.modId + "." + material.name);
		return name.replace("%MATERIAL%", materialName);
	}
	
}
