package net.mcft.copy.betterstorage.proxy;

import java.util.HashMap;
import java.util.Map;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.registry.TickRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.mcft.copy.betterstorage.BetterStorage;
import net.mcft.copy.betterstorage.addon.Addon;
import net.mcft.copy.betterstorage.attachment.Attachment;
import net.mcft.copy.betterstorage.attachment.Attachments;
import net.mcft.copy.betterstorage.attachment.IHasAttachments;
import net.mcft.copy.betterstorage.block.BlockArmorStand;
import net.mcft.copy.betterstorage.block.tileentity.TileEntityArmorStand;
import net.mcft.copy.betterstorage.block.tileentity.TileEntityBackpack;
import net.mcft.copy.betterstorage.block.tileentity.TileEntityLocker;
import net.mcft.copy.betterstorage.block.tileentity.TileEntityReinforcedChest;
import net.mcft.copy.betterstorage.client.renderer.BetterStorageRenderingHandler;
import net.mcft.copy.betterstorage.client.renderer.ItemRendererBackpack;
import net.mcft.copy.betterstorage.client.renderer.RenderFrienderman;
import net.mcft.copy.betterstorage.client.renderer.TileEntityArmorStandRenderer;
import net.mcft.copy.betterstorage.client.renderer.TileEntityBackpackRenderer;
import net.mcft.copy.betterstorage.client.renderer.TileEntityLockerRenderer;
import net.mcft.copy.betterstorage.client.renderer.TileEntityReinforcedChestRenderer;
import net.mcft.copy.betterstorage.entity.EntityFrienderman;
import net.mcft.copy.betterstorage.misc.handlers.TickHandler;
import net.mcft.copy.betterstorage.utils.WorldUtils;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EnumMovingObjectType;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.event.ForgeSubscribe;

@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy {
	
	public static int reinforcedChestRenderId;
	public static int lockerRenderId;
	public static int armorStandRenderId;
	public static int backpackRenderId;
	
	public static final Map<Class<? extends TileEntity>, BetterStorageRenderingHandler> renderingHandlers =
			new HashMap<Class<? extends TileEntity>, BetterStorageRenderingHandler>();
	
	@Override
	public void init() {
		super.init();
		TickRegistry.registerTickHandler(new TickHandler(), Side.CLIENT);
		MinecraftForgeClient.registerItemRenderer(BetterStorage.backpack.blockID, ItemRendererBackpack.instance);
		MinecraftForgeClient.registerItemRenderer(BetterStorage.enderBackpack.blockID, ItemRendererBackpack.instance);
		Addon.postClientInitAll();
	}
	
	@Override
	public void registerEntities() {
		super.registerEntities();
		RenderingRegistry.registerEntityRenderingHandler(EntityFrienderman.class, new RenderFrienderman());
	}
	
	@Override
	public void registerTileEntites() {
		super.registerTileEntites();
		reinforcedChestRenderId = registerTileEntityRenderer(TileEntityReinforcedChest.class, new TileEntityReinforcedChestRenderer());
		lockerRenderId = registerTileEntityRenderer(TileEntityLocker.class, new TileEntityLockerRenderer());
		armorStandRenderId = registerTileEntityRenderer(TileEntityArmorStand.class, new TileEntityArmorStandRenderer(), false, 0, 1, 0);
		backpackRenderId = registerTileEntityRenderer(TileEntityBackpack.class, new TileEntityBackpackRenderer(), true, -160, 1.5F, 0.14F);
	}
	
	public static int registerTileEntityRenderer(Class<? extends TileEntity> tileEntityClass, TileEntitySpecialRenderer renderer,
	                                       boolean render3dInInventory, float rotation, float scale, float yOffset) {
		ClientRegistry.bindTileEntitySpecialRenderer(tileEntityClass, renderer);
		BetterStorageRenderingHandler renderingHandler =
			new BetterStorageRenderingHandler(tileEntityClass, renderer, render3dInInventory, rotation, scale, yOffset);
		RenderingRegistry.registerBlockHandler(renderingHandler);
		renderingHandlers.put(tileEntityClass, renderingHandler);
		return renderingHandler.getRenderId();
	}
	public static int registerTileEntityRenderer(Class<? extends TileEntity> tileEntityClass, TileEntitySpecialRenderer renderer) {
		return registerTileEntityRenderer(tileEntityClass, renderer, true, 90, 1, 0);
	}
	
	@ForgeSubscribe
	public void drawBlockHighlight(DrawBlockHighlightEvent event) {

		EntityPlayer player = event.player;
		World world = player.worldObj;
		MovingObjectPosition target = WorldUtils.rayTrace(player, event.partialTicks);
		if ((target == null) || (target.typeOfHit != EnumMovingObjectType.TILE)) return;
		int x = target.blockX;
		int y = target.blockY;
		int z = target.blockZ;
		
		AxisAlignedBB box = null;
		Block block = Block.blocksList[world.getBlockId(x, y, z)];
		TileEntity tileEntity = world.getBlockTileEntity(x, y, z);
		
		if (block instanceof BlockArmorStand)
			box = getArmorStandHighlightBox(player, world, x, y, z, target.hitVec);
		else if (tileEntity instanceof IHasAttachments)
			box = getAttachmentPointsHighlightBox(player, tileEntity, target);
		
		if (box == null) return;
		
		double xOff = player.lastTickPosX + (player.posX - player.lastTickPosX) * event.partialTicks;
		double yOff = player.lastTickPosY + (player.posY - player.lastTickPosY) * event.partialTicks;
		double zOff = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * event.partialTicks;
		box.offset(-xOff, -yOff, -zOff);
        
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glColor4f(0.0F, 0.0F, 0.0F, 0.4F);
		GL11.glLineWidth(2.0F);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glDepthMask(false);
		
		Tessellator tes = Tessellator.instance;
		tes.startDrawing(3);
		tes.addVertex(box.minX, box.minY, box.minZ);
		tes.addVertex(box.maxX, box.minY, box.minZ);
		tes.addVertex(box.maxX, box.minY, box.maxZ);
		tes.addVertex(box.minX, box.minY, box.maxZ);
		tes.addVertex(box.minX, box.minY, box.minZ);
		tes.draw();
		tes.startDrawing(3);
		tes.addVertex(box.minX, box.maxY, box.minZ);
		tes.addVertex(box.maxX, box.maxY, box.minZ);
		tes.addVertex(box.maxX, box.maxY, box.maxZ);
		tes.addVertex(box.minX, box.maxY, box.maxZ);
		tes.addVertex(box.minX, box.maxY, box.minZ);
		tes.draw();
		tes.startDrawing(1);
		tes.addVertex(box.minX, box.minY, box.minZ);
		tes.addVertex(box.minX, box.maxY, box.minZ);
		tes.addVertex(box.maxX, box.minY, box.minZ);
		tes.addVertex(box.maxX, box.maxY, box.minZ);
		tes.addVertex(box.maxX, box.minY, box.maxZ);
		tes.addVertex(box.maxX, box.maxY, box.maxZ);
		tes.addVertex(box.minX, box.minY, box.maxZ);
		tes.addVertex(box.minX, box.maxY, box.maxZ);
		tes.draw();
		
		GL11.glDepthMask(true);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_BLEND);
		
		event.setCanceled(true);
		
	}
	
	private AxisAlignedBB getArmorStandHighlightBox(EntityPlayer player, World world, int x, int y, int z, Vec3 hitVec) {
		
		int metadata = world.getBlockMetadata(x, y, z);
		if (metadata > 0) y -= 1;
		
		TileEntityArmorStand armorStand = WorldUtils.get(world, x, y, z, TileEntityArmorStand.class);
		if (armorStand == null) return null;
		int slot = Math.min(3, (int)((hitVec.yCoord - y) * 2));
		
		ItemStack item = armorStand.armor[slot];
		ItemStack holding = player.getCurrentEquippedItem();
		ItemStack armor = player.inventory.armorInventory[slot];
		
		if (player.isSneaking()) {
			if ((holding != null) ||
			    ((item == null) && (armor == null)) ||
			    ((armor != null) && !armor.getItem().isValidArmor(armor, 3 - slot, player)))
				return null;
		} else if (!((item != null) && (holding == null)) &&
		           !((holding != null) && holding.getItem().isValidArmor(holding, 3 - slot, player)))
			return null;
		
		double minX = x + 2 / 16.0;
		double minY = y + slot / 2.0;
		double minZ = z + 2 / 16.0;
		double maxX = x + 14 / 16.0;
		double maxY = y + slot / 2.0 + 0.5;
		double maxZ = z + 14 / 16.0;
		
		return AxisAlignedBB.getAABBPool().getAABB(minX, minY, minZ, maxX, maxY, maxZ);
		
	}
	
	private AxisAlignedBB getAttachmentPointsHighlightBox(EntityPlayer player, TileEntity tileEntity,
	                                                      MovingObjectPosition target) {
		Attachments attachments = ((IHasAttachments)tileEntity).getAttachments();
		Attachment attachment = attachments.get(target.subHit);
		if (attachment == null) return null;
		return attachment.getBox().copy().offset(tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord);
	}
	
}
