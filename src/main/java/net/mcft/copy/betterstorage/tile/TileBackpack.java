package net.mcft.copy.betterstorage.tile;

import java.util.Random;

import net.mcft.copy.betterstorage.BetterStorage;
import net.mcft.copy.betterstorage.config.GlobalConfig;
import net.mcft.copy.betterstorage.content.BetterStorageItems;
import net.mcft.copy.betterstorage.item.ItemBackpack;
import net.mcft.copy.betterstorage.proxy.ClientProxy;
import net.mcft.copy.betterstorage.tile.entity.TileEntityBackpack;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentTranslation;
<<<<<<< HEAD
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
=======
>>>>>>> refs/remotes/Thog92/1.8
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TileBackpack extends TileContainerBetterStorage {
	
	public TileBackpack() {
		super(Material.cloth);
		
		setHardness(1.5f);
		setStepSound(soundTypeCloth);
		float w = getBoundsWidth() / 16.0F;
		float h = getBoundsHeight() / 16.0F;
		setBlockBounds(0.5F - w / 2, 0.0F, 0.5F - w / 2, 0.5F + w / 2, h, 0.5F + w / 2);
	}
	
	public int getBoundsWidth() { return 12; }
	public int getBoundsHeight() { return 13; }
	public int getBoundsDepth() { return 10; }
	
	@Override
	public Class<? extends ItemBlock> getItemClass() { return null; }

	public ItemBackpack getItemType() { return BetterStorageItems.itemBackpack; }
	
<<<<<<< HEAD
	//TODO (1.8): Moved to betterstorage.backpack.json, left here for reference.
	/*@Override
=======
	
	/*
	@Override
>>>>>>> refs/remotes/Thog92/1.8
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister iconRegister) {
		blockIcon = iconRegister.registerIcon("wool_colored_brown");
	}*/
	
	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess world, BlockPos pos) {
		float w = getBoundsWidth() / 16.0F;
		float h = getBoundsHeight() / 16.0F;
		float d = getBoundsDepth() / 16.0F;
		//TODO (1.8): Get behind block-states.
		EnumFacing orientation = EnumFacing.getFront(world.getBlockMetadata(x, y, z));
		if ((orientation == EnumFacing.NORTH) || (orientation == EnumFacing.SOUTH))
			setBlockBounds(0.5F - w / 2, 0.0F, 0.5F - d / 2, 0.5F + w / 2, h, 0.5F + d / 2);
		else if ((orientation == EnumFacing.WEST) || (orientation == EnumFacing.EAST))
			setBlockBounds(0.5F - d / 2, 0.0F, 0.5F - w / 2, 0.5F + d / 2, h, 0.5F + w / 2);
		else setBlockBounds(0.5F - w / 2, 0.0F, 0.5F - w / 2, 0.5F + w / 2, h, 0.5F + w / 2);
	}
	*/
	
	@Override
	public boolean isOpaqueCube() { return false; }
	
	@Override
	@SideOnly(Side.CLIENT)
	public int getRenderType() { return ClientProxy.backpackRenderId; }
	
	@Override
	public int quantityDropped(IBlockState state, int fortune, Random random) { return 0; }
	
	@Override
	public float getPlayerRelativeBlockHardness(EntityPlayer player, World world, BlockPos pos) {
		float hardness = super.getPlayerRelativeBlockHardness(player, world, pos);
		boolean sneaking = player.isSneaking();
		boolean canEquip = ItemBackpack.canEquipBackpack(player);
		boolean stoppedSneaking = localPlayerStoppedSneaking(player);
		return ((stoppedSneaking || (sneaking && !canEquip)) ? -1.0F : (hardness * (sneaking ? 4 : 1)));
	}
	
	boolean lastSneaking = false;
	private boolean localPlayerStoppedSneaking(EntityPlayer player) {
		if (!player.worldObj.isRemote || (player != Minecraft.getMinecraft().thePlayer)) return false;
		boolean stoppedSneaking = (!player.isSneaking() && lastSneaking);
		lastSneaking = player.isSneaking();
		return stoppedSneaking;
	}
	
	private long lastHelpMessage = System.currentTimeMillis();
	@Override
	public void onBlockClicked(World world, BlockPos pos, EntityPlayer player) {
		if (world.isRemote && player.isSneaking() && !ItemBackpack.canEquipBackpack(player) &&
		    BetterStorage.globalConfig.getBoolean(GlobalConfig.enableHelpTooltips) &&
		    (System.currentTimeMillis() > lastHelpMessage + 10 * 1000)) {
			boolean backpack = (ItemBackpack.getBackpack(player) != null);
			player.addChatMessage(new ChatComponentTranslation("tile.betterstorage.backpack.cantEquip." + (backpack ? "backpack" : "chestplate")));
			lastHelpMessage = System.currentTimeMillis();
		}
	}
	
	@Override
	public TileEntity createTileEntity(World world, IBlockState state) {
		return new TileEntityBackpack();
	}
	
}
