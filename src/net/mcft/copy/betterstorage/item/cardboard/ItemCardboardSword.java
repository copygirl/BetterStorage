package net.mcft.copy.betterstorage.item.cardboard;

import net.mcft.copy.betterstorage.BetterStorage;
import net.mcft.copy.betterstorage.misc.Constants;
import net.mcft.copy.betterstorage.utils.StackUtils;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemCardboardSword extends ItemSword implements ICardboardItem {
	
	public ItemCardboardSword(int id) {
		super(id - 256, ItemCardboardSheet.toolMaterial);
		setCreativeTab(BetterStorage.creativeTab);
		setUnlocalizedName(Constants.modId + ".cardboardSword");
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister iconRegister) {
		itemIcon = iconRegister.registerIcon(Constants.modId + ":cardboardSword");
	}
	
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
	@Override public boolean onBlockDestroyed(ItemStack stack, World world, int blockID, int x, int y, int z, EntityLivingBase player) {
		return ItemCardboardSheet.onBlockDestroyed(world, blockID, x, y, z, stack, player); }
	@Override public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase player) {
		return ItemCardboardSheet.damageItem(stack, 1, player); }
	
}
