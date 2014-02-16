package net.mcft.copy.betterstorage.addon.thaumcraft;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import thaumcraft.api.aspects.Aspect;

public abstract class BackpackFluxEffect extends FluxEffect {
	
	public BackpackFluxEffect(Aspect aspect) {
		super(aspect);
		effects.put(aspect, this);
	}
	
	public abstract void apply(EntityPlayer player, ItemStack backpack);
	
	public static Map<Aspect, FluxEffect> effects = new HashMap<Aspect, FluxEffect>();
	
	static {
		/*
		new BackpackFluxEffect(Aspect.AIR) { @Override public void apply(EntityPlayer player, ItemStack backpack) {
			float motionY = RandomUtils.getFloat(0.3F, 0.65F);
			Packet packet = new Packet28EntityVelocity(player.entityId, 0, motionY, 0);
			((EntityPlayerMP)player).playerNetServerHandler.sendPacketToPlayer(packet);
			player.worldObj.playSoundAtEntity(player, "random.breath", 0.75F, 0.5F);
		} };
		new BackpackFluxEffect(Aspect.FIRE) { @Override public void apply(EntityPlayer player, ItemStack backpack) {
			player.setFire(RandomUtils.getInt(1, 3));
			player.worldObj.playSoundAtEntity(player, "random.breath", 0.75F, 0.5F);
		} };
		new BackpackFluxEffect(Aspect.SOUND) { @Override public void apply(EntityPlayer player, ItemStack backpack) {
			player.worldObj.playSoundAtEntity(player, "enderdragon.growl", 1.2F, 0.75F);
		} };
		new BackpackFluxEffect(Aspect.LIFE) { @Override public void apply(EntityPlayer player, ItemStack backpack) {
			player.addPotionEffect(new PotionEffect(Potion.regeneration.id, RandomUtils.getInt(3 * 20, 5 * 20),0));
			player.worldObj.playSoundAtEntity(player, "random.breath", 0.75F, 0.5F);
		} };
		new BackpackFluxEffect(Aspect.DEATH) { @Override public void apply(EntityPlayer player, ItemStack backpack) {
			player.addPotionEffect(new PotionEffect(Potion.wither.id, RandomUtils.getInt(2 * 20, 4 * 20), 0));
			player.worldObj.playSoundAtEntity(player, "random.breath", 0.75F, 0.5F);
		} };
		new BackpackFluxEffect(Aspect.VOID) { @Override public void apply(EntityPlayer player, ItemStack backpack) {
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
		new BackpackFluxEffect(Aspect.VISION) { @Override public void apply(EntityPlayer player, ItemStack backpack) {
			if (RandomUtils.getFloat() < 0.5)
				player.addPotionEffect(new PotionEffect(Potion.nightVision.id, RandomUtils.getInt(3 * 20, 8 * 20), 0));
			else player.addPotionEffect(new PotionEffect(Potion.confusion.id, RandomUtils.getInt(2 * 20, 7 * 20), 0));
			player.worldObj.playSoundAtEntity(player, "random.breath", 0.75F, 0.5F);
		} };
		new BackpackFluxEffect(Aspect.KNOWLEDGE) { @Override public void apply(EntityPlayer player, ItemStack backpack) {
			if (RandomUtils.getFloat() < 0.5) player.addExperienceLevel(1);
			else player.addExperienceLevel(-1);
			player.worldObj.playSoundAtEntity(player, "random.levelup", 0.75F, 0.5F);
		} };
		new BackpackFluxEffect(Aspect.) { @Override public void apply(EntityPlayer player, ItemStack backpack) {
			((ItemBackpack)backpack.getItem()).damageArmor(player, backpack, DamageSource.generic, 8, 0);
			if (backpack.stackSize <= 0)
				player.inventory.armorInventory[2] = null;
			player.worldObj.playSoundAtEntity(player, "random.break", 0.75F, 0.5F);
		} };
		new BackpackFluxEffect(Aspect.POWER) { @Override public void apply(EntityPlayer player, ItemStack backpack) {
			if (player.worldObj.canLightningStrikeAt((int)player.posX, (int)player.posY, (int)player.posZ))
				player.worldObj.addWeatherEffect(new EntityLightningBolt(player.worldObj, player.posX, player.posY, player.posZ));
			else player.attackEntityFrom(DamageSource.magic, RandomUtils.getInt(2, 4));
		} };
		new BackpackFluxEffect(Aspect.ARMOR) { @Override public void apply(EntityPlayer player, ItemStack backpack) {
			if (RandomUtils.getFloat() < 0.5)
				player.addPotionEffect(new PotionEffect(Potion.resistance.id, RandomUtils.getInt(3 * 20, 11 * 20), 0));
			else player.addPotionEffect(new PotionEffect(Potion.fireResistance.id, RandomUtils.getInt(3 * 20, 11 * 20), 0));
			player.worldObj.playSoundAtEntity(player, "random.breath", 0.75F, 0.5F);
		} };
		new BackpackFluxEffect(Aspect.WEAPON) { @Override public void apply(EntityPlayer player, ItemStack backpack) {
			if (RandomUtils.getFloat() < 0.5)
				player.addPotionEffect(new PotionEffect(Potion.damageBoost.id, RandomUtils.getInt(4 * 20, 15 * 20), 0));
			else player.addPotionEffect(new PotionEffect(Potion.weakness.id, RandomUtils.getInt(3 * 20, 11 * 20), 0));
			player.worldObj.playSoundAtEntity(player, "random.breath", 0.75F, 0.5F);
		} };
		new BackpackFluxEffect(Aspect.TOOL) { @Override public void apply(EntityPlayer player, ItemStack backpack) {
			if (RandomUtils.getFloat() < 0.5)
				player.addPotionEffect(new PotionEffect(Potion.digSpeed.id, RandomUtils.getInt(4 * 20, 13 * 20), 0));
			else player.addPotionEffect(new PotionEffect(Potion.digSlowdown.id, RandomUtils.getInt(3 * 20, 7 * 20), 0));
			player.worldObj.playSoundAtEntity(player, "random.breath", 0.75F, 0.5F);
		} };
		new BackpackFluxEffect(Aspect.POISON) { @Override public void apply(EntityPlayer player, ItemStack backpack) {
			if (RandomUtils.getFloat() < 0.5)
				player.addPotionEffect(new PotionEffect(Potion.hunger.id, RandomUtils.getInt(6 * 20, 19 * 20), 0));
			else player.addPotionEffect(new PotionEffect(Potion.poison.id, RandomUtils.getInt(3 * 20, 6 * 20), 0));
			player.worldObj.playSoundAtEntity(player, "random.breath", 0.75F, 0.5F);
		} };
		new BackpackFluxEffect(Aspect.PURE) { @Override public void apply(EntityPlayer player, ItemStack backpack) {  } };
		new BackpackFluxEffect(Aspect.DARK) { @Override public void apply(EntityPlayer player, ItemStack backpack) {
			player.addPotionEffect(new PotionEffect(Potion.blindness.id, RandomUtils.getInt(1 * 20, 5 * 20), 0));
			player.worldObj.playSoundAtEntity(player, "random.breath", 0.75F, 0.5F);
		} };
		new BackpackFluxEffect(Aspect.MOTION) { @Override public void apply(EntityPlayer player, ItemStack backpack) {
			if (RandomUtils.getFloat() < 0.5)
				player.addPotionEffect(new PotionEffect(Potion.moveSpeed.id, RandomUtils.getInt(6 * 20, 19 * 20), 0));
			else player.addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, RandomUtils.getInt(3 * 20, 6 * 20), 0));
			player.worldObj.playSoundAtEntity(player, "random.breath", 0.75F, 0.5F);
		} };
		new BackpackFluxEffect(Aspect.FLIGHT) { @Override public void apply(EntityPlayer player, ItemStack backpack) {
			player.addPotionEffect(new PotionEffect(Potion.jump.id, RandomUtils.getInt(6 * 20, 19 * 20), RandomUtils.getInt(0, 2)));
			player.worldObj.playSoundAtEntity(player, "random.breath", 0.75F, 0.5F);
		} };
		new BackpackFluxEffect(Aspect.HEAL) { @Override public void apply(EntityPlayer player, ItemStack backpack) {
			if (RandomUtils.getFloat() < 0.5)
				player.heal(RandomUtils.getInt(3, 6));
			else player.attackEntityFrom(DamageSource.magic, RandomUtils.getInt(2, 4));
			player.worldObj.playSoundAtEntity(player, "random.breath", 0.75F, 0.5F);
		} };
		*/
	}
	
}
