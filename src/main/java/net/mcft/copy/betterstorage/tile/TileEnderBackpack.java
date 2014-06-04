package net.mcft.copy.betterstorage.tile;

import net.mcft.copy.betterstorage.BetterStorage;
import net.mcft.copy.betterstorage.container.ContainerBetterStorage;
import net.mcft.copy.betterstorage.content.BetterStorageItems;
import net.mcft.copy.betterstorage.inventory.InventoryTileEntity;
import net.mcft.copy.betterstorage.item.ItemBackpack;
import net.mcft.copy.betterstorage.item.ItemEnderBackpack;
import net.mcft.copy.betterstorage.misc.Constants;
import net.mcft.copy.betterstorage.network.packet.PacketBackpackTeleport;
import net.mcft.copy.betterstorage.tile.entity.TileEntityBackpack;
import net.mcft.copy.betterstorage.utils.PlayerUtils;
import net.mcft.copy.betterstorage.utils.RandomUtils;
import net.mcft.copy.betterstorage.utils.WorldUtils;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileEnderBackpack extends TileBackpack {
	
	public TileEnderBackpack() {
		setHardness(3.0f);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister iconRegister) {
		blockIcon = iconRegister.registerIcon("obsidian");
	}
	
	@Override
	public TileEntity createTileEntity(World world, int metadata) {
		return new TileEntityBackpack();
	}
	
	@Override
	public ItemBackpack getItemType() { return BetterStorageItems.itemEnderBackpack; }
	
	@Override
	public boolean removedByPlayer(World world, EntityPlayer player, int x, int y, int z) {
		if (player.capabilities.isCreativeMode)
			WorldUtils.get(world, x, y, z, TileEntityBackpack.class).equipped = true;
		return super.removedByPlayer(world, player, x, y, z);
	}
	
	@Override
	public void breakBlock(World world, int x, int y, int z, Block block, int meta) {
		TileEntityBackpack backpack = WorldUtils.get(world, x, y, z, TileEntityBackpack.class);
		if (!world.isRemote && (backpack != null) && !backpack.equipped)
			for (int i = 0; i < 64; i++)
				if (teleportRandomly(world, x, y, z, (i > 48), backpack.stack))
					break;
		world.removeTileEntity(x, y, z);
	}
	
	public static boolean teleportRandomly(World world, double sourceX, double sourceY, double sourceZ, boolean canFloat, ItemStack stack) {
		
		int x = (int)sourceX + RandomUtils.getInt(-12, 12 + 1);
		int y = (int)sourceY + RandomUtils.getInt(-8, 8 + 1);
		int z = (int)sourceZ + RandomUtils.getInt(-12, 12 + 1);
		y = Math.max(1, Math.min(world.getHeight() - 1, y));
		
		if (!world.blockExists(x, y, z)) return false;
		Block block = world.getBlock(x, y, z);
		if (block.isAir(world, x, y, z) && !block.isReplaceable(world, x, y, z)) return false;
		if (!canFloat) {
			Block blockBelow = world.getBlock(x, y - 1, z);
			if (blockBelow.isAir(world, x, y, z) ||
			    !blockBelow.isSideSolid(world, x, y - 1, z, ForgeDirection.UP)) return false;
		}
		
		BetterStorage.networkChannel.sendToEveryoneNear(
				world, sourceX + 0.5, sourceY + 0.5, sourceZ + 0.5, 256,
				new PacketBackpackTeleport(sourceX, sourceY, sourceZ, x, y, z));
		
		world.playSoundEffect(sourceX + 0.5, sourceY + 0.5, sourceZ + 0.5,
		                      "mob.endermen.portal", 1.0F, 1.0F);
		world.playSoundEffect(x + 0.5, y + 0.5, z + 0.5,
		                      "mob.endermen.portal", 1.0F, 1.0F);
		
		world.setBlock(x, y, z, ((ItemBackpack)stack.getItem()).getBlockType(), RandomUtils.getInt(2, 6), 3);
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
