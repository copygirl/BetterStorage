package net.mcft.copy.betterstorage.item.tile;

import net.mcft.copy.betterstorage.content.BetterStorageTiles;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class ItemTileBetterStorage extends ItemBlock {
	
	public ItemTileBetterStorage(Block block) {
		super(block);
	}
	
	@Override
	public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, IBlockState newState) {
		if (!super.placeBlockAt(stack, player, world, pos, side, hitX, hitY, hitZ, newState)) return false;
		BetterStorageTiles.crate.onBlockPlacedExtended(world, pos, side, hitX, hitY, hitZ, player, stack);
		return true;
	}
	
}
