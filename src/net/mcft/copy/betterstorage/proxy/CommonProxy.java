package net.mcft.copy.betterstorage.proxy;

import net.mcft.copy.betterstorage.addon.Addon;
import net.mcft.copy.betterstorage.block.BlockEnderBackpack;
import net.mcft.copy.betterstorage.block.TileEntityArmorStand;
import net.mcft.copy.betterstorage.block.TileEntityBackpack;
import net.mcft.copy.betterstorage.block.TileEntityLocker;
import net.mcft.copy.betterstorage.block.TileEntityReinforcedChest;
import net.mcft.copy.betterstorage.block.crate.CratePileCollection;
import net.mcft.copy.betterstorage.block.crate.TileEntityCrate;
import net.mcft.copy.betterstorage.container.ContainerBetterStorage;
import net.mcft.copy.betterstorage.inventory.InventoryBackpackEquipped;
import net.mcft.copy.betterstorage.item.ItemBackpack;
import net.mcft.copy.betterstorage.item.ItemEnderBackpack;
import net.mcft.copy.betterstorage.misc.PropertiesBackpack;
import net.mcft.copy.betterstorage.utils.EntityUtils;
import net.mcft.copy.betterstorage.utils.NbtUtils;
import net.mcft.copy.betterstorage.utils.PacketUtils;
import net.mcft.copy.betterstorage.utils.PlayerUtils;
import net.mcft.copy.betterstorage.utils.StackUtils;
import net.mcft.copy.betterstorage.utils.WorldUtils;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.packet.Packet103SetSlot;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.Event.Result;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.EntityEvent.EntityConstructing;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.player.EntityInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;
import net.minecraftforge.event.world.WorldEvent.Save;
import net.minecraftforge.event.world.WorldEvent.Unload;
import cpw.mods.fml.common.IPlayerTracker;
import cpw.mods.fml.common.registry.GameRegistry;

public class CommonProxy implements IPlayerTracker {
	
	public void init() {
		MinecraftForge.EVENT_BUS.register(this);
		GameRegistry.registerPlayerTracker(this);
		registerTileEntites();
	}
	
	public void registerTileEntites() {
		GameRegistry.registerTileEntity(TileEntityCrate.class, "container.crate");
		GameRegistry.registerTileEntity(TileEntityReinforcedChest.class, "container.reinforcedChest");
		GameRegistry.registerTileEntity(TileEntityLocker.class, "container.locker");
		GameRegistry.registerTileEntity(TileEntityArmorStand.class, "container.armorStand");
		GameRegistry.registerTileEntity(TileEntityBackpack.class, "container.backpack");
		Addon.registerAllTileEntites();
	}
	
	@ForgeSubscribe
	public void onWorldSave(Save event) {
		CratePileCollection.saveAll(event.world);
	}
	
	@ForgeSubscribe
	public void onWorldUnload(Unload event) {
		CratePileCollection.unload(event.world);
	}
	
	@ForgeSubscribe
	public void onEntityConstructing(EntityConstructing event) {
		if (!(event.entity instanceof EntityLiving)) return;
		ItemBackpack.initBackpackData((EntityLiving)event.entity);
	}
	
	@ForgeSubscribe
	public void onPlayerInteract(PlayerInteractEvent event) {
		
		// Places an equipped backpack when the player right clicks
		// on the ground while sneaking and holding nothing.
		
		if (event.action != Action.RIGHT_CLICK_BLOCK) return;
		EntityPlayer player = event.entityPlayer;
		if (player.getCurrentEquippedItem() != null || !player.isSneaking()) return;
		ItemStack backpack = ItemBackpack.getBackpack(player);
		if (backpack == null) return;
		
		if (!ItemBackpack.isBackpackOpen(player)) {
			// Try to place the backpack as if it was being held and used by the player.
			backpack.getItem().onItemUse(backpack, player, player.worldObj,
			                             event.x, event.y, event.z, event.face, 0, 0, 0);
			
			if (backpack.stackSize <= 0) backpack = null;
		}
		
		// Send set slot packet to for the chest slot to make
		// sure the client has the same information as the server.
		// This is especially important when there's a large delay, the
		// client might think e placed the backpack, but server disagrees.
		if (!player.worldObj.isRemote)
			PacketUtils.sendPacket(player, new Packet103SetSlot(0, 6, backpack));
		
		// Only continue if the backpack was placed successfully.
		if (backpack != null) return;
		
		player.swingItem();
		event.useBlock = Result.DENY;
		
	}
	
	@ForgeSubscribe
	public void onEntityInteract(EntityInteractEvent event) {
		
		// Right clicking the back equipped by another
		// entity will open a GUI for that backpack.
		
		if (event.entity.worldObj.isRemote) return;
		EntityPlayerMP player = (EntityPlayerMP)event.entity;
		if (!(event.target instanceof EntityLiving)) return;
		EntityLiving target = (EntityLiving)event.target;
		
		ItemStack backpack = ItemBackpack.getBackpack(target);
		if (backpack == null) return;
		ItemBackpack backpackType = (ItemBackpack)backpack.getItem();
		
		IInventory inventory = ItemBackpack.getBackpackItems(target, player);
		inventory = new InventoryBackpackEquipped(target, player, inventory);
		if (!inventory.isUseableByPlayer(player)) return;
		
		int columns = backpackType.getColumns();
		int rows = backpackType.getRows();
		Container container = new ContainerBetterStorage(player, inventory, columns, rows);
		
		String title = StackUtils.get(backpack, "", "display", "Name");
		PlayerUtils.openGui(player, inventory.getInvName(), columns, rows, title, container);
		
		player.swingItem();
		
	}
	
	@ForgeSubscribe
	public void onEntityJoinWorldEvent(EntityJoinWorldEvent event) {
		if (!(event.entity instanceof EntityItem)) return;
		EntityItem entity = (EntityItem)event.entity;
		ItemStack stack = entity.getDataWatcher().getWatchableObjectItemStack(10);
		if ((stack == null) || !(stack.getItem() instanceof ItemEnderBackpack)) return;
		event.setCanceled(true);
		for (int i = 0; i < 64; i++)
			if (BlockEnderBackpack.teleportRandomly(entity.worldObj, entity.posX, entity.posY, entity.posZ, (i > 48), stack))
				break;
	}
	
	@ForgeSubscribe
	public void onLivingUpdate(LivingUpdateEvent event) {
		
		// Update backpack animation and play sound when it opens / closes
		
		EntityLiving entity = event.entityLiving;
		ItemStack backpack = ItemBackpack.getBackpack(entity);
		PropertiesBackpack backpackData;
		if (backpack == null) {
			backpackData = EntityUtils.getProperties(entity, PropertiesBackpack.class);
			// If the entity doesn't have a backpack equipped,
			// but still has some backpack data, drop the items.
			if (backpackData.contents != null) {
				for (ItemStack stack : backpackData.contents)
					WorldUtils.dropStackFromEntity(entity, stack);
				backpackData.contents = null;
			}
			return;
		} else backpackData = ItemBackpack.getBackpackData(entity);
		
		backpackData.prevLidAngle = backpackData.lidAngle;
		float lidSpeed = 0.2F;
		if (ItemBackpack.isBackpackOpen(entity))
			backpackData.lidAngle = Math.min(1.0F, backpackData.lidAngle + lidSpeed);
		else backpackData.lidAngle = Math.max(0.0F, backpackData.lidAngle - lidSpeed);
		
		String sound = Block.soundSnowFootstep.getStepSound();
		// Play sound when opening
		if ((backpackData.lidAngle > 0.0F) && (backpackData.prevLidAngle <= 0.0F))
			entity.worldObj.playSoundEffect(entity.posX, entity.posY, entity.posZ, sound, 1.0F, 0.5F);
		// Play sound when closing
		if ((backpackData.lidAngle < 0.2F) && (backpackData.prevLidAngle >= 0.2F))
			entity.worldObj.playSoundEffect(entity.posX, entity.posY, entity.posZ, sound, 0.8F, 0.3F);
		
	}
	
	@ForgeSubscribe
	public void onLivingDeath(LivingDeathEvent event) {
		
		// Drops the contents from an equipped backpack when the entity dies.
		
		EntityLiving entity = event.entityLiving;
		if (entity.worldObj.isRemote) return;
		ItemStack backpack = ItemBackpack.getBackpack(entity);
		if (backpack == null) return;
		PropertiesBackpack backpackData = ItemBackpack.getBackpackData(entity);
		if (backpackData.contents == null) return;
		
		boolean keepInventory = entity.worldObj.getGameRules().getGameRuleBooleanValue("keepInventory");
		if ((entity instanceof EntityPlayer) && keepInventory) {
			
			// If keep inventory is on, instead temporarily save the contents
			// to the persistent NBT tag and get them back when the player respawns.
			
			EntityPlayer player = (EntityPlayer)entity;
			NBTTagCompound compound = player.getEntityData();
			NBTTagCompound persistent;
			if (!compound.hasKey(EntityPlayer.PERSISTED_NBT_TAG)) {
				persistent = new NBTTagCompound();
				compound.setTag(EntityPlayer.PERSISTED_NBT_TAG, persistent);
			} else persistent = compound.getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG);;
			
			NBTTagCompound backpackCompound = new NBTTagCompound();
			backpackCompound.setInteger("count", backpackData.contents.length);
			backpackCompound.setTag("Items", NbtUtils.writeItems(backpackData.contents));
			persistent.setTag("Backpack", backpackCompound);
			
		} else {
			
			for (ItemStack stack : backpackData.contents)
				WorldUtils.dropStackFromEntity(entity, stack);
			backpackData.contents = null;
			
		}
		
	}
	
	// IPlayerTracker implementation
	
	@Override
	public void onPlayerLogin(EntityPlayer player) {  }
	@Override
	public void onPlayerLogout(EntityPlayer player) {  }
	@Override
	public void onPlayerChangedDimension(EntityPlayer player) {  }
	@Override
	public void onPlayerRespawn(EntityPlayer player) {
		
		// If the player dies when when keepInventory is on and respawns,
		// retrieve the backpack items from eir persistent NBT tag.
		
		NBTTagCompound compound = player.getEntityData();
		if (!compound.hasKey(EntityPlayer.PERSISTED_NBT_TAG)) return;
		NBTTagCompound persistent = compound.getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG);
		if (!persistent.hasKey("Backpack")) return;
		NBTTagCompound backpack = persistent.getCompoundTag("Backpack");
		
		int size = backpack.getInteger("count");
		ItemStack[] contents = new ItemStack[size];
		NbtUtils.readItems(contents, backpack.getTagList("Items"));
		
		ItemBackpack.getBackpackData(player).contents = contents;
		
		persistent.removeTag("Backpack");
		if (persistent.hasNoTags())
			compound.removeTag(EntityPlayer.PERSISTED_NBT_TAG);
		
	}
	
}
