package net.mcft.copy.betterstorage.addon.thaumcraft;

import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.mcft.copy.betterstorage.item.ItemBackpack;
import net.mcft.copy.betterstorage.misc.Constants;
import net.mcft.copy.betterstorage.utils.StackUtils;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import thaumcraft.api.IRepairable;
import thaumcraft.api.IRepairableExtended;
import thaumcraft.api.IVisDiscounter;
import thaumcraft.api.ThaumcraftApi;

public class ItemThaumcraftBackpack extends ItemBackpack implements IRepairable, IVisDiscounter {
	
	public ItemThaumcraftBackpack(int id) {
		super(id, material);
		setMaxDamage(288);
	}
	
	@Override
	public String getName() { return Constants.containerThaumcraftBackpack; }
	
	@Override
	public int getColumns() { return 13; }
	
	@Override
	protected int getDefaultColor() { return -1; }
	
	@Override
	public String getArmorTexture(ItemStack stack, Entity entity, int slot, String type) {
		return ThaumcraftResources.thaumcraftBackpackTexture.toString();
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean advancedTooltips) {
		super.addInformation(stack, player, list, advancedTooltips);
		list.add(String.format("%s: %s%%", StatCollector.translateToLocal("tc.visdiscount"), getVisDiscount()));
	}
	
	@Override
	public int getItemEnchantability() { return 25; }
	
	@Override
	public boolean getIsRepairable(ItemStack stack, ItemStack repairMaterial) {
		return StackUtils.matches(stack, ThaumcraftAddon.fabric, false);
	}
	
	@Override
	public void onEquippedUpdate(EntityLivingBase entity, ItemStack backpack) {
		super.onEquippedUpdate(entity, backpack);
		if (!entity.worldObj.isRemote && (entity instanceof EntityPlayer))
			repairItems(backpack, (EntityPlayer)entity);
	}
	
	/* TODO: Readd flux effects.
	
	private void createFlux(EntityPlayer player, ItemStack backpack) {
		
		// Every 5 seconds.
		if (player.ticksExisted % 100 != 0) return;
		
		// Count items over normal backpack capacity.
		IInventory inventory = ItemBackpack.getBackpackItems(player);
		int count = -(getRows() * 9);
		for (int i = 0; i < inventory.getSizeInventory(); i++)
			if (inventory.getStackInSlot(i) != null) count++;
		
		// When count <= 0, return.
		// When count = 1, chance is ~2%.
		// When count = 12, chance is 25%.
		if (RandomUtils.getInt(48) > count) return;
		
		int auraId = ThaumcraftApi.getClosestAuraWithinRange(player.worldObj, player.posX, player.posY, player.posZ, 640);
		if (auraId == -1) return;
		ThaumcraftApi.queueNodeChanges(auraId, 0, 0, false, (new ObjectTags()).add(EnumTag.VOID, 3), 0, 0, 0);
		
	}
	
	private void fluxEffects(EntityPlayer player, ItemStack backpack) {
		
		// Every 10 seconds.
		if (player.ticksExisted % 200 != 0) return;
		
		BackpackFluxEffect effect = (BackpackFluxEffect)FluxEffect.pick(
				BackpackFluxEffect.effects, player.worldObj, player.posX, player.posY, player.posZ);
		if (effect == null) return;
		effect.apply(player, backpack);
		
	}
	*/
	
	public void repairItems(ItemStack stack, EntityPlayer player) {
		
		int time = 30 * 20;
		IInventory backpackInventory = ItemBackpack.getBackpackItems(player);
		int backpackRepair = EnchantmentHelper.getEnchantmentLevel(ThaumcraftApi.enchantRepair, stack);
		if ((backpackRepair <= 0) || ((player.ticksExisted % time) > 0)) return;
		
		for (int i = 0; i < backpackInventory.getSizeInventory(); i++) {
			ItemStack itemStack = backpackInventory.getStackInSlot(i);
			if (itemStack == null) continue;
			Item item = itemStack.getItem();
			int repair = EnchantmentHelper.getEnchantmentLevel(ThaumcraftApi.enchantRepair, itemStack);
			if (!(item instanceof IRepairable) || (repair <= 0) ||
			    ((item instanceof IRepairableExtended) &&
			     !((IRepairableExtended)item).doRepair(itemStack, player, repair))) continue;
			itemStack.setItemDamage(Math.max(itemStack.getItemDamage() - repair, 0));
		}
		
	}
	
	// IVisDiscounter implementation
	
	@Override
	public int getVisDiscount() { return 2; }
	
}
