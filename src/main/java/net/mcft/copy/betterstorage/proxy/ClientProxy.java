package net.mcft.copy.betterstorage.proxy;

import java.util.HashMap;
import java.util.Map;

import net.mcft.copy.betterstorage.BetterStorage;
import net.mcft.copy.betterstorage.addon.Addon;
import net.mcft.copy.betterstorage.api.stand.BetterStorageArmorStand;
import net.mcft.copy.betterstorage.attachment.Attachment;
import net.mcft.copy.betterstorage.attachment.Attachments;
import net.mcft.copy.betterstorage.attachment.IHasAttachments;
import net.mcft.copy.betterstorage.client.model.ModelBackpackArmor;
import net.mcft.copy.betterstorage.client.model.ModelCluckington;
import net.mcft.copy.betterstorage.client.renderer.BetterStorageRenderingHandler;
import net.mcft.copy.betterstorage.client.renderer.ItemRendererBackpack;
import net.mcft.copy.betterstorage.client.renderer.ItemRendererCardboardBox;
import net.mcft.copy.betterstorage.client.renderer.ItemRendererContainer;
import net.mcft.copy.betterstorage.client.renderer.RenderFrienderman;
import net.mcft.copy.betterstorage.client.renderer.TileEntityArmorStandRenderer;
import net.mcft.copy.betterstorage.client.renderer.TileEntityBackpackRenderer;
import net.mcft.copy.betterstorage.client.renderer.TileEntityLockableDoorRenderer;
import net.mcft.copy.betterstorage.client.renderer.TileEntityLockerRenderer;
import net.mcft.copy.betterstorage.client.renderer.TileEntityPresentRenderer;
import net.mcft.copy.betterstorage.client.renderer.TileEntityReinforcedChestRenderer;
import net.mcft.copy.betterstorage.content.BetterStorageItems;
import net.mcft.copy.betterstorage.content.BetterStorageTiles;
import net.mcft.copy.betterstorage.entity.EntityCluckington;
import net.mcft.copy.betterstorage.entity.EntityFrienderman;
import net.mcft.copy.betterstorage.item.ItemBackpack;
import net.mcft.copy.betterstorage.misc.Constants;
import net.mcft.copy.betterstorage.misc.Resources;
import net.mcft.copy.betterstorage.misc.handlers.KeyBindingHandler;
import net.mcft.copy.betterstorage.tile.entity.TileEntityBackpack;
import net.mcft.copy.betterstorage.tile.entity.TileEntityLockableDoor;
import net.mcft.copy.betterstorage.tile.entity.TileEntityLocker;
import net.mcft.copy.betterstorage.tile.entity.TileEntityPresent;
import net.mcft.copy.betterstorage.tile.entity.TileEntityReinforcedChest;
import net.mcft.copy.betterstorage.tile.entity.TileEntityReinforcedLocker;
import net.mcft.copy.betterstorage.tile.stand.TileEntityArmorStand;
import net.mcft.copy.betterstorage.tile.stand.VanillaArmorStandRenderHandler;
import net.mcft.copy.betterstorage.utils.RenderUtils;
import net.mcft.copy.betterstorage.utils.WorldUtils;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.entity.RenderChicken;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy {
	
	public static int reinforcedChestRenderId;
	public static int lockerRenderId;
	public static int armorStandRenderId;
	public static int backpackRenderId;
	public static int reinforcedLockerRenderId;
	public static int lockableDoorRenderId;
	public static int presentRenderId;
	
	public static final Map<Class<? extends TileEntity>, BetterStorageRenderingHandler> renderingHandlers =
			new HashMap<Class<? extends TileEntity>, BetterStorageRenderingHandler>();
	
	@Override
	public void initialize() {
		
		super.initialize();	
		new KeyBindingHandler();
		registerRenderers();
		
	}
	
	@Override
	protected void registerArmorStandHandlers() {
		super.registerArmorStandHandlers();
		BetterStorageArmorStand.registerRenderHandler(new VanillaArmorStandRenderHandler());
	}
	
	private void registerRenderers() {
		
		registerItemRenderer(BetterStorageItems.itemBackpack, new ItemRendererBackpack(TileEntityBackpack.class));
		registerItemRenderer(BetterStorageItems.itemEnderBackpack, new ItemRendererBackpack(TileEntityBackpack.class));
		
		registerItemRenderer(BetterStorageTiles.reinforcedChest, new ItemRendererContainer(TileEntityReinforcedChest.class));
		registerItemRenderer(BetterStorageTiles.reinforcedLocker, new ItemRendererContainer(TileEntityReinforcedLocker.class));
		
		registerItemRenderer(BetterStorageTiles.cardboardBox, new ItemRendererCardboardBox(BetterStorageTiles.cardboardBox));
		registerItemRenderer(BetterStorageTiles.present, new ItemRendererContainer(TileEntityPresent.class));
		
		RenderManager manager = Minecraft.getMinecraft().getRenderManager();
		RenderingRegistry.registerEntityRenderingHandler(EntityFrienderman.class, new RenderFrienderman(manager));
		RenderingRegistry.registerEntityRenderingHandler(EntityCluckington.class, new RenderChicken(manager, new ModelCluckington(), 0.4F));
		
		reinforcedChestRenderId = registerTileEntityRenderer(TileEntityReinforcedChest.class, new TileEntityReinforcedChestRenderer());
		lockerRenderId = registerTileEntityRenderer(TileEntityLocker.class, new TileEntityLockerRenderer());
		armorStandRenderId = registerTileEntityRenderer(TileEntityArmorStand.class, new TileEntityArmorStandRenderer(), false, 0, 1, 0);
		backpackRenderId = registerTileEntityRenderer(TileEntityBackpack.class, new TileEntityBackpackRenderer(), true, -160, 1.5F, 0.14F);
		reinforcedLockerRenderId = registerTileEntityRenderer(TileEntityReinforcedLocker.class, new TileEntityLockerRenderer());
		lockableDoorRenderId = registerTileEntityRenderer(TileEntityLockableDoor.class, new TileEntityLockableDoorRenderer());
		presentRenderId = registerTileEntityRenderer(TileEntityPresent.class, new TileEntityPresentRenderer());
//		RenderingRegistry.registerBlockHandler(new TileLockableDoorRenderingHandler());
		Addon.registerRenderersAll();
		
	}
	
	public static void registerItemRenderer(Block block, IItemRenderer renderer) {
		if (block != null)
			MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(block), renderer);
	}
	
	public static void registerItemRenderer(Item item, IItemRenderer renderer) {
		if (item != null)
			MinecraftForgeClient.registerItemRenderer(item, renderer);
	}
	
	@Deprecated
	public static int registerTileEntityRenderer(Class<? extends TileEntity> tileEntityClass, TileEntitySpecialRenderer renderer,
	                                             boolean render3dInInventory, float rotation, float scale, float yOffset) {
		ClientRegistry.bindTileEntitySpecialRenderer(tileEntityClass, renderer);
//		BetterStorageRenderingHandler renderingHandler =
//			new BetterStorageRenderingHandler(tileEntityClass, renderer, render3dInInventory, rotation, scale, yOffset);
//		RenderingRegistry.registerBlockHandler(renderingHandler);
//		renderingHandlers.put(tileEntityClass, renderingHandler);
//		return renderingHandler.getRenderId();
		//TODO (1.8): Remove the render IDs.
		return -1;
	}
	public static int registerTileEntityRenderer(Class<? extends TileEntity> tileEntityClass, TileEntitySpecialRenderer renderer) {
		return registerTileEntityRenderer(tileEntityClass, renderer, true, 90, 1, 0);
	}
	
	@SubscribeEvent
	public void drawBlockHighlight(DrawBlockHighlightEvent event) {
		
		//TODO (1.8): Probably needs some severe changes.
		EntityPlayer player = event.player;
		World world = player.worldObj;
		MovingObjectPosition target = WorldUtils.rayTrace(player, event.partialTicks);
		
		if ((target == null) || (target.typeOfHit != MovingObjectType.BLOCK)) return;
		BlockPos pos = target.func_178782_a();
		
		AxisAlignedBB box = null;
		Block block = world.getChunkFromBlockCoords(pos).getBlock(pos);
		TileEntity tileEntity = world.getTileEntity(pos);
		
//		if (block instanceof TileArmorStand)
//			box = getArmorStandHighlightBox(player, world, pos, target.hitVec);
		/*else*/ if (block == Blocks.iron_door)
			box = getIronDoorHightlightBox(player, world, pos, target.hitVec, block);
//		else if (tileEntity instanceof IHasAttachments)
//			box = getAttachmentPointsHighlightBox(player, tileEntity, target);
		
		if (box == null) return;
		
		double xOff = player.lastTickPosX + (player.posX - player.lastTickPosX) * event.partialTicks;
		double yOff = player.lastTickPosY + (player.posY - player.lastTickPosY) * event.partialTicks;
		double zOff = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * event.partialTicks;
		box = box.offset(-xOff, -yOff, -zOff).expand(0.002, 0.002, 0.002);
        
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GlStateManager.color(0.0F, 0.0F, 0.0F, 0.4F);
		GL11.glLineWidth(2.0F);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GlStateManager.depthMask(false);
		
		RenderGlobal.drawOutlinedBoundingBox(box, -1);
		
		GlStateManager.depthMask(true);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GlStateManager.disableBlend();
		
		event.setCanceled(true);
		
	}

	private AxisAlignedBB getArmorStandHighlightBox(EntityPlayer player, World world, BlockPos pos, Vec3 hitVec) {
		
		//TODO (1.8): A little rewrite required here.
		/*int metadata = world.getBlockMetadata(x, y, z);
		if (metadata > 0) y -= 1;
		
		TileEntityArmorStand armorStand = WorldUtils.get(world, x, y, z, TileEntityArmorStand.class);
		if (armorStand == null) return null;
		
		int slot = Math.max(0, Math.min(3, (int)((hitVec.yCoord - y) * 2)));
		
		double minX = x + 2 / 16.0;
		double minY = y + slot / 2.0;
		double minZ = z + 2 / 16.0;
		double maxX = x + 14 / 16.0;
		double maxY = y + slot / 2.0 + 0.5;
		double maxZ = z + 14 / 16.0;
		
		EnumArmorStandRegion region = EnumArmorStandRegion.values()[slot];
		for (ArmorStandEquipHandler handler : BetterStorageArmorStand.getEquipHandlers(region)) {
			ItemStack item = armorStand.getItem(handler);
			if (player.isSneaking()) {
				// Check if we can swap the player's equipped armor with armor stand's.
				ItemStack equipped = handler.getEquipment(player);
				if (((item == null) && (equipped == null)) ||
				    ((item != null) && !handler.isValidItem(player, item)) ||
				    ((equipped != null) && !handler.isValidItem(player, equipped)) ||
				    !handler.canSetEquipment(player, item)) continue;
				return AxisAlignedBB.getBoundingBox(minX, minY, minZ, maxX, maxY, maxZ);
			} else {
				// Check if we can swap the player's held item with armor stand's.
				ItemStack holding = player.getCurrentEquippedItem();
				if (((item == null) && (holding == null)) ||
				    ((holding != null) && !handler.isValidItem(player, holding))) continue;
				return AxisAlignedBB.getBoundingBox(minX, minY, minZ, maxX, maxY, maxZ);
			}
		}
		
		return AxisAlignedBB.getBoundingBox(minX, y, minZ, maxX, y + 2, maxZ);
		*/
		return null;
	}
	
	private AxisAlignedBB getAttachmentPointsHighlightBox(EntityPlayer player, TileEntity tileEntity,
	                                                      MovingObjectPosition target) {
		Attachments attachments = ((IHasAttachments)tileEntity).getAttachments();
		Attachment attachment = attachments.get(target.subHit);
		if (attachment == null) return null;
		return attachment.getHighlightBox();
	}
	
	@SubscribeEvent
	public void onRenderPlayerSpecialsPre(RenderPlayerEvent.Specials.Pre event) {
		ItemStack backpack = ItemBackpack.getBackpackData(event.entityPlayer).backpack;
		if (backpack != null) {
			
			EntityPlayer player = event.entityPlayer;
			float partial = event.partialRenderTick;
			ItemBackpack backpackType = (ItemBackpack)backpack.getItem();
			int color = backpackType.getColor(backpack);
			ModelBackpackArmor model = (ModelBackpackArmor)backpackType.getArmorModel(player, backpack, 0);
			
			//TODO (1.8): The reflection won't work anyways :P
			/*model.onGround = ReflectionUtils.invoke(
					RendererLivingEntity.class, event.renderer, "func_77040_d", "renderSwingProgress",
					EntityLivingBase.class, float.class, player, partial);*/
			model.setLivingAnimations(player, 0, 0, partial);
			
			RenderUtils.bindTexture(new ResourceLocation(backpackType.getArmorTexture(backpack, player, 0, null)));
			RenderUtils.setColorFromInt((color >= 0) ? color : 0xFFFFFF);
			model.render(player, 0, 0, 0, 0, 0, 0);
			
			if (color >= 0) {
				RenderUtils.bindTexture(new ResourceLocation(backpackType.getArmorTexture(backpack, player, 0, "overlay")));
				GlStateManager.color(1.0F, 1.0F, 1.0F);
				model.render(player, 0, 0, 0, 0, 0, 0);
			}
			
			if (backpack.isItemEnchanted()) {
				float f9 = player.ticksExisted + partial;
				
				RenderUtils.bindTexture(Resources.enchantedEffect);

				GlStateManager.enableBlend();
				GlStateManager.color(0.5F, 0.5F, 0.5F, 1.0F);
				GlStateManager.depthFunc(GL11.GL_EQUAL);
				GlStateManager.depthMask(false);
				for (int k = 0; k < 2; ++k) {
					GlStateManager.disableLighting();
					float f11 = 0.76F;
					GlStateManager.color(0.5F * f11, 0.25F * f11, 0.8F * f11, 1.0F);
					GlStateManager.blendFunc(GL11.GL_SRC_COLOR, GL11.GL_ONE);
					GlStateManager.matrixMode(GL11.GL_TEXTURE);
					GlStateManager.loadIdentity();
					float f12 = f9 * (0.001F + k * 0.003F) * 20.0F;
					float f13 = 0.33333334F;
					GlStateManager.scale(f13, f13, f13);
					GlStateManager.rotate(30.0F - k * 60.0F, 0.0F, 0.0F, 1.0F);
					GlStateManager.translate(0.0F, f12, 0.0F);
					GlStateManager.matrixMode(GL11.GL_MODELVIEW);
					model.render(player, 0, 0, 0, 0, 0, 0);
				}
				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
				GlStateManager.matrixMode(GL11.GL_TEXTURE);
				GlStateManager.depthMask(true);
				GlStateManager.loadIdentity();
				GlStateManager.matrixMode(GL11.GL_MODELVIEW);
				GlStateManager.enableLighting();
				GlStateManager.disableBlend();
				GlStateManager.depthFunc(GL11.GL_LEQUAL);
			}
			
		} else backpack = ItemBackpack.getBackpack(event.entityPlayer);
		if (backpack != null) event.renderCape = false;
	}
	
	@Override
	public void registerItemRender(Item item, int id, String name, String type){
		BetterStorage.log.info("Registering item render for " + name + "...");
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(item, id, new ModelResourceLocation(Constants.modId+ ":" + name, type));
	}
	
}
