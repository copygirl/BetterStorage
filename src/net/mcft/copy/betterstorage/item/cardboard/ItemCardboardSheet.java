package net.mcft.copy.betterstorage.item.cardboard;

import net.mcft.copy.betterstorage.item.ItemBetterStorage;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.EnumArmorMaterial;
import net.minecraft.item.EnumToolMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumHelper;

public class ItemCardboardSheet extends ItemBetterStorage {

	public static final EnumToolMaterial toolMaterial =
			EnumHelper.addToolMaterial("cardboard", 0, 64, 2.5F, 0.0F, 0);
	public static final EnumArmorMaterial armorMaterial =
			EnumHelper.addArmorMaterial("cardboard", 5, new int[]{ 1, 2, 2, 1 }, 0);
	
	public ItemCardboardSheet(int id) {
		super(id);
		setMaxStackSize(8);
	}
	
	public static boolean isEffective(ItemStack stack) {
		return (stack.getItemDamage() < stack.getMaxDamage());
	}
	public static boolean canHarvestBlock(ItemStack stack, boolean canHarvestDefault) {
		return (isEffective(stack) ? canHarvestDefault : false);
	}
	public static boolean damageItem(ItemStack stack, int damage, EntityLivingBase entity) {
		if (!isEffective(stack)) return true;
		stack.damageItem(1, entity);
		if (!isEffective(stack)) {
			entity.renderBrokenItemStack(stack);
			stack.setItemDamage(stack.getMaxDamage());
			stack.stackSize = 1;
		}
		return true;
	}
	public static boolean onBlockDestroyed(World world, int blockID, int x, int y, int z,
	                                       ItemStack stack, EntityLivingBase entity) {
		return ((Block.blocksList[blockID].getBlockHardness(world, x, y, z) > 0)
				? ItemCardboardSheet.damageItem(stack, 1, entity) : true);
	}
	
}
