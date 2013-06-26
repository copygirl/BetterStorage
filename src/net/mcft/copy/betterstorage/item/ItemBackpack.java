package net.mcft.copy.betterstorage.item;

import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.mcft.copy.betterstorage.Config;
import net.mcft.copy.betterstorage.block.tileentity.TileEntityBackpack;
import net.mcft.copy.betterstorage.client.model.ModelBackpackArmor;
import net.mcft.copy.betterstorage.container.SlotArmorBackpack;
import net.mcft.copy.betterstorage.inventory.InventoryStacks;
import net.mcft.copy.betterstorage.misc.Constants;
import net.mcft.copy.betterstorage.misc.PropertiesBackpack;
import net.mcft.copy.betterstorage.misc.handlers.PacketHandler;
import net.mcft.copy.betterstorage.utils.DirectionUtils;
import net.mcft.copy.betterstorage.utils.EntityUtils;
import net.mcft.copy.betterstorage.utils.PacketUtils;
import net.mcft.copy.betterstorage.utils.RandomUtils;
import net.mcft.copy.betterstorage.utils.StackUtils;
import net.mcft.copy.betterstorage.utils.WorldUtils;
import net.minecraft.block.Block;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.EnumArmorMaterial;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumHelper;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.common.ISpecialArmor;

public class ItemBackpack extends ItemArmor implements ISpecialArmor {
	
	public static final EnumArmorMaterial material = EnumHelper.addArmorMaterial(
			"backpack", 240, new int[]{ 0, 2, 0, 0 }, 15);
	
	protected ItemBackpack(int id, EnumArmorMaterial material) {
		super(id - 256, material, 0, 1);
		setMaxDamage(240);
	}
	public ItemBackpack(int id) {
		this(id, EnumArmorMaterial.CLOTH);
	}
	
	public String getName() { return "container.backpack"; }
	
	/** Returns the number of columns this backpack has. */
	public int getColumns() { return 9; }
	/** Returns the number of rows this backpack has. */
	public int getRows() { return Config.backpackRows; }
	
	protected IInventory getBackpackItemsInternal(EntityLiving carrier, EntityPlayer player) {
		PropertiesBackpack backpackData = getBackpackData(carrier);
		if (backpackData.contents == null)
			backpackData.contents = new ItemStack[getColumns() * getRows()];
		return new InventoryStacks(getName(), backpackData.contents);
	}
	
	public boolean canTake(PropertiesBackpack backpackData, ItemStack backpack) { return true; }
	
	public boolean containsItems(PropertiesBackpack backpackData, ItemStack backpack) {
		return (backpackData.hasItems || stackHasItems(backpack) ||
		        ((backpackData.contents != null) && !StackUtils.isEmpty(backpackData.contents)));
	}
	// For compatibility with previous versions.
	public boolean stackHasItems(ItemStack backpack) {
		int size = getColumns() * getRows();
		return (StackUtils.has(backpack, "Items") &&
		        !StackUtils.isEmpty(StackUtils.getStackContents(backpack, size)));
	}
	
	// Item stuff
	
	@Override
	@SideOnly(Side.CLIENT)
	public int getSpriteNumber() { return 0; }
	
	@Override
	public boolean isValidArmor(ItemStack stack, int armorType, Entity entity) { return false; }
	
	@Override
	@SideOnly(Side.CLIENT)
	public ModelBiped getArmorModel(EntityLiving entity, ItemStack stack, int slot) {
		return ModelBackpackArmor.instance;
	}
	@Override
	public String getArmorTexture(ItemStack stack, Entity entity, int slot, int layer) {
		return String.format(Constants.backpackTexture, layer - 1);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean advancedTooltips) {
		if (getBackpack(player) != stack) return;
		PropertiesBackpack backpackData = getBackpackData(player);
		boolean containsItems = containsItems(backpackData, stack);
		if (ItemBackpack.isBackpackOpen(player)) {
			if (containsItems)
				list.add("Contains items.");
			list.add("Currently being used by a player.");
		} else if (containsItems) {
			list.add("Contains items. Sneak and right click");
			list.add("ground with empty hand to unequip.");
		}
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
		
		if (stack.stackSize == 0) return false;
		
		Block blockBackpack = Block.blocksList[itemID];
		Block blockClicked = Block.blocksList[world.getBlockId(x, y, z)];
		
		ForgeDirection orientation = DirectionUtils.getOrientation(player).getOpposite();
		
		// If a replacable block was clicked, move on.
		// Otherwise, check if the top side was clicked and adjust the position.
		if ((blockClicked != null) &&
		    (blockClicked != Block.snow) &&
		    (blockClicked != Block.vine) &&
		    (blockClicked != Block.tallGrass) &&
		    (blockClicked != Block.deadBush) &&
		    !blockClicked.isBlockReplaceable(world, x, y, z)) {
			if (side != 1) return false;
			y++;
		}
		
		// Return false if there's block is too low or too high.
		if ((y <= 0) || (y >= world.getHeight() - 1)) return false;
		
		// Return false if not placed on top of a solid block.	
		Block blockBelow = Block.blocksList[world.getBlockId(x, y - 1, z)];
		if ((blockBelow == null) || !blockBelow.isBlockSolidOnSide(world, x, y - 1, z, ForgeDirection.UP)) return false;
		
		// Return false if the player can't edit the block.
		if (!player.canPlayerEdit(x, y, z, side, stack)) return false;
		
		// Return false if there's an entity blocking the placement.
		if (!world.canPlaceEntityOnSide(blockBackpack.blockID, x, y, z, false, side, player, stack)) return false;
		
		// Actually place the block in the world,
		// play place sound and decrease stack size if successful.
		if (!world.setBlock(x, y, z, blockBackpack.blockID, orientation.ordinal(), 3))
			return false;
		
		if (world.getBlockId(x, y, z) != blockBackpack.blockID)
			return false;
		
		blockBackpack.onBlockPlacedBy(world, x, y, z, player, stack);
		blockBackpack.onPostBlockPlaced(world, x, y, z, orientation.ordinal());
		
		TileEntityBackpack backpack = WorldUtils.get(world, x, y, z, TileEntityBackpack.class);
		backpack.stack = stack.copy();
		if (ItemBackpack.getBackpack(player) == stack)
			backpack.unequip(player);
		
		String sound = blockBackpack.stepSound.getPlaceSound();
		float volume = (blockBackpack.stepSound.getVolume() + 1.0F) / 2.0F;
		float pitch = blockBackpack.stepSound.getPitch() * 0.8F;
		world.playSoundEffect(x + 0.5, y + 0.5, z + 0.5F, sound, volume, pitch);
		stack.stackSize--;
		
		return true;
		
	}
	
	// ISpecialArmor implementation
	
	@Override
	public ArmorProperties getProperties(EntityLiving player, ItemStack armor,
	                                     DamageSource source, double damage, int slot) {
		return new ArmorProperties(0, 2 / 25.0, armor.getMaxDamage() + 1 - armor.getItemDamage());
	}
	
	@Override
	public int getArmorDisplay(EntityPlayer player, ItemStack armor, int slot) { return 2; }
	
	@Override
	public void damageArmor(EntityLiving entity, ItemStack stack,
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
		level = Math.min(level - 1, chance.length);
		return ((level >= 0) && RandomUtils.getBoolean(chance[level]));
	}
	
	// Helper functions
	
	public static ItemStack getBackpack(EntityLiving entity) {
		ItemStack backpack = entity.getCurrentArmor(2);
		if ((backpack != null) &&
		    (backpack.getItem() instanceof ItemBackpack)) return backpack;
		else return null;
	}
	public static void setBackpack(EntityLiving entity, ItemStack backpack, ItemStack[] contents) {
		entity.setCurrentItemOrArmor(3, backpack);
		PropertiesBackpack backpackData = getBackpackData(entity);
		backpackData.contents = contents;
		ItemBackpack.updateHasItems(entity, backpackData);
	}
	public static void removeBackpack(EntityLiving entity) {
		setBackpack(entity, null, null);
	}
	
	public static IInventory getBackpackItems(EntityLiving carrier, EntityPlayer player) {
		ItemStack backpack = getBackpack(carrier);
		if (backpack == null) return null;
		return ((ItemBackpack)backpack.getItem()).getBackpackItemsInternal(carrier, player);
	}
	public static IInventory getBackpackItems(EntityLiving carrier) {
		return getBackpackItems(carrier, null);
	}
	
	public static void initBackpackData(EntityLiving entity) {
		EntityUtils.createProperties(entity, PropertiesBackpack.class);
	}
	public static PropertiesBackpack getBackpackData(EntityLiving entity) {
		PropertiesBackpack backpackData = EntityUtils.getProperties(entity, PropertiesBackpack.class);
		if (!backpackData.initialized) {
			ItemBackpack.initBackpackOpen(entity);
			updateHasItems(entity, backpackData);
			backpackData.initialized = true;
		}
		return backpackData;
	}
	
	public static void updateHasItems(EntityLiving entity, PropertiesBackpack backpackData) {
		if (entity.worldObj.isRemote || !(entity instanceof EntityPlayer)) return;
		EntityPlayer player = (EntityPlayer)entity;
		boolean hasItems = ((backpackData.contents != null) && !StackUtils.isEmpty(backpackData.contents));
		if (backpackData.hasItems == hasItems) return;
		PacketUtils.sendPacket(player, PacketHandler.backpackHasItems, hasItems);
		backpackData.hasItems = hasItems;
	}
	
	public static void initBackpackOpen(EntityLiving entity) {
		entity.getDataWatcher().addObject(Config.backpackOpenDataWatcherId, (byte)0);
	}
	public static void setBackpackOpen(EntityLiving entity, boolean isOpen) {
		entity.getDataWatcher().updateObject(Config.backpackOpenDataWatcherId, (byte)(isOpen ? 1 : 0));
	}
	public static boolean isBackpackOpen(EntityLiving entity) {
		return (entity.getDataWatcher().getWatchableObjectByte(Config.backpackOpenDataWatcherId) != 0);
	}
	
}
