package net.mcft.copy.betterstorage.addon.thaumcraft;

import net.mcft.copy.betterstorage.item.ItemBackpack;

public class ItemThaumcraftBackpack extends ItemBackpack /*implements IRepairable, IVisDiscountGear*/ {
	
	public ItemThaumcraftBackpack() {
		super(ItemBackpack.material);
		setMaxDamage(288);
	}
	
	/*@Override
	public String getBackpackName() { return Constants.containerThaumcraftBackpack; }
	
	@Override
	public TileBackpack getBlockType() { return ThaumcraftAddon.thaumcraftBackpack; }
	
	@Override
	public int getBackpackColumns() { return 13; }
	
	@Override
	protected int getDefaultColor() { return -1; }
	
	@Override
	public String getArmorTexture(ItemStack stack, Entity entity, int slot, String type) {
		return ThaumcraftResources.thaumcraftBackpackTexture.toString();
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public EnumRarity getRarity(ItemStack stack) { return EnumRarity.UNCOMMON; }
	
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean advancedTooltips) {
		super.addInformation(stack, player, list, advancedTooltips);
		list.add(String.format("%s%s: %s%%", EnumChatFormatting.DARK_PURPLE,
		                       StatCollector.translateToLocal("tc.visdiscount"), 2));
	}
	
	@Override
	public int getItemEnchantability() { return 25; }
	
	@Override
	public boolean getIsRepairable(ItemStack stack, ItemStack repairMaterial) {
		return StackUtils.matches(stack, ThaumcraftAddon.fabric, false);
	}
	
	@Override
	public void onEquippedUpdate(EntityLivingBase entity, ItemStack backpack) {
		super.onEquippedUpdate(entity, backpack);
		if (!entity.worldObj.isRemote && (entity instanceof EntityPlayer))
			repairItems(backpack, (EntityPlayer)entity);
	}
	
	//TODO: Read flux effects.
	
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
	
	
	public void repairItems(ItemStack backpack, EntityPlayer player) {
		
		IInventory backpackInventory = ItemBackpack.getBackpackItems(player);
		int repair = EnchantmentHelper.getEnchantmentLevel(ThaumcraftApi.enchantRepair, backpack);
		int time = ((repair > 0) ? (15 - Math.min(repair, 2) * 5) : 30) * 20;
		if ((player.ticksExisted % time) > 0) return;
		
		for (int i = 0; i < backpackInventory.getSizeInventory(); i++) {
			ItemStack stack = backpackInventory.getStackInSlot(i);
			if ((stack != null) && (stack.isItemDamaged()) && (stack.getItem() instanceof IRepairable) &&
			    (EnchantmentHelper.getEnchantmentLevel(ThaumcraftApi.enchantRepair, stack) > 0))
				repairItem(stack, player);
		}
		
	}
	
	private static Method doRepairMethod = null;
	private static void repairItem(ItemStack stack, EntityPlayer player) {
		if (doRepairMethod == null) {
			try {
				Class clazz = Class.forName("thaumcraft.common.lib.events.EventHandlerEntity");
				doRepairMethod = clazz.getMethod("doRepair", ItemStack.class, EntityPlayer.class);
			} catch (Exception ex) { ex.printStackTrace(); return; }
		}
		try { doRepairMethod.invoke(null, stack, player); }
		catch (Exception ex) { ex.printStackTrace(); }
	}
	
	// IVisDiscountGear implementation
	
	@Override
	public int getVisDiscount(ItemStack stack, EntityPlayer player, Aspect aspect) { return 2; }
	*/
}
