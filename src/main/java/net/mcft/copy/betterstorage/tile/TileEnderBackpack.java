package net.mcft.copy.betterstorage.tile;

import net.mcft.copy.betterstorage.BetterStorage;
import net.mcft.copy.betterstorage.container.ContainerBetterStorage;
import net.mcft.copy.betterstorage.content.BetterStorageItems;
import net.mcft.copy.betterstorage.inventory.InventoryTileEntity;
import net.mcft.copy.betterstorage.item.ItemBackpack;
import net.mcft.copy.betterstorage.misc.Constants;
import net.mcft.copy.betterstorage.misc.SetBlockFlag;
import net.mcft.copy.betterstorage.network.packet.PacketBackpackTeleport;
import net.mcft.copy.betterstorage.tile.entity.TileEntityBackpack;
import net.mcft.copy.betterstorage.utils.PlayerUtils;
import net.mcft.copy.betterstorage.utils.RandomUtils;
import net.mcft.copy.betterstorage.utils.WorldUtils;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class TileEnderBackpack extends TileBackpack {
	
	public TileEnderBackpack() {
		setHardness(3.0f);
	}
	
	/*
	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister iconRegister) {
		blockIcon = iconRegister.registerIcon("obsidian");
	}
	
	@Override
	public TileEntity createTileEntity(World world, int metadata) {
		return new TileEntityBackpack();
	}
	*/
	
	@Override
	public ItemBackpack getItemType() { return BetterStorageItems.itemEnderBackpack; }
	
	@Override
	public boolean removedByPlayer(World world, BlockPos pos, EntityPlayer player, boolean willHarvest) {
		if (player.capabilities.isCreativeMode)
			WorldUtils.get(world, pos, TileEntityBackpack.class).equipped = true;
		return super.removedByPlayer(world, pos, player, willHarvest);
	}
	
	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state) {
		TileEntityBackpack backpack = WorldUtils.get(world, pos, TileEntityBackpack.class);
		if (!world.isRemote && (backpack != null) && !backpack.equipped)
			for (int i = 0; i < 64; i++)
				if (teleportRandomly(world, pos, (i > 48), backpack.stack))
					break;
		world.removeTileEntity(pos);
	}
	
	public static boolean teleportRandomly(World world, BlockPos pos, boolean canFloat, ItemStack stack) {
		BlockPos targetPos = pos.add(RandomUtils.getInt(-12, 12 + 1), Math.max(1, Math.min(world.getHeight() - 1, RandomUtils.getInt(-8, 8 + 1))), RandomUtils.getInt(-12, 12 + 1));
		
		if (!world.isAirBlock(targetPos)) return false;
		Block block = world.getBlockState(targetPos).getBlock();
		if (!block.isReplaceable(world, targetPos)) return false;
		if (!canFloat && !world.isSideSolid(pos.offsetDown(), EnumFacing.UP)) return false;
		
		BetterStorage.networkChannel.sendToAllAround(
				new PacketBackpackTeleport(pos.getX(), pos.getY(), pos.getZ(), pos.getX(), pos.getY(), pos.getZ()),
				world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 256);
		
		world.playSoundEffect(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
		                      "mob.endermen.portal", 1.0F, 1.0F);
		world.playSoundEffect(targetPos.getX() + 0.5, targetPos.getY() + 0.5, targetPos.getZ() + 0.5,
		                      "mob.endermen.portal", 1.0F, 1.0F);
		
		//TODO: Make the side random (1.8)
		world.setBlockState(pos, (IBlockState) ((ItemBackpack)stack.getItem()).getBlockType().getDefaultState(), SetBlockFlag.DEFAULT);
		TileEntityBackpack newBackpack = WorldUtils.get(world, pos, TileEntityBackpack.class);
		newBackpack.stack = stack;
		
		return true;
		
	}
	
	@Override
	 public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumFacing side, float hitX, float hitY, float hitZ) {
		if (!world.isRemote) {
			TileEntityBackpack backpack = WorldUtils.get(world, pos, TileEntityBackpack.class);
			IInventory inventory = new InventoryTileEntity(backpack, player.getInventoryEnderChest());
			Container container = new ContainerBetterStorage(player, inventory, 9, 3);
			String name = "container." + Constants.modId + ".enderBackpack";
			PlayerUtils.openGui(player, name, 9, 3, backpack.getCustomTitle(), container);
		}
		return true;
	}
	
}
