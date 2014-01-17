package net.mcft.copy.betterstorage.block;

import java.util.Random;

import net.mcft.copy.betterstorage.BetterStorage;
import net.mcft.copy.betterstorage.block.tileentity.TileEntityBackpack;
import net.mcft.copy.betterstorage.config.GlobalConfig;
import net.mcft.copy.betterstorage.item.ItemBackpack;
import net.mcft.copy.betterstorage.proxy.ClientProxy;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockBackpack extends BlockContainerBetterStorage {
	
	public BlockBackpack(int id) {
		super(id, Material.cloth);
		
		setHardness(0.7f);
		setStepSound(soundClothFootstep);
		float w = getBoundsWidth() / 16.0F;
		float h = getBoundsHeight() / 16.0F;
		setBlockBounds(0.5F - w / 2, 0.0F, 0.5F - w / 2, 0.5F + w / 2, h, 0.5F + w / 2);
	}
	
	public int getBoundsWidth() { return 12; }
	public int getBoundsHeight() { return 13; }
	public int getBoundsDepth() { return 10; }
	
	@Override
	public Class<? extends Item> getItemClass() { return ItemBackpack.class; }
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister iconRegister) {
		blockIcon = iconRegister.registerIcon("wool_colored_brown");
	}
	
	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
		float w = getBoundsWidth() / 16.0F;
		float h = getBoundsHeight() / 16.0F;
		float d = getBoundsDepth() / 16.0F;
		ForgeDirection orientation = ForgeDirection.getOrientation(world.getBlockMetadata(x, y, z));
		if ((orientation == ForgeDirection.NORTH) || (orientation == ForgeDirection.SOUTH))
			setBlockBounds(0.5F - w / 2, 0.0F, 0.5F - d / 2, 0.5F + w / 2, h, 0.5F + d / 2);
		else if ((orientation == ForgeDirection.WEST) || (orientation == ForgeDirection.EAST))
			setBlockBounds(0.5F - d / 2, 0.0F, 0.5F - w / 2, 0.5F + d / 2, h, 0.5F + w / 2);
		else setBlockBounds(0.5F - w / 2, 0.0F, 0.5F - w / 2, 0.5F + w / 2, h, 0.5F + w / 2);
	}
	
	@Override
	public boolean isOpaqueCube() { return false; }
	@Override
	public boolean renderAsNormalBlock() { return false; }
	
	@Override
	@SideOnly(Side.CLIENT)
	public int getRenderType() { return ClientProxy.backpackRenderId; }
	
	@Override
	public int quantityDropped(int meta, int fortune, Random random) { return 0; }
	
	@Override
	public float getPlayerRelativeBlockHardness(EntityPlayer player, World world, int x, int y, int z) {
		return ((player.isSneaking() && !ItemBackpack.canEquipBackpack(player)) ? -1.0F
				: super.getPlayerRelativeBlockHardness(player, world, x, y, z));
	}
	
	private long lastHelpMessage = System.currentTimeMillis();
	@Override
	public void onBlockClicked(World world, int x, int y, int z, EntityPlayer player) {
		if (world.isRemote && player.isSneaking() && !ItemBackpack.canEquipBackpack(player) &&
		    BetterStorage.globalConfig.getBoolean(GlobalConfig.enableHelpTooltips) &&
		    (System.currentTimeMillis() > lastHelpMessage + 10 * 1000)) {
			boolean backpack = (ItemBackpack.getBackpack(player) != null);
			player.addChatMessage("tile.betterstorage.backpack.cantEquip." + (backpack ? "backpack" : "chestplate"));
			lastHelpMessage = System.currentTimeMillis();
		}
	}
	
	@Override
	public TileEntity createNewTileEntity(World world) {
		return new TileEntityBackpack();
	}
	
}
