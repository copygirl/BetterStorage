package net.mcft.copy.betterstorage.item.cardboard;

import net.mcft.copy.betterstorage.BetterStorage;
import net.mcft.copy.betterstorage.misc.Constants;
import net.mcft.copy.betterstorage.utils.MiscUtils;
import net.mcft.copy.betterstorage.utils.StackUtils;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemCardboardSword extends ItemSword implements ICardboardItem {
	
	private String name;
	
	public ItemCardboardSword() {
		super(ItemCardboardSheet.toolMaterial);
		setCreativeTab(BetterStorage.creativeTab);
		setUnlocalizedName(Constants.modId + "." + getItemName());
		GameRegistry.registerItem(this, getItemName());
		BetterStorage.proxy.registerItemRender(this, 0, getItemName());
	}
	
	/** Returns the name of this item, for example "drinkingHelmet". */
	public String getItemName() {
		return ((name != null) ? name : (name = MiscUtils.getName(this)));
	}
	
	/*@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister iconRegister) {
		itemIcon = iconRegister.registerIcon(Constants.modId + ":" + getItemName());
	}*/
	
	@Override
	@SideOnly(Side.CLIENT)
	public int getColorFromItemStack(ItemStack stack, int renderPass) {
		return StackUtils.get(stack, 0x705030, "display", "color");
	}
	
	@Override
	public boolean canDye(ItemStack stack) { return true; }
	
	// Makes sure cardboard tools don't get destroyed,
	// and are ineffective when durability is at 0.
	@Override public boolean canHarvestBlock(Block block, ItemStack stack) {
		return ItemCardboardSheet.canHarvestBlock(stack, super.canHarvestBlock(block, stack)); }
	@Override public boolean onLeftClickEntity(ItemStack stack, EntityPlayer player, Entity entity) {
		return !ItemCardboardSheet.isEffective(stack); }
	@Override  public boolean onBlockDestroyed(ItemStack stack, World world, Block block, BlockPos pos, EntityLivingBase player) {
		return ItemCardboardSheet.onBlockDestroyed(world, block, pos, stack, player); }
	@Override public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase player) {
		return ItemCardboardSheet.damageItem(stack, 1, player); }
	
}
