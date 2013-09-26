package net.mcft.copy.betterstorage.block;

import java.util.Locale;

import net.mcft.copy.betterstorage.BetterStorage;
import net.mcft.copy.betterstorage.attachment.IHasAttachments;
import net.mcft.copy.betterstorage.block.tileentity.TileEntityContainer;
import net.mcft.copy.betterstorage.misc.Constants;
import net.mcft.copy.betterstorage.utils.WorldUtils;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import cpw.mods.fml.common.registry.GameRegistry;

public abstract class BlockContainerBetterStorage extends BlockContainer {
	
	protected BlockContainerBetterStorage(int id, Material material) {
		
		super(id, material);
		
		setCreativeTab(BetterStorage.creativeTab);
		
		String name = getClass().getSimpleName();                                    // BlockMyBlock
		name = name.substring(5, 6).toLowerCase(Locale.ENGLISH) + name.substring(6); // 'm' + "yBlock"
		setUnlocalizedName(Constants.modId + "." + name);                            // modname.myBlock
		
		registerBlock();
		
	}
	
	/** Returns the item class used for this block. <br>
	 *  Doesn't have to be an ItemBlock. */
	protected Class<? extends Item> getItemClass() { return ItemBlock.class; }
	
	/** Registers the block in the GameRegistry. */
	protected void registerBlock() {
		String name = getUnlocalizedName();
		name = name.substring(name.lastIndexOf('.') + 1);
		
		Class<? extends Item> itemClass = getItemClass();
		if (ItemBlock.class.isAssignableFrom(itemClass)) {
			GameRegistry.registerBlock(this, (Class<? extends ItemBlock>)itemClass, name, Constants.modId);
		} else {
			GameRegistry.registerBlock(this, ItemBlock.class, name, Constants.modId);
			Item.itemsList[blockID] = null;
			try { itemClass.getConstructor(int.class).newInstance(blockID); }
			catch (Exception e) { throw new RuntimeException(e); }
		}
	}
	
	// Pass actions to TileEntityContainer
	
	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase player, ItemStack stack) {
		getContainer(world, x, y, z).onBlockPlaced(player, stack);
	}
	
	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player,
	                                int side, float hitX, float hitY, float hitZ) {
		return getContainer(world, x, y, z).onBlockActivated(player, side, hitX, hitY, hitZ);
	}
	
	@Override
	public boolean removeBlockByPlayer(World world, EntityPlayer player, int x, int y, int z) {
		if (!getContainer(world, x, y, z).onBlockBreak(player)) return false;
		return super.removeBlockByPlayer(world, player, x, y, z);
	}
	
	@Override
	public void breakBlock(World world, int x, int y, int z, int id, int meta) {
		getContainer(world, x, y, z).onBlockDestroyed();
		super.breakBlock(world, x, y, z, id, meta);
	}
	
	@Override
	public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z) {
		TileEntityContainer container = getContainer(world, x, y, z);
		if (container instanceof IHasAttachments) {
			ItemStack pick = ((IHasAttachments)container).getAttachments().pick(target);
			if (pick != null) return pick;
		}
		return container.onPickBlock(super.getPickBlock(target, world, x, y, z), target);
	}
	
	private TileEntityContainer getContainer(World world, int x, int y, int z) {
		return WorldUtils.get(world, x, y, z, TileEntityContainer.class);
	}
	
}
