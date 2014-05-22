package net.mcft.copy.betterstorage.proxy;

import net.mcft.copy.betterstorage.attachment.EnumAttachmentInteraction;
import net.mcft.copy.betterstorage.attachment.IHasAttachments;
import net.mcft.copy.betterstorage.content.BetterStorageItems;
import net.mcft.copy.betterstorage.entity.EntityCluckington;
import net.mcft.copy.betterstorage.item.IDyeableItem;
import net.mcft.copy.betterstorage.item.ItemBucketSlime;
import net.mcft.copy.betterstorage.item.cardboard.ICardboardItem;
import net.mcft.copy.betterstorage.item.cardboard.ItemCardboardSheet;
import net.mcft.copy.betterstorage.misc.handlers.BackpackHandler;
import net.mcft.copy.betterstorage.misc.handlers.CraftingHandler;
import net.mcft.copy.betterstorage.tile.crate.CratePileCollection;
import net.mcft.copy.betterstorage.utils.MiscUtils;
import net.mcft.copy.betterstorage.utils.StackUtils;
import net.mcft.copy.betterstorage.utils.WorldUtils;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.EntityInteractEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;
import net.minecraftforge.event.world.WorldEvent.Save;
import net.minecraftforge.event.world.WorldEvent.Unload;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.eventhandler.Event.Result;
import cpw.mods.fml.common.gameevent.TickEvent.WorldTickEvent;
import cpw.mods.fml.relauncher.Side;

public class CommonProxy {
	
	public void initialize() {
		
		MinecraftForge.EVENT_BUS.register(this);
		FMLCommonHandler.instance().bus().register(this);
		
		new BackpackHandler();
		new CraftingHandler();
	}
	
	@SubscribeEvent
	public void onWorldSave(Save event) {
		CratePileCollection.saveAll(event.world);
	}
	
	@SubscribeEvent
	public void onWorldUnload(Unload event) {
		CratePileCollection.unload(event.world);
	}
	
	@SubscribeEvent
	public void onPlayerInteract(PlayerInteractEvent event) {
		
		if (event.isCanceled()) return;
		
		World world = event.entity.worldObj;
		int x = event.x;
		int y = event.y;
		int z = event.z;
		EntityPlayer player = event.entityPlayer;
		ItemStack holding = player.getCurrentEquippedItem();
		Block block = world.getBlock(x, y, z);
		boolean leftClick = (event.action == Action.LEFT_CLICK_BLOCK);
		boolean rightClick = (event.action == Action.RIGHT_CLICK_BLOCK);
		
		// Interact with attachments.
		if (leftClick || rightClick) {
			IHasAttachments hasAttachments =
					WorldUtils.get(world, x, y, z, IHasAttachments.class);
			if (hasAttachments != null) {
				EnumAttachmentInteraction interactionType =
						((event.action == Action.LEFT_CLICK_BLOCK)
								? EnumAttachmentInteraction.attack
								: EnumAttachmentInteraction.use);
				if (hasAttachments.getAttachments().interact(WorldUtils.rayTrace(player, 1.0F),
				                                             player, interactionType)) {
					event.useBlock = Result.DENY;
					event.useItem = Result.DENY;
				}
			}
		}
		
		// Use cauldron to remove color from dyable items
		if (rightClick && (block == Blocks.cauldron)) {
			int metadata = world.getBlockMetadata(x, y, z);
			if (metadata > 0) {
				IDyeableItem dyeable = (((holding != null) && (holding.getItem() instanceof IDyeableItem))
						? (IDyeableItem)holding.getItem() : null);
				if ((dyeable != null) && (dyeable.canDye(holding))) {
					StackUtils.remove(holding, "display", "color");
					world.setBlockMetadataWithNotify(x, y, z, metadata - 1, 2);
					world.func_147453_f(x, y, z, block);
					
					event.useBlock = Result.DENY;
					event.useItem = Result.DENY;
				}
			}
		}
		
		// Prevent players from breaking blocks with broken cardboard items.
		if (leftClick && (holding != null) &&
		    (holding.getItem() instanceof ICardboardItem) &&
		    !ItemCardboardSheet.isEffective(holding))
			event.useItem = Result.DENY;
		
	}
	
	@SubscribeEvent
	public void onBreakSpeed(BreakSpeed event) {
		// Stupid Forge not firing PlayerInteractEvent for left-clicks!
		// This is a workaround to instead make blocks appear unbreakable.
		EntityPlayer player = event.entityPlayer;
		ItemStack holding = player.getCurrentEquippedItem();
		if ((holding != null) && (holding.getItem() instanceof ICardboardItem) &&
		    !ItemCardboardSheet.isEffective(holding))
			event.newSpeed = -1;
	}
	
	@SubscribeEvent
	public void onEntityInteract(EntityInteractEvent event) {
		
		if (event.entity.worldObj.isRemote || event.isCanceled()) return;
		
		EntityPlayer player = event.entityPlayer;
		Entity target = event.target;
		ItemStack holding = player.getCurrentEquippedItem();
		
		if ((target.getClass() == EntityChicken.class) &&
		    (holding != null) && (holding.getItem() == Items.name_tag)) {
			
			EntityChicken chicken = (EntityChicken)target;
			if (!chicken.isDead && !chicken.isChild() &&
			    "Cluckington".equals(holding.getDisplayName()))
				EntityCluckington.spawn(chicken);
			
		}
		
		if ((BetterStorageItems.slimeBucket != null) && (target instanceof EntityLiving) &&
		    (holding != null) && (holding.getItem() == Items.bucket))
			ItemBucketSlime.pickUpSlime(player, (EntityLiving)target);
		
	}
	
	@SubscribeEvent
	public void onWorldTick(WorldTickEvent event) {
		if (event.side == Side.SERVER)
			CratePileCollection.getCollection(event.world).onTick();
	}
}
