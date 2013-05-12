package net.mcft.copy.betterstorage.block;

import java.util.Random;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.mcft.copy.betterstorage.proxy.ClientProxy;
import net.mcft.copy.betterstorage.utils.PacketUtils;
import net.mcft.copy.betterstorage.utils.WorldUtils;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.Packet103SetSlot;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

public class BlockArmorStand extends BlockContainer {
	
	public BlockArmorStand(int id) {
		super(id, Material.rock);
		
		setHardness(2.5f);
		setBlockBounds(2 / 16.0F, 0, 2 / 16.0F, 14 / 16.0F, 2, 14 / 16.0F);
		
		MinecraftForge.setBlockHarvestLevel(this, "pickaxe", 0);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister iconRegister) {
		blockIcon = iconRegister.registerIcon("blockIron");
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public String getItemIconName() { return "betterstorage:armorstand"; }
	
	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
		int metadata = world.getBlockMetadata(x, y, z);
		if (metadata == 0) setBlockBounds(2 / 16.0F, 0, 2 / 16.0F, 14 / 16.0F, 2, 14 / 16.0F);
		else setBlockBounds(2 / 16.0F, -1, 2 / 16.0F, 14 / 16.0F, 1, 14 / 16.0F);
	}
	
	@Override
	public boolean isOpaqueCube() { return false; }
	@Override
	public boolean renderAsNormalBlock() { return false; }
	
	@Override
	@SideOnly(Side.CLIENT)
	public int getRenderType() { return ClientProxy.armorStandRenderId; }
	
	@Override
	public int quantityDropped(int meta, int fortune, Random random) {
		return ((meta == 0) ? 1 : 0);
	}
	
	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLiving player, ItemStack stack) {
		TileEntityArmorStand locker = WorldUtils.get(world, x, y, z, TileEntityArmorStand.class);
		locker.rotation = Math.round((player.rotationYawHead + 180) * 16 / 360);
		world.setBlock(x, y + 1, z, blockID, 1, 3);
	}
	
	@Override
	public void breakBlock(World world, int x, int y, int z, int id, int meta) {
		if (meta > 0) return;
		TileEntityArmorStand armorStand = WorldUtils.get(world, x, y, z, TileEntityArmorStand.class);
		super.breakBlock(world, x, y, z, id, meta);
		if (armorStand != null)
		    armorStand.dropContents();
	}
	
	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, int id) {
		int metadata = world.getBlockMetadata(x, y, z);
		int targetY = y + ((metadata == 0) ? 1 : -1);
		int targetMeta = ((metadata == 0) ? 1 : 0);
		if (world.getBlockId(x, targetY, z) == blockID &&
		    world.getBlockMetadata(x, targetY, z) == targetMeta) return;
		world.setBlock(x, y, z, 0);
		if (metadata == 0)
			dropBlockAsItem(world, x, y, z, metadata, 0);
	}
	
	@Override
	public TileEntity createNewTileEntity(World world) {
		return new TileEntityArmorStand();
	}
	@Override
	public TileEntity createTileEntity(World world, int metadata) {
		return ((metadata == 0) ? createNewTileEntity(world) : null);
	}
	
	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ) {
		
		if (world.isRemote) return true;
		
		if (world.getBlockMetadata(x, y, z) > 0)
			return onBlockActivated(world, x, y - 1, z, player, side, hitX, hitY + 1, hitZ);
		
		TileEntityArmorStand armorStand = WorldUtils.get(world, x, y, z, TileEntityArmorStand.class);
		int slot = Math.min(3, (int)(hitY * 2));
		
		ItemStack item = armorStand.armor[slot];
		ItemStack holding = player.getCurrentEquippedItem();
		ItemStack armor = player.inventory.armorInventory[slot];
		if (player.isSneaking()) {
			if (((item != null) || (armor != null)) &&
			    ((armor == null) || armor.getItem().isValidArmor(armor, 3 - slot))) {
				armorStand.armor[slot] = player.inventory.armorInventory[slot];
				player.inventory.armorInventory[slot] = item;
				PacketUtils.sendPacket(player, new Packet103SetSlot(0, 8 - slot, item));
				world.markBlockForUpdate(x, y, z);
			}
		} else if (((item != null) && (holding == null)) ||
		           ((holding != null) && holding.getItem().isValidArmor(holding, 3 - slot))) {
			armorStand.armor[slot] = holding;
			player.inventory.mainInventory[player.inventory.currentItem] = item;
			world.markBlockForUpdate(x, y, z);
		}
		
		return true;
		
	}
	
}
