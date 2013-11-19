package net.mcft.copy.betterstorage.item;

import java.util.ArrayList;
import java.util.List;

import net.mcft.copy.betterstorage.BetterStorage;
import net.mcft.copy.betterstorage.Config;
import net.mcft.copy.betterstorage.client.model.ModelDrinkingHelmet;
import net.mcft.copy.betterstorage.misc.Constants;
import net.mcft.copy.betterstorage.misc.CurrentItem;
import net.mcft.copy.betterstorage.misc.Resources;
import net.mcft.copy.betterstorage.misc.SmallPotionEffect;
import net.mcft.copy.betterstorage.utils.LanguageUtils;
import net.mcft.copy.betterstorage.utils.StackUtils;
import net.minecraft.block.Block;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumArmorMaterial;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.common.EnumHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemDrinkingHelmet extends ItemArmor {
	
	public static final int maxUses = 12;
	
	public static final EnumArmorMaterial material = EnumHelper.addArmorMaterial(
			"drinkingHelmet", 11, new int[]{ 3, 0, 0, 0 }, 15);
	static { material.customCraftingMaterial = Item.itemsList[Block.blockRedstone.blockID]; }
	
	public ItemDrinkingHelmet(int id) {
		super(id - 256, material, 0, 0);
		setCreativeTab(BetterStorage.creativeTab);
		setUnlocalizedName(Constants.modId + ".drinkingHelmet");
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister iconRegister) {
		itemIcon = iconRegister.registerIcon(Constants.modId + ":drinkingHelmet");
	}
	
	@Override
	public String getArmorTexture(ItemStack stack, Entity entity, int slot, String type) {
		return Resources.drinkingHelmetTexture.toString();
	}
	@Override
	@SideOnly(Side.CLIENT)
	public ModelBiped getArmorModel(EntityLivingBase entity, ItemStack stack, int slot) {
		return ModelDrinkingHelmet.instance;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public EnumRarity getRarity(ItemStack stack) {
		return (StackUtils.has(stack, "display", "Lore") ? EnumRarity.epic : EnumRarity.rare);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean advancedTooltips) {
		int uses = StackUtils.get(stack, 0, "uses");
		if ((uses > 0) && (getDrinkingHelmet(player) == stack)) {
			String str = GameSettings.getKeyDisplayString(Config.drinkingHelmetKey);
			LanguageUtils.translateTooltip(list, "drinkingHelmet.useHint", "%KEY%", str);
		}
		for (ItemStack potion : getPotions(stack)) {
			if (potion == null) continue;
			String name = potion.getDisplayName();
			if ((list.size() > 0) && (list.get(list.size() - 1).equals(EnumChatFormatting.DARK_AQUA + name)))
				list.set(list.size() - 1, EnumChatFormatting.DARK_AQUA + "2x " + name);
			else list.add(EnumChatFormatting.DARK_AQUA + name);
		}
		if (uses > 0)
			list.add(EnumChatFormatting.DARK_GRAY.toString() + EnumChatFormatting.ITALIC + 
			         LanguageUtils.translateTooltip("drinkingHelmet.uses", "%USES%", Integer.toString(uses)));
		else if (Config.enableHelpTooltips)
			list.add(LanguageUtils.translateTooltip("drinkingHelmet.craftHint"));
	}
	
	public static ItemStack[] getPotions(ItemStack drinkingHelmet) {
		return StackUtils.getStackContents(drinkingHelmet, 2);
	}
	public static void setPotions(ItemStack drinkingHelmet, ItemStack[] potions) {
		if (potions != null) {
			StackUtils.setStackContents(drinkingHelmet, potions);
			StackUtils.set(drinkingHelmet, maxUses, "uses");
		} else {
			StackUtils.remove(drinkingHelmet, "Items");
			StackUtils.remove(drinkingHelmet, "uses");
		}
	}
	
	public static ItemStack getDrinkingHelmet(EntityLivingBase entity) {
		ItemStack drinkingHelmet = entity.getCurrentItemOrArmor(CurrentItem.HEAD);
		if ((drinkingHelmet != null) && (drinkingHelmet.getItem() instanceof ItemDrinkingHelmet))
			return drinkingHelmet;
		else return null;
	}
	
	public static void use(EntityPlayer player) {
		
		ItemStack drinkingHelmet = getDrinkingHelmet(player);
		if (drinkingHelmet == null) return;
		int uses = StackUtils.get(drinkingHelmet, 0, "uses");
		if (uses <= 0) return;
		
		ItemStack[] potions = StackUtils.getStackContents(drinkingHelmet, 2);
		List<PotionEffect> potionEffects = new ArrayList<PotionEffect>();
		for (ItemStack item : potions)
			if (item.getItem() instanceof ItemPotion) {
				List<PotionEffect> effects = ((ItemPotion)item.getItem()).getEffects(item);
				if (effects != null)
					potionEffects.addAll(effects);
			}
		
		for (PotionEffect effect : potionEffects) {
			effect = new SmallPotionEffect(effect);
			Potion potion = Potion.potionTypes[effect.getPotionID()];
			PotionEffect active = player.getActivePotionEffect(potion);
			if (active != null)
				effect.duration += active.duration;
			player.addPotionEffect(effect);
		}
		
		player.worldObj.playSoundAtEntity(player, "random.drink", 0.5F, 0.9F + player.worldObj.rand.nextFloat() * 0.1F);
		if (--uses <= 0) setPotions(drinkingHelmet, null);
		else StackUtils.set(drinkingHelmet, uses, "uses");
		
	}
	
}
