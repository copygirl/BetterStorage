package net.mcft.copy.betterstorage.block;

import java.util.List;
import java.util.Random;

import net.mcft.copy.betterstorage.api.BetterStorageEnchantment;
import net.mcft.copy.betterstorage.attachment.Attachments;
import net.mcft.copy.betterstorage.attachment.EnumAttachmentInteraction;
import net.mcft.copy.betterstorage.attachment.IHasAttachments;
import net.mcft.copy.betterstorage.block.tileentity.TileEntityReinforcedChest;
import net.mcft.copy.betterstorage.item.ItemReinforcedChest;
import net.mcft.copy.betterstorage.proxy.ClientProxy;
import net.mcft.copy.betterstorage.utils.WorldUtils;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockReinforcedChest extends BlockContainerBetterStorage {
	
	public BlockReinforcedChest(int id, Material material) {
		super(id, material);
		
		setHardness(8.0f);
		setResistance(20.0f);
		setStepSound(Block.soundWoodFootstep);
		setBlockBounds(0.0625F, 0.0F, 0.0625F, 0.9375F, 0.875F, 0.9375F);
		
		MinecraftForge.setBlockHarvestLevel(this, "axe", 2);
	}
	public BlockReinforcedChest(int id) {
		this(id, Material.wood);
	}
	
	@Override
	public Class<? extends Item> getItemClass() { return ItemReinforcedChest.class; }
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister iconRegister) {
		blockIcon = iconRegister.registerIcon("log_oak");
	}
	
	@Override
	public boolean isOpaqueCube() { return false; }
	@Override
	public boolean renderAsNormalBlock() { return false; }
	
	@Override
	@SideOnly(Side.CLIENT)
	public int getRenderType() { return ClientProxy.reinforcedChestRenderId; }
	
	@Override
	public int damageDropped(int metadata) { return metadata; }
	
	@Override
	public void getSubBlocks(int id, CreativeTabs tab, List list) {
		for (ContainerMaterial material : ContainerMaterial.getMaterials())
			list.add(material.setMaterial(new ItemStack(id, 1, 0)));
	}
	
	@Override
	public float getBlockHardness(World world, int x, int y, int z) {
		TileEntityReinforcedChest chest = WorldUtils.get(world, x, y, z, TileEntityReinforcedChest.class);
		float hardness = blockHardness;
		if ((chest != null) && (chest.getLock() != null)) {
			hardness *= 15.0F;
			int persistance = BetterStorageEnchantment.getLevel(chest.getLock(), "persistance");
			if (persistance > 0) hardness *= persistance + 2;
		}
		return hardness;
	}
	
	@Override
	public float getExplosionResistance(Entity entity, World world, int x, int y, int z, double explosionX, double explosionY, double explosionZ) {
		float modifier = 1.0F;
		TileEntityReinforcedChest chest = WorldUtils.get(world, x, y, z, TileEntityReinforcedChest.class);
		if (chest != null) {
			int persistance = BetterStorageEnchantment.getLevel(chest.getLock(), "persistance");
			if (persistance > 0) modifier += Math.pow(2, persistance);
		}
		return super.getExplosionResistance(entity) * modifier;
	}
	
	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
		TileEntityReinforcedChest chest = WorldUtils.get(world, x, y, z, TileEntityReinforcedChest.class);
		if (chest.isConnected()) {
			ForgeDirection connected = chest.getConnected();
			if (connected == ForgeDirection.NORTH)
				setBlockBounds(0.0625F, 0.0F, 0.0F, 0.9375F, 0.875F, 0.9375F);
			else if (connected == ForgeDirection.SOUTH)
				setBlockBounds(0.0625F, 0.0F, 0.0625F, 0.9375F, 0.875F, 1.0F);
			else if (connected == ForgeDirection.WEST)
				setBlockBounds(0.0F, 0.0F, 0.0625F, 0.9375F, 0.875F, 0.9375F);
			else if (connected == ForgeDirection.EAST)
				setBlockBounds(0.0625F, 0.0F, 0.0625F, 1.0F, 0.875F, 0.9375F);
		} else setBlockBounds(0.0625F, 0.0F, 0.0625F, 0.9375F, 0.875F, 0.9375F);
	}
	
	@Override
	public MovingObjectPosition collisionRayTrace(World world, int x, int y, int z, Vec3 start, Vec3 end) {
		Attachments attachments = WorldUtils.get(world, x, y, z, IHasAttachments.class).getAttachments();
		return attachments.rayTrace(world, x, y, z, start, end);
	}
	
	@Override
	public TileEntity createNewTileEntity(World world) {
		return new TileEntityReinforcedChest();
	}
	
	@Override
	public void onBlockClicked(World world, int x, int y, int z, EntityPlayer player) {
		// TODO: See if we can make a pull request to Forge to get PlayerInteractEvent to fire for left click on client.
		Attachments attachments = WorldUtils.get(world, x, y, z, IHasAttachments.class).getAttachments();
		boolean abort = attachments.interact(WorldUtils.rayTrace(player, 1.0F), player, EnumAttachmentInteraction.attack);
		// TODO: Abort block breaking? playerController.resetBlockBreaking doesn't seem to do the job.
	}
	
	// Trigger enchantment related
	
	@Override
	public boolean canProvidePower() { return true; }
	
	@Override
	public int isProvidingWeakPower(IBlockAccess world, int x, int y, int z, int side) {
		return (WorldUtils.get(world, x, y, z, TileEntityReinforcedChest.class).isPowered() ? 15 : 0);
	}
	@Override
	public int isProvidingStrongPower(IBlockAccess world, int x, int y, int z, int side) {
		return isProvidingWeakPower(world, x, y, z, side);
	}
	
	@Override
	public void updateTick(World world, int x, int y, int z, Random random) {
		WorldUtils.get(world, x, y, z, TileEntityReinforcedChest.class).setPowered(false);
	}
	
}
