package net.mcft.copy.betterstorage.addon.thaumcraft;

import net.mcft.copy.betterstorage.item.ItemBackpack;
import net.mcft.copy.betterstorage.misc.Constants;
import net.mcft.copy.betterstorage.utils.RandomUtils;
import net.mcft.copy.betterstorage.utils.StackUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import thaumcraft.api.EnumTag;
import thaumcraft.api.IVisDiscounter;
import thaumcraft.api.IVisRepairable;
import thaumcraft.api.ObjectTags;
import thaumcraft.api.ThaumcraftApi;

public class ItemThaumcraftBackpack extends ItemBackpack implements IVisRepairable, IVisDiscounter {
	
	public ItemThaumcraftBackpack(int id) {
		super(id, material);
		setMaxDamage(300);
	}
	
	@Override
	public String getName() { return Constants.containerThaumcraftBackpack; }
	
	@Override
	public int getColumns() { return 13; }
	
	@Override
	public String getArmorTexture(ItemStack stack, Entity entity, int slot, String type) {
		return ThaumcraftResources.thaumcraftBackpackTexture.toString();
	}
	
	@Override
	public int getItemEnchantability() { return 25; }
	
	@Override
	public boolean getIsRepairable(ItemStack stack, ItemStack repairMaterial) {
		return StackUtils.matches(stack, ThaumcraftAddon.fabric, false);
	}
	
	@Override
	public void onArmorTickUpdate(World world, EntityPlayer player, ItemStack itemStack) {
		super.onArmorTickUpdate(world, player, itemStack);
		if (player.worldObj.isRemote || (itemStack.stackSize == 0)) return;
		createFlux(player, itemStack);
		fluxEffects(player, itemStack);
		repairItems(player, itemStack);
	}
	
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
	
	private void repairItems(EntityPlayer player, ItemStack backpack) {
		
		// TODO
		
	}
	
	// Thaumcraft implementations
	
	@Override
	public void doRepair(ItemStack stack, Entity e) {
		if (ThaumcraftApi.decreaseClosestAura(e.worldObj, e.posX, e.posY, e.posZ, 1, true))
			stack.damageItem(-1, (EntityLiving)e);
	}
	@Override
	public int getVisDiscount() { return 5; }
	
}
