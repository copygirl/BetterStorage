package net.mcft.copy.betterstorage.misc.handlers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import net.mcft.copy.betterstorage.BetterStorage;
import net.mcft.copy.betterstorage.api.BetterStorageBackpack;
import net.mcft.copy.betterstorage.config.GlobalConfig;
import net.mcft.copy.betterstorage.content.BetterStorageItems;
import net.mcft.copy.betterstorage.content.BetterStorageTiles;
import net.mcft.copy.betterstorage.entity.EntityFrienderman;
import net.mcft.copy.betterstorage.inventory.InventoryStacks;
import net.mcft.copy.betterstorage.item.ItemBackpack;
import net.mcft.copy.betterstorage.item.ItemEnderBackpack;
import net.mcft.copy.betterstorage.misc.EquipmentSlot;
import net.mcft.copy.betterstorage.misc.PropertiesBackpack;
import net.mcft.copy.betterstorage.network.packet.PacketSyncSetting;
import net.mcft.copy.betterstorage.tile.TileEnderBackpack;
import net.mcft.copy.betterstorage.utils.DirectionUtils;
import net.mcft.copy.betterstorage.utils.EntityUtils;
import net.mcft.copy.betterstorage.utils.NbtUtils;
import net.mcft.copy.betterstorage.utils.RandomUtils;
import net.mcft.copy.betterstorage.utils.ReflectionUtils;
import net.mcft.copy.betterstorage.utils.StackUtils;
import net.mcft.copy.betterstorage.utils.WorldUtils;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.ChestGenHooks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.event.entity.EntityEvent.EntityConstructing;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent.SpecialSpawn;
import net.minecraftforge.event.entity.player.EntityInteractEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.Event.Result;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerChangedDimensionEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent;

public class BackpackHandler {
	
	/** Random items to be found in a backpack. There's also
	    chance the backpack will contain some dungeon loot. */
	private static final WeightedRandomChestContent[] randomBackpackItems = new WeightedRandomChestContent[]{
		
		makeWeightedContent(Items.stick, 8, 20, 100),
		makeWeightedContent(Blocks.planks, 2, 10, 100),
		makeWeightedContent(Blocks.log, 1, 8, 40),
		makeWeightedContent(Blocks.cobblestone, 6, 16, 80),
		
		makeWeightedContent(Items.wooden_pickaxe, 50, 35),
		makeWeightedContent(Items.wooden_pickaxe, 20, 10),
		makeWeightedContent(Items.stone_pickaxe, 120, 10),
		makeWeightedContent(Items.stone_pickaxe, 80, 5),
		makeWeightedContent(Items.iron_pickaxe, 220, 2),
		
		makeWeightedContent(Items.wooden_sword, 40, 30),
		makeWeightedContent(Items.stone_sword, 60, 5),
		
		makeWeightedContent(Items.bow, 200, 1, 1, 10),
		makeWeightedContent(Items.bow, 50, 1, 1, 3),
		makeWeightedContent(Items.fishing_rod, 20, 1, 1, 4),
		makeWeightedContent(Items.compass, 1, 1, 6),
		makeWeightedContent(Items.clock, 1, 1, 5),
		
		makeWeightedContent(Blocks.torch, 6, 24, 30),
		makeWeightedContent(Items.arrow, 2, 12, 10),
		makeWeightedContent(Items.rotten_flesh, 0, 3, 6, 15),
		makeWeightedContent(Items.bone, 2, 5, 20),
		makeWeightedContent(Items.string, 3, 10, 15),
		
		makeWeightedContent(Items.apple, 2, 5, 15),
		makeWeightedContent(Items.bread, 2, 4, 10),
		makeWeightedContent(Items.wheat, 3, 6, 10),
		makeWeightedContent(Items.carrot, 1, 2, 8),
		makeWeightedContent(Items.potato, 1, 2, 5),
		makeWeightedContent(Items.fish, 1, 4, 5),
		makeWeightedContent(Items.cooked_fished, 1, 2, 4),
		
		makeWeightedContent(Items.coal, 3, 9, 20),
		makeWeightedContent(Items.coal, 20, 32, 5),
		makeWeightedContent(Blocks.iron_ore, 2, 5, 15),
		makeWeightedContent(Blocks.iron_ore, 10, 20, 2),
		makeWeightedContent(Blocks.gold_ore, 2, 7, 8),
		makeWeightedContent(Items.diamond, 1, 2, 1),
		makeWeightedContent(Items.emerald, 1, 1, 1),
		
	};

	private static WeightedRandomChestContent makeWeightedContent(Item item, int damage, int min, int max, int weight) {
		return new WeightedRandomChestContent(item, damage, min, max, weight);
	}
	private static WeightedRandomChestContent makeWeightedContent(Item item, int min, int max, int weight) {
		return makeWeightedContent(item, 0, min, max, weight);
	}
	private static WeightedRandomChestContent makeWeightedContent(Item item, int damage, int weight) {
		return makeWeightedContent(item, damage, 1, 1, weight);
	}
	private static WeightedRandomChestContent makeWeightedContent(Block block, int min, int max, int weight) {
		return makeWeightedContent(Item.getItemFromBlock(block), min, max, weight);
	}
	
	public BackpackHandler() {
		MinecraftForge.EVENT_BUS.register(this);
		FMLCommonHandler.instance().bus().register(this);
		
		BetterStorageBackpack.spawnWithBackpack(EntityZombie.class, 1.0 / 800);
		BetterStorageBackpack.spawnWithBackpack(EntitySkeleton.class, 1.0 / 1200);
		BetterStorageBackpack.spawnWithBackpack(EntityPigZombie.class, 1.0 / 1000);
		BetterStorageBackpack.spawnWithBackpack(EntityEnderman.class, 1.0 / 80);
	}
	
	@SubscribeEvent
	public void onEntityConstructing(EntityConstructing event) {
		// Initialize backpack data for every living entity.
		// If the entity does have a backpack, it will be loaded automatically.
		if (event.entity instanceof EntityLivingBase)
			ItemBackpack.initBackpackData((EntityLivingBase)event.entity);
	}
	
	@SubscribeEvent
	public void onPlayerInteract(PlayerInteractEvent event) {
		if (event.isCanceled()) return;
		// When player right clicks a block, attempt to place backpack.
		if (event.action == Action.RIGHT_CLICK_BLOCK)
			if (ItemBackpack.onPlaceBackpack(event.entityPlayer, event.x, event.y, event.z, event.face)) {
				event.useBlock = Result.DENY;
				event.useItem = Result.DENY;
			}
	}
	
	@SubscribeEvent
	public void onEntityInteract(EntityInteractEvent event) {
		
		// Right clicking the back of an entity that
		// has a backpack will open a GUI for that it.
		
		if (event.entity.worldObj.isRemote ||
		    !(event.entity instanceof EntityPlayerMP) ||
		    !(event.target instanceof EntityLivingBase) ||
		    (((EntityPlayerMP)event.entity).playerNetServerHandler == null) ||
		    ((event.target instanceof EntityPlayer) &&
		     !BetterStorage.globalConfig.getBoolean(GlobalConfig.enableBackpackInteraction))) return;
		
		EntityPlayerMP player = (EntityPlayerMP)event.entity;
		EntityLivingBase target = (EntityLivingBase)event.target;
		if (ItemBackpack.openBackpack(player, target))
			player.swingItem();
		
	}
	
	@SubscribeEvent
	public void onSpecialSpawn(SpecialSpawn event) {
		
		// When a mob spawns naturally, see if it has a chance to spawn with a backpack.
		EntityLivingBase entity = event.entityLiving;
		World world = entity.worldObj;
		double probability = 0.0;
		
		for (BetterStorageBackpack.BackpackSpawnEntry entry : BetterStorageBackpack.spawnWithBackpack) {
			if (!entity.getClass().equals(entry.entityClass)) continue;
			probability = entry.probability;
			break;
		}
		
		if (!RandomUtils.getBoolean(probability) || entity.isChild()) return;
		
		// If entity is a vanilla enderman, replace it with a friendly one.
		if (entity.getClass().equals(EntityEnderman.class)) {
			if ((BetterStorageTiles.enderBackpack != null) &&
			    // Don't spawn friendly endermen in the end or end biome, would make them too easy to get.
			    (world.provider.dimensionId != 1) &&
			    (world.getBiomeGenForCoords((int)entity.posX, (int)entity.posZ) != BiomeGenBase.sky)) {
				EntityFrienderman frienderman = new EntityFrienderman(world);
				frienderman.setPositionAndRotation(entity.posX, entity.posY, entity.posZ, entity.rotationYaw, 0);
				world.spawnEntityInWorld(frienderman);
				ItemBackpack.getBackpackData(frienderman).spawnsWithBackpack = true;
				entity.setDead();
			}
		// Otherwise, just mark it to spawn with a backpack.
		} else if (BetterStorageTiles.backpack != null)
			ItemBackpack.getBackpackData(entity).spawnsWithBackpack = true;
		
	}
	
	@SubscribeEvent
	public void onPlayerStartTracking(PlayerEvent.StartTracking event) {
		// When the entity is sent to a player,
		// also send em the backpack data, if any.
		if (event.target instanceof EntityLivingBase)
			ItemBackpack.getBackpackData((EntityLivingBase)event.target)
					.sendDataToPlayer((EntityLivingBase)event.target, event.entityPlayer);
	}
	
	@SubscribeEvent
	public void onPlayerChangedDimensionEvent(PlayerChangedDimensionEvent event) {
		// When a player changes dimensions, resend the backpack data.
		ItemBackpack.getBackpackData(event.player).sendDataToPlayer(event.player, event.player);
	}
	
	@SubscribeEvent
	public void onLivingUpdate(LivingUpdateEvent event) {
		
		EntityLivingBase entity = event.entityLiving;
		EntityPlayer player = ((entity instanceof EntityPlayer) ? (EntityPlayer)entity : null);
		ItemStack backpack = ItemBackpack.getBackpack(entity);
		
		PropertiesBackpack backpackData;
		if (backpack == null) {
			
			backpackData = EntityUtils.getProperties(entity, PropertiesBackpack.class);
			if (backpackData == null) return;
			
			// If the entity is supposed to spawn
			// with a backpack, equip it with one.
			if (backpackData.spawnsWithBackpack) {
				
				ItemStack[] contents = null;
				if (entity instanceof EntityFrienderman) {
					backpack = new ItemStack(BetterStorageItems.itemEnderBackpack);
					// Remove drop chance for the backpack.
					((EntityLiving)entity).setEquipmentDropChance(EquipmentSlot.CHEST, 0.0F);
				} else {
					backpack = new ItemStack(BetterStorageItems.itemBackpack, 1, RandomUtils.getInt(120, 240));
					ItemBackpack backpackType = (ItemBackpack)backpack.getItem();
					if (RandomUtils.getBoolean(0.15)) {
						// Give the backpack a random color.
						int r = RandomUtils.getInt(32, 224);
						int g = RandomUtils.getInt(32, 224);
						int b = RandomUtils.getInt(32, 224);
						int color = (r << 16) | (g << 8) | b;
						StackUtils.set(backpack, color, "display", "color");
					}
					contents = new ItemStack[backpackType.getBackpackColumns() * backpackType.getBackpackRows()];
					// Set drop chance for the backpack to 100%.
					((EntityLiving)entity).setEquipmentDropChance(EquipmentSlot.CHEST, 1.0F);
				}
				
				// If the entity spawned with enchanted armor,
				// move the enchantments over to the backpack.
				ItemStack armor = entity.getEquipmentInSlot(EquipmentSlot.CHEST);
				if (armor != null && armor.isItemEnchanted()) {
					NBTTagCompound compound = new NBTTagCompound();
					compound.setTag("ench", armor.getTagCompound().getTag("ench"));
					backpack.setTagCompound(compound);
				}
				
				if (contents != null) {
					// Add random items to the backpack.
					InventoryStacks inventory = new InventoryStacks(contents);
					// Add normal random backpack loot.
					WeightedRandomChestContent.generateChestContents(
							RandomUtils.random, randomBackpackItems, inventory, 20);
					// With a chance of 10%, add some random dungeon loot.
					if (RandomUtils.getDouble() < 0.1) {
						ChestGenHooks info = ChestGenHooks.getInfo(ChestGenHooks.DUNGEON_CHEST);
						WeightedRandomChestContent.generateChestContents(
								RandomUtils.random, info.getItems(RandomUtils.random), inventory, 5);
					}
					
				}
				
				ItemBackpack.setBackpack(entity, backpack, contents);
				backpackData.spawnsWithBackpack = false;
				
			} else {
				
				// If the entity doesn't have a backpack equipped,
				// but still has some backpack data, drop the items.
				if (backpackData.contents != null) {
					for (ItemStack stack : backpackData.contents)
						WorldUtils.dropStackFromEntity(entity, stack, 1.5F);
					backpackData.contents = null;
				}
				
			}
			
		}
		
		ItemBackpack.getBackpackData(entity).update(entity);
		if (backpack != null) ((ItemBackpack)backpack.getItem()).onEquippedUpdate(entity, backpack);
		
	}
	
	@SubscribeEvent
	public void onLivingDeath(LivingDeathEvent event) {
		
		// If an entity wearing a backpack dies,
		// try to place it, or drop the items.
		
		EntityLivingBase entity = event.entityLiving;
		if (entity.worldObj.isRemote) return;

		EntityPlayer player = ((entity instanceof EntityPlayer) ? (EntityPlayer)entity : null);
		ItemStack backpack = ItemBackpack.getBackpack(entity);
		if (backpack == null) return;
		PropertiesBackpack backpackData = ItemBackpack.getBackpackData(entity);
		if (backpackData.contents == null) return;
		
		boolean keepInventory = entity.worldObj.getGameRules().getGameRuleBooleanValue("keepInventory");
		if ((player != null) && keepInventory) {
			
			// If keep inventory is on, instead temporarily save the contents
			// to the persistent NBT tag and get them back when the player respawns.
			
			NBTTagCompound compound = player.getEntityData();
			NBTTagCompound persistent;
			if (!compound.hasKey(EntityPlayer.PERSISTED_NBT_TAG)) {
				persistent = new NBTTagCompound();
				compound.setTag(EntityPlayer.PERSISTED_NBT_TAG, persistent);
			} else persistent = compound.getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG);;
			
			NBTTagCompound backpackCompound = new NBTTagCompound();
			
			backpackCompound.setInteger("count", backpackData.contents.length);
			backpackCompound.setTag("Items", NbtUtils.writeItems(backpackData.contents));
			
			if (!ItemBackpack.hasChestplateBackpackEquipped(entity))
				backpackCompound.setTag("Stack", backpack.writeToNBT(new NBTTagCompound()));
			
			persistent.setTag("Backpack", backpackCompound);
			
		} else {
			
			// Attempt to place the backpack as a block instead of dropping the items.
			if (BetterStorage.globalConfig.getBoolean(GlobalConfig.dropBackpackOnDeath)) {
				
				ForgeDirection orientation = DirectionUtils.getOrientation(entity);
				int recentlyHit = ReflectionUtils.get(EntityLivingBase.class, entity, "field_70718_bc", "recentlyHit");
				boolean despawn = ((player == null) && (recentlyHit <= 0));
				
				List<BlockCoordinate> coords = new ArrayList<BlockCoordinate>();
				for (int x = -2; x <= 2; x++)
					for (int z = -2; z <= 2; z++)
						coords.add(new BlockCoordinate(entity.posX, entity.posY, entity.posZ, x, 0, z));
				
				// Try to place the backpack on the ground nearby,
				// or look for a ground above or below to place it.
				Collections.sort(coords, blockDistanceComparator);
				while (!coords.isEmpty()) {
					Iterator<BlockCoordinate> iter = coords.iterator();
					while (iter.hasNext()) {
						BlockCoordinate coord = iter.next();
						if (ItemBackpack.placeBackpack(entity, player, backpack,
						                               coord.x, coord.y, coord.z, 1,
						                               orientation, despawn, true)) {
							ItemBackpack.setBackpack(entity, null, null);
							return;
						}
						boolean replacable = entity.worldObj.getBlock(coord.x, coord.y, coord.z)
								.isReplaceable(entity.worldObj, coord.x, coord.y, coord.z);
						coord.y += (replacable ? -1 : 1);
						coord.moved += (replacable ? 1 : 5);
						if ((coord.y <= 0) || (coord.y > entity.worldObj.getHeight()) ||
						    (coord.moved > 24 - coord.distance * 4)) iter.remove();
					}
				}
				
				// If backpack couldn't be placed and isn't equipped as armor, drop it.
				if (backpackData.backpack != null)
					WorldUtils.dropStackFromEntity(entity, backpack, 4.0F);
				
			}
			
			for (ItemStack stack : backpackData.contents)
				WorldUtils.dropStackFromEntity(entity, stack, 4.0F);
			backpackData.contents = null;
			
		}
		
	}
	private static class BlockCoordinate {
		public int x, y, z;
		public double distance;
		public int moved = 0;
		public BlockCoordinate(double ex, double ey, double ez, int x, int y, int z) {
			this.x = (int)ex + x;
			this.y = (int)ey + y;
			this.z = (int)ez + z;
			distance = Math.sqrt(Math.pow(this.x + 0.5 - ex, 2) +
			                     Math.pow(this.y + 0.5 - ey, 2) +
			                     Math.pow(this.z + 0.5 - ez, 2));
		}
	}
	private static Comparator<BlockCoordinate> blockDistanceComparator = new Comparator<BlockCoordinate>() {
		@Override public int compare(BlockCoordinate o1, BlockCoordinate o2) {
			if (o1.distance < o2.distance) return -1;
			else if (o1.distance > o2.distance) return 1;
			else return 0;
		}
	};
	
	@SubscribeEvent
	public void onEntityJoinWorldEvent(EntityJoinWorldEvent event) {
		if (event.world.isRemote) return;
		
		// If an ender backpack ever drops as an item,
		// instead teleport it somewhere as a block.
		
		if (!(event.entity instanceof EntityItem)) return;
		EntityItem entity = (EntityItem)event.entity;
		ItemStack stack = entity.getDataWatcher().getWatchableObjectItemStack(10);
		if ((stack == null) || !(stack.getItem() instanceof ItemEnderBackpack)) return;
		event.setCanceled(true);
		for (int i = 0; i < 64; i++)
			if (TileEnderBackpack.teleportRandomly(entity.worldObj, entity.posX, entity.posY, entity.posZ, (i > 48), stack))
				break;
		
	}
	
	@SubscribeEvent
	public void onPlayerLogin(PlayerLoggedInEvent event) {
		// Send player the information if the backpack open key is enabled on this server.
		BetterStorage.networkChannel.sendTo(new PacketSyncSetting(BetterStorage.globalConfig), event.player);
	}
	
	@SubscribeEvent
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		
		// If the player dies when when keepInventory is on and respawns,
		// retrieve the backpack items from eir persistent NBT tag.
		
		NBTTagCompound entityData = event.player.getEntityData();
		if (!entityData.hasKey(EntityPlayer.PERSISTED_NBT_TAG)) return;
		NBTTagCompound persistent = entityData.getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG);
		
		if (!persistent.hasKey("Backpack")) return;
		NBTTagCompound compound = persistent.getCompoundTag("Backpack");
		PropertiesBackpack backpackData = ItemBackpack.getBackpackData(event.player);
		
		int size = compound.getInteger("count");
		ItemStack[] contents = new ItemStack[size];
		NbtUtils.readItems(contents, compound.getTagList("Items", NBT.TAG_COMPOUND));
		backpackData.contents = contents;
		
		if (compound.hasKey("Stack"))
			backpackData.backpack = ItemStack.loadItemStackFromNBT(compound.getCompoundTag("Stack"));
		
		persistent.removeTag("Backpack");
		if (persistent.hasNoTags())
			entityData.removeTag(EntityPlayer.PERSISTED_NBT_TAG);
		
	}
	
}
