package net.mcft.copy.betterstorage.proxy;

import net.mcft.copy.betterstorage.attachment.EnumAttachmentInteraction;
import net.mcft.copy.betterstorage.attachment.IHasAttachments;
import net.mcft.copy.betterstorage.block.crate.CratePileCollection;
import net.mcft.copy.betterstorage.misc.handlers.BackpackHandler;
import net.mcft.copy.betterstorage.utils.WorldUtils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.Event.Result;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;
import net.minecraftforge.event.world.WorldEvent.Save;
import net.minecraftforge.event.world.WorldEvent.Unload;
import cpw.mods.fml.common.registry.GameRegistry;

public class CommonProxy {
	
	public void initialize() {
		
		MinecraftForge.EVENT_BUS.register(this);
		
		BackpackHandler backpacks = new BackpackHandler();
		MinecraftForge.EVENT_BUS.register(backpacks);
		GameRegistry.registerPlayerTracker(backpacks);
		
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
		
		// Interact with attachments.
		if (event.isCanceled()) return;
		
		if ((event.action != Action.LEFT_CLICK_BLOCK) &&
		    (event.action != Action.RIGHT_CLICK_BLOCK)) return;
		IHasAttachments hasAttachments =
				WorldUtils.get(event.entity.worldObj, event.x, event.y, event.z, IHasAttachments.class);
		if (hasAttachments == null) return;
		EnumAttachmentInteraction interactionType =
				((event.action == Action.LEFT_CLICK_BLOCK)
						? EnumAttachmentInteraction.attack
						: EnumAttachmentInteraction.use);
		if (hasAttachments.getAttachments().interact(event.entityPlayer, interactionType)) {
			event.useBlock = Result.DENY;
			event.useItem = Result.DENY;
		}
		
	}
	
}
