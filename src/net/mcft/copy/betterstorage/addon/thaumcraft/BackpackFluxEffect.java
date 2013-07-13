package net.mcft.copy.betterstorage.addon.thaumcraft;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.mcft.copy.betterstorage.item.ItemBackpack;
import net.mcft.copy.betterstorage.utils.RandomUtils;
import net.mcft.copy.betterstorage.utils.WorldUtils;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet28EntityVelocity;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import thaumcraft.api.EnumTag;

public abstract class BackpackFluxEffect extends FluxEffect {
	
	public BackpackFluxEffect(EnumTag aspect) {
		super(aspect);
		effects.put(aspect, this);
	}
	
	public abstract void apply(EntityPlayer player, ItemStack backpack);
	
	public static Map<EnumTag, FluxEffect> effects = new HashMap<EnumTag, FluxEffect>();
	
	static {
		new BackpackFluxEffect(EnumTag.WIND) { @Override public void apply(EntityPlayer player, ItemStack backpack) {
			float motionY = RandomUtils.getFloat(0.3F, 0.65F);
			Packet packet = new Packet28EntityVelocity(player.entityId, 0, motionY, 0);
			((EntityPlayerMP)player).playerNetServerHandler.sendPacketToPlayer(packet);
			player.worldObj.playSoundAtEntity(player, "random.breath", 0.75F, 0.5F);
		} };
		new BackpackFluxEffect(EnumTag.FIRE) { @Override public void apply(EntityPlayer player, ItemStack backpack) {
			player.setFire(RandomUtils.getInt(1, 3));
			player.worldObj.playSoundAtEntity(player, "random.breath", 0.75F, 0.5F);
		} };
		new BackpackFluxEffect(EnumTag.SOUND) { @Override public void apply(EntityPlayer player, ItemStack backpack) {
			player.worldObj.playSoundAtEntity(player, "enderdragon.growl", 1.2F, 0.75F);
		} };
		new BackpackFluxEffect(EnumTag.LIFE) { @Override public void apply(EntityPlayer player, ItemStack backpack) {
			player.addPotionEffect(new PotionEffect(Potion.regeneration.id, RandomUtils.getInt(3 * 20, 5 * 20),0));
			player.worldObj.playSoundAtEntity(player, "random.breath", 0.75F, 0.5F);
		} };
		new BackpackFluxEffect(EnumTag.DEATH) { @Override public void apply(EntityPlayer player, ItemStack backpack) {
			player.addPotionEffect(new PotionEffect(Potion.wither.id, RandomUtils.getInt(2 * 20, 4 * 20), 0));
			player.worldObj.playSoundAtEntity(player, "random.breath", 0.75F, 0.5F);
		} };
		new BackpackFluxEffect(EnumTag.VOID) { @Override public void apply(EntityPlayer player, ItemStack backpack) {
			ItemBackpack backpackType = (ItemBackpack)backpack.getItem();
			int size = backpackType.getRows() * backpackType.getColumns();
			IInventory inventory = ItemBackpack.getBackpackItems(player);
			List<Integer> indices = new ArrayList<Integer>();
			for (int i = 0; i < inventory.getSizeInventory(); i++)
				if (inventory.getStackInSlot(i) != null) indices.add(i);
			if (indices.size() <= 0) return;
			int index = indices.get(RandomUtils.getInt(indices.size()));
			ItemStack item = inventory.getStackInSlot(index).copy();
			ItemStack drop = item.splitStack(1);
			WorldUtils.dropStackFromEntity(player, drop, 1.0F).delayBeforeCanPickup = 8;
			if (item.stackSize <= 0) item = null;
			inventory.setInventorySlotContents(index, item);
			player.worldObj.playSoundAtEntity(player, "random.pop", 0.75F, 0.5F);
		} };
		new BackpackFluxEffect(EnumTag.VISION) { @Override public void apply(EntityPlayer player, ItemStack backpack) {
			if (RandomUtils.getFloat() < 0.5)
				player.addPotionEffect(new PotionEffect(Potion.nightVision.id, RandomUtils.getInt(3 * 20, 8 * 20), 0));
			else player.addPotionEffect(new PotionEffect(Potion.confusion.id, RandomUtils.getInt(2 * 20, 7 * 20), 0));
			player.worldObj.playSoundAtEntity(player, "random.breath", 0.75F, 0.5F);
		} };
		new BackpackFluxEffect(EnumTag.KNOWLEDGE) { @Override public void apply(EntityPlayer player, ItemStack backpack) {
			if (RandomUtils.getFloat() < 0.5) player.addExperienceLevel(1);
			else player.addExperienceLevel(-1);
			player.worldObj.playSoundAtEntity(player, "random.levelup", 0.75F, 0.5F);
		} };
		new BackpackFluxEffect(EnumTag.DESTRUCTION) { @Override public void apply(EntityPlayer player, ItemStack backpack) {
			((ItemBackpack)backpack.getItem()).damageArmor(player, backpack, DamageSource.generic, 8, 0);
			if (backpack.stackSize <= 0)
				player.inventory.armorInventory[2] = null;
			player.worldObj.playSoundAtEntity(player, "random.break", 0.75F, 0.5F);
		} };
		new BackpackFluxEffect(EnumTag.POWER) { @Override public void apply(EntityPlayer player, ItemStack backpack) {
			if (player.worldObj.canLightningStrikeAt((int)player.posX, (int)player.posY, (int)player.posZ))
				player.worldObj.addWeatherEffect(new EntityLightningBolt(player.worldObj, player.posX, player.posY, player.posZ));
			else player.attackEntityFrom(DamageSource.magic, RandomUtils.getInt(2, 4));
		} };
		new BackpackFluxEffect(EnumTag.ARMOR) { @Override public void apply(EntityPlayer player, ItemStack backpack) {
			if (RandomUtils.getFloat() < 0.5)
				player.addPotionEffect(new PotionEffect(Potion.resistance.id, RandomUtils.getInt(3 * 20, 11 * 20), 0));
			else player.addPotionEffect(new PotionEffect(Potion.fireResistance.id, RandomUtils.getInt(3 * 20, 11 * 20), 0));
			player.worldObj.playSoundAtEntity(player, "random.breath", 0.75F, 0.5F);
		} };
		new BackpackFluxEffect(EnumTag.WEAPON) { @Override public void apply(EntityPlayer player, ItemStack backpack) {
			if (RandomUtils.getFloat() < 0.5)
				player.addPotionEffect(new PotionEffect(Potion.damageBoost.id, RandomUtils.getInt(4 * 20, 15 * 20), 0));
			else player.addPotionEffect(new PotionEffect(Potion.weakness.id, RandomUtils.getInt(3 * 20, 11 * 20), 0));
			player.worldObj.playSoundAtEntity(player, "random.breath", 0.75F, 0.5F);
		} };
		new BackpackFluxEffect(EnumTag.TOOL) { @Override public void apply(EntityPlayer player, ItemStack backpack) {
			if (RandomUtils.getFloat() < 0.5)
				player.addPotionEffect(new PotionEffect(Potion.digSpeed.id, RandomUtils.getInt(4 * 20, 13 * 20), 0));
			else player.addPotionEffect(new PotionEffect(Potion.digSlowdown.id, RandomUtils.getInt(3 * 20, 7 * 20), 0));
			player.worldObj.playSoundAtEntity(player, "random.breath", 0.75F, 0.5F);
		} };
		new BackpackFluxEffect(EnumTag.POISON) { @Override public void apply(EntityPlayer player, ItemStack backpack) {
			if (RandomUtils.getFloat() < 0.5)
				player.addPotionEffect(new PotionEffect(Potion.hunger.id, RandomUtils.getInt(6 * 20, 19 * 20), 0));
			else player.addPotionEffect(new PotionEffect(Potion.poison.id, RandomUtils.getInt(3 * 20, 6 * 20), 0));
			player.worldObj.playSoundAtEntity(player, "random.breath", 0.75F, 0.5F);
		} };
		new BackpackFluxEffect(EnumTag.PURE) { @Override public void apply(EntityPlayer player, ItemStack backpack) {  } };
		new BackpackFluxEffect(EnumTag.DARK) { @Override public void apply(EntityPlayer player, ItemStack backpack) {
			player.addPotionEffect(new PotionEffect(Potion.blindness.id, RandomUtils.getInt(1 * 20, 5 * 20), 0));
			player.worldObj.playSoundAtEntity(player, "random.breath", 0.75F, 0.5F);
		} };
		new BackpackFluxEffect(EnumTag.MOTION) { @Override public void apply(EntityPlayer player, ItemStack backpack) {
			if (RandomUtils.getFloat() < 0.5)
				player.addPotionEffect(new PotionEffect(Potion.moveSpeed.id, RandomUtils.getInt(6 * 20, 19 * 20), 0));
			else player.addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, RandomUtils.getInt(3 * 20, 6 * 20), 0));
			player.worldObj.playSoundAtEntity(player, "random.breath", 0.75F, 0.5F);
		} };
		new BackpackFluxEffect(EnumTag.FLIGHT) { @Override public void apply(EntityPlayer player, ItemStack backpack) {
			player.addPotionEffect(new PotionEffect(Potion.jump.id, RandomUtils.getInt(6 * 20, 19 * 20), RandomUtils.getInt(0, 2)));
			player.worldObj.playSoundAtEntity(player, "random.breath", 0.75F, 0.5F);
		} };
		new BackpackFluxEffect(EnumTag.HEAL) { @Override public void apply(EntityPlayer player, ItemStack backpack) {
			if (RandomUtils.getFloat() < 0.5)
				player.heal(RandomUtils.getInt(3, 6));
			else player.attackEntityFrom(DamageSource.magic, RandomUtils.getInt(2, 4));
			player.worldObj.playSoundAtEntity(player, "random.breath", 0.75F, 0.5F);
		} };
	}
	
}
