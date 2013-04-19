package net.mcft.copy.betterstorage.proxy;

import net.mcft.copy.betterstorage.block.TileEntityArmorStand;
import net.mcft.copy.betterstorage.block.TileEntityBackpack;
import net.mcft.copy.betterstorage.block.TileEntityLocker;
import net.mcft.copy.betterstorage.block.TileEntityReinforcedChest;
import net.mcft.copy.betterstorage.block.crate.CratePileCollection;
import net.mcft.copy.betterstorage.block.crate.TileEntityCrate;
import net.mcft.copy.betterstorage.item.ItemBackpack;
import net.mcft.copy.betterstorage.misc.PropertiesHiddenCloak;
import net.mcft.copy.betterstorage.utils.PlayerUtils;
import net.mcft.copy.betterstorage.utils.StackUtils;
import net.mcft.copy.betterstorage.utils.WorldUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.Event.Result;
import net.minecraftforge.event.ForgeSubscribe;
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
		ItemStack backpack = player.inventory.armorInventory[2];
		if (backpack == null || !(backpack.getItem() instanceof ItemBackpack)) return;
		// Try to place the backpack as if it was being held by the player.
		backpack.getItem().onItemUse(backpack, player, player.worldObj, event.x, event.y, event.z, event.face, 0, 0, 0);
		// Only continue if the backpack was places successfully.
		if (backpack.stackSize > 0) return;
		player.inventory.armorInventory[2] = null;
		player.swingItem();
		// If the player had a cloak, make it visible again.
		if (player.worldObj.isRemote && player.cloakUrl == null) {
			PropertiesHiddenCloak hiddenCloak = PlayerUtils.getProperties(player, PropertiesHiddenCloak.class);
			if (hiddenCloak != null)
				player.cloakUrl = hiddenCloak.cloakUrl;
		}
		event.useBlock = Result.DENY;
		
	}
	
	@ForgeSubscribe
	public void onLivingDeath(LivingDeathEvent event) {
		
		// Drops the contents from an equipped backpack when the entity dies.
		
		if (event.entity.worldObj.isRemote) return;
		ItemStack backpack = event.entityLiving.getCurrentArmor(2);
		if (backpack == null || !(backpack.getItem() instanceof ItemBackpack)) return;
		for (ItemStack stack : StackUtils.getStackContents(backpack))
			WorldUtils.dropStackFromEntity(event.entity, stack);
		StackUtils.remove(backpack, "Items");
		
	}
	
}
