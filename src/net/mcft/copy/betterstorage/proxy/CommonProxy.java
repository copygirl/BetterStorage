package net.mcft.copy.betterstorage.proxy;

import java.util.EnumSet;

import net.mcft.copy.betterstorage.attachment.EnumAttachmentInteraction;
import net.mcft.copy.betterstorage.attachment.IHasAttachments;
import net.mcft.copy.betterstorage.block.crate.CratePileCollection;
import net.mcft.copy.betterstorage.item.IDyeableItem;
import net.mcft.copy.betterstorage.misc.handlers.BackpackHandler;
import net.mcft.copy.betterstorage.utils.StackUtils;
import net.mcft.copy.betterstorage.utils.WorldUtils;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.Event.Result;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;
import net.minecraftforge.event.world.WorldEvent.Save;
import net.minecraftforge.event.world.WorldEvent.Unload;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;

public class CommonProxy implements ITickHandler {
	
	public void initialize() {
		
		MinecraftForge.EVENT_BUS.register(this);
		
		BackpackHandler backpacks = new BackpackHandler();
		MinecraftForge.EVENT_BUS.register(backpacks);
		GameRegistry.registerPlayerTracker(backpacks);
		TickRegistry.registerTickHandler(this, Side.SERVER);
		
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
	public void onPlayerInteract(PlayerInteractEvent event) {
		
		if (event.isCanceled()) return;
		
		World world = event.entity.worldObj;
		int x = event.x;
		int y = event.y;
		int z = event.z;
		EntityPlayer player = event.entityPlayer;
		Block block = Block.blocksList[world.getBlockId(x, y, z)];
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
		if (rightClick && (block == Block.cauldron)) {
			int metadata = world.getBlockMetadata(x, y, z);
			if (metadata > 0) {
				ItemStack holding = player.getCurrentEquippedItem();
				IDyeableItem dyeable = (((holding != null) && (holding.getItem() instanceof IDyeableItem))
						? (IDyeableItem)holding.getItem() : null);
				if ((dyeable != null) && (dyeable.canDye(holding))) {
					StackUtils.remove(holding, "display", "color");
					world.setBlockMetadataWithNotify(x, y, z, metadata - 1, 2);
					world.func_96440_m(x, y, z, block.blockID);
					
					event.useBlock = Result.DENY;
					event.useItem = Result.DENY;
				}
			}
		}
		
	}
	@Override
	public String getLabel() { return "BetterStorage"; }
	@Override
	public EnumSet<TickType> ticks() { return EnumSet.of(TickType.WORLD); }
	
	@Override
	public void tickStart(EnumSet<TickType> type, Object... tickData) {
		CratePileCollection.getCollection((World)tickData[0]).onTick();
	}
	@Override
	public void tickEnd(EnumSet<TickType> type, Object... tickData) {  }
	
}
