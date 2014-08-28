package net.mcft.copy.betterstorage.item;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.mcft.copy.betterstorage.content.BetterStorageItems;
import net.mcft.copy.betterstorage.misc.Constants;
import net.mcft.copy.betterstorage.misc.EquipmentSlot;
import net.mcft.copy.betterstorage.utils.LanguageUtils;
import net.mcft.copy.betterstorage.utils.StackUtils;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.Facing;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import net.minecraft.util.StringUtils;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemBucketSlime extends ItemBetterStorage {
	
	private static Map<String, Handler> handlers = new HashMap<String, Handler>();
	
	private IIcon empty;
	
	public ItemBucketSlime() {
		setContainerItem(Items.bucket);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister iconRegister) {
		empty = iconRegister.registerIcon(Constants.modId + ":bucketSlime_empty");
		for (Handler handler : handlers.values())
			handler.registerIcon(iconRegister);
	}
	
	@Override
	public IIcon getIcon(ItemStack stack, int pass) {
		if (pass == 0) {
			Handler handler = getHandler(stack);
			return ((handler != null) ? handler.icon : null);
		} else return empty;
	}
	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIconIndex(ItemStack stack) { return getIcon(stack, 0); }
	
	@Override
	@SideOnly(Side.CLIENT)
	public boolean requiresMultipleRenderPasses() { return true; }
	
	@Override
	@SideOnly(Side.CLIENT)
	public boolean hasEffect(ItemStack stack, int pass) {
		return (stack.isItemEnchanted() ||
		        ((pass == 0) && StackUtils.has(stack, "Effects")));
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean advancedTooltips) {
		
		String id = getSlimeId(stack);
		Handler handler = getHandler(id);
		String name = StackUtils.get(stack, (String)null, "Slime", "name");
		if ((name != null) || advancedTooltips) 
			list.add("Contains: " + ((name != null) ? ("\"" + name + "\"" +
			                                           (advancedTooltips ? " (" + id + ")" : "")) : id));
		
		NBTTagList effectList = (NBTTagList)StackUtils.getTag(stack, "Effects");
		if ((effectList != null) && (handler != null)) {
			int max = (advancedTooltips ? 8 : 3);
			
			for (int i = 0; i < Math.min(effectList.tagCount(), max); i++) {
				PotionEffect effect = PotionEffect.readCustomPotionEffectFromNBT(
						effectList.getCompoundTagAt(i));
				Potion potion = Potion.potionTypes[effect.getPotionID()];
				int duration = (int)(effect.getDuration() * handler.durationMultiplier()); 
				
				StringBuilder str = new StringBuilder()
					.append(potion.isBadEffect() ? EnumChatFormatting.RED : EnumChatFormatting.GRAY)
					.append(StatCollector.translateToLocal(effect.getEffectName()));
				if (effect.getAmplifier() > 0)
					str.append(" ").append(StatCollector.translateToLocal("potion.potency." + effect.getAmplifier()));
				str.append(" (").append(StringUtils.ticksToElapsedTime(duration)).append(")");
				
				list.add(str.toString());
			}
			
			int more = (effectList.tagCount() - max);
			if (more > 0)
				list.add(EnumChatFormatting.DARK_GRAY.toString() + EnumChatFormatting.ITALIC +
						LanguageUtils.translateTooltip(
								"bucketSlime.more." + ((more == 1) ? "1" : "x"),
								"%X%", Integer.toString(more)));
		}
		
	}
	
	@Override
	public boolean onItemUse(ItemStack stack, EntityPlayer player,
	                         World world, int x, int y, int z, int side,
	                         float hitX, float hitY, float hitZ) {
		// If player is sneaking, eat slime
		// instead of placing it in the world.
		if (player.isSneaking()) return false;
		
		if (world.isRemote) return true;
		
		x += Facing.offsetsXForSide[side];
		y += Facing.offsetsYForSide[side];
		z += Facing.offsetsZForSide[side];
		
		String id = getSlimeId(stack);
		String name = StackUtils.get(stack, (String)null, "Slime", "name");
		Entity entity = EntityList.createEntityByName(id, world);
		Handler handler = getHandler(id);
		
		if ((entity != null) && (handler != null) &&
		    (entity instanceof EntityLiving)) {
			EntityLiving slime = (EntityLiving)entity;
			
			float rotation = MathHelper.wrapAngleTo180_float(world.rand.nextFloat() * 360.0F);
			slime.setLocationAndAngles(x + 0.5, y, z + 0.5, rotation, 0.0F);
			slime.rotationYawHead = slime.renderYawOffset = rotation;
			
			if (name != null) slime.setCustomNameTag(name);
			handler.setSize(slime, 1);
			
			NBTTagList effectList = (NBTTagList)StackUtils.getTag(stack, "Effects");
			if (effectList != null)
				for (int i = 0; i < effectList.tagCount(); i++)
					slime.addPotionEffect(PotionEffect.readCustomPotionEffectFromNBT(
							effectList.getCompoundTagAt(i)));
			
			world.spawnEntityInWorld(slime);
			slime.playSound("mob.slime.big", 1.2F, 0.6F);
			
			player.setCurrentItemOrArmor(EquipmentSlot.HELD, new ItemStack(Items.bucket));
		}
		
		return true;
	}
	
	// Eating slime
	
	@Override
	public EnumAction getItemUseAction(ItemStack stack) { return EnumAction.drink; }
	
	@Override
	public int getMaxItemUseDuration(ItemStack stack) { return 48; }
	
	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
		player.setItemInUse(stack, getMaxItemUseDuration(stack));
		return stack;
	}
	
	@Override
	public ItemStack onEaten(ItemStack stack, World world, EntityPlayer player) {
		Handler handler = getHandler(stack);
		if (handler != null) {
			player.getFoodStats().addStats(handler.foodAmount(), handler.saturationAmount());
			NBTTagList effectList = (NBTTagList)StackUtils.getTag(stack, "Effects");
			if (effectList != null) {
				for (int i = 0; i < effectList.tagCount(); i++) {
					PotionEffect effect = PotionEffect.readCustomPotionEffectFromNBT(effectList.getCompoundTagAt(i));
					int duration = (int)(effect.getDuration() * handler.durationMultiplier());
					effect = new PotionEffect(effect.getPotionID(), duration, effect.getAmplifier());
					player.addPotionEffect(effect);
				}
			}
			handler.onEaten(player, false);
		}
		return new ItemStack(Items.bucket);
	}
	
	// Helper functions
	
	/** Called when a player right clicks an entity with an empty bucket.
	 *  If the entity clicked is a small slime, attempts to pick it up and
	 *  sets the held item to a Slime in a Bucket containing that slime. */
	public static void pickUpSlime(EntityPlayer player, EntityLiving slime) {
		Handler handler = getHandler(slime);
		if (slime.isDead || (handler == null) ||
		    (handler.getSize(slime) != 1)) return;
		
		ItemStack stack = new ItemStack(BetterStorageItems.slimeBucket);
		
		String entityId = EntityList.getEntityString(slime);
		if (!entityId.equals("Slime"))
			StackUtils.set(stack, entityId, "Slime", "id");
		if (slime.hasCustomNameTag())
			StackUtils.set(stack, slime.getCustomNameTag(), "Slime", "name");
		
		Collection<PotionEffect> effects = slime.getActivePotionEffects();
		if (!effects.isEmpty()) {
			NBTTagList effectList = new NBTTagList();
			for (PotionEffect effect : effects)
				effectList.appendTag(effect.writeCustomPotionEffectToNBT(new NBTTagCompound()));
			StackUtils.set(stack, effectList, "Effects");
		}
		
		if (--player.getCurrentEquippedItem().stackSize <= 0)
			player.setCurrentItemOrArmor(EquipmentSlot.HELD, stack);
		else player.dropPlayerItemWithRandomChoice(stack, true);
		
		slime.playSound("mob.slime.big", 1.2F, 0.8F);
		slime.isDead = true;
	}
	
	/** Returns the slime ID from a slime bucket. */
	public static String getSlimeId(ItemStack stack) {
		return StackUtils.get(stack, "Slime", "Slime", "id");
	}
	
	/** Registers a slime handler. */
	public static void registerHandler(Handler handler) {
		handlers.put(handler.entityName, handler);
	}
	/** Returns the handler for an entity id, null if none. */
	public static Handler getHandler(String id) { return handlers.get(id); }
	/** Returns the handler for an entity, null if none. */
	public static Handler getHandler(EntityLiving slime) {
		return getHandler(EntityList.getEntityString(slime));
	}
	/** Returns the handler for slime bucket, null if none. */
	public static Handler getHandler(ItemStack stack) {
		return getHandler(getSlimeId(stack));
	}
	
	// Slime handlers
	
	static {
		registerHandler(new Handler("slime", "Slime") {
			@Override public int foodAmount() { return 3; }
			@Override public float saturationAmount() { return 0.2F; }
		});
		registerHandler(new Handler("magmaCube", "LavaSlime") {
			@Override public float durationMultiplier() { return 0.4F; }
			@Override public void onEaten(EntityPlayer player, boolean potionEffects) {
				player.setFire(2);
				player.addPotionEffect(new PotionEffect(Potion.jump.id, (potionEffects ? 10 : 20) * 20,
				                                                        (potionEffects ? 2 : 3)));
				player.addPotionEffect(new PotionEffect(Potion.damageBoost.id, (potionEffects ? 24 : 32) * 20, 0));
			}});
		registerHandler(new Handler("mazeSlime", "TwilightForest.Maze Slime") {
			@Override public void onEaten(EntityPlayer player, boolean potionEffects) {
				player.attackEntityFrom(DamageSource.magic, 3.0F);
				player.addPotionEffect(new PotionEffect(Potion.jump.id, (potionEffects ? 4 : 8) * 20, 0));
				player.addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, (potionEffects ? 4 : 8) * 20, 1));
				player.addPotionEffect(new PotionEffect(Potion.resistance.id, 30 * 20, 1));
			}});
		registerHandler(new Handler("pinkSlime", "MineFactoryReloaded.mfrEntityPinkSlime") {
			@Override public int foodAmount() { return 6; }
			@Override public float saturationAmount() { return 0.75F; }
			@Override public void onEaten(EntityPlayer player, boolean potionEffects) {
				super.onEaten(player, potionEffects);
				player.addPotionEffect(new PotionEffect(Potion.field_76434_w.id, (potionEffects ? 40 : 60) * 20, 4));
			}
		});
		registerHandler(new Handler("thaumicSlime", "Thaumcraft.ThaumSlime") {
			private int potionFluxId = -1;
			@Override public float durationMultiplier() { return 1.0F; }
			@Override public void onEaten(EntityPlayer player, boolean potionEffects) {
				if (potionFluxId == -1) {
					// Look for flux potion effect.
					for (Potion potion : Potion.potionTypes)
						if (potion.getName() == "potion.fluxtaint") {
							potionFluxId = potion.id; break; }
					// If not found, just use wither.
					if (potionFluxId == -1)
						potionFluxId = Potion.wither.id;
				}
				super.onEaten(player, potionEffects);
				player.addPotionEffect(new PotionEffect(potionFluxId, 8 * 20, 0));
			}
		});
		registerHandler(new Handler("blueSlime", "TConstruct.EdibleSlime") {
			@Override public float durationMultiplier() { return 0.2F; }
		});
	}
	
	public static class Handler {
		
		public final String name;
		public final String entityName;
		
		public IIcon icon;
		
		public Handler(String name, String entityName) {
			this.name = name;
			this.entityName = entityName;
		}
		
		/** Returns the icon location to be used in registerIcons. */
		public void registerIcon(IIconRegister iconRegister) {
			icon = iconRegister.registerIcon(Constants.modId + ":bucketSlime_" + name);
		}
		
		/** Returns the size of the slime. */
		public int getSize(EntityLiving slime) {
			return slime.getDataWatcher().getWatchableObjectByte(16);
		}
		
		/** Sets the size of the slime. */
		public void setSize(EntityLiving slime, int size) {
			NBTTagCompound compound = new NBTTagCompound();
			slime.writeToNBT(compound);
			compound.setInteger("Size", size - 1);
			slime.readFromNBT(compound);
		}
		
		/** How much food is restored when eating this slime. */
		public int foodAmount() { return 4; }
		
		/** The satuation amount added when eating this slime. */
		public float saturationAmount() { return 0.3F; }
		
		/** Duration will get multiplied by this value. */
		public float durationMultiplier() { return 0.25F; }
		
		/** Called when this slime is eaten, allows adding effects to the player
		 *  in addition to the potion effects of the slime itself. */
		public void onEaten(EntityPlayer player, boolean potionEffects) {
			player.addPotionEffect(new PotionEffect(Potion.jump.id, (potionEffects ? 6 : 16) * 20, 1));
		}
		
	}
	
}
