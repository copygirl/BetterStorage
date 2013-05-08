package net.mcft.copy.betterstorage.block;

import net.mcft.copy.betterstorage.container.ContainerBetterStorage;
import net.mcft.copy.betterstorage.inventory.InventoryWrapper;
import net.mcft.copy.betterstorage.misc.handlers.PacketHandler;
import net.mcft.copy.betterstorage.utils.PacketUtils;
import net.mcft.copy.betterstorage.utils.PlayerUtils;
import net.mcft.copy.betterstorage.utils.RandomUtils;
import net.mcft.copy.betterstorage.utils.WorldUtils;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.network.packet.Packet;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;

public class BlockEnderBackpack extends BlockBackpack {
	
	public BlockEnderBackpack(int id) {
		super(id);
		setHardness(1.5f);
	}
	
	@Override
	public TileEntity createNewTileEntity(World world) {
		return new TileEntityBackpack();
	}
	
	@Override
	public boolean removeBlockByPlayer(World world, EntityPlayer player, int x, int y, int z) {
		if (player.capabilities.isCreativeMode)
			WorldUtils.get(world, x, y, z, TileEntityBackpack.class).equipped = true;
		return super.removeBlockByPlayer(world, player, x, y, z);
	}
	
	@Override
	public void breakBlock(World world, int x, int y, int z, int id, int meta) {
		TileEntityBackpack backpack = WorldUtils.get(world, x, y, z, TileEntityBackpack.class);
		world.removeBlockTileEntity(x, y, z);
		if (world.isRemote || (backpack == null) || backpack.equipped) return;
		for (int i = 0; i < 64; i++)
			if (teleportRandomly(backpack, world, x, y, z, (i > 48)))
				break;
	}
	
	public boolean teleportRandomly(TileEntityBackpack backpack, World world, int x, int y, int z, boolean canFloat) {
		
		int sourceX = x;
		int sourceY = y;
		int sourceZ = z;
		
		x += RandomUtils.getInt(-12, 12 + 1);
		y += RandomUtils.getInt(-8, 8 + 1);
		z += RandomUtils.getInt(-12, 12 + 1);
		y = Math.max(1, Math.min(world.getHeight() - 1, y));
		
		if (!world.blockExists(x, y, z)) return false;
		Block block = Block.blocksList[world.getBlockId(x, y, z)];
		if ((block != null) && !block.isAirBlock(world, x, y, z) &&
		    !block.isBlockReplaceable(world, x, y, z)) return false;
		if (!canFloat) {
			Block blockBelow = Block.blocksList[world.getBlockId(x, y - 1, z)];
			if ((blockBelow == null) || !blockBelow.isBlockSolidOnSide(world, x, y - 1, z, ForgeDirection.UP)) return false;
		}
		
		Packet packet = PacketUtils.makePacket(
				(byte)PacketHandler.backpackTeleport,
				sourceX, sourceY, sourceZ, x, y, z);
		MinecraftServer.getServer().getConfigurationManager().sendToAllNear(
				sourceX, sourceY, sourceZ, 512.0, world.getWorldInfo().getDimension(), packet);
		
		world.playSoundEffect(sourceX + 0.5, sourceY + 0.5, sourceZ + 0.5,
		                      "mob.endermen.portal", 1.0F, 1.0F);
		world.playSoundEffect(x + 0.5, y + 0.5, z + 0.5,
		                      "mob.endermen.portal", 1.0F, 1.0F);
		
		world.setBlock(x, y, z, blockID, RandomUtils.getInt(2, 6), 3);
		TileEntityBackpack newBackpack = WorldUtils.get(world, x, y, z, TileEntityBackpack.class);
		newBackpack.stack = backpack.stack;
		
		return true;
		
	}
	
	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player,
	                                int side, float hitX, float hitY, float hitZ) {
		if (!world.isRemote) {
			TileEntityBackpack backpack = WorldUtils.get(world, x, y, z, TileEntityBackpack.class);
			IInventory inventory = new InventoryEnderBackpack(player, backpack);
			Container container = new ContainerBetterStorage(player, inventory, 9, 3);
			PlayerUtils.openGui(player, "container.enderBackpack", 9, 3, backpack.getCustomTitle(), container);
		}
		return true;
	}
	
	static class InventoryEnderBackpack extends InventoryWrapper {
		
		public final TileEntityBackpack backpack;
		
		public InventoryEnderBackpack(EntityPlayer player, TileEntityBackpack backpack) {
			super(player.getInventoryEnderChest());
			this.backpack = backpack;
		}
		@Override
		public boolean isUseableByPlayer(EntityPlayer player) {
			return WorldUtils.isTileEntityUsableByPlayer(backpack, player);
		}
		@Override
		public void openChest() { backpack.onContainerOpened(); }
		@Override
		public void closeChest() { backpack.onContainerClosed(); }
		
	}
	
}
