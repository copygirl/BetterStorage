package net.mcft.copy.betterstorage.tile;

import java.util.List;
import java.util.Random;

import net.mcft.copy.betterstorage.api.BetterStorageEnchantment;
import net.mcft.copy.betterstorage.attachment.Attachments;
import net.mcft.copy.betterstorage.attachment.EnumAttachmentInteraction;
import net.mcft.copy.betterstorage.attachment.IHasAttachments;
import net.mcft.copy.betterstorage.tile.entity.TileEntityLockable;
import net.mcft.copy.betterstorage.utils.WorldUtils;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public abstract class TileLockable extends TileContainerBetterStorage {
	
	protected TileLockable(Material material) {
		super(material);
	}
	
	public boolean hasMaterial() { return true; }
	
	@Override
	public void getSubBlocks(Item item, CreativeTabs tab, List list) {
		if (!hasMaterial()) super.getSubBlocks(item, tab, list);
		else for (ContainerMaterial material : ContainerMaterial.getMaterials())
			list.add(material.setMaterial(new ItemStack(item, 1, 0)));
	}
	
	@Override
	public boolean removedByPlayer(World world, BlockPos pos, EntityPlayer player, boolean willHarvest) {
		if (hasMaterial() && !player.capabilities.isCreativeMode)
			
			dropBlockAsItem(world, pos, world.getBlockState(pos), 0);
		return super.removedByPlayer(world, pos, player, willHarvest);
	}
	@Override
	public void onBlockExploded(World world, BlockPos pos, Explosion explosion) {
		if (hasMaterial())
			dropBlockAsItem(world, pos, world.getBlockState(pos), 0);
		super.onBlockExploded(world, pos, explosion);
	}
	
	@Override
	public int quantityDropped(IBlockState state, int fortune, Random random) { return (hasMaterial() ? 0 : 1); }
	
	@Override
	public float getBlockHardness(World world, BlockPos pos) {
		TileEntityLockable lockable = WorldUtils.get(world, pos, TileEntityLockable.class);
		if ((lockable != null) && (lockable.getLock() != null)) return -1;
		else return super.getBlockHardness(world, pos);
	}
	
	@Override
	public float getExplosionResistance(World world, BlockPos pos, Entity exploder, Explosion explosion) {
		float modifier = 1.0F;
		TileEntityLockable lockable = WorldUtils.get(world, pos, TileEntityLockable.class);
		if (lockable != null) {
			int persistance = BetterStorageEnchantment.getLevel(lockable.getLock(), "persistance");
			if (persistance > 0) modifier += Math.pow(2, persistance);
		}
		return super.getExplosionResistance(exploder) * modifier;
	}
	
	@Override
	public MovingObjectPosition collisionRayTrace(World world, BlockPos pos, Vec3 start, Vec3 end) {
		return WorldUtils.get(world, pos, IHasAttachments.class).getAttachments().rayTrace(world, pos, start, end);
	}
	
	@Override
	public void onBlockClicked(World world, BlockPos pos, EntityPlayer player) {
		// TODO: See if we can make a pull request to Forge to get PlayerInteractEvent to fire for left click on client.
		Attachments attachments = WorldUtils.get(world, pos, IHasAttachments.class).getAttachments();
		boolean abort = attachments.interact(WorldUtils.rayTrace(player, 1.0F), player, EnumAttachmentInteraction.attack);
		// TODO: Abort block breaking? playerController.resetBlockBreaking doesn't seem to do the job.
	}
	
	@Override
	public boolean hasComparatorInputOverride() { return true; }
	
	// Trigger enchantment related
	
	@Override
	public boolean canProvidePower() { return true; }
	
	@Override
	public int isProvidingWeakPower(IBlockAccess world, BlockPos pos, IBlockState state, EnumFacing side) {
		return (WorldUtils.get(world, pos, TileEntityLockable.class).isPowered() ? 15 : 0);
	}
	@Override
	public int isProvidingStrongPower(IBlockAccess world, BlockPos pos, IBlockState state, EnumFacing side) {
		return isProvidingWeakPower(world, pos, state, side);
	}
	
	@Override
	public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
		WorldUtils.get(world, pos, TileEntityLockable.class).setPowered(false);
	}
	
}
