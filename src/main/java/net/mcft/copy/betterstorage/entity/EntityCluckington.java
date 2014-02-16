package net.mcft.copy.betterstorage.entity;

import net.mcft.copy.betterstorage.utils.StackUtils;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentThorns;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAITaskEntry;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityCluckington extends EntityChicken {
	
	private int healTimer = 0;
	
	public EntityCluckington(World world) {
		super(world);
        setSize(0.6F, 0.8F);
		EntityAITaskEntry panic  = (EntityAITaskEntry)tasks.taskEntries.get(1);
		EntityAITaskEntry mate   = (EntityAITaskEntry)tasks.taskEntries.get(2);
		EntityAITaskEntry tempt  = (EntityAITaskEntry)tasks.taskEntries.get(3);
		EntityAITaskEntry follow = (EntityAITaskEntry)tasks.taskEntries.get(4);
		tasks.removeTask(panic.action);
		tasks.removeTask(mate.action);
		tasks.removeTask(tempt.action);
		tasks.removeTask(follow.action);
		tasks.addTask(2, new EntityAIAttackOnCollide(this, EntityZombie.class, 1.0D, false));
		tasks.addTask(2, new EntityAIAttackOnCollide(this, EntityPlayer.class, 1.0D, false));
        targetTasks.addTask(1, new EntityAIHurtByTarget(this, true));
        targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityZombie.class, 0, true));
	}
	
	@Override
	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		getEntityAttribute(SharedMonsterAttributes.maxHealth).setAttribute(30.0);
		getAttributeMap().func_111150_b(SharedMonsterAttributes.attackDamage).setAttribute(2.0D);
	}
	
	@Override
	public boolean hasCustomNameTag() { return true; }
	@Override
	public String getCustomNameTag() { return "Cluckington"; }
	@Override
	public String getEntityName() { return "Cluckington"; }
	
	@Override
	public boolean attackEntityAsMob(Entity entity) {
		float attackDamage = (float)getEntityAttribute(SharedMonsterAttributes.attackDamage).getAttributeValue();
		int knockback = 0;
		if (entity instanceof EntityLivingBase) {
			attackDamage += EnchantmentHelper.getEnchantmentModifierLiving(this, (EntityLivingBase)entity);
			knockback += EnchantmentHelper.getKnockbackModifier(this, (EntityLivingBase)entity);
		}
		if (!entity.attackEntityFrom(DamageSource.causeMobDamage(this), attackDamage))
			return false;
		if (knockback > 0) {
			entity.addVelocity(-MathHelper.sin(rotationYaw * (float)Math.PI / 180.0F) * knockback * 0.5F, 0.1F,
			                    MathHelper.cos(rotationYaw * (float)Math.PI / 180.0F) * knockback * 0.5F);
			motionX *= 0.6D;
			motionZ *= 0.6D;
		}
		int fire = EnchantmentHelper.getFireAspectModifier(this);
		if (fire > 0) entity.setFire(fire * 4);
		if (entity instanceof EntityLivingBase)
			EnchantmentThorns.func_92096_a(this, (EntityLivingBase)entity, rand);
		return true;
	}
	
	@Override
	public boolean isBreedingItem(ItemStack stack) {
		return ((stack.getItem() == Item.skull) &&
		        "wyld".equalsIgnoreCase(StackUtils.get(stack, "", "SkullOwner")));
	}
	
	@Override
	public void onLivingUpdate() {
		super.onLivingUpdate();
		if (++healTimer > 40) {
			healTimer = 0;
			heal(1.0F);
		}
	}
	
	public static void spawn(EntityChicken target) {
		target.setHealth(0);
		target.setDead();
		EntityCluckington cluck = new EntityCluckington(target.worldObj);
		cluck.setPositionAndRotation(target.posX, target.posY, target.posZ, target.rotationYaw, target.rotationPitch);
		cluck.worldObj.spawnEntityInWorld(cluck);
		cluck.worldObj.createExplosion(cluck, cluck.posX, cluck.posY, cluck.posZ, 1, true);
	}
	
}
