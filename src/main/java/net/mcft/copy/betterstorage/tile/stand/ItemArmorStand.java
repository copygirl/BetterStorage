package net.mcft.copy.betterstorage.tile.stand;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class ItemArmorStand extends ItemBlock {
	
	public ItemArmorStand(Block block) {
		super(block);
		setMaxStackSize(1);
	}
	
	@Override
	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ)
	{
		if (stack.stackSize == 0) return false;
		
		IBlockState stateClicked = world.getBlockState(pos);
		Block blockClicked = stateClicked.getBlock();
		
		// If the block clicked is not replaceable,
		// adjust the coordinates depending on the side clicked.
		if (!blockClicked.isReplaceable(world, pos)) {
			switch (side.getIndex()) {
				case 0: pos = pos.add(0, -1, 0); break;
				case 1: pos = pos.add(0, 1, 0); break;
				case 2: pos = pos.add(0, 0, -1); break;
				case 3: pos = pos.add(0, 0, 1); break;
				case 4: pos = pos.add(0, -1, 0); break;
				case 5: pos = pos.add(0, 1, 0); break;
			}
		}
		
		// Return false if there's not enough world height left.
		if (pos.getY() >= world.getHeight() - 2) return false;
		BlockPos topPos = pos.add(0, 1, 0);
		Block blockTop = world.getBlockState(topPos).getBlock();
		
		// Return false if the block above isn't replaceable.
		if (!blockTop.isReplaceable(world, topPos)) return false;
		
		// Return false if the player can't edit any of the
		// two blocks the armor stand would occupy.
		if (!player.func_175151_a(pos, side, stack) ||
			!player.func_175151_a(topPos, side, stack)) return false;
		
		
		// Return false if there's an entity blocking the placement.
		if (!world.canBlockBePlaced(block, pos, false, side, player, stack)) return false;
		
		// Actually place the block in the world,
		// play place sound and decrease stack size if successful.
		if (placeBlockAt(stack, player, world, pos, side, hitX, hitY, hitZ, stateClicked)) {
			String sound = block.stepSound.getPlaceSound();
			float volume = (block.stepSound.getVolume() + 1.0F) / 2.0F;
			float pitch = block.stepSound.getVolume() * 0.8F;
			world.playSoundEffect(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, sound, volume, pitch);
			stack.stackSize--;
		}
		
		return true;
		
	}
	
}
