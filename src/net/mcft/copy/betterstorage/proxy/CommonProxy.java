package net.mcft.copy.betterstorage.proxy;

import net.mcft.copy.betterstorage.addon.Addon;
import net.mcft.copy.betterstorage.block.TileEntityArmorStand;
import net.mcft.copy.betterstorage.block.TileEntityBackpack;
import net.mcft.copy.betterstorage.block.TileEntityLocker;
import net.mcft.copy.betterstorage.block.TileEntityReinforcedChest;
import net.mcft.copy.betterstorage.block.crate.CratePileCollection;
import net.mcft.copy.betterstorage.block.crate.TileEntityCrate;
import net.mcft.copy.betterstorage.item.ItemBackpack;
import net.mcft.copy.betterstorage.misc.PropertiesBackpackItems;
import net.mcft.copy.betterstorage.utils.EntityUtils;
import net.mcft.copy.betterstorage.utils.WorldUtils;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.Event.Result;
import net.minecraftforge.event.ForgeSubscribe;
import net.minecraftforge.event.entity.EntityEvent.EntityConstructing;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;
import net.minecraftforge.event.world.WorldEvent.Save;
import net.minecraftforge.event.world.WorldEvent.Unload;
import cpw.mods.fml.common.registry.GameRegistry;

public class CommonProxy {
	
	public void init() {
		MinecraftForge.EVENT_BUS.register(this);
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
	public void onPlayerInteract(PlayerInteractEvent event) {
		
		// Places an equipped backpack when the player right clicks
		// on the ground while sneaking and holding nothing.
		
		if (event.action != Action.RIGHT_CLICK_BLOCK) return;
		EntityPlayer player = event.entityPlayer;
		if (player.getCurrentEquippedItem() != null || !player.isSneaking()) return;
		ItemStack backpack = ItemBackpack.getBackpack(player);
		if (backpack == null) return;
		// Try to place the backpack as if it was being held and used by the player.
		backpack.getItem().onItemUse(backpack, player, player.worldObj, event.x, event.y, event.z, event.face, 0, 0, 0);
		// Only continue if the backpack was places successfully.
		if (backpack.stackSize > 0) return;
		player.swingItem();
		event.useBlock = Result.DENY;
		
	}
	
	@ForgeSubscribe
	public void onEntityConstruction(EntityConstructing event) {
		if (!event.entity.worldObj.isRemote && (event.entity instanceof EntityLiving))
			EntityUtils.createProperties(event.entity, PropertiesBackpackItems.class);
	}
	
	@ForgeSubscribe
	public void onLivingDeath(LivingDeathEvent event) {
		
		// Drops the contents from an equipped backpack when the entity dies.
		
		EntityLiving entity = event.entityLiving;
		if (entity.worldObj.isRemote) return;
		ItemStack backpack = ItemBackpack.getBackpack(entity);
		if (backpack == null) return;
		PropertiesBackpackItems backpackItems = ItemBackpack.getBackpackItems(entity); 
		for (ItemStack stack : backpackItems.contents)
			WorldUtils.dropStackFromEntity(entity, stack);
		ItemBackpack.removeBackpack(entity, false);
		
	}
	
}
