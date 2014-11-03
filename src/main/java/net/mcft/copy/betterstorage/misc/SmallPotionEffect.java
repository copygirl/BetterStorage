package net.mcft.copy.betterstorage.misc;

import net.mcft.copy.betterstorage.item.ItemDrinkingHelmet;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;

public class SmallPotionEffect extends PotionEffect {
	
	public SmallPotionEffect(int potionID, int duration, int amplifier) {
		super(potionID, Math.max(1, duration / ItemDrinkingHelmet.maxUses), amplifier);
	}
	public SmallPotionEffect(PotionEffect effect, PotionEffect active) {
		super(effect.getPotionID(), getDuration(effect, active), effect.getAmplifier());
	}
	private static int getDuration(PotionEffect effect, PotionEffect active) {
		return effect.getDuration() / ItemDrinkingHelmet.maxUses + ((active != null) ? active.getDuration() : 0);
	}
	
	@Override
	public void performEffect(EntityLivingBase entity) {
		int smallEffect = 6 * (getAmplifier() + 1) / ItemDrinkingHelmet.maxUses;
		Potion potion = Potion.potionTypes[getPotionID()];
		if (entity.isEntityUndead() ? (potion == Potion.heal) : (potion == Potion.harm))
			entity.attackEntityFrom(DamageSource.magic, smallEffect);
		else if (potion == Potion.heal) entity.heal(smallEffect);
		else super.performEffect(entity);
	}
	
}
