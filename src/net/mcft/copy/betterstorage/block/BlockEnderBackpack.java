package net.mcft.copy.betterstorage.block;

import net.mcft.copy.betterstorage.block.tileentity.TileEntityBackpack;
import net.mcft.copy.betterstorage.container.ContainerBetterStorage;
import net.mcft.copy.betterstorage.inventory.InventoryTileEntity;
import net.mcft.copy.betterstorage.item.ItemEnderBackpack;
import net.mcft.copy.betterstorage.misc.Constants;
import net.mcft.copy.betterstorage.misc.handlers.PacketHandler;
import net.mcft.copy.betterstorage.utils.PlayerUtils;
import net.mcft.copy.betterstorage.utils.RandomUtils;
import net.mcft.copy.betterstorage.utils.WorldUtils;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.Packet;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockEnderBackpack extends BlockBackpack {
	
	public BlockEnderBackpack(int id) {
		super(id);
		setHardness(1.5f);
	}
	
	@Override
	public Class<? extends Item> getItemClass() { return ItemEnderBackpack.class; }
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister iconRegister) {
		blockIcon = iconRegister.registerIcon("obsidian");
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
		if (!world.isRemote && (backpack != null) && !backpack.equipped)
			for (int i = 0; i < 64; i++)
				if (teleportRandomly(world, x, y, z, (i > 48), backpack.stack))
					break;
		world.removeBlockTileEntity(x, y, z);
	}
	
	public static boolean teleportRandomly(World world, double sourceX, double sourceY, double sourceZ, boolean canFloat, ItemStack stack) {
		
		int x = (int)sourceX + RandomUtils.getInt(-12, 12 + 1);
		int y = (int)sourceY + RandomUtils.getInt(-8, 8 + 1);
		int z = (int)sourceZ + RandomUtils.getInt(-12, 12 + 1);
		y = Math.max(1, Math.min(world.getHeight() - 1, y));
		
		if (!world.blockExists(x, y, z)) return false;
		Block block = Block.blocksList[world.getBlockId(x, y, z)];
		if ((block != null) && !block.isAirBlock(world, x, y, z) &&
		    !block.isBlockReplaceable(world, x, y, z)) return false;
		if (!canFloat) {
			Block blockBelow = Block.blocksList[world.getBlockId(x, y - 1, z)];
			if ((blockBelow == null) || !blockBelow.isBlockSolidOnSide(world, x, y - 1, z, ForgeDirection.UP)) return false;
		}
		
		Packet packet = PacketHandler.makePacket(PacketHandler.backpackTeleport,
		                                         sourceX, sourceY, sourceZ, x, y, z);
		PacketDispatcher.sendPacketToAllAround(sourceX + 0.5, sourceY + 0.5, sourceZ + 0.5, 512.0,
		                                       world.provider.dimensionId, packet);
		
		world.playSoundEffect(sourceX + 0.5, sourceY + 0.5, sourceZ + 0.5,
		                      "mob.endermen.portal", 1.0F, 1.0F);
		world.playSoundEffect(x + 0.5, y + 0.5, z + 0.5,
		                      "mob.endermen.portal", 1.0F, 1.0F);
		
		world.setBlock(x, y, z, stack.itemID, RandomUtils.getInt(2, 6), 3);
		TileEntityBackpack newBackpack = WorldUtils.get(world, x, y, z, TileEntityBackpack.class);
		newBackpack.stack = stack;
		
		return true;
		
	}
	
	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player,
	                                int side, float hitX, float hitY, float hitZ) {
		if (!world.isRemote) {
			TileEntityBackpack backpack = WorldUtils.get(world, x, y, z, TileEntityBackpack.class);
			IInventory inventory = new InventoryTileEntity(backpack, player.getInventoryEnderChest());
			Container container = new ContainerBetterStorage(player, inventory, 9, 3);
			String name = "container." + Constants.modId + ".enderBackpack";
			PlayerUtils.openGui(player, name, 9, 3, backpack.getCustomTitle(), container);
		}
		return true;
	}
	
}
