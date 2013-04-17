package net.mcft.copy.betterstorage.proxy;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.mcft.copy.betterstorage.block.BlockArmorStand;
import net.mcft.copy.betterstorage.block.TileEntityArmorStand;
import net.mcft.copy.betterstorage.block.TileEntityBackpack;
import net.mcft.copy.betterstorage.block.TileEntityLocker;
import net.mcft.copy.betterstorage.block.TileEntityReinforcedChest;
import net.mcft.copy.betterstorage.client.renderer.BetterStorageRenderingHandler;
import net.mcft.copy.betterstorage.client.renderer.TileEntityArmorStandRenderer;
import net.mcft.copy.betterstorage.client.renderer.TileEntityBackpackRenderer;
import net.mcft.copy.betterstorage.client.renderer.TileEntityLockerRenderer;
import net.mcft.copy.betterstorage.client.renderer.TileEntityReinforcedChestRenderer;
import net.mcft.copy.betterstorage.utils.WorldUtils;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumMovingObjectType;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.event.ForgeSubscribe;

@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy {
	
	public static int reinforcedChestRenderId;
	public static int lockerRenderId;
	public static int armorStandRenderId;
	public static int backpackRenderId;
	
	@Override
	public void registerTileEntites() {
		super.registerTileEntites();
		reinforcedChestRenderId = registerTileEntityRenderer(TileEntityReinforcedChest.class, new TileEntityReinforcedChestRenderer());
		lockerRenderId = registerTileEntityRenderer(TileEntityLocker.class, new TileEntityLockerRenderer());
		armorStandRenderId = registerTileEntityRenderer(TileEntityArmorStand.class, new TileEntityArmorStandRenderer(), false, 0, 1, 0);
		backpackRenderId = registerTileEntityRenderer(TileEntityBackpack.class, new TileEntityBackpackRenderer(), true, -160, 1.5F, 0.14F);
	}
	
	private int registerTileEntityRenderer(Class<? extends TileEntity> tileEntityClass, TileEntitySpecialRenderer renderer,
	                                       boolean render3dInInventory, float rotation, float scale, float yOffset) {
		ClientRegistry.bindTileEntitySpecialRenderer(tileEntityClass, renderer);
		BetterStorageRenderingHandler renderingHandler =
			new BetterStorageRenderingHandler(tileEntityClass, renderer, render3dInInventory, rotation, scale, yOffset);
		RenderingRegistry.registerBlockHandler(renderingHandler);
		return renderingHandler.getRenderId();
	}
	private int registerTileEntityRenderer(Class<? extends TileEntity> tileEntityClass, TileEntitySpecialRenderer renderer) {
		return registerTileEntityRenderer(tileEntityClass, renderer, true, 90, 1, 0);
	}
	
	@ForgeSubscribe
	public void drawBlockHighlight(DrawBlockHighlightEvent event) {
		
		MovingObjectPosition target = event.target;
		if (target.typeOfHit != EnumMovingObjectType.TILE) return;
		
		EntityPlayer player = event.player;
		World world = player.worldObj;
		int x = target.blockX;
		int y = target.blockY;
		int z = target.blockZ;
		
		int blockId = world.getBlockId(x, y, z);
		if (!(Block.blocksList[blockId] instanceof BlockArmorStand)) return;
		
		int metadata = world.getBlockMetadata(x, y, z);
		if (metadata > 0) y -= 1;
		
		TileEntityArmorStand armorStand = WorldUtils.get(world, x, y, z, TileEntityArmorStand.class);
		if (armorStand == null) return;
		int slot = Math.min(3, (int)((target.hitVec.yCoord - y) * 2));
		
		ItemStack item = armorStand.armor[slot];
		ItemStack holding = player.getCurrentEquippedItem();
		ItemStack armor = player.inventory.armorInventory[slot];
		
		if (player.isSneaking()) {
			if (holding != null || (item == null && armor == null) ||
			    (armor != null && !armor.getItem().isValidArmor(armor, 3 - slot))) return;
		} else if (!(item != null && holding == null) &&
		           !(holding != null && holding.getItem().isValidArmor(holding, 3 - slot))) return;
		
        double xOff = player.lastTickPosX + (player.posX - player.lastTickPosX) * event.partialTicks;
        double yOff = player.lastTickPosY + (player.posY - player.lastTickPosY) * event.partialTicks;
        double zOff = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * event.partialTicks;
		
        double minX = x + 2 / 16.0 - xOff;
        double minY = y + slot / 2.0 - yOff;
        double minZ = z + 2 / 16.0 - zOff;
        double maxX = x + 14 / 16.0 - xOff;
        double maxY = y + slot / 2.0 + 0.5 - yOff;
        double maxZ = z + 14 / 16.0 - zOff;
        
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glColor4f(0.0F, 0.0F, 0.0F, 0.4F);
		GL11.glLineWidth(2.0F);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glDepthMask(false);
		
		Tessellator tes = Tessellator.instance;
		tes.startDrawing(3);
		tes.addVertex(minX, minY, minZ);
		tes.addVertex(maxX, minY, minZ);
		tes.addVertex(maxX, minY, maxZ);
		tes.addVertex(minX, minY, maxZ);
		tes.addVertex(minX, minY, minZ);
		tes.draw();
		tes.startDrawing(3);
		tes.addVertex(minX, maxY, minZ);
		tes.addVertex(maxX, maxY, minZ);
		tes.addVertex(maxX, maxY, maxZ);
		tes.addVertex(minX, maxY, maxZ);
		tes.addVertex(minX, maxY, minZ);
		tes.draw();
		tes.startDrawing(1);
		tes.addVertex(minX, minY, minZ);
		tes.addVertex(minX, maxY, minZ);
		tes.addVertex(maxX, minY, minZ);
		tes.addVertex(maxX, maxY, minZ);
		tes.addVertex(maxX, minY, maxZ);
		tes.addVertex(maxX, maxY, maxZ);
		tes.addVertex(minX, minY, maxZ);
		tes.addVertex(minX, maxY, maxZ);
		tes.draw();
		
		GL11.glDepthMask(true);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_BLEND);
		
		event.setCanceled(true);
		
	}
	
}
