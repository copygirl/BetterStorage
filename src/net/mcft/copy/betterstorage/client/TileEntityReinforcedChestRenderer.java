package net.mcft.copy.betterstorage.client;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.mcft.copy.betterstorage.BetterStorage;
import net.mcft.copy.betterstorage.Constants;
import net.mcft.copy.betterstorage.block.BlockReinforcedChest;
import net.mcft.copy.betterstorage.block.TileEntityReinforcedChest;
import net.minecraft.client.model.ModelChest;
import net.minecraft.client.model.ModelLargeChest;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.EntityLiving;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

@SideOnly(Side.CLIENT)
public class TileEntityReinforcedChestRenderer extends TileEntitySpecialRenderer {
	
	private static short[] metadataRotationLookup = { -90, 0, 180, 0, 90, -90 };
	
	private ModelChest chestModel = new ModelChest();
	private ModelChest largeChestModel = new ModelLargeChest();

	private ItemStack lock = new ItemStack(BetterStorage.lock);
	private EntityLiving dummyEntity = new EntityLiving(null) {
		@Override
		public int getMaxHealth() { return 0; }
	};
	
	public void renderTileEntityAt(TileEntityReinforcedChest chest, double x, double y, double z, float par8) {
		BlockReinforcedChest block = (BlockReinforcedChest)chest.getBlockType();
		if (block == null) return;
		int metadata;
		if (chest.func_70309_m()) {
			metadata = chest.getBlockMetadata();
			
			if (block != null && metadata == 0) {
				block.unifyAdjacentChests(chest.getWorldObj(), chest.xCoord, chest.yCoord, chest.zCoord);
				metadata = chest.getBlockMetadata();
			}
			
			chest.checkForAdjacentChests();
		} else metadata = 0;
		
		if (chest.adjacentChestZNeg == null && chest.adjacentChestXNeg == null) {
			boolean large = (chest.adjacentChestXPos != null || chest.adjacentChestZPosition != null);
			ModelChest model = (large ? largeChestModel : chestModel);
			bindTextureByName(Constants.getReinforcedChestTexture(block.name, large));
			
			GL11.glPushMatrix();
			GL11.glEnable(GL12.GL_RESCALE_NORMAL);
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			GL11.glTranslatef((float)x, (float)y + 1.0F, (float)z + 1.0F);
			GL11.glScalef(1.0F, -1.0F, -1.0F);
			GL11.glTranslatef(0.5F, 0.5F, 0.5F);
			
			short rotation = metadataRotationLookup[metadata];
			if (metadata == 2 && chest.adjacentChestXPos != null)
				GL11.glTranslatef(1.0F, 0.0F, 0.0F);
			if (metadata == 5 && chest.adjacentChestZPosition != null)
				GL11.glTranslatef(0.0F, 0.0F, -1.0F);
			GL11.glRotatef(rotation, 0.0F, 1.0F, 0.0F);
			GL11.glTranslatef(-0.5F, -0.5F, -0.5F);
			
			float var12 = chest.prevLidAngle + (chest.lidAngle - chest.prevLidAngle) * par8;
			float var13;
			if (chest.adjacentChestZNeg != null) {
				var13 = chest.adjacentChestZNeg.prevLidAngle + (chest.adjacentChestZNeg.lidAngle - chest.adjacentChestZNeg.prevLidAngle) * par8;
				if (var13 > var12) var12 = var13;
			}
			
			if (chest.adjacentChestXNeg != null) {
				var13 = chest.adjacentChestXNeg.prevLidAngle + (chest.adjacentChestXNeg.lidAngle - chest.adjacentChestXNeg.prevLidAngle) * par8;
				if (var13 > var12) var12 = var13;
			}
			
			var12 = 1.0F - var12;
			var12 = 1.0F - var12 * var12 * var12;
			model.chestLid.rotateAngleX = -(var12 * (float) Math.PI / 2.0F);
			model.renderAll();
			
			if (chest.isLocked()) {
		        GL11.glPushMatrix();
	            GL11.glScalef(0.5F, 0.5F, 1.5F);
	            GL11.glTranslatef((large ? 2.5F : 1.5F), 1.3125F, 1.5F/32F);
	            GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F);
		        GL11.glTranslatef(0.9375F, 0.0625F, 0.0F);
	            GL11.glRotatef(-335.0F, 0.0F, 0.0F, 1.0F);
	            GL11.glRotatef(-50.0F, 0.0F, 1.0F, 0.0F);
	            GL11.glScalef(1/1.5F, 1/1.5F, 1/1.5F);
	            GL11.glTranslatef(0, 0.3F, 0.0F);
				RenderManager.instance.itemRenderer.renderItem(dummyEntity, chest.getLock(), 0);
				GL11.glPopMatrix();
			}
			
			GL11.glDisable(GL12.GL_RESCALE_NORMAL);
			GL11.glPopMatrix();
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		}
	}
	
	@Override
	public void renderTileEntityAt(TileEntity entity, double x, double y, double z, float par8) {
		renderTileEntityAt((TileEntityReinforcedChest) entity, x, y, z, par8);
	}
	
}
