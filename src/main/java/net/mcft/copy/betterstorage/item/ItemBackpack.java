package net.mcft.copy.betterstorage.item;

import java.util.List;

import net.mcft.copy.betterstorage.BetterStorage;
import net.mcft.copy.betterstorage.client.model.ModelBackpack;
import net.mcft.copy.betterstorage.client.model.ModelBackpackArmor;
import net.mcft.copy.betterstorage.config.GlobalConfig;
import net.mcft.copy.betterstorage.container.ContainerBetterStorage;
import net.mcft.copy.betterstorage.container.SlotArmorBackpack;
import net.mcft.copy.betterstorage.content.BetterStorageTiles;
import net.mcft.copy.betterstorage.inventory.InventoryBackpackEquipped;
import net.mcft.copy.betterstorage.inventory.InventoryStacks;
import net.mcft.copy.betterstorage.misc.Constants;
import net.mcft.copy.betterstorage.misc.EquipmentSlot;
import net.mcft.copy.betterstorage.misc.PropertiesBackpack;
import net.mcft.copy.betterstorage.misc.Resources;
import net.mcft.copy.betterstorage.misc.handlers.KeyBindingHandler;
import net.mcft.copy.betterstorage.network.packet.PacketBackpackHasItems;
import net.mcft.copy.betterstorage.tile.TileBackpack;
import net.mcft.copy.betterstorage.tile.entity.TileEntityBackpack;
import net.mcft.copy.betterstorage.utils.DirectionUtils;
import net.mcft.copy.betterstorage.utils.EntityUtils;
import net.mcft.copy.betterstorage.utils.LanguageUtils;
import net.mcft.copy.betterstorage.utils.PlayerUtils;
import net.mcft.copy.betterstorage.utils.RandomUtils;
import net.mcft.copy.betterstorage.utils.StackUtils;
import net.mcft.copy.betterstorage.utils.WorldUtils;
import net.minecraft.block.Block;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.S2FPacketSetSlot;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.common.ISpecialArmor;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.common.util.ForgeDirection;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemBackpack extends ItemArmorBetterStorage implements ISpecialArmor, IDyeableItem {
	
	public static final ArmorMaterial material = EnumHelper.addArmorMaterial(
			"backpack", 14, new int[]{ 0, 2, 0, 0 }, 15);
	static { material.customCraftingMaterial = Items.leather; }
	
	protected ItemBackpack(ArmorMaterial material) { super(material, 0, 1); }
	public ItemBackpack() { this(material); }
	
	public String getBackpackName() { return Constants.containerBackpack; }
	
	/** Returns the number of columns this backpack has. */
	public int getBackpackColumns() { return 9; }
	/** Returns the number of rows this backpack has. */
	public int getBackpackRows() { return BetterStorage.globalConfig.getInteger(GlobalConfig.backpackRows); }
	
	protected int getDefaultColor() { return 0x805038; }
	
	protected IInventory getBackpackItemsInternal(EntityLivingBase carrier, EntityPlayer player) {
		PropertiesBackpack backpackData = getBackpackData(carrier);
		int size = (getBackpackColumns() * getBackpackRows());
		if (backpackData.contents == null)
			backpackData.contents = new ItemStack[size];
		// In case the backpack size got changed in
		// the configuration file, update it here.
		else if (backpackData.contents.length != size) {
			ItemStack[] newContents = new ItemStack[size];
			System.arraycopy(backpackData.contents, 0, newContents, 0, Math.min(size, backpackData.contents.length));
			backpackData.contents = newContents;
		}
		return new InventoryStacks(getBackpackName(), backpackData.contents);
	}
	
	public boolean containsItems(PropertiesBackpack backpackData) {
		return (backpackData.hasItems || ((backpackData.contents != null) && !StackUtils.isEmpty(backpackData.contents)));
	}
	
	// Model and texture
	
	@SideOnly(Side.CLIENT)
	private ModelBackpack model;
	@SideOnly(Side.CLIENT)
	private ModelBackpackArmor modelArmor;
	
	/** Returns the model class of the backpack. */
	@SideOnly(Side.CLIENT)
	public Class<? extends ModelBackpack> getModelClass() { return ModelBackpack.class; }
	
	@SideOnly(Side.CLIENT)
	public ModelBackpack getModel() {
		if (model == null) {
			try { model = getModelClass().getConstructor(boolean.class).newInstance(true); }
			catch (Exception e) { e.printStackTrace(); }
		}
		return model;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public ModelBiped getArmorModel(EntityLivingBase entity, ItemStack stack, int slot) {
		if (modelArmor == null) {
			try {
				ModelBackpack model = getModelClass().getConstructor(boolean.class).newInstance(false);
				modelArmor = new ModelBackpackArmor(model);
			} catch (Exception e) { e.printStackTrace(); }
		}
		return modelArmor;
	}
	
	@Override
	public String getArmorTexture(ItemStack stack, Entity entity, int slot, String type) {
		return ((type == "overlay") ? Resources.textureBackpackOverlay : Resources.textureBackpack).toString();
	}
	
	// Item stuff
	
	@Override
	@SideOnly(Side.CLIENT)
	public int getSpriteNumber() { return 0; }
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister iconRegister) {  }
	
	@Override
	public String getUnlocalizedName() { return getBlockType().getUnlocalizedName(); }
	@Override
	public String getUnlocalizedName(ItemStack stack) { return getUnlocalizedName(); }
	
	public TileBackpack getBlockType() { return BetterStorageTiles.backpack; }
	
	@Override
	public boolean isValidArmor(ItemStack stack, int armorType, Entity entity) { return false; }
	
	@Override
	public int getColor(ItemStack stack) {
		int color = getDefaultColor();
		return ((color >= 0) ? StackUtils.get(stack, color, "display", "color") : color);
	}
	@Override
	public int getRenderPasses(int metadata) { return ((getDefaultColor() >= 0) ? 2 : 1); }
	
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean advancedTooltips) {
		boolean enableHelpTooltips = BetterStorage.globalConfig.getBoolean(GlobalConfig.enableHelpTooltips);
		if (getBackpack(player) == stack) {
			String info = LanguageUtils.translateTooltip(getAdditionalInfo(stack, player));
			// Tell players if someone's using their backpack when they hovers over it in the GUI.
			// This is because if the backpack is used by another player it can't be placed down.
			if (ItemBackpack.isBackpackOpen(player)) {
				if (info != null) list.add(info);
				LanguageUtils.translateTooltip(list, "backpack.used");
			// If the backpack can't be removed from its slot (only placed down),
			// tell the player why, like "Contains items" or "Bound backpack".
			} else if (enableHelpTooltips)
				LanguageUtils.translateTooltip(list,
						(info != null) ? "backpack.unequipHint.extended" : "backpack.unequipHint",
						(info != null) ? new String[]{ "%INFO%", info } : new String[0]);
			else if (info != null) list.add(info);
			// If the backpack can be opened by pressing a key, let the player know.
			if (BetterStorage.globalConfig.getBoolean(GlobalConfig.enableBackpackOpen)) {
				String str = GameSettings.getKeyDisplayString(KeyBindingHandler.backpackOpen.getKeyCode());
				LanguageUtils.translateTooltip(list, "backpack.openHint", "%KEY%", str);
			}
		// Tell the player to place down and break a backpack to equip it.
		} else if (enableHelpTooltips) {
			boolean chestplate = BetterStorage.globalConfig.getBoolean(GlobalConfig.backpackChestplate);
			LanguageUtils.translateTooltip(list, (chestplate ? "backpack.equipHint"
			                                                 : "backpack.equipHint.extended"));
			// If the backpack doesn't get equipped to the chestplate slot,
			// let players know they can open it in the regular item tooltip.
			if (!chestplate && BetterStorage.globalConfig.getBoolean(GlobalConfig.enableBackpackOpen)) {
				String str = GameSettings.getKeyDisplayString(KeyBindingHandler.backpackOpen.getKeyCode());
				LanguageUtils.translateTooltip(list, "backpack.openHint", "%KEY%", str);
			}
		}
	}
	/** Returns additional info (a string to be translated) of the backpack. */
	protected String getAdditionalInfo(ItemStack stack, EntityPlayer player) {
		return (containsItems(getBackpackData(player)) ? "backpack.containsItems" : null);
	}
	
	@Override
	public void onArmorTick(World world, EntityPlayer player, ItemStack itemStack) {
		
		// Replace the armor slot with a custom one, so the player
		// can't unequip the backpack when there's items inside.
		
		int index = 5 + armorType;
		Slot slotBefore = player.inventoryContainer.getSlot(index);
		if (slotBefore instanceof SlotArmorBackpack) return;
		int slotIndex = player.inventory.getSizeInventory() - getChestSlotOffset(player) - armorType;
		SlotArmorBackpack slot = new SlotArmorBackpack(player.inventory, slotIndex, 8, 8 + armorType * 18);
		slot.slotNumber = index;
		player.inventoryContainer.inventorySlots.set(index, slot);
		
	}
	
	// For compatibility with Galacticraft.
	private int getChestSlotOffset(EntityPlayer player) {
		return isExact(player.inventory, "micdoodle8.mods.galacticraft.core.inventory.GCCoreInventoryPlayer") ? 6 : 1;
	}
	private static boolean isExact(Object obj, String str) {
		try { return obj.getClass().getName().equals(str); } 
		catch (Exception e) { return false; }
	}
	
	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) { return stack; }
	
	@Override
	public boolean onItemUse(ItemStack stack, EntityPlayer player,
	                         World world, int x, int y, int z, int side,
	                         float hitX, float hitY, float hitZ) {
		ForgeDirection orientation = DirectionUtils.getOrientation(player).getOpposite();
		return placeBackpack(player, player, stack, x, y, z, side, orientation, false, false);
	}
	
	/** Called every tick regardless of whether the
	 *  backpack is equipped in an armor slot or not. */
	public void onEquippedUpdate(EntityLivingBase player, ItemStack backpack) {  }
	
	// ISpecialArmor implementation
	
	@Override
	public ArmorProperties getProperties(EntityLivingBase entity, ItemStack armor,
	                                     DamageSource source, double damage, int slot) {
		return new ArmorProperties(0, 2 / 25.0, armor.getMaxDamage() + 1 - armor.getItemDamage());
	}
	
	@Override
	public int getArmorDisplay(EntityPlayer player, ItemStack armor, int slot) { return 2; }
	
	@Override
	public void damageArmor(EntityLivingBase entity, ItemStack stack,
	                        DamageSource source, int damage, int slot) {
		if (!takesDamage(stack, source)) return;
		stack.damageItem(damage, entity);
		if (stack.stackSize > 0) return;
		PropertiesBackpack backpackData = ItemBackpack.getBackpackData(entity);
		if (backpackData.contents != null)
			for (ItemStack s : backpackData.contents)
				WorldUtils.dropStackFromEntity(entity, s, 2.0F);
		entity.renderBrokenItemStack(stack);
	}
	
	private static final String[] immuneToDamageType = {
		"inWall", "drown", "starve", "cactus", "fall", "outOfWorld",
		"generic", "wither", "anvil", "fallingBlock", "thrown"
	};
	protected boolean takesDamage(ItemStack stack, DamageSource source) {
		// Backpacks don't get damaged from certain
		// damage types (see above) and magic damage.
		if (source.isMagicDamage()) return false;
		for (String immune : immuneToDamageType)
			if (immune.equals(source.getDamageType())) return false;
		// Protection enchantments protect the backpack
		// from taking damage from that damage type.
		return (!enchantmentProtection(stack, Enchantment.protection, 0.3, 0.35, 0.4, 0.45) &&
		        !(source.isProjectile() && enchantmentProtection(stack, Enchantment.projectileProtection, 0.4, 0.5, 0.6, 0.7)) &&
		        !(source.isFireDamage() && enchantmentProtection(stack, Enchantment.fireProtection, 0.55, 0.65, 0.75, 0.85)) &&
		        !(source.isExplosion() && enchantmentProtection(stack, Enchantment.blastProtection, 0.65, 0.75, 0.85, 0.95)));
	}
	private boolean enchantmentProtection(ItemStack stack, Enchantment ench, double... chance) {
		int level = EnchantmentHelper.getEnchantmentLevel(ench.effectId, stack);
		level = Math.min(level - 1, chance.length - 1);
		return ((level >= 0) && RandomUtils.getBoolean(chance[level]));
	}
	
	// IDyeableItem implementation
	
	@Override
	public boolean canDye(ItemStack stack) { return (getDefaultColor() >= 0); }
	
	// Helper functions
	
	public static ItemStack getBackpack(EntityLivingBase entity) {
		ItemStack backpack = entity.getEquipmentInSlot(EquipmentSlot.CHEST);
		if ((backpack != null) && (backpack.getItem() instanceof ItemBackpack)) return backpack;
		return getBackpackData(entity).backpack;
	}
	public static void setBackpack(EntityLivingBase entity, ItemStack backpack, ItemStack[] contents) {
		boolean setChestplate = (BetterStorage.globalConfig.getBoolean(GlobalConfig.backpackChestplate) ||
		                         !(entity instanceof EntityPlayer) || hasChestplateBackpackEquipped(entity));
		PropertiesBackpack backpackData = getBackpackData(entity);
		if (!setChestplate) backpackData.backpack = backpack;
		else entity.setCurrentItemOrArmor(EquipmentSlot.CHEST, backpack);
		backpackData.contents = contents;
		ItemBackpack.updateHasItems(entity, backpackData);
	}
	public static boolean hasChestplateBackpackEquipped(EntityLivingBase entity) {
		ItemStack backpack = getBackpack(entity);
		return ((backpack != null) ? (backpack == entity.getEquipmentInSlot(EquipmentSlot.CHEST)) : false);
	}
	public static boolean canEquipBackpack(EntityPlayer player) {
		return ((getBackpack(player) == null) &&
		        !(BetterStorage.globalConfig.getBoolean(GlobalConfig.backpackChestplate) &&
		          (player.getEquipmentInSlot(EquipmentSlot.CHEST) != null)));
	}
	
	public static IInventory getBackpackItems(EntityLivingBase carrier, EntityPlayer player) {
		ItemStack backpack = getBackpack(carrier);
		if (backpack == null) return null;
		return ((ItemBackpack)backpack.getItem()).getBackpackItemsInternal(carrier, player);
	}
	public static IInventory getBackpackItems(EntityLivingBase carrier) {
		return getBackpackItems(carrier, null);
	}
	
	public static void initBackpackData(EntityLivingBase entity) {
		EntityUtils.createProperties(entity, PropertiesBackpack.class);
	}
	public static PropertiesBackpack getBackpackData(EntityLivingBase entity) {
		PropertiesBackpack backpackData = EntityUtils.getOrCreateProperties(entity, PropertiesBackpack.class);
		if (!backpackData.initialized) {
			updateHasItems(entity, backpackData);
			backpackData.initialized = true;
		}
		return backpackData;
	}
	
	public static void updateHasItems(EntityLivingBase entity, PropertiesBackpack backpackData) {
		if (entity.worldObj.isRemote || !(entity instanceof EntityPlayer)) return;
		EntityPlayer player = (EntityPlayer)entity;
		boolean hasItems = ((backpackData.contents != null) && !StackUtils.isEmpty(backpackData.contents));
		if (backpackData.hasItems == hasItems) return;
		BetterStorage.networkChannel.sendTo(new PacketBackpackHasItems(hasItems), player);
		backpackData.hasItems = hasItems;
	}
	
	public static boolean isBackpackOpen(EntityLivingBase entity) {
		return (getBackpackData(entity).playersUsing > 0);
	}
	
	/** Opens the carrier's equipped backpack for the player.
	 *  Returns if it was successfully opened. */
	public static boolean openBackpack(EntityPlayer player, EntityLivingBase carrier) {
		
		ItemStack backpack = ItemBackpack.getBackpack(carrier);
		if (backpack == null) return false;
		ItemBackpack backpackType = (ItemBackpack)backpack.getItem();
		
		IInventory inventory = ItemBackpack.getBackpackItems(carrier, player);
		inventory = new InventoryBackpackEquipped(carrier, player, inventory);
		if (!inventory.isUseableByPlayer(player)) return false;
		
		int columns = backpackType.getBackpackColumns();
		int rows = backpackType.getBackpackRows();
		Container container = new ContainerBetterStorage(player, inventory, columns, rows);
		
		String title = StackUtils.get(backpack, "", "display", "Name");
		PlayerUtils.openGui(player, inventory.getInventoryName(), columns, rows, title, container);
		
		return true;
		
	}
	
	/** Places an equipped backpack when the player right clicks
	 *  on the ground while sneaking and holding nothing. */
	public static boolean onPlaceBackpack(EntityPlayer player, int x, int y, int z, int side) {
		
		if (player.getCurrentEquippedItem() != null || !player.isSneaking()) return false;
		ItemStack backpack = ItemBackpack.getBackpack(player);
		if (backpack == null) return false;
		
		boolean success = false;
		if (!ItemBackpack.isBackpackOpen(player)) {
			// Try to place the backpack as if it was being held and used by the player.
			success = backpack.getItem().onItemUse(backpack, player, player.worldObj, x, y, z, side, 0, 0, 0);
			if (backpack.stackSize <= 0) {
				ItemBackpack.setBackpack(player, null, null);
				backpack = null;
			}
		}

		// Make sure the client has the same information as the server. It does not sync when backpackChestplate is disabled because there are no changes to the slot in that case.
		if (!player.worldObj.isRemote && success && player instanceof EntityPlayerMP && BetterStorage.globalConfig.getBoolean(GlobalConfig.backpackChestplate)) {	
			((EntityPlayerMP)player).playerNetServerHandler.sendPacket(new S2FPacketSetSlot(0, 6, backpack));
		}
		
		if (success) player.swingItem();
		return success;
		
	}
	
	/** Place a backpack down on a block.
	 * @param carrier The carrier of the backpack (non-null).
	 * @param player The player placing the backpack, if any.
	 *               Used to check if they're allowed to place it.
	 * @param backpack The backpack stack.
	 *                 Stack size is decreased if placed successfully.
	 * @param side The side of block the backpack is placed on.
	 *             Anything other than top usually doesn't place it.
	 * @param orientation The orientation the backpack will be placed in.
	 * @param despawn If the backpack should despawn after a while.
	 *                True for mobs, unless hit recently.
	 * @param deathDrop True if the backpack is dropped on death.
	 *                  Will not check for block solidity or entities.
	 * @return If the backpack was placed successfully. */
	public static boolean placeBackpack(EntityLivingBase carrier, EntityPlayer player, ItemStack backpack,
	                                    int x, int y, int z, int side, ForgeDirection orientation,
	                                    boolean despawn, boolean deathDrop) {
		
		if (backpack.stackSize == 0) return false;
		
		World world = carrier.worldObj;
		Block blockBackpack = ((ItemBackpack)backpack.getItem()).getBlockType();
		
		// Return false if there's block is too low or too high.
		if ((y <= 0) || (y >= world.getHeight() - 1)) return false;
		
		// If a replaceable block was clicked, move on.
		// Otherwise, check if the top side was clicked and adjust the position.
		if (!world.getBlock(x, y, z).isReplaceable(world, x, y, z)) {
			if (side != 1) return false;
			y++;
		}
		
		// If the backpack is dropped on death, return false
		// if it's placed on a non-replaceable block. Otherwise,
		// return false if the block isn't solid on top.
		Block blockBelow = world.getBlock(x, y - 1, z);
		if ((deathDrop ? blockBelow.isReplaceable(world, x, y - 1, z)
		               : !world.isSideSolid(x, y - 1, z, ForgeDirection.UP))) return false;
		
		// Return false if there's an entity blocking the placement.
		if (!world.canPlaceEntityOnSide(blockBackpack, x, y, z, deathDrop, side, carrier, backpack)) return false;
		
		// Return false if the player can't edit the block.
		if ((player != null) && (!world.canMineBlock(player, x, y, z) ||
		                         !player.canPlayerEdit(x, y, z, side, backpack))) return false;
		
		// Do not actually place the backpack on the client.
		if (world.isRemote) return true;
		
		// Actually place the block in the world,
		// play place sound and decrease stack size if successful.
		
		if (!world.setBlock(x, y, z, blockBackpack, orientation.ordinal(), 3))
			return false;
		
		if (world.getBlock(x, y, z) != blockBackpack)
			return false;
		
		blockBackpack.onBlockPlacedBy(world, x, y, z, carrier, backpack);
		blockBackpack.onPostBlockPlaced(world, x, y, z, orientation.ordinal());
		
		TileEntityBackpack te = WorldUtils.get(world, x, y, z, TileEntityBackpack.class);
		te.stack = backpack.copy();
		if (ItemBackpack.getBackpack(carrier) == backpack)
			te.unequip(carrier, despawn);
		
		String sound = blockBackpack.stepSound.func_150496_b();
		float volume = (blockBackpack.stepSound.getVolume() + 1.0F) / 2.0F;
		float pitch = blockBackpack.stepSound.getPitch() * 0.8F;
		world.playSoundEffect(x + 0.5, y + 0.5, z + 0.5F, sound, volume, pitch);
		
		backpack.stackSize--;
		
		return true;
		
	}
	
}
