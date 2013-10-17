package net.mcft.copy.betterstorage.item;

import java.util.List;

import net.mcft.copy.betterstorage.Config;
import net.mcft.copy.betterstorage.block.tileentity.TileEntityBackpack;
import net.mcft.copy.betterstorage.client.model.ModelBackpackArmor;
import net.mcft.copy.betterstorage.container.ContainerBetterStorage;
import net.mcft.copy.betterstorage.container.SlotArmorBackpack;
import net.mcft.copy.betterstorage.inventory.InventoryBackpackEquipped;
import net.mcft.copy.betterstorage.inventory.InventoryStacks;
import net.mcft.copy.betterstorage.misc.Constants;
import net.mcft.copy.betterstorage.misc.CurrentItem;
import net.mcft.copy.betterstorage.misc.PropertiesBackpack;
import net.mcft.copy.betterstorage.misc.Resources;
import net.mcft.copy.betterstorage.misc.handlers.KeyBindingHandler;
import net.mcft.copy.betterstorage.misc.handlers.PacketHandler;
import net.mcft.copy.betterstorage.utils.DirectionUtils;
import net.mcft.copy.betterstorage.utils.EntityUtils;
import net.mcft.copy.betterstorage.utils.LanguageUtils;
import net.mcft.copy.betterstorage.utils.PlayerUtils;
import net.mcft.copy.betterstorage.utils.RandomUtils;
import net.mcft.copy.betterstorage.utils.StackUtils;
import net.mcft.copy.betterstorage.utils.WorldUtils;
import net.minecraft.block.Block;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.EnumArmorMaterial;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet103SetSlot;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumHelper;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.common.ISpecialArmor;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemBackpack extends ItemArmor implements ISpecialArmor {
	
	public static final EnumArmorMaterial material = EnumHelper.addArmorMaterial(
			"backpack", 14, new int[]{ 0, 2, 0, 0 }, 15);
	static { material.customCraftingMaterial = Item.leather; }
	
	protected ItemBackpack(int id, EnumArmorMaterial material) {
		super(id - 256, material, 0, 1);
	}
	public ItemBackpack(int id) { this(id, material); }
	
	public String getName() { return Constants.containerBackpack; }
	
	/** Returns the number of columns this backpack has. */
	public int getColumns() { return 9; }
	/** Returns the number of rows this backpack has. */
	public int getRows() { return Config.backpackRows; }
	
	protected int getDefaultColor() { return 0xA06540; }
	
	protected IInventory getBackpackItemsInternal(EntityLivingBase carrier, EntityPlayer player) {
		PropertiesBackpack backpackData = getBackpackData(carrier);
		if (backpackData.contents == null)
			backpackData.contents = new ItemStack[getColumns() * getRows()];
		return new InventoryStacks(getName(), backpackData.contents);
	}
	
	public boolean canTake(PropertiesBackpack backpackData, ItemStack backpack) { return true; }
	
	public boolean containsItems(PropertiesBackpack backpackData) {
		return (backpackData.hasItems || ((backpackData.contents != null) && !StackUtils.isEmpty(backpackData.contents)));
	}
	
	// Item stuff
	
	@Override
	@SideOnly(Side.CLIENT)
	public int getSpriteNumber() { return 0; }
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IconRegister iconRegister) {  }
	
	@Override
	public String getUnlocalizedName() { return Block.blocksList[itemID].getUnlocalizedName(); }
	@Override
	public String getUnlocalizedName(ItemStack stack) { return getUnlocalizedName(); }
	
	@Override
	@SideOnly(Side.CLIENT)
	public CreativeTabs getCreativeTab() { return Block.blocksList[itemID].getCreativeTabToDisplayOn(); }
	
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
	public ModelBiped getArmorModel(EntityLivingBase entity, ItemStack stack, int slot) {
		return ModelBackpackArmor.instance;
	}
	@Override
	public String getArmorTexture(ItemStack stack, Entity entity, int slot, String type) {
		return ((type == "overlay") ? Resources.backpackOverlayTexture : Resources.backpackTexture).toString();
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean advancedTooltips) {
		if (getBackpack(player) == stack) {
			String reason = LanguageUtils.translateTooltip(getReason(stack, player));
			// Tell players if someone's using their backpack when they hovers over it in the GUI.
			// This is because if the backpack is used by another player it can't be placed down.
			if (ItemBackpack.isBackpackOpen(player)) {
				if (reason != null) list.add(reason);
				LanguageUtils.translateTooltip(list, "backpack.used");
			// If the backpack can't be removed from its slot (only placed down),
			// tell the player why, like "Contains items" or "Bound backpack".
			} else if (reason != null) {
				if (Config.enableHelpTooltips)
					LanguageUtils.translateTooltip(list, "backpack.unequipHint", "%REASON%", reason);
				else list.add(reason);
			}
			// If the backpack can be opened by pressing a key, let the player know.
			if (KeyBindingHandler.serverBackpackKeyEnabled && Config.enableHelpTooltips) {
				String str = GameSettings.getKeyDisplayString(Config.backpackOpenKey);
				LanguageUtils.translateTooltip(list, "backpack.openHint", "%KEY%", str);
			}
		// Tell the player to place down and break a backpack to equip it.
		} else if (Config.enableHelpTooltips)
			LanguageUtils.translateTooltip(list, "backpack.equipHint");
	}
	/** Returns the reason (a string to be translated) why a backpack
	 *  can't be moved from the armor slot, or null if there is none. */
	protected String getReason(ItemStack stack, EntityPlayer player) {
		return (containsItems(getBackpackData(player)) ? "backpack.containsItems" : null);
	}
	
	@Override
	public void onArmorTickUpdate(World world, EntityPlayer player, ItemStack itemStack) {
		
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
		return placeBackpack(player, player, stack, x, y, z, side, orientation);
	}
	
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
		"inWall", "drown", "starve", "catcus", "fall", "outOfWorld",
		"generic", "wither", "anvil", "fallingBlock", "thrown"
	};
	protected boolean takesDamage(ItemStack stack, DamageSource source) {
		// Backpacks don't get damaged from certain
		// damage types (see above) and magic damage.
		if (source.isMagicDamage()) return false;
		for (String immune : immuneToDamageType)
			if (immune == source.getDamageType()) return false;
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
	
	// Helper functions
	
	public static ItemStack getBackpack(EntityLivingBase entity) {
		ItemStack backpack = entity.getCurrentItemOrArmor(CurrentItem.CHEST);
		if ((backpack != null) &&
		    (backpack.getItem() instanceof ItemBackpack)) return backpack;
		else return null;
	}
	public static void setBackpack(EntityLivingBase entity, ItemStack backpack, ItemStack[] contents) {
		entity.setCurrentItemOrArmor(3, backpack);
		PropertiesBackpack backpackData = getBackpackData(entity);
		backpackData.contents = contents;
		ItemBackpack.updateHasItems(entity, backpackData);
	}
	public static void removeBackpack(EntityLivingBase entity) {
		setBackpack(entity, null, null);
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
			ItemBackpack.initBackpackOpen(entity);
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
		Packet packet = PacketHandler.makePacket(PacketHandler.backpackHasItems, hasItems);
		PacketDispatcher.sendPacketToPlayer(packet, (Player)player);
		backpackData.hasItems = hasItems;
	}
	
	public static void initBackpackOpen(EntityLivingBase entity) {
		entity.getDataWatcher().addObject(Config.backpackOpenDataWatcherId, (byte)0);
	}
	public static void setBackpackOpen(EntityLivingBase entity, boolean isOpen) {
		entity.getDataWatcher().updateObject(Config.backpackOpenDataWatcherId, (byte)(isOpen ? 1 : 0));
	}
	public static boolean isBackpackOpen(EntityLivingBase entity) {
		return (entity.getDataWatcher().getWatchableObjectByte(Config.backpackOpenDataWatcherId) != 0);
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
		
		int columns = backpackType.getColumns();
		int rows = backpackType.getRows();
		Container container = new ContainerBetterStorage(player, inventory, columns, rows);
		
		String title = StackUtils.get(backpack, "", "display", "Name");
		PlayerUtils.openGui(player, inventory.getInvName(), columns, rows, title, container);
		
		return true;
		
	}
	
	/** Places an equipped backpack when the player right clicks
	 *  on the ground while sneaking and holding nothing. */
	public static boolean onPlaceBackpack(EntityPlayer player, int x, int y, int z, int side) {
		
		if (player.getCurrentEquippedItem() != null || !player.isSneaking()) return false;
		ItemStack backpack = ItemBackpack.getBackpack(player);
		if (backpack == null) return false;
		
		if (!ItemBackpack.isBackpackOpen(player)) {
			// Try to place the backpack as if it was being held and used by the player.
			backpack.getItem().onItemUse(backpack, player, player.worldObj, x, y, z, side, 0, 0, 0);
			if (backpack.stackSize <= 0) backpack = null;
		}
		
		// Send set slot packet to for the chest slot to make
		// sure the client has the same information as the server.
		// This is especially important when there's a large delay, the
		// client might think it placed the backpack, but server didn't.
		if (!player.worldObj.isRemote)
			PacketDispatcher.sendPacketToPlayer(new Packet103SetSlot(0, 6, backpack), (Player)player);
		
		boolean success = (backpack == null);
		if (success) player.swingItem();
		return success;
		
	}
	
	public static boolean placeBackpack(EntityLivingBase carrier, EntityPlayer player, ItemStack backpack, int x, int y, int z, int side, ForgeDirection orientation) {
		
		if (backpack.stackSize == 0) return false;
		
		World world = carrier.worldObj;
		Block blockBackpack = Block.blocksList[backpack.itemID];
		
		// If a replacable block was clicked, move on.
		// Otherwise, check if the top side was clicked and adjust the position.
		if (!WorldUtils.isBlockReplacable(world, x, y, z)) {
			if (side != 1) return false;
			y++;
		}
		
		// Return false if there's block is too low or too high.
		if ((y <= 0) || (y >= world.getHeight() - 1)) return false;
		
		// Return false if not placed on top of a solid block.	
		Block blockBelow = Block.blocksList[world.getBlockId(x, y - 1, z)];
		if ((blockBelow == null) || !blockBelow.isBlockSolidOnSide(world, x, y - 1, z, ForgeDirection.UP)) return false;
		
		// Return false if the player can't edit the block.
		if ((player != null) && !player.canPlayerEdit(x, y, z, side, backpack)) return false;
		
		// Return false if there's an entity blocking the placement.
		if (!world.canPlaceEntityOnSide(blockBackpack.blockID, x, y, z, false, side, carrier, backpack)) return false;
		
		// Actually place the block in the world,
		// play place sound and decrease stack size if successful.
		
		if (!world.setBlock(x, y, z, blockBackpack.blockID, orientation.ordinal(), 3))
			return false;
		
		if (world.getBlockId(x, y, z) != blockBackpack.blockID)
			return false;
		
		blockBackpack.onBlockPlacedBy(world, x, y, z, carrier, backpack);
		blockBackpack.onPostBlockPlaced(world, x, y, z, orientation.ordinal());
		
		TileEntityBackpack te = WorldUtils.get(world, x, y, z, TileEntityBackpack.class);
		te.stack = backpack.copy();
		if (ItemBackpack.getBackpack(carrier) == backpack)
			te.unequip(carrier);
		
		String sound = blockBackpack.stepSound.getPlaceSound();
		float volume = (blockBackpack.stepSound.getVolume() + 1.0F) / 2.0F;
		float pitch = blockBackpack.stepSound.getPitch() * 0.8F;
		world.playSoundEffect(x + 0.5, y + 0.5, z + 0.5F, sound, volume, pitch);
		
		backpack.stackSize--;
		
		return true;
		
	}
	
}
