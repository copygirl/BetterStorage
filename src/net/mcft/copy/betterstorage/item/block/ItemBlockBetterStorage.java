package net.mcft.copy.betterstorage.item.block;

import net.mcft.copy.betterstorage.content.Blocks;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemBlockBetterStorage extends ItemBlock {
	
	public ItemBlockBetterStorage(int id) {
		super(id);
	}
	
	@Override
	public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, int x, int y, int z,
	                            int side, float hitX, float hitY, float hitZ, int metadata) {
		if (!super.placeBlockAt(stack, player, world, x, y, z, side, hitX, hitY, hitZ, metadata)) return false;
		Blocks.crate.onBlockPlacedExtended(world, x, y, z, side, hitX, hitY, hitZ, player, stack);
		return true;
	}
	
}
