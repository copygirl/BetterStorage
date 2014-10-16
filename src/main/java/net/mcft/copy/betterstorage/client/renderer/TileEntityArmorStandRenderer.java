package net.mcft.copy.betterstorage.client.renderer;

import java.util.UUID;

import net.mcft.copy.betterstorage.addon.armourersworkshop.AWAddon;
import net.mcft.copy.betterstorage.client.model.ModelArmorStand;
import net.mcft.copy.betterstorage.misc.Resources;
import net.mcft.copy.betterstorage.tile.entity.TileEntityArmorStand;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.IChatComponent;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import com.mojang.authlib.GameProfile;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TileEntityArmorStandRenderer extends TileEntitySpecialRenderer {
	
	private EntityPlayer playerDummy = null;
	private RenderArmor renderArmor = new RenderArmor(RenderManager.instance);
	
	private ModelArmorStand armorStandModel = new ModelArmorStand();
	
	public void renderTileEntityAt(TileEntityArmorStand locker, double x, double y, double z, float par8) {
		
		int rotation = locker.rotation * 360 / 16;
		
		ModelArmorStand model = armorStandModel;
		bindTexture(Resources.textureArmorStand);
		
		GL11.glPushMatrix();
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glTranslated(x, y + 2.0, z + 1.0);
		GL11.glScalef(1.0F, -1.0F, -1.0F);
		GL11.glTranslated(0.5F, 0.5F, 0.5F);
		
		model.setRotation(rotation);
		model.renderAll();
		
		GL11.glDisable(GL12.GL_RESCALE_NORMAL);
		GL11.glPopMatrix();
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		
		if (locker.getWorldObj() == null) return;
		
		if (playerDummy == null) {
			playerDummy = new AbstractClientPlayer(locker.getWorldObj(), new GameProfile(UUID.randomUUID(), "PLAYER_DUMMY")) {
				@Override public void addChatMessage(IChatComponent var1) {  }
				@Override public ChunkCoordinates getPlayerCoordinates() { return null; }
				@Override public boolean canCommandSenderUseCommand(int i, String s) { return false; }
			};
			playerDummy.setInvisible(true);
		}
		
		playerDummy.ticksExisted = locker.ticksExisted;
		playerDummy.inventory.armorInventory = locker.getVanillaArmor();
		playerDummy.worldObj = locker.getWorldObj();
		playerDummy.renderYawOffset = playerDummy.prevRenderYawOffset = rotation;
		playerDummy.rotationYawHead = playerDummy.prevRotationYawHead = rotation;
		renderArmor.doRender(playerDummy, x + 0.5, y + 27 / 16.0, z + 0.5, rotation, par8);
		
		if (AWAddon.isLoaded) {
			GL11.glPushMatrix();
			GL11.glTranslated(x, y, z);
			GL11.glRotatef(180, 0, 0, 1);
			GL11.glRotatef(180, 0, 1, 0);
			GL11.glTranslatef(0.5F, -1.5F, -0.5F);
			GL11.glRotatef(rotation, 0, 1, 0);
			int[] eqid = locker.getAWArmor();
			for(int i = 0; i < 5; i++) {
				int id = eqid[i];
				if(id != 0) AWAddon.renderHandler.renderCustomEquipment(id, null);
			}
			GL11.glPopMatrix();
		}
	}
	
	@Override
	public void renderTileEntityAt(TileEntity entity, double x, double y, double z, float par8) {
		renderTileEntityAt((TileEntityArmorStand)entity, x, y, z, par8);
	}
	
}
